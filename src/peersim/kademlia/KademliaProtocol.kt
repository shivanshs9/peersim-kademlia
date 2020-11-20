package peersim.kademlia

import peersim.config.Configuration
import peersim.core.CommonState
import peersim.core.Node
import peersim.edsim.EDProtocol
import peersim.edsim.EDSimulator
import peersim.kademlia.events.ProtocolOperation
import peersim.kademlia.events.RPC
import peersim.kademlia.events.RPCPrimitive
import peersim.kademlia.rpc.*
import peersim.transport.UnreliableTransport
import java.math.BigInteger

class KademliaProtocol(val prefix: String) : EDProtocol, DHTProtocolInterface {
    var kademliaId: Int = 0
        private set
    private val transportPid = Configuration.getPid("$prefix.$PAR_TRANSPORT")

    val routingTable = RoutingTable()
    val dhtTable = DHTable()

    lateinit var nodeId: BigInteger

    private val sentRpcs: MutableMap<Long, RPC<*>> = linkedMapOf()
    private val setFindOps: MutableMap<Long, FindCloseNodesOperation> = linkedMapOf()

    init {
        _init()
    }

    /**
     * This procedure is called only once and allow to initialize the internal state of KademliaProtocol. Every node shares the
     * same configuration, so it is sufficient to call this routine once.
     */
    private fun _init() {
        // execute once
        if (_ALREADY_INSTALLED) return

        // read paramaters
        KademliaCommonConfig.K = Configuration.getInt("$prefix.$PAR_K", KademliaCommonConfig.K)
        KademliaCommonConfig.ALPHA = Configuration.getInt("$prefix.$PAR_ALPHA", KademliaCommonConfig.ALPHA)
        KademliaCommonConfig.BITS = Configuration.getInt("$prefix.$PAR_BITS", KademliaCommonConfig.BITS)
        _ALREADY_INSTALLED = true
    }

    override fun clone(): Any = KademliaProtocol(prefix)

    private fun iterativeNodeLookup(message: ProtocolOperation<BigInteger>) {
        val key = message.data ?: return
        val findOp = message.refMsgId?.let { setFindOps[it] } ?: FindCloseNodesOperation(key, message).also {
            setFindOps[it.operationId] = it
        }
        val neighbors = routingTable.getNeighbours(key, nodeId)
        findOp.refreshClosestNodes(neighbors)
        findOp.availableRequests = KademliaCommonConfig.ALPHA

        repeat(KademliaCommonConfig.ALPHA) {
            findOp.nextClosest?.let {
                // Decide which Primitive RPC to call next
                val findMsg = if (message is FindNodeOperation)
                    FindNodePrimitive(nodeId, it, key, findOp.operationId)
                else FindValuePrimitive(nodeId, it, key, findOp.operationId)

                findMsg.refMsgId = message.id

                sendMessage(findMsg)
                sendTimeout(findMsg)
                findOp.nrHops++
            }
        }
    }

    private fun iterativeFindNode(message: FindNodeOperation) = iterativeNodeLookup(message)

    private fun iterativeFindValue(message: FindValueOperation) = iterativeNodeLookup(message)

    private fun findClosestContacts(message: RPCPrimitive<BigInteger>) {
        val key = message.data ?: return
        // get the ALPHA closest node to destNode
        val neighbors = routingTable.getNeighbours(key, message.srcNodeId)

        // create a response message containing the neighbours (with the same id of the request)
        val response = ResultFindNodePrimitive(
                message, neighbors, message.data,
                operationId = (message as? FindNodePrimitive)?.operationId
                        ?: (message as FindValuePrimitive).operationId
        )

        // send back the neighbours to the source of the message
        sendMessage(response)
    }

    private fun findValueElseClosestContacts(message: FindValuePrimitive) {
        val key = message.data ?: return
        dhtTable.getValue(key)?.also {
            val response = ResultFindValuePrimitive(message, it)
            sendMessage(response)
        } ?: findClosestContacts(message)
    }

    private fun respondBackWithContacts(message: ResultFindNodePrimitive) {
        val requestMsg = sentRpcs.remove(message.refMsgId) ?: return // Return case happens only in the case of timeout

        val findOp = setFindOps[message.operationId] ?: return
        findOp.refreshClosestNodes(message.data!!)
        findOp.availableRequests++

        while (findOp.availableRequests > 0) {
            val neighbor = findOp.nextClosest
            if (neighbor != null) {
                // Probe the next closest neighbor for contacts
                val findMsg = FindNodePrimitive(nodeId, neighbor, message.key, findOp.operationId).apply {
                    refMsgId = findOp.message.id
                }
                sendMessage(findMsg)
                sendTimeout(findMsg)
                findOp.nrHops++
            } else if (findOp.availableRequests == KademliaCommonConfig.ALPHA) { // no outstanding requests pending and no new neighbors
                // Search operation finished
                setFindOps.remove(findOp.operationId)

                // Maybe the find operation is requested by another operation
                val resMsg = findOp.message.refMsgId?.let {
                    sentRpcs.remove(it) as? ProtocolOperation<*>
                } ?: findOp.message

                // Send back the response cross-protocol
                val response = ResultFindNodeOperation(resMsg, findOp.result)
                sendMessage(response, protocolPid = findOp.message.protocolPid)

                // update observer statistics
                val timeInterval: Long = CommonState.getTime() - findOp.timestamp
                KademliaObserver.timeStore.add(timeInterval.toDouble())
                KademliaObserver.hopStore.add(findOp.nrHops.toDouble())
                KademliaObserver.msg_deliv.add(1.0)

                // exit loop
                break
            } else break // no new neighbors but pending outstanding requests
        }
    }

    private fun respondBackWithValue(message: ResultFindValuePrimitive) {
        val requestMsg = sentRpcs.remove(message.refMsgId) as? FindValuePrimitive
                ?: return // Return case happens only in the case of timeout

        // Update the DHT of this node
        message.data?.let { dhtTable.store(requestMsg.data!!, it) }

        // Search operation finished
        val findOp = setFindOps.remove(message.operationId) ?: return

        // Send back the response cross-protocol
        val response = ResultFindValueOperation(findOp.message as FindValueOperation, message.data)
        sendMessage(response, protocolPid = response.protocolPid)
    }

    private fun storeInDht(message: StorePrimitive) {
        message.data?.also {
            dhtTable.store(it.first, it.second)
        }
    }

    private fun iterativeStore(message: StoreValueOperation) {
        val key = message.data?.first ?: return
        sentRpcs[message.id] = message
        val findNodeOp = FindNodeOperation(kademliaId, nodeId, key).apply { refMsgId = message.id }
        val findOp = FindCloseNodesOperation(key, findNodeOp)
        setFindOps[findOp.operationId] = findOp

        // Schedule Find NODE Operation
        sendMessage(findNodeOp)
    }

    private fun continueWithStore(message: StoreValueOperation, neighbors: Set<BigInteger>?) = neighbors?.also { neighbors ->
        if (message.data == null) return@also
        neighbors.forEach {
            val storeMsg = StorePrimitive(nodeId, it, message.data)
            sendMessage(storeMsg)
        }
        dhtTable.store(message.data.first, message.data.second)
        val response = ResultStoreValueOperation(message)
        sendMessage(response, protocolPid = response.protocolPid)
    }

    private fun handleNodeLookupTimeout(event: RPCTimeout, msg: RPCPrimitive<BigInteger>) {
        val opId = (msg as? FindNodePrimitive)?.operationId
                ?: (msg as FindValuePrimitive).operationId

        val findOp = setFindOps[opId] ?: return
        findOp.removeNode(event.nodeId)
        findOp.nextClosest?.also {
            val findMsg = if (msg is FindNodePrimitive) FindNodePrimitive(msg.srcNodeId, it, msg.data!!, opId)
            else FindValuePrimitive(msg.srcNodeId, it, msg.data!!, opId)
            findMsg.refMsgId = findOp.message.id
            sendMessage(findMsg)
            sendTimeout(findMsg)
        }
    }

    fun sendTimeout(msg: RPCPrimitive<*>, protocolPid: Int = kademliaId) {
        // remember the sent RPCs to handle in timeout
        sentRpcs[msg.id] = msg

        // Create and send the TIMEOUT msg
        val destNodeId = msg.destNodeId
        val timeoutMsg = RPCTimeout(destNodeId, msg.id)
        val src: Node = getNode(nodeId)!!
        val dest: Node = getNode(destNodeId)!!
        val transport = src.getProtocol(transportPid) as UnreliableTransport

        // TODO: Impossible to calculate latency beforehand in RL
        val latency = transport.getLatency(src, dest)
        EDSimulator.add(4 * latency, timeoutMsg, src, protocolPid)
    }

    /**
     * Add Target Node to Routing Table to keep the latest contacts updated
     */
    fun syncRoutingTable(targetNodeId: BigInteger) {
        routingTable.addNeighbour(targetNodeId)
    }

    fun sendMessage(message: RPC<*>, protocolPid: Int = kademliaId, forceDelay: Long? = null) {
        syncRoutingTable(message.destNodeId)

        val src: Node = getNode(nodeId)!!
        val dest: Node = getNode(message.destNodeId)!!

        if (forceDelay == null && src != dest) {
            val transport = src.getProtocol(transportPid) as UnreliableTransport
            transport.send(src, dest, message, protocolPid)
        } else EDSimulator.add(forceDelay ?: 0, message, src, protocolPid)
    }

    override fun processEvent(node: Node, pid: Int, event: Any?) {
        if (kademliaId == 0) kademliaId = pid

        (event as? RPC<*>)?.onDelivered(node, this)

        when (event) {
            is FindNodeOperation -> iterativeFindNode(event)
            is FindValueOperation -> iterativeFindValue(event)
            is FindNodePrimitive -> {
                syncRoutingTable(event.srcNodeId)
                findClosestContacts(event)
            }
            is FindValuePrimitive -> {
                syncRoutingTable(event.srcNodeId)
                findValueElseClosestContacts(event)
            }
            is StorePrimitive -> {
                syncRoutingTable(event.srcNodeId)
                storeInDht(event)
            }
            is ResultFindNodePrimitive -> {
                syncRoutingTable(event.srcNodeId)
                respondBackWithContacts(event)
            }
            is ResultFindValuePrimitive -> {
                syncRoutingTable(event.srcNodeId)
                respondBackWithValue(event)
            }
            is RPCTimeout -> {
                if (sentRpcs.containsKey(event.msgId)) {
                    println("${CommonState.getTime()}  -> RPC timeout")
                    // Remove from routing table since no RPC response
                    routingTable.removeNeighbour(event.nodeId)
                    // timeout occurred and RPC not received
                    val msg = sentRpcs.remove(event.msgId) as? RPCPrimitive<BigInteger>
                    if (msg != null) handleNodeLookupTimeout(event, msg)
                }
            }
            is StoreValueOperation -> iterativeStore(event)
            is ResultFindNodeOperation -> {
                if (event.requestOp is StoreValueOperation) continueWithStore(event.requestOp, event.data)
            }
        }
    }

    fun setNode(nodeId: BigInteger, node: Node) {
        this.nodeId = nodeId
        routingTable.nodeId = nodeId
        nodeMapper[nodeId] = node
    }

    companion object {
        private val nodeMapper: MutableMap<BigInteger, Node> = hashMapOf()

        // VARIABLE PARAMETERS
        private const val PAR_K = "K"
        private const val PAR_ALPHA = "ALPHA"
        private const val PAR_BITS = "BITS"
        private const val PAR_TRANSPORT = "transport"

        /**
         * allow to call the service initializer only once
         */
        private var _ALREADY_INSTALLED = false

        fun getNode(nodeId: BigInteger): Node? = nodeMapper[nodeId]
    }
}
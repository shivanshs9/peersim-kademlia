package peersim.kademlia.events

import peersim.core.Node
import peersim.kademlia.DHTProtocolInterface
import java.math.BigInteger

open class ProtocolOperation<T>(
        val protocolPid: Int,
        nodeId: BigInteger,
        data: T? = null,
        type: Int = 0
) : RPC<T>(nodeId, nodeId, data, type = type) {
    override fun onDelivered(node: Node, protocol: DHTProtocolInterface) {
    }
}

open class ProtocolResultOperation<T>(
        val requestOp: ProtocolOperation<*>,
        data: T? = null,
        val status: Int = STATUS_SUCCESS
) : ProtocolOperation<T>(requestOp.protocolPid, requestOp.destNodeId, data, type = requestOp.type) {
    init {
        refMsgId = requestOp.id
    }

    companion object {
        const val STATUS_SUCCESS = 0
        const val STATUS_FAIL = 1
    }
}
package peersim.kademlia

import peersim.core.Node
import peersim.edsim.EDProtocol
import peersim.kademlia.events.RPC
import peersim.kademlia.rpc.FindNodePrimitive

class KademliaProtocol(val prefix: String): EDProtocol, DHTProtocolInterface {
    var kademliaId: Int = 0
        private set

    override fun clone(): Any = KademliaProtocol(prefix)

    override fun processEvent(node: Node, pid: Int, event: Any?) {
        if (kademliaId == 0) kademliaId = pid
        val message = event as? RPC<*> ?: return

        message.onDelivered(node, this)

        when (message) {
            is FindNodePrimitive -> {
            }
        }
    }
}
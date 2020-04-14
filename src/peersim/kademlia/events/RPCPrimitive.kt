package peersim.kademlia.events

import peersim.core.Node
import peersim.kademlia.DHTProtocolInterface
import java.math.BigInteger

open class RPCPrimitive<T>(
        srcNodeId: BigInteger,
        destNodeId: BigInteger,
        data: T? = null,
        type: Int = 0
) : RPC<T>(srcNodeId, destNodeId, data, type) {
    override fun onDelivered(node: Node, protocol: DHTProtocolInterface) {
    }
}

open class RPCResultPrimitive<T>(
        msg: RPCPrimitive<*>,
        data: T? = null,
        type: Int = 0
) : RPCPrimitive<T>(msg.destNodeId, msg.srcNodeId, data, type = type) {
    init {
        refMsgId = msg.id
    }
}
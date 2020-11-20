package peersim.kademlia.rpc

import peersim.kademlia.events.RPCPrimitive
import peersim.kademlia.events.RPCResultPrimitive
import peersim.kademlia.toAscii
import java.math.BigInteger

class StorePrimitive(
        srcNodeId: BigInteger,
        destNodeId: BigInteger,
        data: Pair<BigInteger, Any>
) : RPCPrimitive<Pair<BigInteger, Any>>(srcNodeId, destNodeId, data, type = TYPE_STORE) {
    companion object {
        val TYPE_STORE: Int = "store".toAscii()
        val TYPE_APPEND: Int = "append".toAscii()
    }
}

class ResultStorePrimitive(
        msg: StorePrimitive,
        status: Int = STATUS_SUCCESS
) : RPCResultPrimitive<BigInteger>(msg, msg.data!!.first, status = status)
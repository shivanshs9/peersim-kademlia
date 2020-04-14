package peersim.kademlia.rpc

import peersim.kademlia.events.RPCPrimitive
import peersim.kademlia.events.RPCResultPrimitive
import peersim.kademlia.toAscii
import java.math.BigInteger

class FindValuePrimitive(
        srcNodeId: BigInteger,
        destNodeId: BigInteger,
        key: String
) : RPCPrimitive<Int>(srcNodeId, destNodeId, key.hashCode(), type = TYPE_FIND_VALUE) {
    companion object {
        val TYPE_FIND_VALUE = "fvalue".toAscii()
    }
}

class ResultFindValuePrimitive(
        msg: FindValuePrimitive,
        value: Any?
) : RPCResultPrimitive<Any?>(msg, value)
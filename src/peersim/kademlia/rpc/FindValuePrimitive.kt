package peersim.kademlia.rpc

import peersim.kademlia.events.RPCPrimitive
import peersim.kademlia.events.RPCResultPrimitive
import peersim.kademlia.toAscii
import java.math.BigInteger

class FindValuePrimitive(
        srcNodeId: BigInteger,
        destNodeId: BigInteger,
        key: BigInteger,
        val operationId: Long
) : RPCPrimitive<BigInteger>(srcNodeId, destNodeId, key, type = TYPE_FIND_VALUE) {
    companion object {
        val TYPE_FIND_VALUE = "fvalue".toAscii()
    }
}

class ResultFindValuePrimitive(
        msg: FindValuePrimitive,
        value: Any?,
        val operationId: Long = msg.operationId,
        status: Int = STATUS_SUCCESS
) : RPCResultPrimitive<Any?>(msg, value, status = status)

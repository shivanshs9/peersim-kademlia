package peersim.kademlia.rpc

import peersim.kademlia.events.RPCPrimitive
import peersim.kademlia.events.RPCResultPrimitive
import peersim.kademlia.toAscii
import java.math.BigInteger

class FindNodePrimitive(
        srcNodeId: BigInteger,
        destNodeId: BigInteger,
        key: BigInteger,
        val operationId: Long
) : RPCPrimitive<BigInteger>(srcNodeId, destNodeId, key, type = TYPE_FIND_NODE) {
    companion object {
        val TYPE_FIND_NODE = "fnode".toAscii()
    }
}

class ResultFindNodePrimitive(
        msg: RPCPrimitive<BigInteger>,
        contacts: Array<BigInteger>,
        val key: BigInteger,
        val operationId: Long,
        status: Int = STATUS_SUCCESS
) : RPCResultPrimitive<Array<BigInteger>>(msg, contacts, status = status)
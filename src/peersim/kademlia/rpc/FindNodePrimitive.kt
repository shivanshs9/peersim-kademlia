package peersim.kademlia.rpc

import peersim.kademlia.events.RPCPrimitive
import peersim.kademlia.events.RPCResultPrimitive
import peersim.kademlia.toAscii
import java.math.BigInteger

class FindNodePrimitive(
        srcNodeId: BigInteger,
        destNodeId: BigInteger,
        key: BigInteger
) : RPCPrimitive<BigInteger>(srcNodeId, destNodeId, key, type = TYPE_FIND_NODE) {
    companion object {
        val TYPE_FIND_NODE = "fnode".toAscii()
    }
}

class ResultFindNodePrimitive(
        msg: FindNodePrimitive,
        contacts: Set<BigInteger>
) : RPCResultPrimitive<Set<BigInteger>>(msg, contacts, type = FindNodePrimitive.TYPE_FIND_NODE)
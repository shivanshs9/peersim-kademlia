package peersim.kademlia.rpc

import peersim.kademlia.DHTable
import peersim.kademlia.events.ProtocolOperation
import peersim.kademlia.events.ProtocolResultOperation
import peersim.kademlia.toAscii
import java.math.BigInteger

/**
 * Created by shivanshs9 on 18/04/20.
 */
class FindValueOperation(
        protocolPid: Int,
        nodeId: BigInteger,
        key: String
) : ProtocolOperation<BigInteger>(protocolPid, nodeId, DHTable.hash(key), type = TYPE_FIND_NODE) {
    companion object {
        val TYPE_FIND_NODE = "fnode".toAscii()
    }
}

class ResultFindValueOperation(
        msg: FindValueOperation,
        data: Any?,
        status: Int = STATUS_SUCCESS
) : ProtocolResultOperation<Any?>(msg, data, status = status)
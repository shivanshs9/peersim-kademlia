package peersim.kademlia.rpc

import peersim.kademlia.events.ProtocolOperation
import peersim.kademlia.events.ProtocolResultOperation
import peersim.kademlia.toAscii
import java.math.BigInteger

/**
 * Created by shivanshs9 on 18/04/20.
 */
class FindNodeOperation(
        protocolPid: Int,
        nodeId: BigInteger,
        key: BigInteger
) : ProtocolOperation<BigInteger>(protocolPid, nodeId, key, type = TYPE_FIND_NODE) {
    companion object {
        val TYPE_FIND_NODE = "fnode".toAscii()
    }
}

class ResultFindNodeOperation(
        msg: ProtocolOperation<*>,
        contacts: Set<BigInteger>,
        status: Int = STATUS_SUCCESS
) : ProtocolResultOperation<Set<BigInteger>>(msg, contacts, status = status)
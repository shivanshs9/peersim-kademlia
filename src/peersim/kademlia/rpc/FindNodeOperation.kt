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
        srcNodeId: BigInteger,
        destNodeId: BigInteger,
        key: BigInteger
) : ProtocolOperation<BigInteger>(protocolPid, srcNodeId, destNodeId, key, type = TYPE_FIND_NODE) {
    companion object {
        val TYPE_FIND_NODE = "fnode".toAscii()
    }
}

class ResultFindNodeOperation(
        msg: ProtocolOperation<BigInteger>,
        contacts: Set<BigInteger>,
        status: Int = STATUS_SUCCESS
) : ProtocolResultOperation<Set<BigInteger>>(msg, contacts, status = status)
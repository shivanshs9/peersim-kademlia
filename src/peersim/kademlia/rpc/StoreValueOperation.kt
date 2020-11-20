package peersim.kademlia.rpc

import peersim.kademlia.DHTable
import peersim.kademlia.events.ProtocolOperation
import peersim.kademlia.events.ProtocolResultOperation
import peersim.kademlia.toAscii
import java.math.BigInteger

/**
 * Created by shivanshs9 on 18/04/20.
 */
open class StoreValueOperation(
        protocolPid: Int,
        nodeId: BigInteger,
        key: BigInteger,
        value: Any
) : ProtocolOperation<Pair<BigInteger, Any>>(protocolPid, nodeId, key to value, type = TYPE_STORE) {
    constructor(protocolPid: Int, nodeId: BigInteger, key: String, value: Any) : this(protocolPid, nodeId, DHTable.hash(key), value)

    companion object {
        val TYPE_STORE: Int = "store".toAscii()
    }
}

class ResultStoreValueOperation(
        msg: StoreValueOperation,
        status: Int = STATUS_SUCCESS
) : ProtocolResultOperation<BigInteger>(msg, msg.data?.first, status = status)

package peersim.kademlia.rpc

import peersim.kademlia.DHTable
import peersim.kademlia.events.ProtocolOperation
import peersim.kademlia.events.ProtocolResultOperation
import peersim.kademlia.toAscii
import java.math.BigInteger

/**
 * Created by shivanshs9 on 18/04/20.
 */
class StoreValueOperation(
        protocolPid: Int,
        nodeId: BigInteger,
        key: String,
        value: Any
) : ProtocolOperation<Pair<BigInteger, Any>>(protocolPid, nodeId, getData(key, value), type = TYPE_STORE) {
    companion object {
        val TYPE_STORE: Int = "store".toAscii()

        private fun getData(key: String, value: Any): Pair<BigInteger, Any> {
            return DHTable.hash(key) to value
        }
    }
}

class ResultStoreValueOperation(
        msg: StoreValueOperation,
        status: Int = STATUS_SUCCESS
) : ProtocolResultOperation<BigInteger>(msg, msg.data?.first, status = status)

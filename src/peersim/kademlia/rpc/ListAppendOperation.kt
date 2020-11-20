package peersim.kademlia.rpc

import peersim.kademlia.DHTable
import peersim.kademlia.toAscii
import java.math.BigInteger

/**
 * Created by shivanshs9 on 20/11/20.
 */
class ListAppendOperation(protocolPid: Int, nodeId: BigInteger, key: BigInteger, data: Any) : StoreValueOperation(protocolPid, nodeId, key, data) {
    constructor(protocolPid: Int, nodeId: BigInteger, key: String, value: Any) : this(protocolPid, nodeId, DHTable.hash(key), value)

    init {
        type = TYPE_APPEND
    }

    companion object {
        val TYPE_APPEND = "append".toAscii()
    }
}
package peersim.kademlia.rpc

import peersim.kademlia.events.RPCPrimitive
import peersim.kademlia.toAscii
import java.math.BigInteger

class StorePrimitive(
        srcNodeId: BigInteger,
        destNodeId: BigInteger,
        key: String,
        value: Any
) : RPCPrimitive<Pair<Int, Any>>(srcNodeId, destNodeId, data = getData(key, value), type = TYPE_STORE) {
    companion object {
        val TYPE_STORE: Int = "store".toAscii()
        private fun getData(key: String, value: Any): Pair<Int, Any> {
            return key.hashCode() to value
        }
    }
}
package peersim.kademlia.events

import peersim.core.Node
import peersim.kademlia.DHTProtocolInterface
import java.math.BigInteger
import java.util.concurrent.atomic.AtomicLong

abstract class RPC<out T>(
        val srcNodeId: BigInteger,
        val destNodeId: BigInteger,
        val data: T? = null,
        type: Int = 0) : SimpleEvent(type) {
    val id: Long = uniqueId

    var responseStatus: Long = -1
        private set

    var refMsgId: Long? = null

    abstract fun onDelivered(node: Node, protocol: DHTProtocolInterface)

    companion object {
        const val STATUS_SUCCESS = 0

        private val ID_GENERATOR = AtomicLong()

        private val uniqueId
            get() = ID_GENERATOR.incrementAndGet()
    }
}
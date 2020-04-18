package peersim.kademlia

import peersim.kademlia.events.ProtocolOperation
import java.math.BigInteger
import java.util.concurrent.atomic.AtomicLong

class FindCloseNodesOperation(
        val nodeId: BigInteger,
        val message: ProtocolOperation<*>,
        val timestamp: Long = message.timestamp,
        val operationId: Long = message.refMsgId ?: uniqueId
) {
    var availableRequests = KademliaCommonConfig.ALPHA

    /**
     * Number of hops the message did
     */
    var nrHops = 0

    private val closestSet: MutableMap<BigInteger, Boolean> = mutableMapOf()

    val result: Set<BigInteger>
        get() = closestSet.keys

    val nextClosest: BigInteger?
        get() = closestSet
                .filterValues { !it }
                .map { it.key to Util.distance(it.key, nodeId) }
                .minBy { it.second }
                ?.first?.also {
                    closestSet[it] = true
                    availableRequests--
                }

    fun refreshClosestNodes(neighbors: Array<BigInteger>) {
        neighbors.forEach {
            if (it !in closestSet) {
                if (closestSet.size < KademliaCommonConfig.K)
                    closestSet[it] = false
                else {
                    // New Neighbour will replace the node with max distance in closest set
                    val newDist = Util.distance(it, nodeId)

                    // find the node with max distance
                    val maxDistEntry = closestSet.map { it.key to Util.distance(it.key, nodeId) }.maxBy { it.second }
                            ?: it to newDist
                    if (maxDistEntry.second > newDist) { // if max distance is still greater than the new distance, then new node is closer
                        closestSet.remove(maxDistEntry.first)
                        closestSet[it] = false
                    }
                }
            }
        }
    }

    fun removeNode(nodeId: BigInteger) = closestSet.remove(nodeId)

    companion object {
        private val ID_GENERATOR = AtomicLong()

        private val uniqueId
            get() = ID_GENERATOR.incrementAndGet()
    }
}
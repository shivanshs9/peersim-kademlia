package peersim.kademlia

import peersim.core.CommonState
import java.math.BigInteger
import java.util.*

/**
 * Implements a K-Bucket as specified by Kademlia spec
 */
class KBucket {
    // k-bucket array
    @JvmField
    var neighbours: TreeMap<BigInteger, Long> = TreeMap()

    // add a neighbour to this k-bucket
    fun addNeighbour(node: BigInteger) {
        val time = CommonState.getTime()
        if (neighbours.size < KademliaCommonConfig.K) { // k-bucket isn't full
            neighbours[node] = time // add neighbour to the tail of the list
        } else {
            // remove the oldest neighbour
            neighbours.minBy { it.value }?.also {
                removeNeighbour(it.key)
                neighbours[node] = time
            }
        }
    }

    // remove a neighbour from this k-bucket
    fun removeNeighbour(node: BigInteger) = neighbours.remove(node)
}
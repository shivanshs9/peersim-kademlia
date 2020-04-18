package peersim.kademlia

import peersim.kademlia.Util.distance
import peersim.kademlia.Util.prefixLen
import java.math.BigInteger
import java.util.*

/**
 * Implementation of Routing Table of a node as given in Kademlia spec
 */
class RoutingTable : Cloneable {
    // NodeID of the node
    lateinit var nodeId: BigInteger

    // k-buckets
    private val kBuckets: Array<KBucket> = Array(KademliaCommonConfig.BITS + 1) { KBucket() }

    // add a neighbour to the correct k-bucket
    fun addNeighbour(node: BigInteger) {
        kBuckets[prefixLen(nodeId, node)].addNeighbour(node)
    }

    // remove a neighbour from the correct k-bucket
    fun removeNeighbour(node: BigInteger) {
        kBuckets[prefixLen(nodeId, node)].removeNeighbour(node)
    }

    // return the closest neighbour to a key from the correct k-bucket
    fun getNeighbours(key: BigInteger, src: BigInteger): List<BigInteger> {
        // neighbour candidates
        val candidates = ArrayList<BigInteger>()

        // get the length of the longest common prefix
        var lcpLength = prefixLen(nodeId, key)

        // return the k-bucket if is full
        if (kBuckets[lcpLength].neighbours.size >= KademliaCommonConfig.K) {
            return kBuckets[lcpLength].neighbours.keys.apply {
                remove(src)
            }.toList()
        }
        // else get k closest node from all k-buckets
        lcpLength = 0
        while (lcpLength < KademliaCommonConfig.ALPHA) {
            candidates.addAll(kBuckets[lcpLength].neighbours.keys)
            lcpLength++
        }
        // remove source id
        candidates.remove(src)

        // create a map (distance, node)
        val distanceMap = TreeMap<BigInteger, BigInteger>()
        for (node in candidates) {
            distanceMap[distance(node, key)] = node
        }
        return distanceMap.values.take(KademliaCommonConfig.K)
    }

    // ______________________________________________________________________________________________
    public override fun clone(): Any {
        return RoutingTable()
    }

    /**
     * returns the neighbour counts
     *
     * @return int
     */
    fun degree(): Int {
        var size = 0
        for (i in kBuckets.indices) size += kBuckets[i].neighbours.size
        return size
    }

    /**
     * returns the neighbour set
     *
     * @return Set<BigInteger>
    </BigInteger> */
    fun neighbourSet(): Set<BigInteger> {
        val result: MutableSet<BigInteger> = HashSet(degree())
        for (i in kBuckets.indices) result.addAll(kBuckets[i].neighbours.keys)
        return result
    }
}
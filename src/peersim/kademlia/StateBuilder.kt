package peersim.kademlia

import peersim.config.Configuration
import peersim.core.CommonState
import peersim.core.Control
import peersim.core.Network
import peersim.core.Node
import peersim.kademlia.Util.put0

/**
 * Initialization class that performs the bootsrap filling the k-buckets of all initial nodes.<br></br>
 * In particular every node is added to the routing table of every other node in the network. In the end however the various nodes
 * doesn't have the same k-buckets because when a k-bucket is full a random node in it is deleted.
 *
 * @author Daniele Furlan, Maurizio Bonani
 * @version 1.0
 */
class StateBuilder(private val prefix: String) : Control {
    private val kademliaId: Int = Configuration.getPid("$prefix.$PAR_PROTOCOL")

    override fun execute(): Boolean {
        // Sort the network by nodeId (Ascending)
        Network.sort { n1: Node, n2: Node ->
            val p1 = n1.getProtocol(kademliaId) as KademliaProtocol
            val p2 = n2.getProtocol(kademliaId) as KademliaProtocol
            put0(p1.nodeId).compareTo(put0(p2.nodeId))
        }
        val sz = Network.size()

        // for every node take 50 random node and add to k-bucket of it
        for (i in 0 until sz) {
            val iNode = Network.get(i)
            val iKad = iNode.getProtocol(kademliaId) as KademliaProtocol
            for (k in 0..49) {
                val jKad = Network.get(CommonState.r.nextInt(sz)).getProtocol(kademliaId) as KademliaProtocol
                iKad.routingTable.addNeighbour(jKad.nodeId)
            }
        }

        // add other 50 near nodes
        for (i in 0 until sz) {
            val iNode = Network.get(i)
            val iKad = iNode.getProtocol(kademliaId) as KademliaProtocol
            var start = i
            if (i > sz - 50) {
                start = sz - 25
            }
            for (k in 0..49) {
                start = start++
                if (start in 1 until sz) {
                    val jKad = Network.get(start++).getProtocol(kademliaId) as KademliaProtocol
                    iKad.routingTable.addNeighbour(jKad.nodeId)
                }
            }
        }
        return false
    }

    companion object {
        private const val PAR_PROTOCOL = "protocol"
    }
}
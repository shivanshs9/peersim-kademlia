package peersim.kademlia;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;
import peersim.edsim.EDSimulator;
import peersim.kademlia.rpc.FindNodeOperation;

import java.math.BigInteger;

/**
 * This control generates random search traffic from nodes to random destination node.
 * 
 * @author Daniele Furlan, Maurizio Bonani
 * @version 1.0
 */

// ______________________________________________________________________________________________
public class TrafficGenerator implements Control {

	// ______________________________________________________________________________________________
	/**
	 * MSPastry Protocol to act
	 */
	private final static String PAR_PROT = "protocol";

	/**
	 * MSPastry Protocol ID to act
	 */
	private final int pid;

	// ______________________________________________________________________________________________
	public TrafficGenerator(String prefix) {
		pid = Configuration.getPid(prefix + "." + PAR_PROT);
	}

	// ______________________________________________________________________________________________

	/**
	 * generates a random find node message, by selecting randomly the destination.
	 *
	 * @return Message
	 */
	private BigInteger generateRandomTarget() {
		// existing active destination node
		Node n = Network.get(CommonState.r.nextInt(Network.size()));
		while (!n.isUp()) {
			n = Network.get(CommonState.r.nextInt(Network.size()));
		}
		return ((KademliaProtocol) (n.getProtocol(pid))).nodeId;
	}

	// ______________________________________________________________________________________________
	/**
	 * every call of this control generates and send a random find node message
	 * 
	 * @return boolean
	 */
	public boolean execute() {
		Node start;
		do {
			start = Network.get(CommonState.r.nextInt(Network.size()));
		} while ((start == null) || (!start.isUp()));

		// send message
		BigInteger targetId = generateRandomTarget();
		BigInteger startId = ((KademliaProtocol) start.getProtocol(pid)).nodeId;
		FindNodeOperation message = new FindNodeOperation(pid, startId, targetId);
		EDSimulator.add(0, message, start, pid);

		return false;
	}

	// ______________________________________________________________________________________________

} // End of class
// ______________________________________________________________________________________________

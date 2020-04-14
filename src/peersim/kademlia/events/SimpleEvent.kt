package peersim.kademlia.events

import peersim.core.CommonState
import peersim.kademlia.events.Event

/**
 * This class defines a simple event. A simple event is characterized only by its type.
 *
 * @author Daniele Furlan, Maurizio Bonani
 * @version 1.0
 */
open class SimpleEvent(@JvmField var type: Int) : Event {
    override var timestamp: Long = CommonState.getTime()
}
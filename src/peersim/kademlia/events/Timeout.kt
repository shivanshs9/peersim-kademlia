package peersim.kademlia.events

import peersim.kademlia.toAscii

/**
 * Created by shivanshs9 on 18/04/20.
 */
open class Timeout(
        val msgId: Long,
        type: Int = TYPE_TIMEOUT
) : SimpleEvent(type) {
    companion object {
        val TYPE_TIMEOUT = "timeout".toAscii()
    }
}
package peersim.kademlia.rpc

import peersim.kademlia.events.Timeout
import java.math.BigInteger

/**
 * Created by shivanshs9 on 18/04/20.
 */
open class RPCTimeout(
        val nodeId: BigInteger,
        msgId: Long,
        type: Int = TYPE_TIMEOUT
) : Timeout(msgId, type)
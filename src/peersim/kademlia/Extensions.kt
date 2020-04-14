package peersim.kademlia

fun String.toAscii(): Int = this.map { it.toInt() }.sum()
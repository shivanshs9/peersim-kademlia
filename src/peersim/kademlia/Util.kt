package peersim.kademlia

import java.math.BigInteger

/**
 * Some utility and mathematical function to work with BigInteger numbers and strings.
 *
 * @author Daniele Furlan, Maurizio Bonani
 * @version 1.0
 */
object Util {
    /**
     * Given two numbers, returns the length of the common prefix, i.e. how many digits (in base 2) have in common from the
     * leftmost side of the number
     *
     * @param b1
     * BigInteger
     * @param b2
     * BigInteger
     * @return int
     */
    @JvmStatic
    fun prefixLen(b1: BigInteger, b2: BigInteger): Int {
        val s1 = put0(b1)
        val s2 = put0(b2)
        var i = 0
        i = 0
        while (i < s1!!.length) {
            if (s1[i] != s2!![i]) return i
            i++
        }
        return i
    }

    /**
     * return the distance between two number wich is defined as (a XOR b)
     *
     * @param a
     * BigInteger
     * @param b
     * BigInteger
     * @return BigInteger
     */
    @JvmStatic
    fun distance(a: BigInteger, b: BigInteger): BigInteger = a.xor(b)

    /**
     * convert a BigInteger into a String (base 2) and lead all needed non-significative zeroes in order to reach the canonical
     * length of a nodeid
     *
     * @param b
     * BigInteger
     * @return String
     */
    @JvmStatic
    fun put0(b: BigInteger): String {
        var s = b.toString(2) // base 2
        while (s.length < KademliaCommonConfig.BITS) {
            s = "0$s"
        }
        return s
    }
}
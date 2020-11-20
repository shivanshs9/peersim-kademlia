package peersim.kademlia

import java.math.BigInteger
import java.security.MessageDigest
import java.util.*

/**
 * Created by shivanshs9 on 18/04/20.
 */
class DHTable : Cloneable {
    private val hashTable: TreeMap<BigInteger, Any> = TreeMap()

    fun getValue(key: BigInteger): Any? = hashTable[key]

    fun store(key: BigInteger, value: Any) {
        hashTable[key] = value
    }

    fun append(key: BigInteger, value: Any) {
        val origLst = hashTable.getOrElse(key) { mutableListOf<Any>() } as MutableList<Any>
        origLst.add(value)
        hashTable[key] = origLst
    }

    override fun clone(): Any = DHTable()

    companion object {
        private val hashFunction = MessageDigest.getInstance("SHA1")

        fun hash(key: String): BigInteger {
            hashFunction.reset() // TODO: is it necessary?
            val digest = hashFunction.digest(key.toByteArray())
            return BigInteger(1, digest)
        }
    }
}
package hu.mobilclient.memo.helpers

import android.util.SparseArray
import androidx.core.util.containsKey
import androidx.core.util.forEach
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random
import androidx.core.util.forEach as forEach1

class IdHolder<T>(private val invalidKeys : ArrayList<Int> = ArrayList()) {

    private val ids = SparseArray<T>()

    fun putId(id: T?){
        var key : Int
        do{
          key = Random.nextInt()
        }while (invalidKeys.contains(key) || ids.containsKey(key))

        ids.put(key, id)
    }

    fun getId(index: Int): T{
        return ids[index]
    }

    operator fun get(index: Int): T{
        return getId(index)
    }

    operator fun contains(key: Int): Boolean{
        return ids.containsKey(key)
    }

    fun clear(){
        ids.clear()
    }

    fun getKey(id: T?): Int{
        return ids.keyAt(ids.indexOfValue(id))
    }

    fun getKeys(): ArrayList<Int>{
        val keys = ArrayList<Int>()
        ids.forEach { key: Int, _: T -> keys.add(key) }
        return keys
    }
}
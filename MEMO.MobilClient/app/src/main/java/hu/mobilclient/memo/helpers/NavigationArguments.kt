package hu.mobilclient.memo.helpers

import android.util.SparseArray
import java.util.*

class NavigationArguments {

    private val arguments = SparseArray<String>()

    private val USERID_KEY = 0

    fun putUserId(userId : UUID){
        arguments.put(USERID_KEY, userId.toString())
    }

    fun getUserId() : UUID {
        return UUID.fromString(arguments.get(USERID_KEY))
    }
}
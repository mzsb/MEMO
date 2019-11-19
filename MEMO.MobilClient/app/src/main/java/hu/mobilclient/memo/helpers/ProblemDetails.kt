package hu.mobilclient.memo.helpers

import com.google.gson.Gson
import hu.mobilclient.memo.App
import hu.mobilclient.memo.R

class ProblemDetails(Json: String?) {

    val title: String

    val status: String

    val detail: String

    init{
        val instance: ProblemDetails? = Gson().fromJson(Json, ProblemDetails::class.java)
            title = instance?.title ?: App.instance.getString(R.string.Unkown)
            status = instance?.status ?: App.instance.getString(R.string.Unkown)
            detail = instance?.detail ?: App.instance.getString(R.string.error_occurred)
    }
}
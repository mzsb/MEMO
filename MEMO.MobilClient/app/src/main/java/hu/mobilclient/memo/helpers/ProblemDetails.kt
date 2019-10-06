package hu.mobilclient.memo.helpers

import com.google.gson.Gson
import hu.mobilclient.memo.App
import hu.mobilclient.memo.R

class ProblemDetails(Json: String?) {

    val title: String

    val status: String

    val detail: String

    init{
        var instance: ProblemDetails? = Gson().fromJson(Json, ProblemDetails::class.java)
            title = instance?.title ?: "Unknown"
            status = instance?.status ?: "Unknown"
            detail = instance?.detail ?: App.instance.getString(R.string.error_occurred)
    }
}
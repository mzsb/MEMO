package hu.mobilclient.memo.model.enums

import com.google.gson.annotations.SerializedName
import hu.mobilclient.memo.App
import hu.mobilclient.memo.R

enum class LanguageCode {
    @SerializedName("0")
    HU,
    @SerializedName("1")
    EN
    ;

    override fun toString() = when(this){
        HU -> App.instance.getString(R.string.hungarian)
        EN -> App.instance.getString(R.string.english)
    }
}
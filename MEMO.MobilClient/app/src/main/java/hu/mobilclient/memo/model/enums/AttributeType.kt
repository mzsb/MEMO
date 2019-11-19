package hu.mobilclient.memo.model.enums

import com.google.gson.annotations.SerializedName
import hu.mobilclient.memo.App
import hu.mobilclient.memo.R

enum class AttributeType {
    @SerializedName("0")
    TEXT,
    @SerializedName("1")
    SPINNER,
    @SerializedName("2")
    CHECKBOX
    ;

    override fun toString(): String{
        return when(this){
            TEXT -> App.instance.getString(R.string.text)
            SPINNER -> App.instance.getString(R.string.list)
            CHECKBOX -> App.instance.getString(R.string.checkbox)
        }
    }
}
package hu.mobilclient.memo.model.Enums

import com.google.gson.annotations.SerializedName

enum class MetaType {
    @SerializedName("0")
    TEXT,
    @SerializedName("1")
    SPINNER,
    @SerializedName("2")
    CHECKBOX
}
package hu.mobilclient.memo.model

import com.google.gson.annotations.SerializedName
import java.util.*
import kotlin.collections.ArrayList

class Language(@SerializedName("id")var Id: UUID? = null,
               @SerializedName("code") var Code: String = "",
               @SerializedName("name") var Name: String = "",
               @SerializedName("dictionaries") var Dictionaries: List<Dictionary> = ArrayList())
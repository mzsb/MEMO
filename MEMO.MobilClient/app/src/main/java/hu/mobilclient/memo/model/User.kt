package hu.mobilclient.memo.model

import com.google.gson.annotations.SerializedName
import java.util.*
import kotlin.collections.ArrayList

class User(@SerializedName("id")var Id: UUID? = null,
           @SerializedName("userName") var UserName: String = "",
           @SerializedName("email") var Email: String = "",
           @SerializedName("role") var Role: String = "",
           @SerializedName("dictionaries") var Dictionaries: List<Dictionary> = ArrayList())
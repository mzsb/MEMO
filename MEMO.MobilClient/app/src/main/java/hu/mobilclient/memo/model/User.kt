package hu.mobilclient.memo.model

import com.google.gson.annotations.SerializedName
import java.util.*

class User(@SerializedName("id")var Id: UUID = UUID.randomUUID(),
           @SerializedName("userName") var UserName: String = "",
           @SerializedName("email") var Email: String = "",
           @SerializedName("role") var Role: String = "")
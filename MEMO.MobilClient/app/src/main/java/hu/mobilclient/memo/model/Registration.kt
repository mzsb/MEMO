package hu.mobilclient.memo.model

import com.google.gson.annotations.SerializedName
import java.util.*

class Registration(@SerializedName("userName")var UserName: String = "",
                   @SerializedName("email")var Email: String = "",
                   @SerializedName("password")var Password: String = "")
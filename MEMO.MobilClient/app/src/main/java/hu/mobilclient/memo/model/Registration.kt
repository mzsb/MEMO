package hu.mobilclient.memo.model

import com.google.gson.annotations.SerializedName
import hu.mobilclient.memo.helpers.Constants
import java.util.*

class Registration(@SerializedName("userName")var UserName: String = Constants.EMPTYSTRING,
                   @SerializedName("email")var Email: String = Constants.EMPTYSTRING,
                   @SerializedName("password")var Password: String = Constants.EMPTYSTRING)
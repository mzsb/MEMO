package hu.mobilclient.memo.model

import com.google.gson.annotations.SerializedName
import hu.mobilclient.memo.helpers.Constants


class Login(@SerializedName("userName")var UserName: String = Constants.EMPTYSTRING,
            @SerializedName("password")var Password: String = Constants.EMPTYSTRING)
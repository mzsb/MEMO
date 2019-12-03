package hu.mobilclient.memo.model.authentication

import com.google.gson.annotations.SerializedName
import hu.mobilclient.memo.helpers.Constants


class Login(@SerializedName("userName")var UserName: String = Constants.EMPTY_STRING,
            @SerializedName("password")var Password: String = Constants.EMPTY_STRING)
package hu.mobilclient.memo.model

import com.google.gson.annotations.SerializedName


class Login(@SerializedName("userName")var UserName: String = "",
            @SerializedName("password")var Password: String = "")
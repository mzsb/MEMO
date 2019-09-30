package hu.mobilclient.memo.model

import com.google.gson.annotations.SerializedName
import java.util.*

class TokenHolder(@SerializedName("userId")var UserId: UUID,
                  @SerializedName("token")var Token: String)
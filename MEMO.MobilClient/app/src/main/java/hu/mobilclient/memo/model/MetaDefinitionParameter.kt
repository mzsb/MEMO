package hu.mobilclient.memo.model

import com.google.gson.annotations.SerializedName
import java.util.*

class MetaDefinitionParameter(@SerializedName("id")var Id: UUID? = null,
                              @SerializedName("value") var Value: String = "")
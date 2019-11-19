package hu.mobilclient.memo.model

import com.google.gson.annotations.SerializedName
import hu.mobilclient.memo.helpers.Constants
import java.util.*

class AttributeParameter(@SerializedName("id")var Id: UUID? = null,
                         @SerializedName("value") var Value: String = Constants.EMPTYSTRING)
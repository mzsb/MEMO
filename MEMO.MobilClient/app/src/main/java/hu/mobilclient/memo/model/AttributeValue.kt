package hu.mobilclient.memo.model

import com.google.gson.annotations.SerializedName
import hu.mobilclient.memo.helpers.Constants
import java.util.*

class AttributeValue(@SerializedName("id")var Id: UUID? = null,
                     @SerializedName("value") var Value: String = Constants.EMPTYSTRING,
                     @SerializedName("attributeId") var AttributeId: UUID ?= null,
                     @SerializedName("attribute") var Attribute: Attribute ?= null)
package hu.mobilclient.memo.model.memoapi

import com.google.gson.annotations.SerializedName
import hu.mobilclient.memo.helpers.Constants
import hu.mobilclient.memo.model.enums.AttributeType
import java.util.*

class AttributeValue(@SerializedName("id")var Id: UUID = UUID(0,0),
                     @SerializedName("value") var Value: String = Constants.EMPTY_STRING,
                     @SerializedName("attributeId") var AttributeId: UUID ?= null,
                     @SerializedName("attribute") var Attribute: Attribute?= null){

    val IsChecked: Boolean
        get() {
            return Attribute?.Type == AttributeType.CHECKBOX && Value == true.toString()
        }
}
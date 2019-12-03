package hu.mobilclient.memo.model.memoapi

import com.google.gson.annotations.SerializedName
import hu.mobilclient.memo.helpers.Constants
import hu.mobilclient.memo.model.enums.AttributeType
import java.util.*

class Attribute(@SerializedName("id")var Id: UUID = UUID(0,0),
                @SerializedName("user")var User: User = User(),
                @SerializedName("name") var Name: String = Constants.EMPTY_STRING,
                @SerializedName("type") var Type: AttributeType = AttributeType.TEXT,
                @SerializedName("attributeValuesCount")var AttributeValuesCount: Int = Constants.ZERO,
                @SerializedName("attributeParameters") var AttributeParameters: MutableList<AttributeParameter> = ArrayList()){
    override fun toString(): String = Name
}
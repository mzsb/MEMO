package hu.mobilclient.memo.model.memoapi

import com.google.gson.annotations.SerializedName
import hu.mobilclient.memo.helpers.Constants
import java.util.*

class AttributeParameter(@SerializedName("id")var Id: UUID = UUID(0,0),
                         @SerializedName("value") var Value: String = Constants.EMPTY_STRING){

    override fun toString(): String = Value
}
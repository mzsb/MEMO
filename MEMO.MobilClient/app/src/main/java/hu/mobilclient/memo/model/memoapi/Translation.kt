package hu.mobilclient.memo.model.memoapi

import android.graphics.Color
import androidx.core.content.ContextCompat
import com.google.gson.annotations.SerializedName
import hu.mobilclient.memo.App
import hu.mobilclient.memo.R
import hu.mobilclient.memo.helpers.Constants
import hu.mobilclient.memo.model.memoapi.interfaces.IAuditable
import java.util.*

class Translation(@SerializedName("id")var Id: UUID = UUID(0,0),
                  @SerializedName("original")var Original: String = Constants.EMPTY_STRING,
                  @SerializedName("translated")var Translated: String = Constants.EMPTY_STRING,
                  @SerializedName("dictionaryId") var DictionaryId: UUID? = null,
                  @SerializedName("attributeValues") var AttributeValues: MutableList<AttributeValue> = ArrayList(),
                  @SerializedName("attributeValueCount")var AttributeValueCount: Int = Constants.ZERO,
                  @SerializedName("color")var Color: Int = ContextCompat.getColor(App.instance, R.color.translation_default),
                  @SerializedName("creationDate") override var CreationDate: String = Constants.EMPTY_STRING) : IAuditable
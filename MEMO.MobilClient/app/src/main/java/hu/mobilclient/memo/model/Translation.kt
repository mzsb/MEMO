package hu.mobilclient.memo.model

import com.google.gson.annotations.SerializedName
import hu.mobilclient.memo.helpers.Constants
import hu.mobilclient.memo.model.interfaces.IAuditable
import java.util.*

class Translation(@SerializedName("id")var Id: UUID? = null,
                  @SerializedName("original")var Original: String = Constants.EMPTYSTRING,
                  @SerializedName("translated")var Translated: String = Constants.EMPTYSTRING,
                  @SerializedName("dictionaryId") var DictionaryId: UUID? = null,
                  @SerializedName("attributeValues") var AttributeValues: MutableList<AttributeValue> = ArrayList(),
                  @Transient override var CreationDate: String = Constants.EMPTYSTRING) : IAuditable
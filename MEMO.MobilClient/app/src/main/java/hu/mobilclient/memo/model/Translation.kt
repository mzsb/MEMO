package hu.mobilclient.memo.model

import com.google.gson.annotations.SerializedName
import java.util.*

class Translation(@SerializedName("id")var Id: UUID? = null,
                  @SerializedName("dictionaryId") var DictionaryId: UUID? = null,
                  @SerializedName("translationMetas") var TranslationMetas: MutableList<TranslationMeta> = ArrayList())
package hu.mobilclient.memo.model

import com.google.gson.annotations.SerializedName
import java.util.*

class TranslationMeta(@SerializedName("id")var Id: UUID? = null,
                      @SerializedName("value") var Value: String = "",
                      @SerializedName("metaDefinitionId") var MetaDefinitionId: UUID ?= null,
                      @SerializedName("metaDefinition") var MetaDefinition: MetaDefinition ?= null)
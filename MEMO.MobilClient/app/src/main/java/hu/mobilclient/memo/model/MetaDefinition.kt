package hu.mobilclient.memo.model

import com.google.gson.annotations.SerializedName
import hu.mobilclient.memo.model.Enums.MetaType
import java.util.*

class MetaDefinition(@SerializedName("id")var Id: UUID? = null,
                     @SerializedName("name") var Name: String = "",
                     @SerializedName("type") var Type: MetaType = MetaType.TEXT,
                     @SerializedName("metaDefinitionParameters") var MetaDefinitionParameters: MutableList<MetaDefinitionParameter> = ArrayList())
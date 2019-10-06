package hu.mobilclient.memo.model

import com.google.gson.annotations.SerializedName
import java.util.*

class Dictionary(@SerializedName("id")var Id: UUID? = null,
                 @SerializedName("name") var Name: String = "",
                 @SerializedName("isPublic") var IsPublic: Boolean = false,
                 @SerializedName("owner") var Owner: User = User(),
                 @SerializedName("source") var Source: Language = Language(),
                 @SerializedName("destination") var Destination: Language = Language(),
                 @SerializedName("translations") var Translation: MutableList<Translation> = ArrayList(),
                 @SerializedName("metaDefinitions") var MetaDefinitions: MutableList<MetaDefinition> = ArrayList(),
                 @SerializedName("viewers") var Viewers: MutableList<User> = ArrayList())
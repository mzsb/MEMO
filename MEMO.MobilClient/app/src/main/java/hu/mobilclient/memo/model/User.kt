package hu.mobilclient.memo.model

import com.google.gson.annotations.SerializedName
import hu.mobilclient.memo.helpers.Constants
import hu.mobilclient.memo.model.interfaces.IAuditable
import java.util.*
import kotlin.collections.ArrayList

class User(@SerializedName("id")var Id: UUID? = null,
           @SerializedName("userName") var UserName: String = Constants.EMPTYSTRING,
           @SerializedName("email") var Email: String = Constants.EMPTYSTRING,
           @SerializedName("role") var Role: String = Constants.EMPTYSTRING,
           @SerializedName("dictionaryCount")var DictionaryCount: String = Constants.EMPTYSTRING,
           @SerializedName("viewedDictionaryCount")var ViewedDictionaryCount: String = Constants.EMPTYSTRING,
           @SerializedName("translationCount")var TranslationCount: String = Constants.EMPTYSTRING,
           @SerializedName("creationDate") override var CreationDate: String = Constants.EMPTYSTRING) : IAuditable
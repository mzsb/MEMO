package hu.mobilclient.memo.model.memoapi

import com.google.gson.annotations.SerializedName
import hu.mobilclient.memo.helpers.Constants
import hu.mobilclient.memo.model.memoapi.interfaces.IAuditable
import java.util.*

class User(@SerializedName("id")var Id: UUID = UUID(0,0),
           @SerializedName("userName") var UserName: String = Constants.EMPTY_STRING,
           @SerializedName("email") var Email: String = Constants.EMPTY_STRING,
           @SerializedName("role") var Role: String = Constants.EMPTY_STRING,
           @SerializedName("dictionaryCount")var DictionaryCount: String = Constants.EMPTY_STRING,
           @SerializedName("viewedDictionaryCount")var ViewedDictionaryCount: String = Constants.EMPTY_STRING,
           @SerializedName("translationCount")var TranslationCount: String = Constants.EMPTY_STRING,
           @SerializedName("creationDate") override var CreationDate: String = Constants.EMPTY_STRING) : IAuditable
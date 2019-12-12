package hu.mobilclient.memo.model.memoapi

import android.graphics.drawable.Drawable
import com.google.gson.annotations.SerializedName
import hu.mobilclient.memo.App
import hu.mobilclient.memo.R
import hu.mobilclient.memo.helpers.Constants
import hu.mobilclient.memo.helpers.DateParser
import hu.mobilclient.memo.model.enums.LanguageCode
import hu.mobilclient.memo.model.memoapi.interfaces.IAuditable
import java.util.*

class Dictionary(@SerializedName("id")var Id: UUID = UUID(0,0),
                 @SerializedName("name") var Name: String = Constants.EMPTY_STRING,
                 @SerializedName("description") var Description: String = Constants.EMPTY_STRING,
                 @SerializedName("isPublic") var IsPublic: Boolean = false,
                 @SerializedName("isFastAccessible") var IsFastAccessible: Boolean = false,
                 @SerializedName("owner") var Owner: User = User(),
                 @SerializedName("source") var Source: Language = Language(),
                 @SerializedName("destination") var Destination: Language = Language(),
                 @SerializedName("translations") var Translations: MutableList<Translation> = ArrayList(),
                 @SerializedName("viewers") var Viewers: MutableList<User> = ArrayList(),
                 @SerializedName("translationCount")var TranslationCount: Int = Constants.ZERO,
                 @SerializedName("viewerCount")var ViewerCount: Int = Constants.ZERO,
                 @SerializedName("creationDate") override var CreationDate: String = Constants.EMPTY_STRING) : IAuditable {

    fun parsedCreationDate() = DateParser.parse(CreationDate)

    fun isOwn(): Boolean{
        val userId = Owner.Id
        return App.isCurrent(userId)
    }

    val icon: Drawable
        get(){
            return when(Source.Code){
                LanguageCode.EN -> when(Destination.Code){
                    LanguageCode.HU -> App.instance.applicationContext.resources.getDrawable(R.drawable.ic_en_hu_flag, null)
                    LanguageCode.EN -> App.instance.applicationContext.resources.getDrawable(R.drawable.ic_en_flag, null)
                }
                LanguageCode.HU -> when(Destination.Code){
                    LanguageCode.HU -> App.instance.applicationContext.resources.getDrawable(R.drawable.ic_hu_flag, null)
                    LanguageCode.EN -> App.instance.applicationContext.resources.getDrawable(R.drawable.ic_hu_en_flag, null)
                }
            }
        }

    override fun toString() = Name
}
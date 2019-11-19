package hu.mobilclient.memo.model

import android.graphics.drawable.Drawable
import com.google.gson.annotations.SerializedName
import hu.mobilclient.memo.App
import hu.mobilclient.memo.R
import hu.mobilclient.memo.helpers.Constants
import hu.mobilclient.memo.helpers.DateParser
import hu.mobilclient.memo.model.enums.LanguageCode
import hu.mobilclient.memo.model.interfaces.IAuditable
import java.lang.Exception
import java.util.*

class Dictionary(@SerializedName("id")var Id: UUID? = null,
                 @SerializedName("name") var Name: String = Constants.EMPTYSTRING,
                 @SerializedName("description") var Description: String = Constants.EMPTYSTRING,
                 @SerializedName("isPublic") var IsPublic: Boolean = false,
                 @SerializedName("isFastAccessible") var IsFastAccessible: Boolean = false,
                 @SerializedName("owner") var Owner: User = User(),
                 @SerializedName("source") var Source: Language = Language(),
                 @SerializedName("destination") var Destination: Language = Language(),
                 @SerializedName("translations") var Translations: MutableList<Translation> = ArrayList(),
                 @SerializedName("viewers") var Viewers: MutableList<User> = ArrayList(),
                 @SerializedName("translationCount")var TranslationCount: Int = Constants.ZERO,
                 @SerializedName("viewerCount")var ViewerCount: Int = Constants.ZERO,
                 @SerializedName("creationDate") override var CreationDate: String = Constants.EMPTYSTRING) : IAuditable {

    fun parsedCreationDate() = DateParser.parse(CreationDate)

    fun isOwn(): Boolean{
        val userId = Owner.Id
        return if(userId != null) {
            App.isCurrent(userId)
        }
        else{
            false
        }
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
}
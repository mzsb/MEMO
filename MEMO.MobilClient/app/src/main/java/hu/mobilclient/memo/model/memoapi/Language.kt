package hu.mobilclient.memo.model.memoapi

import android.graphics.drawable.Drawable
import com.google.gson.annotations.SerializedName
import hu.mobilclient.memo.App
import hu.mobilclient.memo.R
import hu.mobilclient.memo.model.enums.LanguageCode
import java.util.*
import kotlin.collections.ArrayList

class Language(@SerializedName("id")var Id: UUID = UUID(0,0),
               @SerializedName("languageCode") var Code: LanguageCode = LanguageCode.HU,
               @SerializedName("ownDictionaries") var Dictionaries: List<Dictionary> = ArrayList()){

    val icon: Drawable
        get(){
            return when(Code){
                LanguageCode.EN -> App.instance.applicationContext.resources.getDrawable(R.drawable.ic_en_flag, null)
                LanguageCode.HU -> App.instance.applicationContext.resources.getDrawable(R.drawable.ic_hu_flag, null)
            }
        }

    override fun toString() = Code.toString()
}
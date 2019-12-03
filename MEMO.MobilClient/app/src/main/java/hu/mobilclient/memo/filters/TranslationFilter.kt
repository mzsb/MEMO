package hu.mobilclient.memo.filters

import android.annotation.SuppressLint
import android.text.Editable
import android.widget.CompoundButton
import android.widget.RadioGroup
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.google.gson.Gson
import hu.mobilclient.memo.App
import hu.mobilclient.memo.R
import hu.mobilclient.memo.helpers.Constants
import hu.mobilclient.memo.helpers.OnceRunTextWatcher
import hu.mobilclient.memo.model.memoapi.Translation
import java.text.SimpleDateFormat


class TranslationFilter(var Original: ObservableField<String> = ObservableField(Constants.EMPTY_STRING),
                        var Translated: ObservableField<String> = ObservableField(Constants.EMPTY_STRING),
                        var TotalTranslationCount: ObservableField<String> = ObservableField(Constants.EMPTY_STRING),
                        var TotalAttributeValueCount: ObservableField<String> = ObservableField(Constants.EMPTY_STRING),
                        var SortByOriginal: ObservableBoolean = ObservableBoolean(true),
                        var SortByTranslated: ObservableBoolean = ObservableBoolean(false),
                        var SortByCreationDate: ObservableBoolean = ObservableBoolean(true),
                        var IsDescending: ObservableBoolean = ObservableBoolean(true)){

    @Transient
    var useFilter: () -> Unit = { throw NotImplementedError() }

    @Transient
    val textWatcher = FilterTextWatcher()

    companion object{
        fun loadFilter() : TranslationFilter{
            return Gson().fromJson<TranslationFilter>(App.instance.getSharedPreferences(Constants.FILTER, 0)
                         .getString(Constants.TRANSLATION_FILTER, null), TranslationFilter::class.java) ?: TranslationFilter()
        }

        fun clearFilter(){
            App.instance.getSharedPreferences(Constants.FILTER, 0).edit()
                        .putString(Constants.TRANSLATION_FILTER, "")
                        .apply()
        }
    }

    fun resetSearch(){
        Original.set(Constants.EMPTY_STRING)
        Translated.set(Constants.EMPTY_STRING)
    }

    fun resetFilter(){
        SortByOriginal.set(false)
        SortByCreationDate.set(true)
        SortByTranslated.set(false)
        IsDescending.set(true)
    }

    private fun saveFilter(){
        App.instance.getSharedPreferences(Constants.FILTER, 0).edit()
                    .putString(Constants.TRANSLATION_FILTER, Gson().toJson(this))
                    .apply()
    }

    fun onIsDescendingChanged(buttonView: CompoundButton, isChecked: Boolean) {
        IsDescending.set(isChecked)
        useFilter()
    }

    fun onSortChanged(group: RadioGroup, checkedId: Int) {
        useFilter()
    }

    inner class FilterTextWatcher : OnceRunTextWatcher() {
        override val onceAfterTextChanged: (p0: Editable?) -> Unit = {
            useFilter()
        }
    }

    @SuppressLint("DefaultLocale")
    fun filter(allTranslation : List<Translation>): List<Translation> {
        var filteredTranslations = allTranslation

        if(!Original.get().isNullOrEmpty()) {
            filteredTranslations = filteredTranslations.filter { it.Original.toLowerCase().contains(Original.get()!!.toLowerCase())}
        }

        if(!Translated.get().isNullOrEmpty()) {
            filteredTranslations = filteredTranslations.filter { it.Translated.toLowerCase().contains(Translated.get()!!.toLowerCase())}
        }

        saveFilter()

        return sortAndPrivate(filteredTranslations)
    }

    @SuppressLint("SimpleDateFormat")
    private fun sortAndPrivate(translations: List<Translation>): List<Translation>{
        var sortedTranslations = translations

        val dateFormat = SimpleDateFormat(App.instance.getString(R.string.date_format))

        if(SortByCreationDate.get()) {
            sortedTranslations = if(IsDescending.get()) sortedTranslations.sortedByDescending { dateFormat.parse(it.CreationDate) }
                                                    else sortedTranslations.sortedBy { dateFormat.parse(it.CreationDate) }
        }
        if(SortByOriginal.get()) {
            sortedTranslations = if(IsDescending.get()) sortedTranslations.sortedByDescending { it.Original }
                                                    else sortedTranslations.sortedBy { it.Original }
        }
        if(SortByTranslated.get()) {
            sortedTranslations = if(IsDescending.get()) sortedTranslations.sortedByDescending { it.Translated }
                                                    else sortedTranslations.sortedBy { it.Translated }
        }

        return sortedTranslations
    }

    fun setTotalTranslationCount(translations : List<Translation>){
        TotalTranslationCount.set((if(translations.count() > 1) translations.count() - 2  else 0).toString())
    }

    fun setTotalAttributeValueCount(translations : List<Translation>){
        var totalAttributeValuesCount = 0

        for(translation in translations){
            totalAttributeValuesCount += translation.AttributeValueCount
        }

        TotalAttributeValueCount.set(totalAttributeValuesCount.toString())
    }


}
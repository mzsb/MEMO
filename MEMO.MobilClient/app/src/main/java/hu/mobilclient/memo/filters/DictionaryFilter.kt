package hu.mobilclient.memo.filters

import android.annotation.SuppressLint
import android.view.View
import android.widget.AdapterView
import android.widget.CompoundButton
import android.widget.RadioGroup
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.google.gson.Gson
import hu.mobilclient.memo.App
import hu.mobilclient.memo.R
import hu.mobilclient.memo.helpers.Constants
import hu.mobilclient.memo.helpers.OnceRunTextWatcher
import hu.mobilclient.memo.model.Dictionary
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class DictionaryFilter(var DictionaryName: ObservableField<String> = ObservableField(Constants.EMPTYSTRING),
                       var UserName: ObservableField<String> = ObservableField(Constants.EMPTYSTRING),
                       var MinTranslationCount: ObservableField<String> = ObservableField(Constants.EMPTYSTRING),
                       var MaxTranslationCount: ObservableField<String> = ObservableField(Constants.EMPTYSTRING),
                       var MinViewerCount: ObservableField<String> = ObservableField(Constants.EMPTYSTRING),
                       var MaxViewerCount: ObservableField<String> = ObservableField(Constants.EMPTYSTRING),
                       var SourceLanguage: String = App.instance.getString(R.string.source),
                       var DestinationLanguage: String = App.instance.getString(R.string.destination),
                       var TotalDictionaryCount: ObservableField<String> = ObservableField(Constants.EMPTYSTRING),
                       var TotalTranslationCount: ObservableField<String> = ObservableField(Constants.EMPTYSTRING),
                       var OnlyOwn: ObservableBoolean = ObservableBoolean(true),
                       var All: ObservableBoolean = ObservableBoolean(false),
                       var OnlyPrivate: ObservableBoolean = ObservableBoolean(false),
                       var SortByDictionaryName: ObservableBoolean = ObservableBoolean(true),
                       var SortByTranslation: ObservableBoolean = ObservableBoolean(false),
                       var SortByDate: ObservableBoolean = ObservableBoolean(true),
                       var SortByUserName: ObservableBoolean = ObservableBoolean(false),
                       var SortByViewer: ObservableBoolean = ObservableBoolean(false),
                       var IsDescending: ObservableBoolean = ObservableBoolean(true)){

    @Transient
    var useFilter: () -> Unit = { throw NotImplementedError() }
    @Transient
    val sourceLanguages = ArrayList<String>()
    @Transient
    val destinationLanguages = ArrayList<String>()
    @Transient
    val sourceLanguageOnItemSelectedListener =  SourceLanguageOnItemSelectedListener()
    @Transient
    val destinationLanguageOnItemSelectedListener = DestinationLanguageOnItemSelectedListener()
    @Transient
    val textWatcher = FilterTextWatcher()

    val IsAdmin: Boolean
        get(){
            return App.isAdmin()
        }

    companion object{
        fun loadFilter() : DictionaryFilter{
            return Gson().fromJson<DictionaryFilter>(App.instance.getSharedPreferences(Constants.FILTER, 0)
                         .getString(Constants.DICTIONARYFILTER, null), DictionaryFilter::class.java) ?: DictionaryFilter()
        }

        fun clearFilter(){
            App.instance.getSharedPreferences(Constants.FILTER, 0).edit()
                        .putString(Constants.DICTIONARYFILTER, "")
                        .apply()
        }
    }

    fun resetSearch(){
        DictionaryName.set(Constants.EMPTYSTRING)
        UserName.set(Constants.EMPTYSTRING)
    }

    fun resetFilter(){
        MinTranslationCount.set(Constants.EMPTYSTRING)
        MaxTranslationCount.set(Constants.EMPTYSTRING)
        MinViewerCount.set(Constants.EMPTYSTRING)
        MaxViewerCount.set(Constants.EMPTYSTRING)
        SourceLanguage = App.instance.getString(R.string.source)
        DestinationLanguage = App.instance.getString(R.string.destination)
        OnlyOwn.set(true)
        OnlyPrivate.set(false)
        All.set(false)
        SortByDictionaryName.set(false)
        SortByDate.set(true)
        SortByTranslation.set(false)
        SortByUserName.set(false)
        SortByViewer.set(false)
        IsDescending.set(true)
    }

    private fun saveFilter(){
        App.instance.getSharedPreferences(Constants.FILTER, 0).edit()
                    .putString(Constants.DICTIONARYFILTER, Gson().toJson(this))
                    .apply()
    }

    fun onIsDescendingChanged(buttonView: CompoundButton, isChecked: Boolean) {
        IsDescending.set(isChecked)
        useFilter()
    }

    fun onOnlyOwnChanged(buttonView: CompoundButton, isChecked: Boolean) {
        OnlyOwn.set(isChecked)

        useFilter()
    }

    fun onAllChanged(buttonView: CompoundButton, isChecked: Boolean) {
        All.set(isChecked)

        useFilter()
    }

    fun onSortChanged(group: RadioGroup, checkedId: Int) {
        useFilter()
    }

    fun onOnlyPrivateChanged(buttonView: CompoundButton, isChecked: Boolean) {
        OnlyPrivate.set(isChecked)

        useFilter()
    }

    inner class SourceLanguageOnItemSelectedListener : AdapterView.OnItemSelectedListener {
        var init = false
        override fun onItemSelected(arg0: AdapterView<*>, arg1: View, position: Int, id: Long) {
            if(!init){
                SourceLanguage = sourceLanguages[position]
                useFilter()
            }
            else{
                init = false
            }
        }

        override fun onNothingSelected(arg0: AdapterView<*>) { }
    }

    inner class DestinationLanguageOnItemSelectedListener : AdapterView.OnItemSelectedListener {
        var init = false
        override fun onItemSelected(arg0: AdapterView<*>, arg1: View, position: Int, id: Long) {
            if(!init){
                DestinationLanguage = destinationLanguages[position]
                useFilter()
            }
            else{
                init = false
            }
        }

        override fun onNothingSelected(arg0: AdapterView<*>) { }
    }

    inner class FilterTextWatcher : OnceRunTextWatcher() {
        override val onceAfterTextChanged: () -> Unit = {
            useFilter()
        }
    }

    @SuppressLint("DefaultLocale")
    fun filter(allDictionaries : List<Dictionary>): List<Dictionary> {
        var filteredDictionaries = allDictionaries

        if(!DictionaryName.get().isNullOrEmpty()) {
            filteredDictionaries = filteredDictionaries.filter { it.Name.toLowerCase().contains(DictionaryName.get()!!.toLowerCase())}
        }

        if(!UserName.get().isNullOrEmpty()){
            filteredDictionaries = filteredDictionaries.filter { it.Owner.UserName.toLowerCase().contains(UserName.get()!!.toLowerCase())}
        }

        if(!MinTranslationCount.get().isNullOrEmpty() && MinTranslationCount.get()!!.matches(Regex("[0-9]+"))) {
            filteredDictionaries = filteredDictionaries.filter { it.TranslationCount >= MinTranslationCount.get()!!.toInt() }
        }

        if(!MaxTranslationCount.get().isNullOrEmpty() && MaxTranslationCount.get()!!matches(Regex("[0-9]+"))){
            filteredDictionaries = filteredDictionaries.filter { it.TranslationCount <= MaxTranslationCount.get()!!.toInt() }
        }

        if(!MinViewerCount.get().isNullOrEmpty() && MinViewerCount.get()!!matches(Regex("[0-9]+"))) {
            filteredDictionaries = filteredDictionaries.filter { it.ViewerCount >= MinViewerCount.get()!!.toInt() }
        }

        if(!MaxViewerCount.get().isNullOrEmpty() && MaxViewerCount.get()!!matches(Regex("[0-9]+"))){
            filteredDictionaries = filteredDictionaries.filter { it.ViewerCount <= MaxViewerCount.get()!!.toInt() }
        }

        if(SourceLanguage != App.instance.getString(R.string.source)){
            filteredDictionaries = filteredDictionaries.filter { it.Source.toString() == SourceLanguage }
        }

        if(DestinationLanguage != App.instance.getString(R.string.destination)){
            filteredDictionaries = filteredDictionaries.filter { it.Destination.toString() == DestinationLanguage }
        }

        saveFilter()

        return sortAndPrivate(filteredDictionaries)
    }

    @SuppressLint("SimpleDateFormat")
    private fun sortAndPrivate(dictionaries: List<Dictionary>): List<Dictionary>{
        var sortedDictionaries = dictionaries

        val dateFormat = SimpleDateFormat(App.instance.getString(R.string.date_format))

        if(OnlyPrivate.get()) {
            sortedDictionaries = sortedDictionaries.filter { !it.IsPublic }
        }

        if(SortByDate.get()) {
            sortedDictionaries = if(IsDescending.get()) sortedDictionaries.sortedByDescending { dateFormat.parse(it.CreationDate) }
                                                    else sortedDictionaries.sortedBy { dateFormat.parse(it.CreationDate) }
        }
        if(SortByDictionaryName.get()) {
            sortedDictionaries = if(IsDescending.get()) sortedDictionaries.sortedByDescending { it.Name }
                                                    else sortedDictionaries.sortedBy { it.Name }
        }
        if(SortByTranslation.get()) {
            sortedDictionaries = if(IsDescending.get()) sortedDictionaries.sortedByDescending { it.TranslationCount }
                                                    else sortedDictionaries.sortedBy { it.TranslationCount }
        }
        if(SortByUserName.get()) {
            sortedDictionaries = if(IsDescending.get()) sortedDictionaries.sortedByDescending { it.Owner.UserName }
                                                    else sortedDictionaries.sortedBy { it.Owner.UserName }
        }
        if(SortByViewer.get()) {
            sortedDictionaries = if(IsDescending.get()) sortedDictionaries.sortedByDescending { it.ViewerCount }
                                                    else sortedDictionaries.sortedBy { it.ViewerCount }
        }
        return sortedDictionaries
    }

    fun setTotalDictionaryCount(dictionaries : List<Dictionary>){
        TotalDictionaryCount.set((dictionaries.count() - 1).toString())
    }

    fun setTotalTranslationCount(dictionaries : List<Dictionary>){
        var totalTranslationCount = 0

        for(dictionary in dictionaries){
            totalTranslationCount += dictionary.TranslationCount
        }

        TotalTranslationCount.set(totalTranslationCount.toString())
    }

}
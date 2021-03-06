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
import hu.mobilclient.memo.model.memoapi.User
import java.text.SimpleDateFormat
import java.util.*

class UserFilter(var UserName: ObservableField<String> = ObservableField(Constants.EMPTY_STRING),
                 var MinDictionaryCount: ObservableField<String> = ObservableField(Constants.EMPTY_STRING),
                 var MaxDictionaryCount: ObservableField<String> = ObservableField(Constants.EMPTY_STRING),
                 var MinTranslationCount: ObservableField<String> = ObservableField(Constants.EMPTY_STRING),
                 var MaxTranslationCount: ObservableField<String> = ObservableField(Constants.EMPTY_STRING),
                 var TotalUserCount: ObservableField<String> = ObservableField(Constants.EMPTY_STRING),
                 var TotalDictionaryCount: ObservableField<String> = ObservableField(Constants.EMPTY_STRING),
                 var All: ObservableBoolean = ObservableBoolean(false),
                 var SortByUserName: ObservableBoolean = ObservableBoolean(true),
                 var SortByTranslation: ObservableBoolean = ObservableBoolean(false),
                 var SortByDictionary: ObservableBoolean = ObservableBoolean(false),
                 var SortByCreationDate: ObservableBoolean = ObservableBoolean(false),
                 var IsDescending: ObservableBoolean = ObservableBoolean(true)){

    @Transient
    var useFilter: () -> Unit = { throw NotImplementedError() }
    @Transient
    val textWatcher = FilterTextWatcher()

    val IsAdmin: Boolean
        get(){
            return App.isAdmin()
        }

    companion object{
        fun loadFilter() : UserFilter {
            return Gson().fromJson<UserFilter>(App.instance.getSharedPreferences(Constants.FILTER, 0)
                    .getString(Constants.USER_FILTER, null), UserFilter::class.java) ?: UserFilter()
        }

        fun clearFilter(){
            App.instance.getSharedPreferences(Constants.FILTER, 0).edit()
                    .putString(Constants.USER_FILTER, "")
                    .apply()
        }
    }

    fun resetSearch(){
        UserName.set(Constants.EMPTY_STRING)
    }

    fun resetFilter(){
        MinDictionaryCount.set(Constants.EMPTY_STRING)
        MaxDictionaryCount.set(Constants.EMPTY_STRING)
        All.set(false)
        SortByUserName.set(true)
        SortByTranslation.set(false)
        SortByDictionary.set(false)
        SortByCreationDate.set(false)
        IsDescending.set(false)
    }

    private fun saveFilter(){
        App.instance.getSharedPreferences(Constants.FILTER, 0).edit()
                .putString(Constants.USER_FILTER, Gson().toJson(this))
                .apply()
    }

    fun onIsDescendingChanged(buttonView: CompoundButton, isChecked: Boolean) {
        IsDescending.set(isChecked)
        useFilter()
    }

    fun onAllChanged(buttonView: CompoundButton, isChecked: Boolean) {
        All.set(isChecked)

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
    fun filter(allUsers : List<User>): List<User> {
        var filteredUsers = allUsers

        if(!UserName.get().isNullOrEmpty()) {
            filteredUsers = filteredUsers.filter { it.UserName.toLowerCase().contains(UserName.get()!!.toLowerCase())}
        }

        if(IsAdmin && !MinDictionaryCount.get().isNullOrEmpty() && MinDictionaryCount.get()!!.matches(Regex("[0-9]+"))) {
            filteredUsers = filteredUsers.filter { (if(it.DictionaryCount.isNotEmpty()) it.DictionaryCount.toInt() else 0) >= MinDictionaryCount.get()!!.toInt() }
        }

        if(IsAdmin && !MaxDictionaryCount.get().isNullOrEmpty() && MaxDictionaryCount.get()!!matches(Regex("[0-9]+"))){
            filteredUsers = filteredUsers.filter { (if(it.DictionaryCount.isNotEmpty()) it.DictionaryCount.toInt() else 0) <= MaxDictionaryCount.get()!!.toInt() }
        }

        if(IsAdmin && !MinTranslationCount.get().isNullOrEmpty() && MinTranslationCount.get()!!.matches(Regex("[0-9]+"))) {
            filteredUsers = filteredUsers.filter { (if(it.TranslationCount.isNotEmpty()) it.TranslationCount.toInt() else 0) >= MinTranslationCount.get()!!.toInt() }
        }

        if(IsAdmin && !MaxTranslationCount.get().isNullOrEmpty() && MaxTranslationCount.get()!!matches(Regex("[0-9]+"))){
            filteredUsers = filteredUsers.filter { (if(it.TranslationCount.isNotEmpty()) it.TranslationCount.toInt() else 0) <= MaxTranslationCount.get()!!.toInt() }
        }

        saveFilter()

        return sortAndPrivate(filteredUsers)
    }

    @SuppressLint("SimpleDateFormat")
    private fun sortAndPrivate(Users: List<User>): List<User>{
        var sortedUsers = Users

        if(SortByUserName.get()) {
            sortedUsers = if(IsDescending.get()) sortedUsers.sortedByDescending { it.UserName }
            else sortedUsers.sortedBy { it.UserName }
        }
        if(SortByTranslation.get()) {
            sortedUsers = if(IsDescending.get()) sortedUsers.sortedByDescending { it.TranslationCount }
            else sortedUsers.sortedBy { it.TranslationCount }
        }
        if(SortByDictionary.get()) {
            sortedUsers = if(IsDescending.get()) sortedUsers.sortedByDescending { it.DictionaryCount }
            else sortedUsers.sortedBy { it.DictionaryCount }
        }
        if(SortByCreationDate.get()) {
            sortedUsers = if(IsDescending.get()) sortedUsers.sortedByDescending { it.CreationDate }
            else sortedUsers.sortedBy { it.CreationDate }
        }
        return sortedUsers
    }

    fun setTotalUserCount(users: List<User>){
        TotalUserCount.set(users.filter { it.Id != UUID(0,0) }.count().toString())
    }

    fun setTotalDictionaryCount(users: List<User>){
        var totalDictionaryCount = 0

        for(user in users){
            totalDictionaryCount += (if(user.DictionaryCount.isNotEmpty()) user.DictionaryCount.toInt() else 0)
        }

        TotalDictionaryCount.set(totalDictionaryCount.toString())
    }

}
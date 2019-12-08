package hu.mobilclient.memo.filters

import android.annotation.SuppressLint
import android.text.Editable
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
import hu.mobilclient.memo.model.memoapi.Attribute
import hu.mobilclient.memo.model.enums.AttributeType
import java.util.*
import kotlin.collections.ArrayList

class AttributeFilter(var AttributeName: ObservableField<String> = ObservableField(Constants.EMPTY_STRING),
                      var UserName: ObservableField<String> = ObservableField(Constants.EMPTY_STRING),
                      var MinParameterCount: ObservableField<String> = ObservableField(Constants.EMPTY_STRING),
                      var MaxParameterCount: ObservableField<String> = ObservableField(Constants.EMPTY_STRING),
                      var TotalAttributeCount: ObservableField<String> = ObservableField(Constants.EMPTY_STRING),
                      var TotalValueCount: ObservableField<String> = ObservableField(Constants.EMPTY_STRING),
                      var Type: String = App.instance.getString(R.string.all),
                      var All: ObservableBoolean = ObservableBoolean(false),
                      var SortByAttributeName: ObservableBoolean = ObservableBoolean(true),
                      var SortByParameter: ObservableBoolean = ObservableBoolean(false),
                      var SortByUserName: ObservableBoolean = ObservableBoolean(false),
                      var SortByType: ObservableBoolean = ObservableBoolean(false),
                      var IsDescending: ObservableBoolean = ObservableBoolean(true)){

    @Transient
    var useFilter: () -> Unit = { throw NotImplementedError() }
    @Transient
    val types = ArrayList<String>()
    @Transient
    val textWatcher = FilterTextWatcher()
    @Transient
    val typeOnItemSelectedListener =  TypeOnItemSelectedListener()

    val IsAdmin: Boolean
        get(){
            return App.isAdmin()
        }

    companion object{
        fun loadFilter() : AttributeFilter {
            return Gson().fromJson<AttributeFilter>(App.instance.getSharedPreferences(Constants.FILTER, 0)
                    .getString(Constants.ATTRIBUTE_FILTER, null), AttributeFilter::class.java) ?: AttributeFilter()
        }

        fun clearFilter(){
            App.instance.getSharedPreferences(Constants.FILTER, 0).edit()
                    .putString(Constants.ATTRIBUTE_FILTER, "")
                    .apply()
        }
    }

    fun resetSearch(){
        AttributeName.set(Constants.EMPTY_STRING)
        UserName.set(Constants.EMPTY_STRING)
    }

    fun resetFilter(){
        MinParameterCount.set(Constants.EMPTY_STRING)
        MaxParameterCount.set(Constants.EMPTY_STRING)
        All.set(false)
        Type = App.instance.getString(R.string.all)
        SortByAttributeName.set(true)
        SortByParameter.set(false)
        SortByUserName.set(false)
        SortByType.set(false)
        IsDescending.set(false)
    }

    private fun saveFilter(){
        App.instance.getSharedPreferences(Constants.FILTER, 0).edit()
                .putString(Constants.ATTRIBUTE_FILTER, Gson().toJson(this))
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

    inner class TypeOnItemSelectedListener : AdapterView.OnItemSelectedListener {
        var init = false
        override fun onItemSelected(arg0: AdapterView<*>, arg1: View, position: Int, id: Long) {
            if(!init){
                Type = types[position]
                useFilter()
            }
            else{
                init = false
            }
        }

        override fun onNothingSelected(arg0: AdapterView<*>) { }
    }

    inner class FilterTextWatcher : OnceRunTextWatcher() {
        override val onceAfterTextChanged: (p0: Editable?) -> Unit = {
            useFilter()
        }
    }

    @SuppressLint("DefaultLocale")
    fun filter(allAttributes : List<Attribute>): List<Attribute> {
        var filteredAttributes = allAttributes

        if(!AttributeName.get().isNullOrEmpty()) {
            filteredAttributes = filteredAttributes.filter { it.Name.toLowerCase().contains(AttributeName.get()!!.toLowerCase())}
        }

        if (IsAdmin &&!UserName.get().isNullOrEmpty()) {
            filteredAttributes = filteredAttributes.filter { it.User.UserName.toLowerCase().contains(UserName.get()!!.toLowerCase()) }
        }

        if(!MinParameterCount.get().isNullOrEmpty() && MinParameterCount.get()!!.matches(Regex("[0-9]+"))) {
            filteredAttributes = filteredAttributes.filter { it.AttributeParameters.size >= MinParameterCount.get()!!.toInt() }
        }

        if(!MaxParameterCount.get().isNullOrEmpty() && MaxParameterCount.get()!!matches(Regex("[0-9]+"))){
            filteredAttributes = filteredAttributes.filter { it.AttributeParameters.size <= MaxParameterCount.get()!!.toInt() }
        }

        if(Type != App.instance.getString(R.string.all)){
            filteredAttributes = filteredAttributes.filter { it.Type.toString() == Type }
        }

        saveFilter()

        return sortAndPrivate(filteredAttributes)
    }

    private fun sortAndPrivate(attributes: List<Attribute>): List<Attribute>{
        var sortedAttributes = attributes

        if(SortByType.get()) {
            sortedAttributes = if(IsDescending.get()) sortedAttributes.sortedByDescending { it.Type.toString() }
            else sortedAttributes.sortedBy { it.Type.name }
        }
        if(SortByAttributeName.get()) {
            sortedAttributes = if(IsDescending.get()) sortedAttributes.sortedByDescending { it.Name }
            else sortedAttributes.sortedBy { it.Name }
        }
        if(SortByParameter.get()) {
            sortedAttributes = if(IsDescending.get()) sortedAttributes.sortedByDescending { it.AttributeParameters.size }
            else sortedAttributes.sortedBy { it.AttributeParameters.size }
        }
        if(SortByUserName.get()) {
            sortedAttributes = if(IsDescending.get()) sortedAttributes.sortedByDescending { it.User.UserName }
            else sortedAttributes.sortedBy { it.User.UserName }
        }
        return sortedAttributes
    }

    fun setTypes(){
        types.clear()
        types.add(App.instance.getString(R.string.all))
        types.addAll(AttributeType.values()
                .contentToString()
                .replace(" ", Constants.EMPTY_STRING)
                .replace("[", Constants.EMPTY_STRING)
                .replace("]", Constants.EMPTY_STRING)
                .split(","))
    }

    fun setTotalAttributeCount(attributes: List<Attribute>){
        TotalAttributeCount.set(attributes.filter { it.Id != UUID(0,0) }.count().toString())
    }

    fun setTotalValueCount(attributes: List<Attribute>){
        var totalValueCount = 0

        for(attribute in attributes){
            totalValueCount += attribute.AttributeValuesCount
        }

        TotalValueCount.set(totalValueCount.toString())
    }

}
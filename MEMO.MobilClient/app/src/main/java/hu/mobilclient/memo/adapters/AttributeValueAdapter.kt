package hu.mobilclient.memo.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.RecyclerView
import hu.mobilclient.memo.App
import hu.mobilclient.memo.R
import hu.mobilclient.memo.databinding.AttributeValueListItemBinding
import hu.mobilclient.memo.helpers.Constants
import hu.mobilclient.memo.managers.ServiceManager
import hu.mobilclient.memo.model.memoapi.Attribute
import hu.mobilclient.memo.model.memoapi.AttributeValue
import hu.mobilclient.memo.model.enums.AttributeType
import kotlinx.android.synthetic.main.attribute_value_list_item.view.*

class AttributeValueAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    lateinit var serviceManager: ServiceManager

    private val displayedAttributeValues = ArrayList<AttributeValue>()
    private val attributeValues = ArrayList<AttributeValue>()

    private val attributes = ArrayList<Attribute>()

    private val valueEditTexts = ArrayList<EditText>()
    private lateinit var addEditText: EditText

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val binding: AttributeValueListItemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), R.layout.attribute_value_list_item, parent, false)

        return AttributeValueViewHolder(binding.root, binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is AttributeValueViewHolder) {
            val itemView = holder.itemView

            val attributeValue = displayedAttributeValues[position]

            holder.binding.setVariable(BR.attributeValue, attributeValue)

            val controlButton = itemView.fg_attribute_value_list_iv_control
            val valueEditText = itemView.fg_attribute_value_list_et_value
            val attributeTextView = itemView.fg_attribute_value_list_tv_attribute
            val attributeSpinner = itemView.fg_attribute_value_list_sp_attribute
            val attributeParameterSpinner = itemView.fg_attribute_value_list_sp_attribute_parameter
            val valueCheckBox = itemView.fg_attribute_value_list_cb_attribute_value

            if(position == displayedAttributeValues.size - 1){
                attributeValue.Attribute = attributes.first()
            }

            val attributeParameterOnSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(arg0: AdapterView<*>, arg1: View?, position: Int, id: Long) {
                    attributeValue.Value = attributeValue.Attribute!!.AttributeParameters[position].Value
                }

                override fun onNothingSelected(arg0: AdapterView<*>) {}
            }

            val valueOnCheckedChangeListener = fun (_:CompoundButton, isChecked: Boolean) {
                attributeValue.Value = isChecked.toString()
            }

            when(attributeValue.Attribute?.Type) {
                AttributeType.SPINNER -> {
                    valueCheckBox.visibility = View.GONE
                    valueEditText.visibility = View.GONE

                    attributeParameterSpinner.adapter = ArrayAdapter(App.instance, R.layout.spinner_item_blue, attributeValue.Attribute!!.AttributeParameters)
                    attributeParameterSpinner.onItemSelectedListener = attributeParameterOnSelectedListener
                    if(attributeValue.Value != Constants.EMPTY_STRING) {
                        attributeParameterSpinner.setSelection(attributeValue.Attribute!!.AttributeParameters.indexOf(attributeValue.Attribute!!.AttributeParameters.singleOrNull { it.Value == attributeValue.Value } ?: Constants.ZERO))
                    }
                    else{
                        attributeParameterSpinner.setSelection(attributeValue.Attribute!!.AttributeParameters.indexOf(attributeValue.Attribute!!.AttributeParameters.first()))
                    }
                    attributeParameterSpinner.visibility = View.VISIBLE
                }
                AttributeType.TEXT -> {
                    attributeParameterSpinner.visibility = View.GONE
                    valueCheckBox.visibility = View.GONE
                    valueEditText.setText(attributeValue.Value)
                    valueEditText.visibility = View.VISIBLE
                }
                AttributeType.CHECKBOX -> {
                    attributeParameterSpinner.visibility = View.GONE
                    valueEditText.visibility = View.GONE
                    valueCheckBox.isChecked = attributeValue.Value == true.toString()
                    valueCheckBox.visibility = View.VISIBLE
                }
            }

            valueCheckBox.setOnCheckedChangeListener(valueOnCheckedChangeListener)

            if (position == displayedAttributeValues.size - 1) {

                attributeSpinner.visibility = View.VISIBLE
                attributeTextView.visibility = View.GONE

                attributeSpinner.adapter = ArrayAdapter(App.instance, R.layout.spinner_item_blue, attributes)

                val attributeOnSelectedListener = object : AdapterView.OnItemSelectedListener {
                    var init = false
                    override fun onItemSelected(arg0: AdapterView<*>, arg1: View?, position: Int, id: Long) {
                        if (!init) {
                            attributeValue.Attribute = attributes[position]
                            attributeValue.Value = Constants.EMPTY_STRING

                            when (attributeValue.Attribute?.Type) {
                                AttributeType.SPINNER -> {
                                    valueCheckBox.visibility = View.GONE
                                    valueEditText.visibility = View.GONE
                                    attributeParameterSpinner.adapter = ArrayAdapter(App.instance, R.layout.spinner_item_blue, attributeValue.Attribute!!.AttributeParameters)
                                    attributeParameterSpinner.onItemSelectedListener = attributeParameterOnSelectedListener
                                    attributeParameterSpinner.visibility = View.VISIBLE
                                }
                                AttributeType.TEXT -> {
                                    attributeParameterSpinner.visibility = View.GONE
                                    valueCheckBox.visibility = View.GONE
                                    valueEditText.visibility = View.VISIBLE
                                }
                                AttributeType.CHECKBOX -> {
                                    attributeParameterSpinner.visibility = View.GONE
                                    valueEditText.visibility = View.GONE
                                    valueCheckBox.visibility = View.VISIBLE
                                    attributeValue.Value = false.toString()
                                }
                            }
                        }
                        else{
                            init = false
                        }

                        if(attributeValue.Attribute?.Type == AttributeType.CHECKBOX){

                            if(attributeValue.Value == Constants.EMPTY_STRING){
                                attributeValue.Value = false.toString()
                            }

                            valueCheckBox.setOnCheckedChangeListener(valueOnCheckedChangeListener)
                        }
                    }

                    override fun onNothingSelected(arg0: AdapterView<*>) {}
                }

                if(attributeValues.contains(attributeValue)) {
                    attributeOnSelectedListener.init = true
                }

                attributeSpinner.onItemSelectedListener = attributeOnSelectedListener

                attributeSpinner.setSelection(attributes.indexOf(attributes.single { it.Id == attributeValue.Attribute?.Id }))

                addEditText = valueEditText
                controlButton.setImageResource(R.drawable.ic_add_accent_48dp)
                controlButton.setOnClickListener {
                    if (attributeValue.Attribute?.Type == AttributeType.TEXT && valueEditText.isNotEmpty() && !valueEditText.tooLong(Constants.ATTRIBUTE_PARAMETER_VALUE_MAX_LENGTH, App.instance.getString(R.string.parameter_value_too_long, Constants.ATTRIBUTE_PARAMETER_VALUE_MAX_LENGTH)) ||
                        attributeValue.Attribute?.Type != AttributeType.TEXT){
                        val newParameter = AttributeValue().copy(attributeValue)
                        displayedAttributeValues.add(displayedAttributeValues.size - 1, newParameter)
                        attributeValues.add(newParameter)
                        attributeValue.Value = Constants.EMPTY_STRING
                        valueEditTexts.clear()
                        notifyDataSetChanged()
                    }
                }
            }
            else {
                    attributeSpinner.visibility = View.GONE
                    attributeTextView.visibility = View.VISIBLE

                    if(attributeValue.Attribute?.Type == AttributeType.TEXT && !valueEditTexts.contains(valueEditText)){
                        valueEditTexts.add(valueEditText)
                    }

                    controlButton.setImageResource(R.drawable.ic_remove_accent_24dp)
                    controlButton.setOnClickListener {
                        displayedAttributeValues.remove(attributeValue)
                        attributeValues.remove(attributeValue)
                        valueEditTexts.clear()
                        notifyDataSetChanged()
                }
            }
        }
    }

    fun isValid(): Boolean {
        for(editText in valueEditTexts){
            if (!editText.isNotEmpty() || editText.tooLong(Constants.ATTRIBUTE_VALUE_VALUE_MAX_LENGTH, App.instance.getString(R.string.attribute_value_too_long, Constants.ATTRIBUTE_VALUE_VALUE_MAX_LENGTH))){
                return false
            }
        }
        return true
    }

    private fun EditText.isNotEmpty(): Boolean{
        if(this.text.isEmpty()){
            this.requestFocus()
            this.error = App.instance.getString(R.string.required_field)
            return false
        }
        return true
    }

    private fun EditText.tooLong(maxLength: Int, errorMessage: String): Boolean{
        if(this.text.length > maxLength){
            this.requestFocus()
            this.error = errorMessage + " " + App.instance.getString(R.string.now_dd, this.text.length)
            return true
        }
        return false
    }

    fun getValues(): List<AttributeValue> {
        return attributeValues
    }

    private fun AttributeValue.copy(rightAttributeValue: AttributeValue): AttributeValue {
        Value = rightAttributeValue.Value
        Attribute = rightAttributeValue.Attribute
        return this
    }

    private fun reset(){
        attributes.clear()
        displayedAttributeValues.clear()
        attributeValues.clear()
    }

    fun initializeAdapter(attributeValues: List<AttributeValue>, attributes: List<Attribute>){
        reset()
        if(attributes.isNotEmpty()) {
            this.attributes.addAll(attributes)
            displayedAttributeValues.add(AttributeValue())
            displayedAttributeValues.addAll(0, attributeValues)
            this.attributeValues.addAll(attributeValues)

            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int = displayedAttributeValues.size

    inner class AttributeValueViewHolder(itemView: View, val binding: AttributeValueListItemBinding) : RecyclerView.ViewHolder(itemView)
}
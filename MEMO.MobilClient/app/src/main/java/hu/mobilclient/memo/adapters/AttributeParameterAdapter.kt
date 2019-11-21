package hu.mobilclient.memo.adapters

import android.annotation.SuppressLint
import android.provider.SyncStateContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.RecyclerView
import hu.mobilclient.memo.App
import hu.mobilclient.memo.R
import hu.mobilclient.memo.databinding.AttributeParameterListItemBinding
import hu.mobilclient.memo.helpers.Constants
import hu.mobilclient.memo.model.AttributeParameter
import kotlinx.android.synthetic.main.attribute_parameter_list_item.view.*
import kotlinx.android.synthetic.main.fragment_attribute.*
import kotlin.collections.ArrayList

class AttributeParameterAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val displayedAttributeParameters = ArrayList<AttributeParameter>()
    private val attributeParameters = ArrayList<AttributeParameter>()
    private val valueEditTexts = ArrayList<EditText>()
    private lateinit var addEditText: EditText

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val binding: AttributeParameterListItemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), R.layout.attribute_parameter_list_item, parent, false)

        return AttributeParameterViewHolder(binding.root, binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is AttributeParameterViewHolder) {
            val itemView = holder.itemView

            val parameter = displayedAttributeParameters[position]

            holder.binding.setVariable(BR.parameter, parameter)

            val controlButton = itemView.fg_attribute_parameter_list_iv_control
            val valueEditText = itemView.fg_attribute_parameter_list_et_value

            if (position == displayedAttributeParameters.size - 1) {
                addEditText = valueEditText
                controlButton.setImageResource(R.drawable.ic_add_48dp)
                controlButton.setOnClickListener {
                    if (valueEditText.isNotEmpty() && !valueEditText.tooLong(10, App.instance.getString(R.string.parameter_value_too_long) + " " + App.Companion.instance.getString(R.string.now_dd) + " ")){
                        val newParameter = AttributeParameter().copy(parameter)
                        displayedAttributeParameters.add(displayedAttributeParameters.size - 1,newParameter)
                        attributeParameters.add(newParameter)
                        parameter.Value = Constants.EMPTYSTRING
                        valueEditTexts.clear()
                        notifyDataSetChanged()
                    }
                }
            }
            else {
                if(!valueEditTexts.contains(valueEditText)){
                    valueEditTexts.add(valueEditText)
                }
                controlButton.setImageResource(R.drawable.ic_remove_24dp)
                controlButton.setOnClickListener {
                    displayedAttributeParameters.remove(parameter)
                    attributeParameters.remove(parameter)
                    valueEditTexts.clear()
                    notifyDataSetChanged()
                }
            }
        }
    }

    fun isValid(): Boolean {
        for(editText in valueEditTexts){
            if (!editText.isNotEmpty() || editText.tooLong(10, App.instance.getString(R.string.parameter_value_too_long) + " " + App.Companion.instance.getString(R.string.now_dd) + " ")){
                return false
            }
        }
        return true
    }

    fun setError(message: String){
        addEditText.requestFocus()
        addEditText.error = message
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
            this.error = errorMessage + this.text.length + " " + App.instance.getString(R.string.character)
            return true
        }
        return false
    }

    fun getParameters(): List<AttributeParameter> {
        return attributeParameters
    }

    private fun AttributeParameter.copy(rightParameter: AttributeParameter): AttributeParameter{
        Value = rightParameter.Value
        return this
    }

    fun initializeAdapter(attributeParameters: List<AttributeParameter>){
        displayedAttributeParameters.clear()
        displayedAttributeParameters.add(AttributeParameter())
        displayedAttributeParameters.addAll(0, attributeParameters)
        this.attributeParameters.clear()
        this.attributeParameters.addAll(attributeParameters)

        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = displayedAttributeParameters.size


    inner class AttributeParameterViewHolder(itemView: View, val binding: AttributeParameterListItemBinding) : RecyclerView.ViewHolder(itemView)
}
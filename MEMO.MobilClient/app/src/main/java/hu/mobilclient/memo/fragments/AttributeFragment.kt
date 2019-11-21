package hu.mobilclient.memo.fragments

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableBoolean
import androidx.databinding.library.baseAdapters.BR
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import hu.mobilclient.memo.App
import hu.mobilclient.memo.R
import hu.mobilclient.memo.activities.bases.NetworkActivityBase
import hu.mobilclient.memo.adapters.AttributeAdapter
import hu.mobilclient.memo.adapters.AttributeParameterAdapter
import hu.mobilclient.memo.databinding.FragmentAttributeBinding
import hu.mobilclient.memo.helpers.Constants
import hu.mobilclient.memo.helpers.EmotionToast
import hu.mobilclient.memo.model.Attribute
import hu.mobilclient.memo.model.Dictionary
import hu.mobilclient.memo.model.enums.AttributeType
import kotlinx.android.synthetic.main.fragment_attribute.*
import kotlinx.android.synthetic.main.fragment_attribute.view.*
import kotlinx.android.synthetic.main.fragment_dictionary.*
import kotlinx.android.synthetic.main.fragment_dictionary.view.*
import org.w3c.dom.Attr


class AttributeFragment(private var Attribute: Attribute = Attribute(),
                        private val OkCallback: ()->Unit) : DialogFragment() {

    private val adapter : AttributeParameterAdapter = AttributeParameterAdapter()

    var isUpdate = false

    var IsDelete: Boolean = false

    var IsSpinnerType: ObservableBoolean = ObservableBoolean(false)

    private val originalAttribute = Attribute()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val contextThemeWrapper: Context = ContextThemeWrapper(activity, R.style.AppTheme)

        val binding: FragmentAttributeBinding = DataBindingUtil.inflate(
                inflater.cloneInContext(contextThemeWrapper), R.layout.fragment_attribute, container, false)

        originalAttribute.copy(Attribute)

        isUpdate = Attribute.Id != null

        binding.setVariable(BR.attribute, Attribute)
        binding.setVariable(BR.fragment, this)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.fg_attribute_rv_parameters
        recyclerView.layoutManager = GridLayoutManager(context, 1)
        recyclerView.adapter = adapter

        adapter.initializeAdapter(Attribute.AttributeParameters)

        initializeTypeSpinner(view)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        Attribute.copy(originalAttribute)
    }

    private fun Attribute.copy(rightAttribute: Attribute){
        Name = rightAttribute.Name
        Type = rightAttribute.Type
        AttributeParameters.clear()
        AttributeParameters.addAll(rightAttribute.AttributeParameters)
    }

    private fun Attribute.attributeNotEquals(rightAttribute: Attribute): Boolean {
        if(Name != rightAttribute.Name ||
           Type != rightAttribute.Type ||
           AttributeParameters.count() != rightAttribute.AttributeParameters.count()) {
            return true
        }
        for(i in 0 until AttributeParameters.count()){
            if(AttributeParameters[i].Value != rightAttribute.AttributeParameters[i].Value){
                return true
            }
        }
        return false
    }

    private fun initializeTypeSpinner(view: View){
        val typeSpinner = view.fg_attribute_sp_type

        val types = AttributeType.values()
                                            .contentToString()
                                            .replace(" ", Constants.EMPTYSTRING)
                                            .replace("[", Constants.EMPTYSTRING)
                                            .replace("]", Constants.EMPTYSTRING)
                                            .split(",")


        typeSpinner.adapter = ArrayAdapter(App.instance, R.layout.spinner_item_blue, types)

        typeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(arg0: AdapterView<*>, arg1: View?, position: Int, id: Long) {
                Attribute.Type = AttributeType.values().single { it.toString() == types[position] }
                IsSpinnerType.set(Attribute.Type == AttributeType.SPINNER)
            }

            override fun onNothingSelected(arg0: AdapterView<*>) { }
        }

        if(isUpdate){
            typeSpinner.setSelection(types.indexOf(Attribute.Type.toString()))
        }
    }

    private fun isValid() = fg_attribute_et_attribute_name.isNotEmpty() &&
                                    !fg_attribute_et_attribute_name.tooLong(15, getString(R.string.attribute_name_too_long) + " " + getString(R.string.now_dd) + " ")

    private fun EditText.isNotEmpty(): Boolean{
        if(this.text.isEmpty()){
            this.requestFocus()
            this.error = getString(R.string.required_field)
            return false
        }
        return true
    }

    private fun EditText.tooLong(maxLength: Int, errorMessage: String): Boolean{
        if(this.text.length > maxLength){
            this.requestFocus()
            this.error = errorMessage + this.text.length + " " + getString(R.string.character)
            return true
        }
        return false
    }

    fun cancelClick(view: View) = dismiss()

    fun saveClick(view: View){
        val serviceManager = (activity as NetworkActivityBase).serviceManager
        if (isUpdate) {
            if(IsDelete){
                SureFragment(Message = Attribute.Name + getString(R.string.named_attribute_delete),
                             OkCallback = {
                                 serviceManager.attribute?.delete(Attribute.Id!!,{
                                     OkCallback()
                                     EmotionToast.showSuccess(getString(R.string.attribute_delete_success))
                                     dismiss() },{
                                     EmotionToast.showSad(getString(R.string.attribute_delete_fail))
                                 })
                             }).show(requireActivity().supportFragmentManager, "TAG")
            }
            else{
                if(Attribute.attributeNotEquals(originalAttribute)) {
                    if (isValid()) {
                        if (validateSpinnerAttribute()) {
                            serviceManager.attribute?.update(Attribute, {
                                OkCallback()
                                EmotionToast.showSuccess()
                                dismiss()
                            }, {
                                EmotionToast.showSad(getString(R.string.attribute_update_fail))
                            })
                        }
                    }
                }
                else{
                    EmotionToast.showHelp(getString(R.string.no_changes))
                }
            }
        } else {
            if(isValid()) {
                if(validateSpinnerAttribute()) {
                    Attribute.AttributeParameters.clear()
                    Attribute.AttributeParameters.addAll(adapter.getParameters())
                    Attribute.User = App.currentUser
                    serviceManager.attribute?.insert(Attribute, {
                        OkCallback()
                        dismiss()
                        EmotionToast.showSuccess(getString(R.string.attribute_create_success))
                    },{
                        EmotionToast.showSad(getString(R.string.attribute_create_fail))
                    })
                }
            }
        }
    }

    private fun validateSpinnerAttribute(): Boolean{
        if(Attribute.Type == AttributeType.SPINNER) {
            if(adapter.getParameters().any()) {
                if (adapter.isValid()) {
                    Attribute.AttributeParameters.clear()
                    Attribute.AttributeParameters.addAll(adapter.getParameters())
                    return true
                }
                return false
            }
            else{
                adapter.setError(getString(R.string.one_parameter_required))
                return false
            }
        }

        return true
    }
}
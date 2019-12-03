package hu.mobilclient.memo.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.view.ContextThemeWrapper
import androidx.databinding.DataBindingUtil
import androidx.databinding.library.baseAdapters.BR
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import hu.mobilclient.memo.App
import hu.mobilclient.memo.R
import hu.mobilclient.memo.activities.LoginActivity
import hu.mobilclient.memo.activities.NavigationActivity
import hu.mobilclient.memo.activities.bases.NetworkActivityBase
import hu.mobilclient.memo.adapters.AttributeValueAdapter
import hu.mobilclient.memo.databinding.FragmentTranslationBinding
import hu.mobilclient.memo.databinding.SpinnerItemDictionaryBinding
import hu.mobilclient.memo.helpers.BindableArrayAdapter
import hu.mobilclient.memo.helpers.Constants
import hu.mobilclient.memo.helpers.EmotionToast
import hu.mobilclient.memo.managers.ServiceManager
import hu.mobilclient.memo.model.memoapi.AttributeValue
import hu.mobilclient.memo.model.memoapi.Dictionary
import hu.mobilclient.memo.model.memoapi.Translation
import kotlinx.android.synthetic.main.fragment_translation.*
import kotlinx.android.synthetic.main.fragment_translation.view.*
import java.util.*


class TranslationFragment(private var Translation: Translation = Translation(),
                          var Dictionary: Dictionary = Dictionary(),
                          private val FromLogin: Boolean = false) : DialogFragment() {

    private lateinit var activity: NetworkActivityBase

    private lateinit var serviceManager: ServiceManager

    private val adapter : AttributeValueAdapter = AttributeValueAdapter()

    private lateinit var translationTranslatedEditText: EditText
    private lateinit var translationOriginalEditText: EditText
    private lateinit var dictionariesSpinner : Spinner
    private lateinit var colorPickerDialog: androidx.appcompat.app.AlertDialog

    var IsDelete: Boolean = false
    var isUpdate = false

    private val originalTranslation = Translation()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        activity = if(FromLogin){
            requireActivity() as LoginActivity
        }
        else{
            requireActivity() as NavigationActivity
        }

        val contextThemeWrapper: Context = ContextThemeWrapper(activity, R.style.AppTheme)

        val binding: FragmentTranslationBinding = DataBindingUtil.inflate(
                inflater.cloneInContext(contextThemeWrapper), R.layout.fragment_translation, container, false)

        originalTranslation.copy(Translation)

        isUpdate = Translation.Id != UUID(0,0)

        binding.setVariable(BR.translation, Translation)
        binding.setVariable(BR.fragment, this)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        colorPickerDialog = ColorPickerDialogBuilder.with(contextThemeWrapper, R.style.ColorPickerDialogTheme)
                                                    .setTitle(getString(R.string.choose_color))
                                                    .initialColor(Translation.Color)
                                                    .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                                                    .density(Constants.COLORPICKERDIALOG_DENSITY)
                                                    .setPositiveButton(getString(R.string.choose)) { _, selectedColor, _ -> run {
                                                        Translation.Color = selectedColor
                                                        translationTranslatedEditText.setTextColor(selectedColor)
                                                        translationOriginalEditText.setTextColor(selectedColor)
                                                    } }
                                                    .setNegativeButton(getString(R.string.cancel)) { _, _ ->  colorPickerDialog.dismiss() }
                                                    .build()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        serviceManager = activity.serviceManager

        translationTranslatedEditText = view.fg_translation_et_translated
        translationOriginalEditText = view.fg_translation_et_original

        val recyclerView = view.fg_translation_rv_attributes
        recyclerView.layoutManager = GridLayoutManager(context, 1)
        recyclerView.adapter = adapter

        serviceManager.attribute.getByUserId(App.currentUser.Id,{ attributes ->
            adapter.initializeAdapter(Translation.AttributeValues, attributes)
        },{
            EmotionToast.showSad(App.instance.getString(R.string.unable_load_all_attributes))
        })

        dictionariesSpinner = view.fg_translation_sp_dictionaries

        serviceManager.dictionary.getByUserId(App.currentUser.Id, { dictionaries ->
            val ownDictionaries = dictionaries.filter { it.isOwn() }
            dictionariesSpinner.adapter = BindableArrayAdapter<Dictionary, SpinnerItemDictionaryBinding>(App.instance, R.layout.spinner_item_dictionary, ownDictionaries)
            dictionariesSpinner.onItemSelectedListener =  object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(arg0: AdapterView<*>, arg1: View?, position: Int, id: Long) {
                    Dictionary = ownDictionaries[position]
                }

                override fun onNothingSelected(arg0: AdapterView<*>) { }
            }

            setDictionarySpinnerSelection(dictionariesSpinner)
        })
    }

    private var setDictionarySpinnerSelection: (dictionarySpinner: Spinner)->Unit = {}

    fun setDictionarySpinnerSelection(selectedDictionaryId: UUID){
        setDictionarySpinnerSelection = {dictionariesSpinner ->
            for(i in 0 until dictionariesSpinner.adapter.count){
                if((dictionariesSpinner.adapter.getItem(i) as Dictionary).Id == selectedDictionaryId) {
                    dictionariesSpinner.setSelection(i)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Translation.copy(originalTranslation)
    }

    private fun Translation.copy(rightTranslation: Translation){
        Original = rightTranslation.Original
        Translated = rightTranslation.Translated
        Color = rightTranslation.Color
        AttributeValues.clear()
        for(attributeValue in rightTranslation.AttributeValues){
            AttributeValues.add(AttributeValue().copy(attributeValue))
        }
    }

    private fun AttributeValue.copy(rightAttributeValue: AttributeValue): AttributeValue {
        Value = rightAttributeValue.Value
        Attribute = rightAttributeValue.Attribute
        AttributeId = rightAttributeValue.AttributeId

        return this
    }

    private fun Translation.translationNotEquals(rightTranslation: Translation): Boolean {
        if(Original != rightTranslation.Original ||
           Translated != rightTranslation.Translated ||
           Color != rightTranslation.Color ||
           AttributeValues.count() != rightTranslation.AttributeValues.count()) {
            return true
        }
        for(i in 0 until AttributeValues.count()){
            if(AttributeValues[i].Value != rightTranslation.AttributeValues[i].Value ||
               AttributeValues[i].Attribute?.Type != rightTranslation.AttributeValues[i].Attribute?.Type){
               return true
            }
        }
        return false
    }

    private fun isValid() = fg_translation_et_original.isNotEmpty() &&
                                    !fg_translation_et_original.tooLong(Constants.TRANSLATION_ORIGINAL_MAX_LENGTH, getString(R.string.translation_original_too_long, Constants.TRANSLATION_ORIGINAL_MAX_LENGTH))  &&
                                    fg_translation_et_translated.isNotEmpty() &&
                                    !fg_translation_et_translated.tooLong(Constants.TRANSLATION_TRANSLATED_MAX_LENGTH, getString(R.string.translation_translated_too_long, Constants.TRANSLATION_TRANSLATED_MAX_LENGTH))


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
            this.error = errorMessage + " " + App.instance.getString(R.string.now_dd, this.text.length)
            return true
        }
        return false
    }

    @SuppressLint("DefaultLocale")
    fun translateClick(view: View) {
        if(translationOriginalEditText.text.isNotEmpty()) {
            serviceManager.translation.translate(Translation.Original, Dictionary.Source.Code.name, Dictionary.Destination.Code.name, {
                Translation.Translated = it.Translated
                translationTranslatedEditText.setText(it.Translated)
            }, {
                EmotionToast.showSad(getString(R.string.unable_to_translate))
            })
        }
        else{
            if(translationTranslatedEditText.text.isNotEmpty()) {
                serviceManager.translation.translate(Translation.Translated, Dictionary.Destination.Code.name, Dictionary.Source.Code.name, {
                    Translation.Original = it.Translated
                    translationOriginalEditText.setText(it.Translated)
                }, {
                    EmotionToast.showSad(getString(R.string.unable_to_translate))
                })
            }
        }
    }

    fun cancelClick(view: View) {
        if(FromLogin){
            activity.finish()
        }
        dismiss()
    }

    fun colorClick(view: View){
        colorPickerDialog.show()
    }

    fun saveClick(view: View){
        if (isUpdate) {
            if(IsDelete){
                SureFragment(Message = Translation.Original + " " + getString(R.string.translation_delete),
                             OkCallback = {
                                 serviceManager.translation.delete(Translation.Id,{
                                     (activity as NavigationActivity).onTranslationDeleted(Translation.Id)
                                     EmotionToast.showSuccess(getString(R.string.translation_delete_success))
                                     dismiss()
                                 },{
                                     EmotionToast.showSad(getString(R.string.translation_delete_fail))
                                 })
                             }).show(requireActivity().supportFragmentManager, "TAG")
            }
            else{
                if (isValid() && adapter.isValid()) {
                        Translation.AttributeValues.clear()
                        Translation.AttributeValues.addAll(adapter.getValues())

                        if(Translation.translationNotEquals(originalTranslation)) {
                            for (attributeValue in Translation.AttributeValues) {
                                attributeValue.AttributeId = attributeValue.Attribute?.Id
                                attributeValue.Attribute = null
                            }
                            serviceManager.translation.update(Translation, {
                                (activity as NavigationActivity).onTranslationUpdated(Translation.Id)
                                dismiss()
                                EmotionToast.showSuccess()
                            }, {
                                EmotionToast.showSad(getString(R.string.translation_update_fail))
                            })
                        }
                        else{
                            EmotionToast.showHelp(getString(R.string.no_changes))
                        }
                }
            }
        } else {
            if(isValid() && adapter.isValid()) {
                    Translation.AttributeValues.clear()
                    Translation.AttributeValues.addAll(adapter.getValues())
                    for (attributeValue in Translation.AttributeValues){
                        attributeValue.AttributeId = attributeValue.Attribute?.Id
                        attributeValue.Attribute = null
                    }
                    Translation.DictionaryId = Dictionary.Id
                    serviceManager.translation.insert(Translation, { translation ->
                        if(FromLogin){
                            activity.finish()
                        }
                        else{
                            (activity as NavigationActivity).onTranslationCreated(translation.Id)
                        }
                        dismiss()
                        EmotionToast.showSuccess(getString(R.string.translation_create_success))
                    },{
                        EmotionToast.showSad(getString(R.string.translation_create_fail))
                    })
            }
        }
    }
}
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
import hu.mobilclient.memo.App
import hu.mobilclient.memo.R
import hu.mobilclient.memo.activities.bases.NetworkActivityBase
import hu.mobilclient.memo.databinding.FragmentDictionaryBinding
import hu.mobilclient.memo.model.Dictionary
import kotlinx.android.synthetic.main.fragment_dictionary.*
import kotlinx.android.synthetic.main.fragment_dictionary.view.*


class DictionaryFragment(private var Dictionary: Dictionary = Dictionary(),
                         private val OkCallback: ()->Unit) : DialogFragment() {

    var isUpdate = false

    var isEnabled = true
        private set

    var IsViewed: Boolean = false
    var IsDelete: Boolean = false

    var IsViewersVisible: ObservableBoolean = ObservableBoolean(false)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val contextThemeWrapper: Context = ContextThemeWrapper(activity, R.style.AppTheme)

        val binding: FragmentDictionaryBinding = DataBindingUtil.inflate(
                inflater.cloneInContext(contextThemeWrapper), R.layout.fragment_dictionary, container, false)

        val ownerId = Dictionary.Owner.Id
        if(ownerId != null && !App.isCurrent(ownerId)) {
            isEnabled = false
            IsViewed = Dictionary.Viewers.any()
        }

        if(App.isAdmin()){
            isEnabled = true
        }

        isUpdate = Dictionary.Id != null

        binding.setVariable(BR.dictionary, Dictionary)
        binding.setVariable(BR.fragment, this)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cancelButton = view.fg_dictionary_tv_cancel
        cancelButton.setOnClickListener {
            dismiss()
        }

        val viewerCountTextView = view.fg_dictionary_tv_viewer_count
        viewerCountTextView.text = Dictionary.ViewerCount.toString()

        if(isEnabled && Dictionary.Viewers.any()) {

            val viewersHolderLinerLayout = fg_dictionary_lv_viewers_holder

            for(viewer in Dictionary.Viewers) {
                val viewerTextView = TextView(requireContext())
                viewerTextView.text = viewer.UserName
                viewerTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
                viewerTextView.typeface = ResourcesCompat.getFont(requireActivity(), R.font.aldrich)
                val params = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    gravity = Gravity.END
                }
                viewerTextView.layoutParams = params
                viewersHolderLinerLayout.addView(viewerTextView)
            }
        }

        val descriptionHiderButton = view.fg_dictionary_iv_viewers_hider
        descriptionHiderButton.setOnClickListener {
            IsViewersVisible.set(!IsViewersVisible.get())

            (it as ImageView).setImageResource(if (IsViewersVisible.get()) R.drawable.ic_drop_up_white_24dp else R.drawable.ic_drop_down_white_24dp)
        }

        initializeLanguageSpinners(view)
    }

    private fun initializeLanguageSpinners(view: View){
        (activity as NetworkActivityBase).serviceManager.language?.get({languages ->

            val sourceLanguageSpinner = view.fg_dictionary_sp_source_language
            val destinationLanguageSpinner = view.fg_dictionary_sp_destination_language

            sourceLanguageSpinner.adapter = ArrayAdapter(App.instance, R.layout.spinner_item_blue, languages)

            sourceLanguageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(arg0: AdapterView<*>, arg1: View, position: Int, id: Long) {
                    Dictionary.Source = languages[position]
                }

                override fun onNothingSelected(arg0: AdapterView<*>) { }
            }

            destinationLanguageSpinner.adapter = ArrayAdapter(App.instance, R.layout.spinner_item_blue, languages)

            destinationLanguageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(arg0: AdapterView<*>, arg1: View, position: Int, id: Long) {
                    Dictionary.Destination = languages[position]
                }

                override fun onNothingSelected(arg0: AdapterView<*>) { }
            }

            sourceLanguageSpinner.setSelection(languages.indexOf(languages.single{ it.Code == Dictionary.Source.Code }))
            destinationLanguageSpinner.setSelection(languages.indexOf(languages.single{ it.Code == Dictionary.Destination.Code }))
        })
    }

    private fun isValid() = fg_dictionary_et_dictionary_name.isNotEmpty() &&
                                    !fg_dictionary_et_dictionary_name.tooLong(20, getString(R.string.dictionary_name_too_long) + " " + getString(R.string.now_dd) + " ") &&
                                    !fg_dictionary_et_dictionary_description.tooLong(250, getString(R.string.description_too_long) + " " + getString(R.string.now_dd) + " ")

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

    fun saveClick(view: View){
        if(isValid()) {
            val serviceManager = (activity as NetworkActivityBase).serviceManager
            if (isUpdate) {
                if(IsDelete){
                    SureFragment(Message = Dictionary.Name + getString(R.string.named_dictionary_delete),
                                 OkCallback = {
                                     serviceManager.dictionary?.delete(Dictionary.Id!!,{
                                         OkCallback()
                                         dismiss() })
                                 }).show(requireActivity().supportFragmentManager, "TAG")
                }
                else{
                    if(isEnabled) {
                        serviceManager.dictionary?.update(Dictionary, {
                            OkCallback()
                            dismiss()
                        })
                    }
                    else{
                        val isUserViewd = Dictionary.Viewers.filter {it.Id == App.currentUser.Id}.any()
                        if(IsViewed){
                            if(!isUserViewd) {
                                serviceManager.dictionary?.subscribe(App.currentUser.Id!!, Dictionary, {
                                    OkCallback()
                                    dismiss()
                                })
                            }
                            else{
                                dismiss()
                            }
                        }
                        else{
                            if(isUserViewd) {
                                SureFragment(Message = Dictionary.Name + getString(R.string.name_dictionary_unsubscribe),
                                        OkCallback = {
                                            serviceManager.dictionary?.unsubscribe(App.currentUser.Id!!, Dictionary, {
                                                OkCallback()
                                                dismiss()
                                            })
                                        }).show(requireActivity().supportFragmentManager, "TAG")
                            }
                            else{
                                dismiss()
                            }
                        }
                    }
                }
            } else {
                Dictionary.Owner = App.currentUser
                serviceManager.dictionary?.insert(Dictionary, {
                    OkCallback()
                    dismiss()
                })
            }
        }
    }
}
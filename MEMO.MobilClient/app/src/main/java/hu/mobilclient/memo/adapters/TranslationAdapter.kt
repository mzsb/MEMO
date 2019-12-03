package hu.mobilclient.memo.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import hu.mobilclient.memo.App
import hu.mobilclient.memo.BR
import hu.mobilclient.memo.R
import hu.mobilclient.memo.databinding.AttributeValueBinding
import hu.mobilclient.memo.databinding.TranslationFilterMenuBinding
import hu.mobilclient.memo.databinding.TranslationListItemBinding
import hu.mobilclient.memo.databinding.TranslationListNewItemBinding
import hu.mobilclient.memo.filters.TranslationFilter
import hu.mobilclient.memo.helpers.Constants
import hu.mobilclient.memo.helpers.EmotionToast
import hu.mobilclient.memo.managers.ServiceManager
import hu.mobilclient.memo.model.memoapi.Dictionary
import hu.mobilclient.memo.model.memoapi.Translation
import kotlinx.android.synthetic.main.translation_filter_content.view.*
import kotlinx.android.synthetic.main.translation_filter_menu.view.*
import kotlinx.android.synthetic.main.translation_list_item.view.*
import kotlinx.android.synthetic.main.translation_list_new_item.view.*
import java.util.*
import kotlin.collections.ArrayList

class TranslationAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    lateinit var serviceManager: ServiceManager
    lateinit var dictionary: Dictionary

    private val TYPE_FILTER = 0
    private val TYPE_TRANSLATION = 1
    private val TYPE_NEW_TRANSLATION = 2

    private val filterPosition = Constants.ZERO
    private val newTranslationPosition: Int
            get(){
                return displayedTranslation.count() - 1
            }

    private val displayedTranslation = ArrayList<Translation>()
    private val translations = ArrayList<Translation>()

    private var translationFilter = TranslationFilter.loadFilter()

    lateinit var itemClickListener: OnTranslationClickedListener

    private val filterOpen: Animation = AnimationUtils.loadAnimation(App.instance, R.anim.filter_open)
    private val searchOpen: Animation = AnimationUtils.loadAnimation(App.instance, R.anim.search_open)
    private val filterMenuOpen: Animation = AnimationUtils.loadAnimation(App.instance, R.anim.filter_menu_open)
    private val filterMenuClose: Animation = AnimationUtils.loadAnimation(App.instance, R.anim.filter_menu_close)
    private val filterClose: Animation = AnimationUtils.loadAnimation(App.instance, R.anim.filter_close)

    private lateinit var translationOriginalEditText: EditText
    private lateinit var translationTranslatedEdiText: EditText

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            TYPE_TRANSLATION -> {
                val binding: TranslationListItemBinding = DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context), R.layout.translation_list_item, parent, false)

                TranslationViewHolder(binding.root, binding)
            }
            TYPE_FILTER ->{
                val binding: TranslationFilterMenuBinding = DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context), R.layout.translation_filter_menu, parent, false)

                FilterViewHolder(binding.root, binding)
            }
            TYPE_NEW_TRANSLATION ->{
                val binding: TranslationListNewItemBinding = DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context), R.layout.translation_list_new_item, parent, false)

                NewTranslationViewHolder(binding.root, binding)
            }
            else -> throw IllegalArgumentException(App.instance.getString(R.string.not_existing_list_item_type))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if(dictionary.Id != UUID(0,0) && App.isCurrent(dictionary.Owner.Id)){
                    when(position) {
                        filterPosition -> TYPE_FILTER
                        newTranslationPosition -> TYPE_NEW_TRANSLATION
                        else -> TYPE_TRANSLATION
                    }
                }
                else{
                    when(position) {
                        filterPosition -> TYPE_FILTER
                        else -> TYPE_TRANSLATION
                    }
                }
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemView = holder.itemView

        when(holder.itemViewType) {
            TYPE_TRANSLATION -> {
                if(holder is TranslationViewHolder) {
                    val translation = displayedTranslation[position]

                    holder.binding.setVariable(BR.translation, translation)

                    val attributeValuesHolderLinearLayout = itemView.fg_translation_list_ll_attribute_values_holder
                    attributeValuesHolderLinearLayout.removeAllViews()

                    if (translation.AttributeValues.any()) {
                        if (attributeValuesHolderLinearLayout.childCount < translation.AttributeValues.count()) {
                            for (attributeValue in translation.AttributeValues) {
                                val binding: AttributeValueBinding = DataBindingUtil.inflate(
                                        LayoutInflater.from(itemView.context), R.layout.attribute_value, attributeValuesHolderLinearLayout, false)

                                binding.setVariable(BR.value, attributeValue)

                                attributeValuesHolderLinearLayout.addView(binding.root)
                            }
                        }
                    }

                    attributeValuesHolderLinearLayout.visibility = if (holder.attributeValuesVisibility) View.VISIBLE else View.GONE

                    itemView.setOnClickListener {
                        if (translation.AttributeValues.any()) {
                            holder.attributeValuesVisibility = !holder.attributeValuesVisibility
                            attributeValuesHolderLinearLayout.visibility = if (holder.attributeValuesVisibility) View.VISIBLE else View.GONE
                        }
                    }

                    itemView.setOnLongClickListener {
                        holder.attributeValuesVisibility = !holder.attributeValuesVisibility
                        if (dictionary.Id != UUID(0,0) && (App.isAdmin() || App.isCurrent(dictionary.Owner.Id))) {
                            itemClickListener.onTranslationLongClicked(translation)
                        }
                        false
                    }
                }
            }
            TYPE_FILTER -> {
                if(holder is FilterViewHolder) {

                    holder.binding.setVariable(BR.filter, translationFilter)

                    translationFilter.setTotalTranslationCount(displayedTranslation)
                    translationFilter.setTotalAttributeValueCount(displayedTranslation)

                    val filterMenuSearchButton = itemView.fg_translation_list_translation_filter_menu_iv_search
                    val filterMenuFilterButton = itemView.fg_translation_list_translation_filter_menu_iv_filter
                    val filterMenuPanel = itemView.fg_translation_list_translation_filter_menu_cl_menu
                    val filterContentHolder = itemView.fg_translation_list_translation_filter_menu_cl_content
                    val filterSearchPanel = itemView.fg_translation_list_translation_filter_content_ll_search
                    val filterFilterPanel = itemView.fg_translation_list_translation_filter_content_ll_filter
                    val filterContentCloseButton = itemView.fg_translation_list_translation_filter_content_iv_close
                    val filterContentResetButton = itemView.fg_translation_list_translation_filter_content_iv_reset
                    val filterOriginalEditText = itemView.fg_translation_list_translation_filter_content_et_original
                    val filterTranslatedEditText = itemView.fg_translation_list_translation_filter_content_et_translated

                    filterMenuSearchButton.setOnClickListener {
                        it.isEnabled = false
                        filterMenuFilterButton.isEnabled = false
                        filterMenuPanel.startAnimation(filterMenuClose)
                        filterMenuPanel.visibility = View.GONE
                        filterContentHolder.startAnimation(searchOpen)
                        filterContentHolder.visibility = View.VISIBLE
                        filterSearchPanel.visibility = View.VISIBLE
                        filterContentCloseButton.setImageResource(R.drawable.ic_search_24dp)
                        filterContentCloseButton.isEnabled = true

                        filterContentResetButton.setOnClickListener {
                            translationFilter.resetSearch()
                            useFilter()
                        }
                    }

                    filterMenuFilterButton.setOnClickListener {
                        it.isEnabled = false
                        filterMenuSearchButton.isEnabled = false
                        filterMenuPanel.startAnimation(filterMenuClose)
                        filterMenuPanel.visibility = View.GONE
                        filterContentHolder.startAnimation(filterOpen)
                        filterContentHolder.visibility = View.VISIBLE
                        filterFilterPanel.visibility = View.VISIBLE
                        filterContentCloseButton.setImageResource(R.drawable.ic_filter_24dp)
                        filterContentCloseButton.isEnabled = true

                        filterContentResetButton.setOnClickListener {
                            translationFilter.resetFilter()
                            useFilter()
                        }
                    }

                    filterContentCloseButton.setOnClickListener {
                        it.isEnabled = false
                        filterContentHolder.visibility = View.GONE
                        filterContentHolder.startAnimation(filterClose)
                        filterMenuPanel.visibility = View.VISIBLE
                        filterMenuPanel.startAnimation(filterMenuOpen)
                        filterSearchPanel.visibility = View.GONE
                        filterFilterPanel.visibility = View.GONE
                        filterMenuSearchButton.isEnabled = true
                        filterMenuFilterButton.isEnabled = true
                    }

                    filterOriginalEditText.addTextChangedListener(translationFilter.textWatcher)
                    filterTranslatedEditText.addTextChangedListener(translationFilter.textWatcher)
                }
            }
            TYPE_NEW_TRANSLATION -> {
                if (holder is NewTranslationViewHolder) {
                    val translation = displayedTranslation[position]

                    translationOriginalEditText = itemView.fg_translation_list_new_et_original
                    translationTranslatedEdiText = itemView.fg_translation_list_new_et_translation

                    holder.binding.setVariable(BR.translation, translation)

                    val newTranslationButton = itemView.fg_translation_list_new_iv_add
                    val translationButton = itemView.fg_translation_list_new_iv_translate

                    newTranslationButton.setOnClickListener {
                        if(isValid()) {
                            translation.DictionaryId = dictionary.Id
                            serviceManager.translation.insert(translation, {
                                itemClickListener.onNewTranslationClicked(it.Id)
                                EmotionToast.showSuccess()
                            },{
                                EmotionToast.showSad(App.instance.getString(R.string.translation_create_fail))
                            })
                        }
                    }

                    translationButton.setOnClickListener {
                        if(translationOriginalEditText.text.isNotEmpty()) {
                            serviceManager.translation.translate(translation.Original, dictionary.Source.Code.name, dictionary.Destination.Code.name, {
                                translation.Translated = it.Translated
                                translationTranslatedEdiText.setText(it.Translated)
                            }, {
                                EmotionToast.showSad(it)
                            })
                        }
                        else{
                            if(translationTranslatedEdiText.text.isNotEmpty()) {
                                serviceManager.translation.translate(translation.Translated, dictionary.Destination.Code.name, dictionary.Source.Code.name, {
                                    translation.Original = it.Translated
                                    translationOriginalEditText.setText(it.Translated)
                                }, {
                                    EmotionToast.showSad(it)
                                })
                            }
                        }
                    }
                }
            }
        }

    }

    private fun isValid() = translationOriginalEditText.isNotEmpty() &&
                                    !translationOriginalEditText.tooLong(Constants.TRANSLATION_ORIGINAL_MAX_LENGTH, App.instance.getString(R.string.translation_original_too_long, Constants.TRANSLATION_ORIGINAL_MAX_LENGTH))  &&
                                    translationTranslatedEdiText.isNotEmpty() &&
                                    !translationTranslatedEdiText.tooLong(Constants.TRANSLATION_TRANSLATED_MAX_LENGTH, App.instance.getString(R.string.translation_translated_too_long, Constants.TRANSLATION_TRANSLATED_MAX_LENGTH))


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

    private fun useFilter(){
        initializeAdapter()
    }

    override fun getItemCount(): Int = displayedTranslation.size

    private var beforeInit = {}
    private var afterInit = {}
    private var onError = {}

    fun initializeAdapter(beforeInit: ()-> Unit = this.beforeInit,
                          afterInit: ()->Unit = this.afterInit,
                          onError: ()->Unit = this.onError){
        this.beforeInit = beforeInit
        this.afterInit = afterInit
        this.onError = onError
        translationFilter.useFilter = ::useFilter
        reset()
        beforeInit()
        serviceManager.translation.getByDictionaryId(dictionary.Id, { dictionaryTranslation ->
            translations.addAll(dictionaryTranslation)
            setTranslations(dictionaryTranslation)
            afterInit()
        }, {
            EmotionToast.showSad(App.instance.getString(R.string.unable_to_load_translations))
            onError()
        })
    }

    private fun setTranslations(translations: List<Translation>) {
        displayedTranslation.clear()
        displayedTranslation.addAll(0, translationFilter.filter(translations))
        displayedTranslation.add(filterPosition, Translation())
        if(dictionary.Id != UUID(0,0) && App.isCurrent(dictionary.Owner.Id)) {
            displayedTranslation.add(newTranslationPosition + 1, Translation())
        }
        notifyDataSetChanged()
    }

    private fun reset(){
        translations.clear()
    }

    inner class TranslationViewHolder(itemView: View, val binding: TranslationListItemBinding, var attributeValuesVisibility : Boolean = false) : RecyclerView.ViewHolder(itemView)

    inner class FilterViewHolder(itemView: View, val binding: TranslationFilterMenuBinding): RecyclerView.ViewHolder(itemView)

    inner class NewTranslationViewHolder(itemView: View, val binding: TranslationListNewItemBinding): RecyclerView.ViewHolder(itemView)

    interface OnTranslationClickedListener {
        fun onTranslationLongClicked(translation: Translation): Boolean
        fun onNewTranslationClicked(translationId: UUID)
    }
}
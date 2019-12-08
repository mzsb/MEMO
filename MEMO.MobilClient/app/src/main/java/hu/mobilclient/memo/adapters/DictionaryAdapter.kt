package hu.mobilclient.memo.adapters

import android.annotation.SuppressLint
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import hu.mobilclient.memo.App
import hu.mobilclient.memo.BR
import hu.mobilclient.memo.R
import hu.mobilclient.memo.databinding.DictionaryFilterMenuBinding
import hu.mobilclient.memo.databinding.DictionaryListItemBinding
import hu.mobilclient.memo.filters.DictionaryFilter
import hu.mobilclient.memo.helpers.Constants
import hu.mobilclient.memo.helpers.EmotionToast
import hu.mobilclient.memo.managers.ServiceManager
import hu.mobilclient.memo.model.memoapi.Dictionary
import hu.mobilclient.memo.model.memoapi.Language
import kotlinx.android.synthetic.main.dictionary_filter_content.view.*
import kotlinx.android.synthetic.main.dictionary_filter_menu.view.*
import kotlinx.android.synthetic.main.dictionary_list_item.view.*
import java.util.*
import kotlin.collections.ArrayList


class DictionaryAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    lateinit var serviceManager: ServiceManager
    lateinit var userId: UUID

    private val TYPE_FILTER = 0
    private val TYPE_DICTIONARY = 1

    private val filterPosition = Constants.ZERO

    private val displayedDictionaries = ArrayList<Dictionary>()
    private var dictionaryFilter = DictionaryFilter.loadFilter()

    lateinit var itemClickListener: OnDictionaryClickedListener
    lateinit var itemLongClickListener: OnDictionaryClickedListener

    private val filterOpen: Animation = AnimationUtils.loadAnimation(App.instance, R.anim.filter_open)
    private val searchOpen: Animation = AnimationUtils.loadAnimation(App.instance, R.anim.search_open)
    private val filterMenuOpen: Animation = AnimationUtils.loadAnimation(App.instance, R.anim.filter_menu_open)
    private val filterMenuClose: Animation = AnimationUtils.loadAnimation(App.instance, R.anim.filter_menu_close)
    private val filterClose: Animation = AnimationUtils.loadAnimation(App.instance, R.anim.filter_close)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            TYPE_DICTIONARY -> {
                val binding: DictionaryListItemBinding = DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context), R.layout.dictionary_list_item, parent, false)

                DictionaryViewHolder(binding.root, binding)
            }
            TYPE_FILTER ->{
                val binding: DictionaryFilterMenuBinding = DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context), R.layout.dictionary_filter_menu, parent, false)

                FilterViewHolder(binding.root, binding)
            }
            else -> throw IllegalArgumentException(App.instance.getString(R.string.not_existing_list_item_type))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == filterPosition) {
            TYPE_FILTER
        } else TYPE_DICTIONARY
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemView = holder.itemView

        when(holder.itemViewType) {
            TYPE_DICTIONARY -> {
                if(holder is DictionaryViewHolder) {
                    val dictionary = displayedDictionaries[position]

                    holder.binding.setVariable(BR.dictionary, dictionary)

                    val userNameTextView = itemView.fg_dictionary_list_tv_user_name
                    val creationDateTextView = itemView.fg_dictionary_list_tv_creation_date
                    val descriptionTextView = itemView.fg_dictionary_list_tv_description
                    val descriptionHiderButton = itemView.fg_dictionary_list_iv_description_hider

                    userNameTextView.paintFlags = userNameTextView.paintFlags or Paint.UNDERLINE_TEXT_FLAG

                    if(App.isCurrent(dictionary.Owner.Id) || App.isAdmin()){
                        creationDateTextView.visibility = View.VISIBLE
                    }
                    else{
                        creationDateTextView.visibility = View.INVISIBLE
                    }

                    descriptionHiderButton.setOnClickListener {
                        holder.descriptionVisibility = !holder.descriptionVisibility

                        (it as ImageView).setImageResource(if (holder.descriptionVisibility) R.drawable.ic_drop_up_24dp else R.drawable.ic_drop_down_24dp)
                        descriptionTextView.visibility = if (holder.descriptionVisibility) View.VISIBLE else View.GONE
                    }

                    itemView.setOnClickListener {
                        itemClickListener.onDictionaryClicked(dictionary, position)
                    }

                    itemView.setOnLongClickListener {
                        itemLongClickListener.onDictionaryLongClicked(dictionary)
                    }
                }
            }
            TYPE_FILTER -> {
                if(holder is FilterViewHolder) {

                    holder.binding.setVariable(BR.filter, dictionaryFilter)

                    dictionaryFilter.setTotalDictionaryCount(displayedDictionaries)
                    dictionaryFilter.setTotalTranslationCount(displayedDictionaries)

                    val filterMenuSearchButton = itemView.fg_dictionary_list_dictionary_filter_menu_iv_search
                    val filterMenuFilterButton = itemView.fg_dictionary_list_dictionary_filter_menu_iv_filter
                    val filterMenuPanel = itemView.fg_dictionary_list_dictionary_filter_menu_cl_menu
                    val filterContentHolder = itemView.fg_dictionary_list_dictionary_filter_menu_cl_content
                    val filterSearchPanel = itemView.fg_dictionary_list_dictionary_filter_content_ll_search
                    val filterFilterPanel = itemView.fg_dictionary_list_dictionary_filter_content_ll_filter
                    val filterContentCloseButton = itemView.fg_dictionary_list_dictionary_filter_content_iv_close
                    val filterContentResetButton = itemView.fg_dictionary_list_dictionary_filter_content_iv_reset
                    val filterDestinationSourceSpinner = itemView.fg_dictionary_list_dictionary_filter_content_sp_source_language
                    val filterDestinationLanguageSpinner = itemView.fg_dictionary_list_dictionary_filter_content_sp_destination_language
                    val filterDictionaryNameEditText = itemView.fg_dictionary_list_dictionary_filter_content_et_dictionary_name
                    val filterUserNameEditText = itemView.fg_dictionary_list_dictionary_filter_content_et_user_name
                    val filterMinTranslationCountEditText = itemView.fg_dictionary_list_dictionary_filter_content_et_min_translation_count
                    val filterMaxTranslationCountEditText = itemView.fg_dictionary_list_dictionary_filter_content_et_max_translation_count
                    val filterMinViewerCountEditText = itemView.fg_dictionary_list_dictionary_filter_content_et_min_viewer_count
                    val filterMaxViewerCountEditText = itemView.fg_dictionary_list_dictionary_filter_content_et_max_viewer_count

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
                            dictionaryFilter.resetSearch()
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
                            dictionaryFilter.resetFilter()
                            filterDestinationLanguageSpinner.setSelection(dictionaryFilter.destinationLanguages.indexOf(dictionaryFilter.DestinationLanguage))
                            filterDestinationSourceSpinner.setSelection(dictionaryFilter.sourceLanguages.indexOf(dictionaryFilter.SourceLanguage))
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

                    filterDictionaryNameEditText.addTextChangedListener(dictionaryFilter.textWatcher)
                    filterUserNameEditText.addTextChangedListener(dictionaryFilter.textWatcher)

                    filterMinTranslationCountEditText.addTextChangedListener(dictionaryFilter.textWatcher)
                    filterMaxTranslationCountEditText.addTextChangedListener(dictionaryFilter.textWatcher)
                    filterMinViewerCountEditText.addTextChangedListener(dictionaryFilter.textWatcher)
                    filterMaxViewerCountEditText.addTextChangedListener(dictionaryFilter.textWatcher)

                    filterDestinationSourceSpinner.adapter = ArrayAdapter(App.instance, R.layout.spinner_item_accent, dictionaryFilter.sourceLanguages)

                    dictionaryFilter.sourceLanguageOnItemSelectedListener.init = true
                    filterDestinationSourceSpinner.setSelection(dictionaryFilter.sourceLanguages.indexOf(dictionaryFilter.SourceLanguage))
                    filterDestinationSourceSpinner.onItemSelectedListener = dictionaryFilter.sourceLanguageOnItemSelectedListener

                    filterDestinationLanguageSpinner.adapter = ArrayAdapter(App.instance, R.layout.spinner_item_accent, dictionaryFilter.destinationLanguages)

                    dictionaryFilter.destinationLanguageOnItemSelectedListener.init = true
                    filterDestinationLanguageSpinner.setSelection(dictionaryFilter.destinationLanguages.indexOf(dictionaryFilter.DestinationLanguage))
                    filterDestinationLanguageSpinner.onItemSelectedListener = dictionaryFilter.destinationLanguageOnItemSelectedListener
                }
            }
        }

    }

    private fun useFilter(){
        initializeAdapter(beforeInit,afterInit)
    }

    override fun getItemCount(): Int = displayedDictionaries.size

    private var beforeInit = {}
    private var afterInit = {}
    private var onError = {}

    fun initializeAdapter(beforeInit: ()-> Unit = this.beforeInit,
                          afterInit: ()->Unit = this.afterInit,
                          onError: ()->Unit = this.onError){
        this.beforeInit = beforeInit
        this.afterInit = afterInit
        dictionaryFilter.useFilter = ::useFilter
        dictionaryFilter.sourceLanguages.clear()
        if(!dictionaryFilter.sourceLanguages.any()){
            serviceManager.language.get({languages ->
                setLanguages(languages)
            })
        }
        when {
            dictionaryFilter.All.get() -> {
                    beforeInit()
                    serviceManager.dictionary.get({allDictionaries ->
                        setDictionaries(allDictionaries)
                        afterInit()
                    },{
                        EmotionToast.showSad(App.instance.getString(R.string.unable_load_all_dictionaries))
                        onError()
                    })
                }
            dictionaryFilter.OnlyOwn.get() -> {
                    beforeInit()
                    serviceManager.dictionary.getByUserId(userId,{ ownDictionaries ->
                        setDictionaries(ownDictionaries)
                        afterInit()
                    },{
                        EmotionToast.showSad(App.instance.getString(R.string.unable_load_own_dictionaries))
                        onError()
                    })
            }
            !dictionaryFilter.OnlyOwn.get() -> {
                beforeInit()
                serviceManager.dictionary.getPublic(userId,{ publicDictionaries ->
                    setDictionaries(publicDictionaries)
                    afterInit()
                },{
                    EmotionToast.showSad(App.instance.getString(R.string.unable_load_public_dictionaries))
                    onError()
                })
            }
        }
    }

    private fun setDictionaries(dictionaries: List<Dictionary>) {
        displayedDictionaries.clear()
        displayedDictionaries.addAll(0, dictionaryFilter.filter(dictionaries))
        displayedDictionaries.add(filterPosition, Dictionary())
        notifyDataSetChanged()
    }

    private fun setLanguages(languages: List<Language>) {
        dictionaryFilter.sourceLanguages.clear()
        dictionaryFilter.destinationLanguages.clear()
        dictionaryFilter.sourceLanguages.add(Constants.ZERO, App.instance.getString(R.string.source))
        dictionaryFilter.destinationLanguages.add(Constants.ZERO, App.instance.getString(R.string.destination))
        for (language in languages){
            dictionaryFilter.sourceLanguages.add(language.toString())
            dictionaryFilter.destinationLanguages.add(language.toString())
        }
    }

    inner class DictionaryViewHolder(itemView: View, val binding: DictionaryListItemBinding, var descriptionVisibility : Boolean = false) : RecyclerView.ViewHolder(itemView)

    inner class FilterViewHolder(itemView: View, val binding: DictionaryFilterMenuBinding): RecyclerView.ViewHolder(itemView)

    interface OnDictionaryClickedListener {
        fun onDictionaryClicked(dictionary: Dictionary, position: Int)
        fun onDictionaryLongClicked(dictionary: Dictionary): Boolean
    }
}
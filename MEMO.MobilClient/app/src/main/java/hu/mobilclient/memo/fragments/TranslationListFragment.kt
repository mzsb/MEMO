package hu.mobilclient.memo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hu.mobilclient.memo.App
import hu.mobilclient.memo.BR
import hu.mobilclient.memo.R
import hu.mobilclient.memo.adapters.NavigationPagerAdapter
import hu.mobilclient.memo.adapters.TranslationAdapter
import hu.mobilclient.memo.databinding.FragmentTranslationListBinding
import hu.mobilclient.memo.databinding.SpinnerItemDictionaryAccentBinding
import hu.mobilclient.memo.fragments.bases.NavigationFragmentBase
import hu.mobilclient.memo.fragments.interfaces.IFullscreenHandler
import hu.mobilclient.memo.fragments.interfaces.attribute.IAttributeCreationHandler
import hu.mobilclient.memo.fragments.interfaces.attribute.IAttributeDeletionHandler
import hu.mobilclient.memo.fragments.interfaces.attribute.IAttributeUpdateHandler
import hu.mobilclient.memo.fragments.interfaces.dictionary.*
import hu.mobilclient.memo.fragments.interfaces.translation.ITranslationCreationHandler
import hu.mobilclient.memo.fragments.interfaces.translation.ITranslationDeletionHandler
import hu.mobilclient.memo.fragments.interfaces.translation.ITranslationUpdateHandler
import hu.mobilclient.memo.helpers.BindableArrayAdapter
import hu.mobilclient.memo.helpers.Constants
import hu.mobilclient.memo.helpers.EmotionToast
import hu.mobilclient.memo.model.memoapi.Dictionary
import hu.mobilclient.memo.model.memoapi.Translation
import kotlinx.android.synthetic.main.fab_menu.*
import kotlinx.android.synthetic.main.fragment_translation_list.view.*
import java.util.*
import kotlin.collections.ArrayList


class TranslationListFragment: NavigationFragmentBase(),
                               TranslationAdapter.OnTranslationClickedListener,
                               IDictionaryCreationHandler,
                               IDictionaryUpdateHandler,
                               IDictionaryDeletionHandler,
                               IDictionarySelectionHandler,
                               ITranslationCreationHandler,
                               ITranslationUpdateHandler,
                               ITranslationDeletionHandler,
                               ISelectedDictionaryHolder,
                               IAttributeCreationHandler,
                               IAttributeUpdateHandler,
                               IAttributeDeletionHandler,
                               IFullscreenHandler {

    private val adapter: TranslationAdapter = TranslationAdapter()

    var Dictionary : ObservableField<Dictionary> = ObservableField(Dictionary())
    private var dictionaries = ArrayList<Dictionary>()
    private val dictionaryOnItemSelectedListener = DictionaryOnItemSelectedListener()
    private val translationOnScrollListener = TranslationOnScrollListener()

    var IsPublic: ObservableBoolean = ObservableBoolean(false)

    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerView: RecyclerView
    private lateinit var fabMenu: LinearLayout
    private lateinit var dictionaryHolderLinearLayout: LinearLayout
    private lateinit var dictionarySpinner: Spinner
    private lateinit var normalScreenButton: ImageView

    private lateinit var popUp: Animation
    private lateinit var vanish: Animation

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentTranslationListBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_translation_list, container, false)

        binding.setVariable(BR.fragment, this)

        adapter.serviceManager = serviceManager

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.fg_translation_list_rv
        progressBar = view.fg_translation_list_pb

        dictionaryHolderLinearLayout = view.fg_translation_list_ll_dictionary_holder
        dictionarySpinner = view.fg_translation_list_sp_dictionary

        normalScreenButton = view.fg_translation_list_iv_normal_screen

        fabMenu = requireActivity().ac_navigation_ll_fab_menu

        popUp = AnimationUtils.loadAnimation(requireContext(), R.anim.pop_up)
        vanish = AnimationUtils.loadAnimation(requireContext(), R.anim.vanish)

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        getDictionaries {
            if (dictionaries.isNotEmpty() && Dictionary.get()!!.Id == UUID(0,0)) {
                Dictionary.set(dictionaries[Constants.ZERO])
            }
            else{
                if(dictionaries.any { it.Id == Dictionary.get()!!.Id }) {
                    dictionarySpinner.setSelection(dictionaries.indexOf(dictionaries.single { it.Id == Dictionary.get()!!.Id }))
                }
            }
            initializeAdapter()
        }
    }

    private fun getDictionaries(afterInit:()->Unit = {}){
        if(App.isAdmin()){
            serviceManager.dictionary.get({ allDictionary ->
                dictionaries.clear()
                dictionaries.addAll(allDictionary)
                initializeDictionarySpinner()
                afterInit()
            },{
                EmotionToast.showSad(getString(R.string.unable_load_all_dictionaries))
            })
        }
        else{
            serviceManager.dictionary.getByUserId(App.getCurrentUserId(), { ownDictionaries ->
                dictionaries.clear()
                dictionaries.addAll(ownDictionaries)
                initializeDictionarySpinner()
                afterInit()
            },{
                EmotionToast.showSad(getString(R.string.unable_load_own_dictionaries))
            })
        }
    }

    private fun initializeDictionarySpinner(){
        dictionarySpinner.adapter = BindableArrayAdapter<Dictionary, SpinnerItemDictionaryAccentBinding>(App.instance, R.layout.spinner_item_dictionary_accent, dictionaries)

        dictionaryOnItemSelectedListener.init = true
        dictionarySpinner.onItemSelectedListener = dictionaryOnItemSelectedListener
    }

    inner class DictionaryOnItemSelectedListener : AdapterView.OnItemSelectedListener {
        var init = false
        override fun onItemSelected(arg0: AdapterView<*>, arg1: View?, position: Int, id: Long) {
            if(!init){
                Dictionary.set(dictionaries[position])
                initializeAdapter()
            }
            else{
                init = false
            }
        }

        override fun onNothingSelected(arg0: AdapterView<*>) { }
    }

    inner class TranslationOnScrollListener : RecyclerView.OnScrollListener() {
        var onEnd = false
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if(Dictionary.get()!!.isOwn() && !adapter.isFullscreen) {
                if (!recyclerView.canScrollVertically(1)) {
                    if (!onEnd) {
                        fabMenu.startAnimation(vanish)
                        fabMenu.visibility = View.GONE
                        onEnd = true
                    }
                } else {
                    if (onEnd) {
                        fabMenu.startAnimation(popUp)
                        fabMenu.visibility = View.VISIBLE
                        onEnd = false
                    }
                }
            }
        }
    }

    private fun initializeAdapter() {
        adapter.dictionary = Dictionary.get()!!

        recyclerView.addOnScrollListener(translationOnScrollListener)

        val isCurrentPage = activity.isCurrentPage(NavigationPagerAdapter.TRANSLATION_LIST_PAGE_POSITION)

        adapter.initializeAdapter(beforeInit = {
            if(isCurrentPage) {
                fabMenu.visibility = View.GONE
            }
            progressBar.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        }, afterInit = {
            adapter.itemClickListener = this
            progressBar.visibility = View.GONE
            recyclerView.startAnimation(popUp)
            recyclerView.visibility = View.VISIBLE
            if(isCurrentPage && !adapter.isFullscreen) {
                fabMenu.startAnimation(popUp)
                fabMenu.visibility = View.VISIBLE
            }
        }, onError = {
            progressBar.visibility = View.GONE
        })
    }

    fun normalScreenClick(view: View) = activity.onNotFullscreen()

    override fun update() = ifInitialized {
        getDictionaries{
            if(dictionaries.any {it.Id == Dictionary.get()!!.Id}) {
                dictionarySpinner.setSelection(dictionaries.indexOf(dictionaries.single { it.Id == Dictionary.get()!!.Id }))
            }
            initializeAdapter()
        }
    }

    override fun onTranslationLongClicked(translation: Translation): Boolean {
        TranslationFragment(translation, Dictionary.get()!!).show(requireActivity().supportFragmentManager, Constants.TRANSLATION_FRAGMENT_TAG)
        return true
    }

    override fun onNewTranslationClicked(translationId: UUID) = activity.onTranslationCreated(translationId)

    override fun onDictionaryCreated(dictionaryId: UUID) = ifInitialized {
        getDictionaries {
            Dictionary.set(dictionaries.single { it.Id == dictionaryId})
            dictionarySpinner.setSelection(dictionaries.indexOf(Dictionary.get()))
            initializeAdapter()
        }
    }

    override fun onDictionaryUpdated(dictionaryId: UUID)  = ifInitialized {
        getDictionaries {
            Dictionary.set(dictionaries.single { it.Id == dictionaryId})
            dictionarySpinner.setSelection(dictionaries.indexOf(Dictionary.get()))
        }
    }

    override fun onDictionaryDeleted(dictionaryId: UUID) = ifInitialized {
        if(Dictionary.get()?.Id == dictionaryId){
            getDictionaries{
                if(dictionaries.isNotEmpty()){
                    Dictionary.set(dictionaries[Constants.ZERO])
                    dictionarySpinner.setSelection(Constants.ZERO)
                }
                else{
                    Dictionary.set(Dictionary())
                }
                initializeAdapter()
            }
        }
        else{
            update()
        }
    }

    override fun onDictionarySelected(dictionaryId: UUID) = ifInitialized {
        IsPublic.set(false)

        val afterInit = {
            Dictionary.set(dictionaries.single {it.Id == dictionaryId})
            dictionarySpinner.setSelection(dictionaries.indexOf(Dictionary.get()))
            initializeAdapter()
        }

        if (App.isAdmin()) {
            if (dictionaries.none { it.Id == dictionaryId }) {
                serviceManager.dictionary.get({ allDictionary ->
                    this.dictionaries.clear()
                    this.dictionaries.addAll(allDictionary)
                    dictionarySpinner.adapter = BindableArrayAdapter<Dictionary, SpinnerItemDictionaryAccentBinding>(App.instance, R.layout.spinner_item_dictionary_accent, allDictionary)
                    afterInit()
                },{
                    EmotionToast.showSad(getString(R.string.unable_load_all_dictionaries))
                })
            } else {
                afterInit()
            }
        } else {
            serviceManager.dictionary.getByUserId(App.currentUser.Id, { ownDictionaries ->
                if (ownDictionaries.none { it.Id == dictionaryId }) {
                    IsPublic.set(true)
                    serviceManager.dictionary.get(dictionaryId, { dictionary ->
                        Dictionary.set(dictionary)
                        initializeAdapter()
                    }, {
                        EmotionToast.showSad(getString(R.string.unable_to_load_dictionary))
                    })
                } else {
                    this.dictionaries.clear()
                    this.dictionaries.addAll(ownDictionaries)
                    dictionarySpinner.adapter = BindableArrayAdapter<Dictionary, SpinnerItemDictionaryAccentBinding>(App.instance, R.layout.spinner_item_dictionary_accent, ownDictionaries)
                    afterInit()
                }
            }, {
                EmotionToast.showSad(getString(R.string.unable_load_own_dictionaries))
            })
        }
    }

    override fun onTranslationCreated(translationId: UUID) = update()

    override fun onTranslationUpdated(translationId: UUID) = update()

    override fun onTranslationDeleted(translationId: UUID) = update()

    override fun getSelectedDictionaryId(): UUID = Dictionary.get()?.Id ?: UUID(0,0)

    override fun onAttributeCreated(attributeId: UUID) = update()

    override fun onAttributeUpdated(attributeId: UUID) = update()

    override fun onAttributeDeleted(attributeId: UUID) = update()

    override fun onFullscreen() = ifInitialized {
        dictionaryHolderLinearLayout.visibility = View.GONE
        adapter.isFullscreen = true
        normalScreenButton.visibility = View.VISIBLE
        initializeAdapter()
    }

    override fun onNotFullscreen() = ifInitialized {
        dictionaryHolderLinearLayout.visibility = View.VISIBLE
        adapter.isFullscreen = false
        normalScreenButton.visibility = View.GONE
        initializeAdapter()
    }
}

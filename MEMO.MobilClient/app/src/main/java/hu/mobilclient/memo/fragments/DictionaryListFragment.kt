package hu.mobilclient.memo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ProgressBar
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hu.mobilclient.memo.App
import hu.mobilclient.memo.BR
import hu.mobilclient.memo.R
import hu.mobilclient.memo.activities.NavigationActivity
import hu.mobilclient.memo.adapters.DictionaryAdapter
import hu.mobilclient.memo.databinding.FragmentDictionaryListBinding
import hu.mobilclient.memo.fragments.bases.NavigationFragmentBase
import hu.mobilclient.memo.helpers.EmotionToast
import hu.mobilclient.memo.model.Dictionary
import hu.mobilclient.memo.model.Language
import kotlinx.android.synthetic.main.fab_menu.*
import kotlinx.android.synthetic.main.fragment_dictionary_list.view.*


class DictionaryListFragment : NavigationFragmentBase(), DictionaryAdapter.OnDictionaryClickedListener {

    private val adapter : DictionaryAdapter = DictionaryAdapter()

    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerView: RecyclerView

    private lateinit var popUp: Animation

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentDictionaryListBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_dictionary_list, container, false)

        binding.setVariable(BR.fragment, this)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.fg_dictionary_list_rv
        progressBar = view.fg_dictionary_list_pb

        popUp = AnimationUtils.loadAnimation(requireContext(), R.anim.pop_up)

        initializeAdapter()
    }

    private fun initializeAdapter(){
        val fabMenu = requireActivity().ac_navigation_ll_fab_menu
        fabMenu.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        serviceManager.dictionary?.getByUserId(args.getUserId(), { ownDictionaries ->

            recyclerView.layoutManager = GridLayoutManager(context, 1)
            recyclerView.adapter = adapter
            adapter.itemClickListener = this
            adapter.itemLongClickListener = this

            serviceManager.dictionary?.getPublic(args.getUserId(),{ publicDictionaries ->
                serviceManager.language?.get({ languages ->
                    if(App.isAdmin()){
                        serviceManager.dictionary?.get({ allDictionaries ->
                            updateAdapter(ownDictionaries, publicDictionaries, allDictionaries, languages)
                        },::onFailure)
                    }
                    else {
                        updateAdapter(ownDictionaries, publicDictionaries, languages = languages)
                    }
                },::onFailure)
            },::onFailure)
        },{onFailure(getString(R.string.unable_load_dictionaries))})
    }

    private fun onFailure(message: String){
        if(message.isNotEmpty()) {
            EmotionToast.showSad(message)
        }
        progressBar.visibility = View.GONE
    }

    private fun updateAdapter(ownDictionaries: List<Dictionary>,
                              publicDictionaries: List<Dictionary>,
                              allDictionaries: List<Dictionary> = ArrayList(),
                              languages: List<Language>){
        adapter.initializeAdapter(ownDictionaries, publicDictionaries, allDictionaries, languages)
        progressBar.visibility = View.GONE
        recyclerView.startAnimation(popUp)
        recyclerView.visibility = View.VISIBLE
        val fabMenu = requireActivity().ac_navigation_ll_fab_menu
        fabMenu.startAnimation(popUp)
        fabMenu.visibility = View.VISIBLE
    }

    override fun update(){
        initializeAdapter()
    }

    override fun onDictionaryLongClicked(dictionary: Dictionary) {
        DictionaryFragment(dictionary) {(activity as NavigationActivity).onRefresh()}.show(requireActivity().supportFragmentManager, "TAG")
    }

    override fun onDictionaryClicked(dictionary: Dictionary, position: Int) {
        adapter.notifyItemChanged(position)
    }

}
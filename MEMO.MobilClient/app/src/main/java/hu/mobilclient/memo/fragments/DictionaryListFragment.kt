package hu.mobilclient.memo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ProgressBar
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hu.mobilclient.memo.App
import hu.mobilclient.memo.BR
import hu.mobilclient.memo.R
import hu.mobilclient.memo.activities.NavigationActivity
import hu.mobilclient.memo.adapters.DictionaryAdapter
import hu.mobilclient.memo.adapters.NavigationPagerAdapter
import hu.mobilclient.memo.databinding.FragmentDictionaryListBinding
import hu.mobilclient.memo.fragments.bases.NavigationFragmentBase
import hu.mobilclient.memo.fragments.interfaces.dictionary.IDictionaryCreationHandler
import hu.mobilclient.memo.fragments.interfaces.dictionary.IDictionaryDeletionHandler
import hu.mobilclient.memo.fragments.interfaces.dictionary.IDictionaryUpdateHandler
import hu.mobilclient.memo.fragments.interfaces.translation.ITranslationCreationHandler
import hu.mobilclient.memo.fragments.interfaces.translation.ITranslationDeletionHandler
import hu.mobilclient.memo.fragments.interfaces.user.IUserUpdateHandler
import hu.mobilclient.memo.helpers.Constants
import hu.mobilclient.memo.model.memoapi.Dictionary
import kotlinx.android.synthetic.main.fab_menu.*
import kotlinx.android.synthetic.main.fragment_dictionary_list.view.*
import java.util.*


class DictionaryListFragment : NavigationFragmentBase(),
                               DictionaryAdapter.OnDictionaryClickedListener,
                               IDictionaryCreationHandler,
                               IDictionaryUpdateHandler,
                               IDictionaryDeletionHandler,
                               ITranslationCreationHandler,
                               ITranslationDeletionHandler,
                               IUserUpdateHandler {

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

        adapter.serviceManager = serviceManager
        adapter.userId = App.getCurrentUserId()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.fg_dictionary_list_rv
        progressBar = view.fg_dictionary_list_pb

        popUp = AnimationUtils.loadAnimation(requireContext(), R.anim.pop_up)

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        initializeAdapter()
    }

    private fun initializeAdapter(){
        val isCurrentPage = activity.isCurrentPage(NavigationPagerAdapter.DICTIONARY_LIST_PAGE_POSITION)
        val fabMenu = requireActivity().ac_navigation_ll_fab_menu

        adapter.initializeAdapter(beforeInit = {
            if(isCurrentPage){
                fabMenu.visibility = View.GONE
            }
            progressBar.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            }, afterInit = {
            adapter.itemClickListener = this
            adapter.itemLongClickListener = this
            progressBar.visibility = View.GONE
            recyclerView.startAnimation(popUp)
            recyclerView.visibility = View.VISIBLE
            if(isCurrentPage) {
                fabMenu.startAnimation(popUp)
                fabMenu.visibility = View.VISIBLE
            }
        }, onError = {
            progressBar.visibility = View.GONE
        })
    }

    override fun update(){
        initializeAdapter()
    }

    override fun onDictionaryLongClicked(dictionary: Dictionary): Boolean {
        DictionaryFragment(dictionary).show(requireActivity().supportFragmentManager, Constants.DICTIONARY_FRAGMENT_TAG)
        return true
    }

    override fun onDictionaryClicked(dictionary: Dictionary, position: Int) {
        activity.onDictionarySelected(dictionary.Id)
    }

    override fun onDictionaryCreated(dictionaryId: UUID) = update()

    override fun onDictionaryUpdated(dictionaryId: UUID) = update()

    override fun onDictionaryDeleted(dictionaryId: UUID) = update()

    override fun onTranslationCreated(translationId: UUID) = update()

    override fun onTranslationDeleted(translationId: UUID) = update()

    override fun onUserUpdated(userId: UUID) = update()
}
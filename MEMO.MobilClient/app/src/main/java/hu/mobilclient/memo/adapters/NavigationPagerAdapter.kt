package hu.mobilclient.memo.adapters

import android.nfc.Tag
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import hu.mobilclient.memo.App
import hu.mobilclient.memo.R
import hu.mobilclient.memo.fragments.AccountFragment
import hu.mobilclient.memo.fragments.DictionaryListFragment
import hu.mobilclient.memo.fragments.TranslationListFragment
import hu.mobilclient.memo.fragments.bases.NavigationFragmentBase

class NavigationPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    companion object {
        private const val NUM_PAGES = 3

        const val TRANSLATION_LIST_PAGE_POSITION = 0
        const val DICTIONARY_LIST_PAGE_POSITION = 1
        const val ACCOUNT_PAGE_POSITION = 2
    }

    private val fragments: List<NavigationFragmentBase> = listOf(TranslationListFragment(),
                                                                 DictionaryListFragment(),
                                                                 AccountFragment())

    fun forEachOnFragments(function: (fragment: NavigationFragmentBase)->Unit){
        fragments.map{fragment ->
            function(fragment)
        }
    }

    override fun getCount(): Int = NUM_PAGES

    override fun getItem(position: Int): NavigationFragmentBase {
        return when (position) {
            TRANSLATION_LIST_PAGE_POSITION -> fragments[TRANSLATION_LIST_PAGE_POSITION]
            DICTIONARY_LIST_PAGE_POSITION -> fragments[DICTIONARY_LIST_PAGE_POSITION]
            ACCOUNT_PAGE_POSITION -> fragments[ACCOUNT_PAGE_POSITION]
            else -> throw IllegalArgumentException(App.instance.getString(R.string.not_existing_page))
        }
    }
}
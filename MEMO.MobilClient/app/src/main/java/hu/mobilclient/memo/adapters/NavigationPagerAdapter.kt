package hu.mobilclient.memo.adapters

import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import hu.mobilclient.memo.App
import hu.mobilclient.memo.R
import hu.mobilclient.memo.fragments.AccountFragment
import hu.mobilclient.memo.fragments.DictionaryListFragment
import hu.mobilclient.memo.fragments.bases.NavigationFragmentBase

class NavigationPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    companion object {
        private const val NUM_PAGES = 2

        const val DICTIONARY_LIST_PAGE_POSITION = 0
        const val ACCOUNT_PAGE_POSITION = 1
    }

    private val fragment = arrayOf(DictionaryListFragment(),
                                                             AccountFragment())

    override fun getCount(): Int = NUM_PAGES

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) { }

    override fun getItem(position: Int): NavigationFragmentBase {
        return when (position) {
            DICTIONARY_LIST_PAGE_POSITION -> fragment[DICTIONARY_LIST_PAGE_POSITION]
            ACCOUNT_PAGE_POSITION -> fragment[ACCOUNT_PAGE_POSITION]
            else -> throw IllegalArgumentException(App.instance.getString(R.string.not_existing_page))
        }
    }
}
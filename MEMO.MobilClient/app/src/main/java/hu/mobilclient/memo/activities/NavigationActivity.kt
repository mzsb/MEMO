package hu.mobilclient.memo.activities


import android.R.id
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewParent
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.view.GravityCompat
import androidx.core.view.children
import androidx.core.view.forEach
import androidx.drawerlayout.widget.DrawerLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.google.android.material.navigation.NavigationView
import hu.mobilclient.memo.App
import hu.mobilclient.memo.R
import hu.mobilclient.memo.activities.bases.NetworkActivityBase
import hu.mobilclient.memo.adapters.NavigationPagerAdapter
import hu.mobilclient.memo.filters.AttributeFilter
import hu.mobilclient.memo.filters.DictionaryFilter
import hu.mobilclient.memo.filters.UserFilter
import hu.mobilclient.memo.fragments.AttributeFragment
import hu.mobilclient.memo.fragments.DictionaryFragment
import hu.mobilclient.memo.fragments.NetworkSettingFragment
import hu.mobilclient.memo.fragments.bases.NavigationFragmentBase
import hu.mobilclient.memo.fragments.interfaces.IUpdateable
import hu.mobilclient.memo.helpers.Constants
import hu.mobilclient.memo.helpers.EmotionToast
import hu.mobilclient.memo.helpers.IdHolder
import hu.mobilclient.memo.helpers.NavigationArguments
import hu.mobilclient.memo.model.Dictionary
import hu.mobilclient.memo.model.Translation
import kotlinx.android.synthetic.main.activity_navigation.*
import kotlinx.android.synthetic.main.app_bar_navigation.*
import kotlinx.android.synthetic.main.content_navigation.*
import kotlinx.android.synthetic.main.fab_menu.*
import java.util.*
import kotlin.collections.ArrayList


class NavigationActivity : NetworkActivityBase(), SwipeRefreshLayout.OnRefreshListener {

    val args:  NavigationArguments = NavigationArguments()

    private lateinit var idHolder : IdHolder<UUID>
    private lateinit var navigationViewPage : ViewPager
    private lateinit var refreshLayout: SwipeRefreshLayout
    private lateinit var navigationView: NavigationView
    private lateinit var drawerLayout: DrawerLayout

    private var isFabMenuOpen = false

    private lateinit var fabOpen: Animation
    private lateinit var fabClose: Animation
    private lateinit var fabRotateOpen: Animation
    private lateinit var fabRotateClose: Animation

    private var dictionaryFragment: DictionaryFragment? = null
    private var attributeFragment: AttributeFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)

        fabClose = AnimationUtils.loadAnimation(this, R.anim.fab_close)
        fabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open)
        fabRotateOpen = AnimationUtils.loadAnimation(this, R.anim.fab_rotate_open)
        fabRotateClose = AnimationUtils.loadAnimation(this, R.anim.fab_rotate_close)

        navigationView = ac_navigation_nv
        drawerLayout = ac_navigation_dl

        refreshLayout = ac_navigation_rl
        refreshLayout.setOnRefreshListener(this)
        refreshLayout.setColorSchemeResources(R.color.accent)
        refreshLayout.setProgressBackgroundColorSchemeResource(R.color.primary_light)

        navigationViewPage = ac_navigation_vp
        navigationViewPage.adapter = NavigationPagerAdapter(supportFragmentManager)

        val fabMenu = ac_navigation_ll_fab_menu
        fabMenu.setOnClickListener {
            val fabCreate = fab_create
            if(isFabMenuOpen){
                fabCreate.callOnClick()
            }
        }
        fabMenu.isClickable = false
        fabMenu.visibility = View.GONE

        setSupportActionBar(toolbar)
        supportActionBar?.title = Constants.EMPTYSTRING
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_navigation_menu_34dp)

        val userId = UUID.fromString(intent.extras?.getString(Constants.USERID))
        serviceManager.user?.get(userId, callback = {
            App.currentUser = it
        })
        args.putUserId(userId)

        serviceManager.dictionary?.getFastAccessible(userId, callback = ::getFastAccessibleDictionariesSuccess)

        navigationViewPage.addOnPageChangeListener(object : OnPageChangeListener {
            val fabCreate = fab_create
            val fabCreateDictionary = fab_create_dictionary
            val fabCreateTranslation = fab_create_translation
            val fabCreateAttribute = fab_create_attribute
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                if(position == NavigationPagerAdapter.ACCOUNT_PAGE_POSITION){
                    fabMenu.alpha = 1 - positionOffset
                }
                if(fabMenu.alpha < 0.6){
                    fabCreate.isEnabled = false
                    if(isFabMenuOpen){
                        fabCreate.callOnClick()
                    }
                }
                else{
                    fabCreate.isEnabled = true
                    fabCreateDictionary.isEnabled = true
                    fabCreateTranslation.isEnabled = true
                    fabCreateAttribute.isEnabled = true
                }
            }
            override fun onPageSelected(position: Int) {
                navigationView.menu.getItem(position).isChecked = true
                supportActionBar?.title = getPageName(position)
            }
        })

        navigationView.setNavigationItemSelectedListener(::navigationItemSelectedListener)
        navigationView.menu.findItem(R.id.nav_dictionarylist).isChecked = true

        val invalidKeys = ArrayList<Int>()
        for (menuItem in navigationView.menu.children){
            invalidKeys.add(menuItem.itemId)
        }
        idHolder = IdHolder(invalidKeys)

        supportActionBar?.title = getPageName(NavigationPagerAdapter.DICTIONARY_LIST_PAGE_POSITION)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            id.home -> drawerLayout.openDrawer(GravityCompat.START)
            R.id.refresh -> {
                val currentItem = navigationViewPage.currentItem
                navigationViewPage.adapter = NavigationPagerAdapter(supportFragmentManager)
                navigationViewPage.setCurrentItem(currentItem, false)

                serviceManager.dictionary?.getFastAccessible(args.getUserId(), callback = ::getFastAccessibleDictionariesSuccess)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun navigationItemSelectedListener(menuItem: MenuItem) : Boolean {
        when(menuItem.itemId){
            R.id.nav_dictionarylist -> navigationViewPage.setCurrentItem(NavigationPagerAdapter.DICTIONARY_LIST_PAGE_POSITION, true)
            R.id.nav_account -> navigationViewPage.setCurrentItem(NavigationPagerAdapter.ACCOUNT_PAGE_POSITION, true)
            R.id.nav_logout -> {
                getSharedPreferences(Constants.AUTHDATA, 0).edit().clear().apply()
                startActivity(Intent(this, LoginActivity::class.java))
                DictionaryFilter.clearFilter()
                AttributeFilter.clearFilter()
                UserFilter.clearFilter()
                finish()
            }
            in idHolder -> {
                navigationView.menu.findItem(menuItem.itemId).isChecked = true
                val i = idHolder[menuItem.itemId]
            }
            else -> throw IllegalArgumentException(getString(R.string.not_existing_page))
        }

        drawerLayout.closeDrawers()

        return true
    }

    fun closeNavigationDrawer(view: View){
        ac_navigation_dl.closeDrawers()
    }

    private fun getPageName(pagePosition: Int) : String {
        return getString(when(pagePosition){
            NavigationPagerAdapter.DICTIONARY_LIST_PAGE_POSITION -> R.string.dictionaries
            NavigationPagerAdapter.ACCOUNT_PAGE_POSITION -> R.string.account
            else -> throw IllegalArgumentException(getString(R.string.not_existing_page))
        })
    }


    private fun getFastAccessibleDictionariesSuccess(dictionaries: List<Dictionary>){
        for(key in idHolder.getKeys()){
            navigationView.menu.removeItem(key)
        }
        idHolder.clear()
        for (d in dictionaries) {
            idHolder.putId(d.Id)
            navigationView.menu.add(R.id.group_dictionaries, idHolder.getKey(d.Id), Menu.NONE, d.Name).isCheckable = true
        }
    }

    fun createClick(view: View){
        val fabMenu = ac_navigation_ll_fab_menu
        val fabCreate = fab_create
        val fabCreateDictionary = fab_create_dictionary
        val fabCreateTranslation = fab_create_translation
        val fabCreateAttribute = fab_create_attribute

        if (isFabMenuOpen) {
            fabCreateTranslation.startAnimation(fabClose)
            fabCreateDictionary.startAnimation(fabClose)
            fabCreateAttribute.startAnimation(fabClose)
            fabCreate.startAnimation(fabRotateClose)
            fabCreateTranslation.isClickable = false
            fabCreateDictionary.isClickable = false
            fabCreateAttribute.isClickable = false
            fabMenu.isClickable = false
            isFabMenuOpen = false
        } else {
            fabCreateTranslation.startAnimation(fabOpen)
            fabCreateDictionary.startAnimation(fabOpen)
            fabCreateAttribute.startAnimation(fabOpen)
            fabCreate.startAnimation(fabRotateOpen)
            fabCreateTranslation.isClickable = true
            fabCreateDictionary.isClickable = true
            fabCreateAttribute.isClickable = true
            fabMenu.isClickable = true
            isFabMenuOpen = true
        }
    }

    override fun onRefresh() {
        serviceManager.dictionary?.getFastAccessible(args.getUserId(), callback = ::getFastAccessibleDictionariesSuccess)

        getCurrentPage().update()

        refreshLayout.isRefreshing = false
    }

    private fun getCurrentPage(): NavigationFragmentBase {
        return (navigationViewPage.adapter as NavigationPagerAdapter).getItem(navigationViewPage.currentItem)
    }

    private fun getPage(index: Int): NavigationFragmentBase {
        return (navigationViewPage.adapter as NavigationPagerAdapter).getItem(index)
    }

    fun createDictionaryClick(view: View){
        val fragment = dictionaryFragment?: DictionaryFragment(OkCallback = {getPage(NavigationPagerAdapter.DICTIONARY_LIST_PAGE_POSITION).update(); dictionaryFragment = null})
        dictionaryFragment = fragment
        fragment.show(supportFragmentManager, "TAG")
    }

    fun createAttributeClick(view: View){
        val fragment = attributeFragment?: AttributeFragment(OkCallback = {getPage(NavigationPagerAdapter.ACCOUNT_PAGE_POSITION).update(); attributeFragment = null})
        attributeFragment = fragment
        fragment.show(supportFragmentManager, "TAG")
    }
}

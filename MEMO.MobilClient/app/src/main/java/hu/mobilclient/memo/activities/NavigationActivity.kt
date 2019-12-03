package hu.mobilclient.memo.activities


import android.R.id
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.view.GravityCompat
import androidx.core.view.children
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
import hu.mobilclient.memo.filters.TranslationFilter
import hu.mobilclient.memo.filters.UserFilter
import hu.mobilclient.memo.fragments.AttributeFragment
import hu.mobilclient.memo.fragments.DictionaryFragment
import hu.mobilclient.memo.fragments.TranslationFragment
import hu.mobilclient.memo.fragments.TranslationListFragment
import hu.mobilclient.memo.fragments.bases.NavigationFragmentBase
import hu.mobilclient.memo.fragments.interfaces.attribute.IAttributeCreationHandler
import hu.mobilclient.memo.fragments.interfaces.attribute.IAttributeDeletionHandler
import hu.mobilclient.memo.fragments.interfaces.attribute.IAttributeUpdateHandler
import hu.mobilclient.memo.fragments.interfaces.dictionary.*
import hu.mobilclient.memo.fragments.interfaces.translation.ITranslationCreationHandler
import hu.mobilclient.memo.fragments.interfaces.translation.ITranslationDeletionHandler
import hu.mobilclient.memo.fragments.interfaces.translation.ITranslationUpdateHandler
import hu.mobilclient.memo.fragments.interfaces.user.IUserDeletionHandler
import hu.mobilclient.memo.fragments.interfaces.user.IUserUpdateHandler
import hu.mobilclient.memo.helpers.Constants
import hu.mobilclient.memo.helpers.EmotionToast
import hu.mobilclient.memo.helpers.IdHolder
import hu.mobilclient.memo.model.memoapi.Dictionary
import hu.mobilclient.memo.model.enums.LanguageCode
import kotlinx.android.synthetic.main.activity_navigation.*
import kotlinx.android.synthetic.main.app_bar_navigation.*
import kotlinx.android.synthetic.main.content_navigation.*
import kotlinx.android.synthetic.main.fab_menu.*
import java.util.*
import kotlin.collections.ArrayList


class NavigationActivity : NetworkActivityBase(),
                           SwipeRefreshLayout.OnRefreshListener,
                           IDictionaryCreationHandler,
                           IDictionaryUpdateHandler,
                           IDictionaryDeletionHandler,
                           IDictionarySelectionHandler,
                           ITranslationCreationHandler,
                           ITranslationUpdateHandler,
                           ITranslationDeletionHandler,
                           IAttributeCreationHandler,
                           IAttributeUpdateHandler,
                           IAttributeDeletionHandler,
                           IUserUpdateHandler,
                           IUserDeletionHandler {

    private lateinit var idHolder : IdHolder<UUID>
    private lateinit var navigationViewPager : ViewPager
    private lateinit var viewPagerAdapter: NavigationPagerAdapter
    private lateinit var refreshLayout: SwipeRefreshLayout
    private lateinit var navigationView: NavigationView
    private lateinit var drawerLayout: DrawerLayout

    private var isFabMenuOpen = false
    private var isFastAccess = false

    private lateinit var fabOpen: Animation
    private lateinit var fabClose: Animation
    private lateinit var fabRotateOpen: Animation
    private lateinit var fabRotateClose: Animation

    private var dictionaryFragment: DictionaryFragment? = null
    private var attributeFragment: AttributeFragment? = null
    private var translationFragment: TranslationFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)

        val userId = UUID.fromString(intent.extras?.getString(Constants.USERID))
        serviceManager.user.get(userId, { user->
            App.currentUser = user

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

            navigationViewPager = ac_navigation_vp
            navigationViewPager.adapter = NavigationPagerAdapter(supportFragmentManager)
            viewPagerAdapter = (navigationViewPager.adapter as NavigationPagerAdapter)

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
            supportActionBar?.title = Constants.EMPTY_STRING
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_navigation_menu_34dp)

            serviceManager.dictionary.getFastAccessible(userId, ::getFastAccessibleDictionariesSuccess)

            navigationViewPager.addOnPageChangeListener(object : OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {}
                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) { }
                override fun onPageSelected(position: Int) {
                    navigationView.menu.getItem(position).isChecked = true
                    supportActionBar?.title = getPageName(position)
                }
            })

            navigationView.setNavigationItemSelectedListener(::navigationItemSelectedListener)
            navigationView.menu.findItem(R.id.nav_translations).isChecked = true

            val invalidKeys = ArrayList<Int>()
            for (menuItem in navigationView.menu.children){
                invalidKeys.add(menuItem.itemId)
            }
            idHolder = IdHolder(invalidKeys)

            supportActionBar?.title = getPageName(NavigationPagerAdapter.TRANSLATION_LIST_PAGE_POSITION)
        },errorCallback = {
            EmotionToast.showSad(getString(R.string.unable_load_user))
            logout()
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            id.home -> drawerLayout.openDrawer(GravityCompat.START)
            R.id.refresh -> {
                serviceManager.translation.translate("kutya", LanguageCode.HU.name, LanguageCode.EN.name,callback = {
                    val i = it
                    it
                })
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun navigationItemSelectedListener(menuItem: MenuItem) : Boolean {
        when(menuItem.itemId){
            R.id.nav_translations -> navigationViewPager.setCurrentItem(NavigationPagerAdapter.TRANSLATION_LIST_PAGE_POSITION, true)
            R.id.nav_dictionarylist -> navigationViewPager.setCurrentItem(NavigationPagerAdapter.DICTIONARY_LIST_PAGE_POSITION, true)
            R.id.nav_account -> navigationViewPager.setCurrentItem(NavigationPagerAdapter.ACCOUNT_PAGE_POSITION, true)
            R.id.nav_logout -> logout()
            in idHolder -> {
                navigationView.menu.findItem(menuItem.itemId).isChecked = true
                isFastAccess = true
                onDictionarySelected(idHolder[menuItem.itemId])
            }
            else -> throw IllegalArgumentException(getString(R.string.not_existing_page))
        }

        drawerLayout.closeDrawers()

        return true
    }

    private fun logout(){
        getSharedPreferences(Constants.AUTHENTICATION_DATA, 0).edit().clear().apply()
        startActivity(Intent(this, LoginActivity::class.java))
        DictionaryFilter.clearFilter()
        AttributeFilter.clearFilter()
        UserFilter.clearFilter()
        TranslationFilter.clearFilter()
        finish()
    }

    fun closeNavigationDrawer(view: View){
        ac_navigation_dl.closeDrawers()
    }

    private fun getPageName(pagePosition: Int) : String {
        return getString(when(pagePosition){
            NavigationPagerAdapter.TRANSLATION_LIST_PAGE_POSITION -> R.string.translations
            NavigationPagerAdapter.DICTIONARY_LIST_PAGE_POSITION -> R.string.dictionaries
            NavigationPagerAdapter.ACCOUNT_PAGE_POSITION -> R.string.account
            else -> throw IllegalArgumentException(getString(R.string.not_existing_page))
        })
    }

    private fun getFastAccessibleDictionariesSuccess(dictionaries: List<Dictionary>){
        navigationView.menu.findItem(R.id.nav_fast_access).isVisible = dictionaries.isNotEmpty()
        for (key in idHolder.getKeys()) {
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
        serviceManager.dictionary.getFastAccessible(App.getCurrentUserId(), ::getFastAccessibleDictionariesSuccess)

        getCurrentPage().update()

        refreshLayout.isRefreshing = false
    }

    fun isCurrentPage(index: Int): Boolean{
        return navigationViewPager.currentItem == index
    }

    private fun getCurrentPage(): NavigationFragmentBase {
        return (navigationViewPager.adapter as NavigationPagerAdapter).getItem(navigationViewPager.currentItem)
    }

    fun createDictionaryClick(view: View){
        val fragment = dictionaryFragment?: DictionaryFragment()
        dictionaryFragment = fragment
        fragment.show(supportFragmentManager, Constants.DICTIONARY_FRAGMENT_TAG)
        fab_create.callOnClick()
    }

    fun createTranslationClick(view: View){
        if(App.currentUser.DictionaryCount.toInt() != 0) {
            val fragment = translationFragment ?: TranslationFragment()
            translationFragment = fragment
            viewPagerAdapter.forEachOnFragments {navigationFragment ->
                if(navigationFragment is ISelectedDictionaryHolder && navigationFragment is TranslationListFragment){
                    fragment.setDictionarySpinnerSelection(navigationFragment.getSelectedDictionaryId())
                }
            }
            fragment.show(supportFragmentManager, Constants.TRANSLATION_FRAGMENT_TAG)
            fab_create.callOnClick()
        }
        else{
            EmotionToast.showHelp(getString(R.string.create_dictionary_first))
        }
    }

    fun createAttributeClick(view: View){
        val fragment = attributeFragment?: AttributeFragment()
        attributeFragment = fragment
        fragment.show(supportFragmentManager, Constants.ATTRIBUTE_FRAGMENT_TAG)
        fab_create.callOnClick()
    }

    override fun onDictionarySelected(dictionaryId: UUID) {
        navigationViewPager.setCurrentItem(NavigationPagerAdapter.TRANSLATION_LIST_PAGE_POSITION, true)
        navigationView.menu.getItem(Constants.ZERO).isChecked = !isFastAccess
        isFastAccess = false
        viewPagerAdapter.forEachOnFragments {navigationFragment ->
            if(navigationFragment is IDictionarySelectionHandler){
                navigationFragment.onDictionarySelected(dictionaryId)
            }
        }
    }

    override fun onDictionaryCreated(dictionaryId: UUID) {
        dictionaryFragment = null
        serviceManager.dictionary.getFastAccessible(App.getCurrentUserId(), ::getFastAccessibleDictionariesSuccess)
        navigationViewPager.setCurrentItem(NavigationPagerAdapter.TRANSLATION_LIST_PAGE_POSITION, true)
        viewPagerAdapter.forEachOnFragments {navigationFragment ->
            if(navigationFragment is IDictionaryCreationHandler){
                navigationFragment.onDictionaryCreated(dictionaryId)
            }
        }
    }

    override fun onDictionaryUpdated(dictionaryId: UUID) {
        serviceManager.dictionary.getFastAccessible(App.getCurrentUserId(), ::getFastAccessibleDictionariesSuccess)
        viewPagerAdapter.forEachOnFragments {navigationFragment ->
            if(navigationFragment is IDictionaryUpdateHandler){
                navigationFragment.onDictionaryUpdated(dictionaryId)
            }
        }
    }

    override fun onDictionaryDeleted(dictionaryId: UUID) {
        viewPagerAdapter.forEachOnFragments {navigationFragment ->
            if(navigationFragment is IDictionaryDeletionHandler){
                navigationFragment.onDictionaryDeleted(dictionaryId)
            }
        }
    }

    override fun onTranslationCreated(translationId: UUID) {
        translationFragment = null
        viewPagerAdapter.forEachOnFragments {navigationFragment ->
            if(navigationFragment is ITranslationCreationHandler){
                navigationFragment.onTranslationCreated(translationId)
            }
        }
    }

    override fun onTranslationUpdated(translationId: UUID) {
        viewPagerAdapter.forEachOnFragments {navigationFragment ->
            if(navigationFragment is ITranslationUpdateHandler){
                navigationFragment.onTranslationUpdated(translationId)
            }
        }
    }

    override fun onTranslationDeleted(translationId: UUID) {
        viewPagerAdapter.forEachOnFragments {navigationFragment ->
            if(navigationFragment is ITranslationDeletionHandler){
                navigationFragment.onTranslationDeleted(translationId)
            }
        }
    }

    override fun onAttributeCreated(attributeId: UUID) {
        attributeFragment = null
        viewPagerAdapter.forEachOnFragments {navigationFragment ->
            if(navigationFragment is IAttributeCreationHandler){
                navigationFragment.onAttributeCreated(attributeId)
            }
        }
    }

    override fun onAttributeUpdated(attributeId: UUID) {
        viewPagerAdapter.forEachOnFragments {navigationFragment ->
            if(navigationFragment is IAttributeUpdateHandler){
                navigationFragment.onAttributeUpdated(attributeId)
            }
        }
    }

    override fun onAttributeDeleted(attributeId: UUID) {
        viewPagerAdapter.forEachOnFragments {navigationFragment ->
            if(navigationFragment is IAttributeDeletionHandler){
                navigationFragment.onAttributeDeleted(attributeId)
            }
        }
    }

    override fun onUserUpdated(userId: UUID) {
        viewPagerAdapter.forEachOnFragments {navigationFragment ->
            if(navigationFragment is IUserUpdateHandler){
                navigationFragment.onUserUpdated(userId)
            }
        }
    }

    override fun onUserDeleted(userId: UUID) {
        viewPagerAdapter.forEachOnFragments {navigationFragment ->
            if(navigationFragment is IUserDeletionHandler){
                navigationFragment.onUserDeleted(userId)
            }
        }
        logout()
    }
}

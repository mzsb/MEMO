package hu.mobilclient.memo.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
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
import hu.mobilclient.memo.activities.LoginActivity
import hu.mobilclient.memo.adapters.AttributeAdapter
import hu.mobilclient.memo.adapters.NavigationPagerAdapter
import hu.mobilclient.memo.adapters.UserAdapter
import hu.mobilclient.memo.databinding.FragmentAccountBinding
import hu.mobilclient.memo.filters.AttributeFilter
import hu.mobilclient.memo.filters.DictionaryFilter
import hu.mobilclient.memo.filters.TranslationFilter
import hu.mobilclient.memo.filters.UserFilter
import hu.mobilclient.memo.fragments.bases.NavigationFragmentBase
import hu.mobilclient.memo.helpers.Constants
import hu.mobilclient.memo.helpers.EmotionToast
import hu.mobilclient.memo.model.memoapi.Attribute
import hu.mobilclient.memo.model.memoapi.Dictionary
import hu.mobilclient.memo.model.memoapi.User
import hu.mobilclient.memo.activities.MemoryGameActivity
import hu.mobilclient.memo.data.PracticeDatabase
import hu.mobilclient.memo.fragments.interfaces.attribute.IAttributeCreationHandler
import hu.mobilclient.memo.fragments.interfaces.attribute.IAttributeDeletionHandler
import hu.mobilclient.memo.fragments.interfaces.attribute.IAttributeUpdateHandler
import hu.mobilclient.memo.fragments.interfaces.dictionary.IDictionaryCreationHandler
import hu.mobilclient.memo.fragments.interfaces.dictionary.IDictionaryDeletionHandler
import hu.mobilclient.memo.fragments.interfaces.user.IUserUpdateHandler
import hu.mobilclient.memo.model.enums.PracticeType
import hu.mobilclient.memo.network.ApiService
import kotlinx.android.synthetic.main.fab_menu.*
import kotlinx.android.synthetic.main.fragment_account.view.*
import java.util.*


class AccountFragment: NavigationFragmentBase(),
                       AttributeAdapter.OnAttributeClickedListener,
                       UserAdapter.OnUserClickedListener,
                       IDictionaryCreationHandler,
                       IDictionaryDeletionHandler,
                       IAttributeCreationHandler,
                       IAttributeUpdateHandler,
                       IAttributeDeletionHandler,
                       IUserUpdateHandler {

    private val attributeAdapter : AttributeAdapter = AttributeAdapter()
    private val userAdapter : UserAdapter = UserAdapter()

    var user: ObservableField<User> = ObservableField()
    var dictionary: Dictionary = Dictionary()

    private var originalUserName = Constants.EMPTY_STRING
    private var originalEmail = Constants.EMPTY_STRING
    var IsUpdateUserName: ObservableBoolean = ObservableBoolean(false)
    var IsUpdateEmail: ObservableBoolean = ObservableBoolean(false)
    var IsUserDataLoaded: ObservableBoolean = ObservableBoolean(false)
    var IsAttributesVisible: ObservableBoolean = ObservableBoolean(false)
    var IsUsersVisible: ObservableBoolean = ObservableBoolean(false)
    var IsDelete: ObservableBoolean = ObservableBoolean(false)

    private lateinit var emailEditText: EditText
    private lateinit var userNameEditText: EditText

    private lateinit var practiceTypeSpinner: Spinner

    private lateinit var attributesProgressBar: ProgressBar
    private lateinit var attributesRecyclerView: RecyclerView

    private lateinit var userDataProgressBar: ProgressBar
    private lateinit var usersProgressBar: ProgressBar
    private lateinit var usersRecyclerView: RecyclerView

    private lateinit var popUp: Animation

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentAccountBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_account, container, false)

        binding.setVariable(BR.fragment, this)

        attributeAdapter.serviceManager = serviceManager
        attributeAdapter.userId = App.getCurrentUserId()

        userAdapter.serviceManager = serviceManager
        userAdapter.userId = App.getCurrentUserId()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        attributesRecyclerView = view.fg_account_rv_attribute_list
        attributesProgressBar = view.fg_account_pb_attribute_list

        userDataProgressBar = view.fg_account_pb_user_data

        usersRecyclerView = view.fg_account_rv_user_list
        usersProgressBar = view.fg_account_pb_user_list

        popUp = AnimationUtils.loadAnimation(requireContext(), R.anim.pop_up)

        emailEditText = view.fg_account_et_email
        userNameEditText = view.fg_account_et_user_name

        practiceTypeSpinner = view.fg_account_sp_practice_type

        val attributesHiderButton = view.fg_account_iv_attribute_list_hider
        val attributesHolder = view.fg_account_rl_attribute_list_holder
        val attributesLinearLayout = view.fg_account_ll_attributes
        val usersHiderButton = view.fg_account_iv_user_list_hider
        val usersHolder = view.fg_account_rl_user_list_holder
        val usersLinearLayout = view.fg_account_ll_users

        val showAttributes = {
            attributesHolder.visibility = if (IsAttributesVisible.get()) {
                attributesRecyclerView.startAnimation(popUp)
                View.VISIBLE
            } else View.GONE

            if(IsAttributesVisible.get()){
                usersLinearLayout.visibility = View.GONE
                usersHolder.visibility = View.GONE
            }
            else{
                usersLinearLayout.visibility = View.VISIBLE
            }
        }
        showAttributes()

        attributesHiderButton.setOnClickListener {
            IsAttributesVisible.set(!IsAttributesVisible.get())
            (it as ImageView).setImageResource(if (IsAttributesVisible.get()) R.drawable.ic_drop_up_24dp else R.drawable.ic_drop_down_24dp)
            showAttributes()
        }

        val showUsers = {
            usersHolder.visibility = if (IsUsersVisible.get()) {
                usersRecyclerView.startAnimation(popUp)
                View.VISIBLE
            }
            else View.GONE

            if(IsUsersVisible.get()){
                attributesHolder.visibility = View.GONE
                attributesLinearLayout.visibility = View.GONE
            }
            else{
                attributesLinearLayout.visibility = View.VISIBLE
            }
        }
        showUsers()

        usersHiderButton.setOnClickListener {
            IsUsersVisible.set(!IsUsersVisible.get())
            (it as ImageView).setImageResource(if (IsUsersVisible.get()) R.drawable.ic_drop_up_24dp else R.drawable.ic_drop_down_24dp)
            showUsers()
        }

        usersRecyclerView.layoutManager = LinearLayoutManager(context)
        usersRecyclerView.adapter = userAdapter

        attributesRecyclerView.layoutManager = LinearLayoutManager(context)
        attributesRecyclerView.adapter = attributeAdapter

        userDataProgressBar.visibility = View.GONE

        IsUserDataLoaded.set(true)
        user.set(App.currentUser)
        originalUserName = user.get()!!.UserName
        originalEmail = user.get()!!.Email
        initializeAttributeAdapter()
        initializeUserAdapter()
        initializePractice()
    }

    private fun initializePractice(){
        practiceTypeSpinner.adapter = ArrayAdapter(App.instance, R.layout.spinner_item_accent, PracticeType.values())
        practiceTypeSpinner.visibility = View.VISIBLE
        practiceTypeSpinner.setSelection(Constants.ZERO)
    }

    private fun initializeUserData(){
        IsUserDataLoaded.set(false)

        userDataProgressBar.visibility = View.VISIBLE
        serviceManager.user.get(App.getCurrentUserId(), { user ->
            App.currentUser = user
            this.user.set(user)
            originalUserName = user.UserName
            originalEmail = user.Email
            userDataProgressBar.visibility = View.GONE
            IsUserDataLoaded.set(true)
        }, {
            IsUserDataLoaded.set(false)
            EmotionToast.showSad(getString(R.string.unable_load_user))
            userDataProgressBar.visibility = View.GONE
        })
    }

    fun updateUserNameClick(view: View){
        originalUserName = user.get()!!.UserName
        IsUpdateUserName.set(!IsUpdateUserName.get())
    }

    fun updateUserNameCancelClick(view: View){
        user.get()!!.UserName = originalUserName
        user.set(user.get())
        userNameEditText.setText(user.get()!!.UserName)
        IsUpdateUserName.set(!IsUpdateUserName.get())
    }

    fun updateEmailClick(view: View){
        originalEmail = user.get()!!.Email
        IsUpdateEmail.set(!IsUpdateEmail.get())
    }

    fun updateEmailCancelClick(view: View){
        user.get()!!.Email = originalEmail
        user.set(user.get())
        emailEditText.setText(user.get()!!.Email)
        IsUpdateEmail.set(!IsUpdateEmail.get())
    }

    fun updateClick(view: View){
        if(IsDelete.get()){
            SureFragment(Message = getString(R.string.account_delete_message),
                    OkCallback = {
                        serviceManager.user.delete(user.get()!!.Id,{
                            activity.onUserDeleted(user.get()!!.Id)
                            activity.logout()
                        }, {
                            EmotionToast.showSad(getString(R.string.account_delete_fail))
                        })
                    }).show(requireActivity().supportFragmentManager, Constants.SURE_FRAGMENT_TAG)
        }
        else {
            if(user.get()!!.UserName == originalUserName && user.get()!!.Email == originalEmail ){
                EmotionToast.showHelp(getString(R.string.no_changes))
            }
            else {
                if (isValid()) {
                    emailEditText.isEnabled = false
                    userNameEditText.isEnabled = false
                    serviceManager.user.update(user.get()!!, {
                        activity.onUserUpdated(user.get()!!.Id)
                        EmotionToast.showSuccess()
                    }, {
                        emailEditText.isEnabled = true
                        userNameEditText.isEnabled = true
                        view.isEnabled = true
                        EmotionToast.showError(getString(R.string.account_update_fail))
                    })
                }
            }
        }
    }

    fun practiceClick(view: View){
        if(user.get()!!.DictionaryCount.toInt() != Constants.ZERO || user.get()!!.ViewedDictionaryCount.toInt() != Constants.ZERO) {
            when (practiceTypeSpinner.selectedItem) {
                PracticeType.MEMORYGAME -> {
                    startActivity(Intent(requireContext(), MemoryGameActivity::class.java))
                }
            }
        }
        else{
            EmotionToast.showHelp(getString(R.string.create_or_view_one_dictionary))
        }
    }

    private fun EditText.isNotEmpty(): Boolean{
        if(this.text.isEmpty()){
            this.requestFocus()
            this.error = getString(R.string.required_field)
            return false
        }
        return true
    }

    private fun EditText.isValidEmail(): Boolean{
        if(!Patterns.EMAIL_ADDRESS.matcher(this.text).matches()){
            this.requestFocus()
            this.error = getString(R.string.invalid_email)
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

    private fun isValid() =
                    userNameEditText.isNotEmpty() &&
                    !userNameEditText.tooLong(Constants.USER_NAME_MAX_LENGTH, getString(R.string.username_too_long, Constants.USER_NAME_MAX_LENGTH)) &&
                    emailEditText.isNotEmpty() &&
                    !emailEditText.tooLong(Constants.USER_EMAIL_MAX_LENGTH, getString(R.string.email_too_long, Constants.USER_EMAIL_MAX_LENGTH)) &&
                    emailEditText.isValidEmail()

    private fun initializeAttributeAdapter(){
        val isCurrentPage = activity.isCurrentPage(NavigationPagerAdapter.ACCOUNT_PAGE_POSITION)

        val fabMenu = requireActivity().ac_navigation_ll_fab_menu

        attributeAdapter.initializeAdapter(beforeInit = {
            if(isCurrentPage) {
                fabMenu.visibility = View.GONE
            }
            attributesProgressBar.visibility = View.VISIBLE
            attributesRecyclerView.visibility = View.GONE
        }, afterInit = {
            attributeAdapter.itemLongClickListener = this
            attributesProgressBar.visibility = View.GONE
            attributesRecyclerView.startAnimation(popUp)
            attributesRecyclerView.visibility = View.VISIBLE
            if(isCurrentPage) {
                fabMenu.startAnimation(popUp)
                fabMenu.visibility = View.VISIBLE
            }
        }, onError = {
            attributesProgressBar.visibility = View.GONE
        })
    }

    private fun initializeUserAdapter(){
        userAdapter.initializeAdapter(beforeInit = {
            usersProgressBar.visibility = View.VISIBLE
            usersRecyclerView.visibility = View.GONE
        }, afterInit = {
            userAdapter.itemLongClickListener = this
            usersProgressBar.visibility = View.GONE
            usersRecyclerView.startAnimation(popUp)
            usersRecyclerView.visibility = View.VISIBLE
        }, onError = {
            usersProgressBar.visibility = View.GONE
        })
    }

    override fun update() = ifInitialized {
        initializeUserData()
        initializeAttributeAdapter()
        initializeUserAdapter()
        initializePractice()
    }

    override fun onAttributeLongClicked(attribute: Attribute): Boolean {
        AttributeFragment(attribute).show(requireActivity().supportFragmentManager, Constants.ATTRIBUTE_FRAGMENT_TAG)
        return true
    }

    override fun onUserLongClicked(user: User): Boolean {
        if(App.isAdmin()) {
            UserFragment(user).show(requireActivity().supportFragmentManager, Constants.USER_FRAGMENT_TAG)
        }
        return true
    }

    override fun onDictionaryDeleted(dictionaryId: UUID) = ifInitialized {
        initializeUserData()
        initializeUserAdapter()

        val savedDictionaryId = UUID.fromString(
                activity.getSharedPreferences(Constants.MEMORYGAME_DATA, 0)
                        .getString(Constants.DICTIONARYID, UUID(0,0).toString()))

        if(savedDictionaryId == dictionaryId){
            Thread {
                PracticeDatabase.getInstance(activity).memoryCardDao().deleteMemoryCards()
                activity.getSharedPreferences(Constants.MEMORYGAME_DATA, 0).edit().clear().apply()
            }.start()
        }
    }

    override fun onDictionaryCreated(dictionaryId: UUID) = ifInitialized {
        initializeUserData()
        initializeUserAdapter()
    }

    override fun onAttributeCreated(attributeId: UUID) = ifInitialized { initializeAttributeAdapter() }

    override fun onAttributeUpdated(attributeId: UUID) = ifInitialized { initializeAttributeAdapter() }

    override fun onAttributeDeleted(attributeId: UUID) = ifInitialized { initializeAttributeAdapter() }

    override fun onUserUpdated(userId: UUID) = ifInitialized {
        emailEditText.isEnabled = true
        userNameEditText.isEnabled = true
        IsUpdateEmail.set(false)
        IsUpdateUserName.set(false)
        initializeUserData()
        if(App.isAdmin()){
            initializeUserAdapter()
        }
    }
}
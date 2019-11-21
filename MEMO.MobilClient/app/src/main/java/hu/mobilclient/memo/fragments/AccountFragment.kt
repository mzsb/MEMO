package hu.mobilclient.memo.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hu.mobilclient.memo.App
import hu.mobilclient.memo.BR
import hu.mobilclient.memo.R
import hu.mobilclient.memo.activities.LoginActivity
import hu.mobilclient.memo.activities.NavigationActivity
import hu.mobilclient.memo.adapters.AttributeAdapter
import hu.mobilclient.memo.adapters.UserAdapter
import hu.mobilclient.memo.databinding.FragmentAccountBinding
import hu.mobilclient.memo.filters.AttributeFilter
import hu.mobilclient.memo.filters.DictionaryFilter
import hu.mobilclient.memo.filters.UserFilter
import hu.mobilclient.memo.fragments.bases.NavigationFragmentBase
import hu.mobilclient.memo.helpers.Constants
import hu.mobilclient.memo.helpers.EmotionToast
import hu.mobilclient.memo.model.Attribute
import hu.mobilclient.memo.model.User
import kotlinx.android.synthetic.main.fab_menu.*
import kotlinx.android.synthetic.main.fragment_account.view.*


class AccountFragment: NavigationFragmentBase(), AttributeAdapter.OnAttributeClickedListener, UserAdapter.OnUserClickedListener {

    private val attributeAdapter : AttributeAdapter = AttributeAdapter()
    private val userAdapter : UserAdapter = UserAdapter()

    var user: ObservableField<User> = ObservableField()

    private var originalUserName = Constants.EMPTYSTRING
    private var originalEmail = Constants.EMPTYSTRING
    var IsUpdateUserName: ObservableBoolean = ObservableBoolean(false)
    var IsUpdateEmail: ObservableBoolean = ObservableBoolean(false)
    var IsAttributesVisible: ObservableBoolean = ObservableBoolean(false)
    var IsUsersVisible: ObservableBoolean = ObservableBoolean(false)
    var IsDelete: ObservableBoolean = ObservableBoolean(false)

    private lateinit var emailEditText: EditText
    private lateinit var userNameEditText: EditText

    private lateinit var attributesProgressBar: ProgressBar
    private lateinit var attributesRecyclerView: RecyclerView

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
        attributeAdapter.userId = args.getUserId()

        userAdapter.serviceManager = serviceManager
        userAdapter.userId = args.getUserId()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        attributesRecyclerView = view.fg_account_rv_attribute_list
        attributesProgressBar = view.fg_account_attribute_list_pb

        usersRecyclerView = view.fg_account_rv_user_list
        usersProgressBar = view.fg_account_user_list_pb

        popUp = AnimationUtils.loadAnimation(requireContext(), R.anim.pop_up)

        emailEditText = view.fg_account_et_email
        userNameEditText = view.fg_account_et_user_name


        val attributesHiderButton = view.fg_account_iv_attribute_list_hider
        val attributesHolder = view.fg_account_rl_attribute_list_holder
        val attributesLinearLayout = view.fg_account_ll_attributes
        val usersHiderButton = view.fg_account_iv_user_list_hider
        val usersHolder = view.fg_account_rl_user_list_holder
        val usersLinearLayout = view.fg_account_ll_users

        attributesHiderButton.setOnClickListener {
            IsAttributesVisible.set(!IsAttributesVisible.get())
            (it as ImageView).setImageResource(if (IsAttributesVisible.get()) R.drawable.ic_drop_up_24dp else R.drawable.ic_drop_down_24dp)
            attributesHolder.visibility = if (IsAttributesVisible.get()) {
                                                attributesRecyclerView.startAnimation(popUp)
                                                View.VISIBLE
                                           }
                                           else View.GONE
            if(IsAttributesVisible.get()){
                usersLinearLayout.visibility = View.GONE
                usersHolder.visibility = View.GONE
            }
            else{
                usersLinearLayout.visibility = View.VISIBLE
            }
        }

        usersHiderButton.setOnClickListener {
            IsUsersVisible.set(!IsUsersVisible.get())
            (it as ImageView).setImageResource(if (IsUsersVisible.get()) R.drawable.ic_drop_up_24dp else R.drawable.ic_drop_down_24dp)
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

        initializeUserData()
        initializeAttributeAdapter()
        initializeUserAdapter()
    }

    private fun initializeUserData(){
        serviceManager.user?.get(args.getUserId(), { user ->
            this.user.set(user)
        },{EmotionToast.showSad(getString(R.string.unable_load_user))})
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
                        serviceManager.user?.delete(user.get()!!.Id!!,{
                            requireActivity().getSharedPreferences(Constants.AUTHDATA, 0).edit().clear().apply()
                            startActivity(Intent(requireActivity(), LoginActivity::class.java))
                            DictionaryFilter.clearFilter()
                            AttributeFilter.clearFilter()
                            UserFilter.clearFilter()
                            EmotionToast.showSuccess(getString(R.string.account_delete_success))
                            requireActivity().finish()}, {
                            EmotionToast.showSad(getString(R.string.account_delete_fail))
                        })
                    }).show(requireActivity().supportFragmentManager, "TAG")
        }
        else {
            if (isValid()) {
                view.isEnabled = false
                emailEditText.isEnabled = false
                userNameEditText.isEnabled = false
                serviceManager.user?.update(user.get()!!, {
                    emailEditText.isEnabled = true
                    userNameEditText.isEnabled = true
                    view.isEnabled = true
                    IsUpdateEmail.set(false)
                    IsUpdateUserName.set(false)
                    EmotionToast.showSuccess()
                    initializeUserData()
                }, {
                    emailEditText.isEnabled = true
                    userNameEditText.isEnabled = true
                    view.isEnabled = true
                    EmotionToast.showError(getString(R.string.account_update_fail))
                })
            }
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
            this.error = errorMessage + this.text.length + " " + getString(R.string.character)
            return true
        }
        return false
    }

    private fun isValid() =
                    userNameEditText.isNotEmpty() &&
                    !userNameEditText.tooLong(15, getString(R.string.username_too_long) + " " + getString(R.string.now_dd) + " ") &&
                    emailEditText.isNotEmpty() &&
                    !emailEditText.tooLong(50, getString(R.string.email_too_long) + " " + getString(R.string.now_dd) + " ") &&
                    emailEditText.isValidEmail()

    private fun initializeAttributeAdapter(){
        val fabMenu = requireActivity().ac_navigation_ll_fab_menu

        attributeAdapter.initializeAdapter({
            fabMenu.visibility = View.GONE
            attributesProgressBar.visibility = View.VISIBLE
            attributesRecyclerView.visibility = View.GONE
        },{
            attributesRecyclerView.layoutManager = GridLayoutManager(context, 1)
            attributesRecyclerView.adapter = attributeAdapter
            attributeAdapter.itemLongClickListener = this
            attributesProgressBar.visibility = View.GONE
            attributesRecyclerView.startAnimation(popUp)
            attributesRecyclerView.visibility = View.VISIBLE
            fabMenu.startAnimation(popUp)
            fabMenu.visibility = View.VISIBLE
        })
    }

    private fun initializeUserAdapter(){
        val fabMenu = requireActivity().ac_navigation_ll_fab_menu

        userAdapter.initializeAdapter({
            fabMenu.visibility = View.GONE
            usersProgressBar.visibility = View.VISIBLE
            usersRecyclerView.visibility = View.GONE
        },{
            usersRecyclerView.layoutManager = GridLayoutManager(context, 1)
            usersRecyclerView.adapter = userAdapter
            userAdapter.itemLongClickListener = this
            usersProgressBar.visibility = View.GONE
            usersRecyclerView.startAnimation(popUp)
            usersRecyclerView.visibility = View.VISIBLE
            fabMenu.startAnimation(popUp)
            fabMenu.visibility = View.VISIBLE
        })
    }

    override fun update() {
        attributeAdapter.reset()
        userAdapter.reset()
        initializeAttributeAdapter()
        initializeUserAdapter()
    }

    override fun onAttributeLongClicked(attribute: Attribute) {
        AttributeFragment(attribute) {(activity as NavigationActivity).onRefresh()}.show(requireActivity().supportFragmentManager, "TAG")
    }

    override fun onUserLongClicked(user: User) {
        if(App.isAdmin()) {
            UserFragment(user) { (activity as NavigationActivity).onRefresh() }.show(requireActivity().supportFragmentManager, "TAG")
        }
    }
}
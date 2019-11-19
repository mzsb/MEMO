package hu.mobilclient.memo.fragments

import android.os.Bundle
import android.util.Base64
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.ProgressBar
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hu.mobilclient.memo.App
import hu.mobilclient.memo.BR
import hu.mobilclient.memo.R
import hu.mobilclient.memo.adapters.AttributeAdapter
import hu.mobilclient.memo.databinding.FragmentAccountBinding
import hu.mobilclient.memo.fragments.bases.NavigationFragmentBase
import hu.mobilclient.memo.helpers.Constants
import hu.mobilclient.memo.helpers.EmotionToast
import hu.mobilclient.memo.model.Attribute
import hu.mobilclient.memo.model.User
import kotlinx.android.synthetic.main.fab_menu.*
import kotlinx.android.synthetic.main.fragment_account.view.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec


class AccountFragment: NavigationFragmentBase(), AttributeAdapter.OnAttributeClickedListener {

    private val adapter : AttributeAdapter = AttributeAdapter()
    var user: ObservableField<User> = ObservableField()

    private var originalUserName = Constants.EMPTYSTRING
    private var originalEmail = Constants.EMPTYSTRING
    var IsUpdateUserName: ObservableBoolean = ObservableBoolean(false)
    var IsUpdateEmail: ObservableBoolean = ObservableBoolean(false)

    private lateinit var emailEditText: EditText
    private lateinit var userNameEditText: EditText

    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerView: RecyclerView

    private lateinit var popUp: Animation

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentAccountBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_account, container, false)

        binding.setVariable(BR.fragment, this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.fg_account_rv_attribute_list
        progressBar = view.fg_account_list_pb

        popUp = AnimationUtils.loadAnimation(requireContext(), R.anim.pop_up)

        emailEditText = view.fg_account_et_email
        userNameEditText = view.fg_account_et_user_name

        initializeUserData()
        initializeAdapter()
    }

    private fun initializeUserData(){
        serviceManager.user?.get(args.getUserId(), { user ->
            this.user.set(user)
        },{onFailure(getString(R.string.unable_load_user))})
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
        if(isValid()) {
            view.isEnabled = false
            emailEditText.isEnabled = false
            userNameEditText.isEnabled = false
            serviceManager.user?.update(user.get()!!, {
                emailEditText.isEnabled = true
                userNameEditText.isEnabled = true
                view.isEnabled = true
                IsUpdateEmail.set(false)
                IsUpdateUserName.set(false)
                initializeUserData()
            }, {
                emailEditText.isEnabled = true
                userNameEditText.isEnabled = true
                view.isEnabled = true
                EmotionToast.showError(it)
            })
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

    private fun initializeAdapter(){
        val fabMenu = requireActivity().ac_navigation_ll_fab_menu
        fabMenu.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        serviceManager.attribute?.getByUserId(args.getUserId(), { ownAttributes ->
            recyclerView.layoutManager = GridLayoutManager(context, 1)
            recyclerView.adapter = adapter
            adapter.itemLongClickListener = this

            if(App.isAdmin()){
                serviceManager.attribute?.get({ allAttributes ->
                    updateAdapter(ownAttributes, allAttributes)
                },::onFailure)
            }
            else {
                updateAdapter(ownAttributes)
            }

        },{onFailure(getString(R.string.unable_load_attributes))})
    }

    private fun onFailure(message: String){
        if(message.isNotEmpty()) {
            EmotionToast.showSad(message)
        }
        progressBar.visibility = View.GONE
    }

    private fun updateAdapter(ownAttributes: List<Attribute>,
                              allAttributes: List<Attribute> = ArrayList()){
        adapter.initializeAdapter(ownAttributes, allAttributes)
        progressBar.visibility = View.GONE
        recyclerView.startAnimation(popUp)
        recyclerView.visibility = View.VISIBLE
        val fabMenu = requireActivity().ac_navigation_ll_fab_menu
        fabMenu.startAnimation(popUp)
        fabMenu.visibility = View.VISIBLE
    }

    override fun update(){
        initializeUserData()
        initializeAdapter()
    }

    override fun onAttributeLongClicked(attribute: Attribute) {
        //DictionaryFragment(attribute) {(activity as NavigationActivity).onRefresh()}.show(requireActivity().supportFragmentManager, "TAG")
    }
}
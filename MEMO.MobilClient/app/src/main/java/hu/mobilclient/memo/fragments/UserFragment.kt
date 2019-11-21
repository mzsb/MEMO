package hu.mobilclient.memo.fragments

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.view.ContextThemeWrapper
import androidx.databinding.DataBindingUtil
import androidx.databinding.library.baseAdapters.BR
import androidx.fragment.app.DialogFragment
import hu.mobilclient.memo.R
import hu.mobilclient.memo.activities.bases.NetworkActivityBase
import hu.mobilclient.memo.databinding.FragmentUserBinding
import hu.mobilclient.memo.helpers.Constants
import hu.mobilclient.memo.helpers.EmotionToast
import hu.mobilclient.memo.model.User
import kotlinx.android.synthetic.main.fragment_user.*


class UserFragment(private var User: User = User(),
                   private val OkCallback: ()->Unit) : DialogFragment() {

    var IsDelete: Boolean = false

    private val originalUser = User()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val contextThemeWrapper: Context = ContextThemeWrapper(activity, R.style.AppTheme)

        val binding: FragmentUserBinding = DataBindingUtil.inflate(
                inflater.cloneInContext(contextThemeWrapper), R.layout.fragment_user, container, false)

        originalUser.copy(User)

        binding.setVariable(BR.user, User)
        binding.setVariable(BR.fragment, this)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()

        User.copy(originalUser)
    }

    private fun User.copy(rightUser: User){
        UserName = rightUser.UserName
        Email = rightUser.Email
    }

    private fun User.userEquals(rightUser: User)= UserName == rightUser.UserName &&
                                                          Email == rightUser.Email

    private fun isValid() =
                     fg_user_et_user_name.isNotEmpty() &&
                    !fg_user_et_user_name.tooLong(15, getString(R.string.username_too_long) + " " + getString(R.string.now_dd) + " ") &&
                     fg_user_et_user_email.isNotEmpty() &&
                    !fg_user_et_user_email.tooLong(50, getString(R.string.email_too_long) + " " + getString(R.string.now_dd) + " ") &&
                     fg_user_et_user_email.isValidEmail()

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

    fun cancelClick(view: View) = dismiss()

    fun saveClick(view: View){
        val serviceManager = (activity as NetworkActivityBase).serviceManager
        if(User.Role != Constants.ADMIN) {
                if (IsDelete) {
                    SureFragment(Message = User.UserName + getString(R.string.named_user_delete),
                            OkCallback = {
                                serviceManager.user?.delete(User.Id!!, {
                                    OkCallback()
                                    EmotionToast.showSuccess(getString(R.string.user_delete_success))
                                    dismiss()
                                }, {
                                    EmotionToast.showSad(getString(R.string.user_delete_fail))
                                })
                            }).show(requireActivity().supportFragmentManager, "TAG")
                } else {
                    if(!User.userEquals(originalUser)) {
                        if (isValid()) {
                            serviceManager.user?.update(User, {
                                OkCallback()
                                EmotionToast.showSuccess()
                                dismiss()
                            }, {
                                EmotionToast.showSad(it)
                            })
                        }
                    else{
                        EmotionToast.showHelp(getString(R.string.no_changes))
                    }
                }
            }
        }
        else{
            EmotionToast.showHelp(getString(R.string.cant_update_admin))
        }
    }
}
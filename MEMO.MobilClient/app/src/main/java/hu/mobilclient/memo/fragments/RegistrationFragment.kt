package hu.mobilclient.memo.fragments

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.text.method.TransformationMethod
import android.util.Patterns
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.view.ContextThemeWrapper
import androidx.databinding.DataBindingUtil
import androidx.databinding.library.baseAdapters.BR
import androidx.fragment.app.DialogFragment
import hu.mobilclient.memo.App
import hu.mobilclient.memo.R
import hu.mobilclient.memo.activities.NavigationActivity
import hu.mobilclient.memo.activities.bases.NetworkActivityBase
import hu.mobilclient.memo.databinding.FragmentRegistrationBinding
import hu.mobilclient.memo.helpers.Constants
import hu.mobilclient.memo.helpers.EmotionToast
import hu.mobilclient.memo.model.authentication.Registration
import kotlinx.android.synthetic.main.fragment_registration.*
import kotlinx.android.synthetic.main.fragment_registration.view.*

class RegistrationFragment : DialogFragment() {

    private val registration = Registration()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val contextThemeWrapper: Context = ContextThemeWrapper(activity, R.style.AppTheme)

        val binding: FragmentRegistrationBinding = DataBindingUtil.inflate(
                inflater.cloneInContext(contextThemeWrapper), R.layout.fragment_registration, container, false)

        binding.setVariable(BR.registration,registration)
        binding.setVariable(BR.fragment,this)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val cancelButton = view.fg_registration_tv_cancel

        fg_registration_et_password_image.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    fg_registration_et_password_image.setImageDrawable(null)
                    setTransformationMethod(null)
                    true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    fg_registration_et_password_image.setImageDrawable(activity?.getDrawable(R.drawable.ic_visibe_48dp))
                    setTransformationMethod(PasswordTransformationMethod.getInstance())
                    true
                }
                else -> false
            }
        }

        super.onViewCreated(view, savedInstanceState)
    }

    fun cancelClick(view: View) = dismiss()

    fun registrationClick(view: View){
        if(isValid()){
            (activity as NetworkActivityBase).serviceManager.authentication.registration(registration,{
                val intent = Intent(activity, NavigationActivity::class.java)
                intent.putExtra(Constants.USERID, it.UserId.toString())
                startActivity(intent)
                dismiss()
                activity?.finish()
            },{
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

    private fun EditText.isValidPassword(): Boolean{
        if(this.text.length < Constants.USER_PASSWORD_MIN_LENGTH){
            this.requestFocus()
            this.error = getString(R.string.too_short_password)
            return false
        }
        return true
    }

    private fun EditText.textEquals(et: EditText): Boolean{
        if(this.text.toString() != et.text.toString()){
            et.requestFocus()
            et.error = getString(R.string.passwords_not_match)
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
            fg_registration_et_username.isNotEmpty() &&
             !fg_registration_et_username.tooLong(Constants.USER_NAME_MAX_LENGTH, getString(R.string.username_too_long, Constants.USER_NAME_MAX_LENGTH)) &&
            fg_registration_et_email.isNotEmpty() &&
             !fg_registration_et_email.tooLong(Constants.USER_EMAIL_MAX_LENGTH, getString(R.string.email_too_long, Constants.USER_EMAIL_MAX_LENGTH)) &&
            fg_registration_et_email.isValidEmail() &&
            fg_registration_et_password.isNotEmpty() &&
             !fg_registration_et_password.tooLong(Constants.USER_PASSWORD_MAX_LENGTH, getString(R.string.password_too_long, Constants.USER_PASSWORD_MAX_LENGTH)) &&
            fg_registration_et_password.isValidPassword() &&
            fg_registration_et_password_again.isNotEmpty() &&
            fg_registration_et_password.textEquals(fg_registration_et_password_again)


    private fun setTransformationMethod(method: TransformationMethod?) {
        var selectionStart = fg_registration_et_password.selectionStart
        var selectionEnd = fg_registration_et_password.selectionEnd
        fg_registration_et_password.transformationMethod = method
        fg_registration_et_password.setSelection(selectionStart, selectionEnd)

        selectionStart = fg_registration_et_password_again.selectionStart
        selectionEnd = fg_registration_et_password_again.selectionEnd
        fg_registration_et_password_again.transformationMethod = method
        fg_registration_et_password_again.setSelection(selectionStart, selectionEnd)
    }

}
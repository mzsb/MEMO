package hu.mobilclient.memo.activities

import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.text.method.TransformationMethod
import android.util.Patterns
import android.view.MotionEvent
import android.view.View
import android.view.Window
import androidx.databinding.DataBindingUtil
import hu.mobilclient.memo.BR
import hu.mobilclient.memo.R
import hu.mobilclient.memo.activities.bases.NetworkActivityBase
import hu.mobilclient.memo.databinding.ActivityRegistrationBinding
import hu.mobilclient.memo.helpers.EmotionToast
import hu.mobilclient.memo.model.Registration
import hu.mobilclient.memo.model.TokenHolder
import hu.mobilclient.memo.network.callbacks.Authentication.IRegistrationCallBack
import hu.mobilclient.memo.network.interfaces.IInternetConnectionListener
import kotlinx.android.synthetic.main.activity_registration.*

class RegistrationActivity : NetworkActivityBase(), IRegistrationCallBack, IInternetConnectionListener {

    var registration = Registration()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        val binding: ActivityRegistrationBinding = DataBindingUtil.setContentView(this, R.layout.activity_registration)

        binding.setVariable(BR.registration, registration)
        binding.executePendingBindings()

        ac_registration_et_password_image.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    ac_registration_et_password_image.setImageDrawable(null)
                    setTransformationMethod(null)
                    true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    ac_registration_et_password_image.setImageDrawable(getDrawable(R.drawable.ic_visibility_48dp))
                    setTransformationMethod(PasswordTransformationMethod.getInstance())
                    true
                }
                else -> false
            }
        }
    }

    private fun setTransformationMethod(method: TransformationMethod?) {
        var selectionStart = ac_registration_et_password.selectionStart
        var selectionEnd = ac_registration_et_password.selectionEnd
        ac_registration_et_password.transformationMethod = method
        ac_registration_et_password.setSelection(selectionStart, selectionEnd)

        selectionStart = ac_registration_et_password_again.selectionStart
        selectionEnd = ac_registration_et_password_again.selectionEnd
        ac_registration_et_password_again.transformationMethod = method
        ac_registration_et_password_again.setSelection(selectionStart, selectionEnd)
    }

    fun registrationClick(view: View){
        if(isValid()){
            serviceManager.authentication?.registration(registration)
        }
    }

    private fun isValid(): Boolean {

        if(registration.UserName.isEmpty()) {
            ac_registration_et_username.requestFocus()
            ac_registration_et_username.error = getString(R.string.required_field)
            return false
        }

        if(registration.Email.isEmpty()) {
            ac_registration_et_email.requestFocus()
            ac_registration_et_email.error = getString(R.string.required_field)
            return false
        }
        else{
            if(!Patterns.EMAIL_ADDRESS.matcher(registration.Email).matches()){
                ac_registration_et_email.requestFocus()
                ac_registration_et_email.error = getString(R.string.invalid_email)
                return false;
            }
        }

        if(registration.Password.isEmpty()) {
            ac_registration_et_password.requestFocus()
            ac_registration_et_password.error = getString(R.string.required_field)
            return false
        }
        else {
            if(registration.Password.length < 6){
                ac_registration_et_password.requestFocus()
                ac_registration_et_password.error = getString(R.string.too_short_password)
                return false
            }
        }

        if(registration.Password.isEmpty()) {
            ac_registration_et_password_again.requestFocus()
            ac_registration_et_password_again.error = getString(R.string.required_field)
            return false
        }

        if(registration.Password != ac_registration_et_password_again.text.toString()) {
            ac_registration_et_password_again.requestFocus()
            ac_registration_et_password_again.setText("")
            ac_registration_et_password_again.error = getString(R.string.not_match)
            return false
        }

        return true
    }

    override fun onRegistrationSuccess(tokenHolder: TokenHolder) {
        intent = Intent(this, HomeActivity::class.java)
        intent.putExtra("userId", tokenHolder.UserId.toString())
        startActivity(intent)
        finish()
    }

    override fun onRegistrationError(errorMessage: String?) {
        EmotionToast.showError(this, errorMessage)
    }
}

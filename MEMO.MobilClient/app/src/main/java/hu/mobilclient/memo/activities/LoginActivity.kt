package hu.mobilclient.memo.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import hu.mobilclient.memo.BR
import hu.mobilclient.memo.R
import hu.mobilclient.memo.activities.bases.NetworkActivityBase
import hu.mobilclient.memo.databinding.ActivityLoginBinding
import hu.mobilclient.memo.helpers.EmotionToast
import hu.mobilclient.memo.model.Login
import hu.mobilclient.memo.model.TokenHolder
import hu.mobilclient.memo.network.callbacks.Authentication.ILoginCallBack
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : NetworkActivityBase(), ILoginCallBack {

    var login = Login(UserName = "User", Password = "123456")

    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        binding.setVariable(BR.login, login)
        binding.executePendingBindings()

        val token = getSharedPreferences("authData", 0).getString("token", null)

        if(!token.isNullOrEmpty()){
            serviceManager.authentication?.autoLogin(token)
        }
    }

    fun loginClick(view: View){
        if(isValid()){
            serviceManager.authentication?.login(login)
        }
    }

    fun registrationClick(view: View){
        startActivity(Intent(this, RegistrationActivity::class.java))
    }

    private fun isValid(): Boolean {

        if(login.UserName == "") {
            ac_login_et_username.requestFocus()
            ac_login_et_username.error = getString(R.string.required_field)
            return false
        }

        if(login.Password == "") {
            ac_login_et_password.requestFocus()
            ac_login_et_password.error = getString(R.string.required_field)
            return false
        }

        return true
    }

    override fun onLoginSuccess(tokenHolder: TokenHolder) {
        intent = Intent(this, HomeActivity::class.java)
        intent.putExtra("userId", tokenHolder.UserId.toString())
        startActivity(intent)
        finish()
    }

    override fun onLoginError(errorMessage: String?) {
        EmotionToast.showError(this, errorMessage)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}

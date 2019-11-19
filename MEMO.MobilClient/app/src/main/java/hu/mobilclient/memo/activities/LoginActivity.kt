package hu.mobilclient.memo.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import hu.mobilclient.memo.BR
import hu.mobilclient.memo.R
import hu.mobilclient.memo.activities.bases.NetworkActivityBase
import hu.mobilclient.memo.databinding.ActivityLoginBinding
import hu.mobilclient.memo.fragments.NetworkSettingFragment
import hu.mobilclient.memo.fragments.RegistrationFragment
import hu.mobilclient.memo.helpers.Constants
import hu.mobilclient.memo.helpers.EmotionToast
import hu.mobilclient.memo.model.Login
import hu.mobilclient.memo.model.TokenHolder
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : NetworkActivityBase() {

    private val login = Login(UserName = "User1", Password = "123456")
    private var checkLoginError = false
    private var registrationFragment: RegistrationFragment? = null
    private var networkFragment: NetworkSettingFragment? = null

    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        serviceManager.connection?.connect(::autoLogin) { checkLoginError = true}

        val binding: ActivityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        binding.setVariable(BR.login, login)
    }

    private fun autoLogin(){
        val token = getSharedPreferences(Constants.AUTHDATA, 0).getString(Constants.TOKEN, null)

        if(!token.isNullOrEmpty()){
            serviceManager.authentication?.autoLogin(token, callback = ::loginSuccess)
        }
    }

    fun loginClick(view: View){
        if(isValid()){
            serviceManager.authentication?.login(login, callback = ::loginSuccess, checkError = checkLoginError, errorCallback = {EmotionToast.showError(it)})
        }
    }

    fun registrationClick(view: View){
        val fragment = registrationFragment?: RegistrationFragment()
        registrationFragment = fragment
        fragment.show(supportFragmentManager, "TAG")
    }

    fun settingsClick(view: View){
        val fragment = networkFragment?: NetworkSettingFragment()
        networkFragment = fragment
        fragment.show(supportFragmentManager, "TAG")
    }

    private fun EditText.isNotEmpty(): Boolean{
        if(this.text.isEmpty()){
            this.requestFocus()
            this.error = getString(R.string.required_field)
            return false
        }
        return true
    }

    private fun isValid() = ac_login_et_username.isNotEmpty() && ac_login_et_password.isNotEmpty()

    private fun loginSuccess(tokenHolder: TokenHolder) {
        intent = Intent(this, NavigationActivity::class.java)
        intent.putExtra(Constants.USERID, tokenHolder.UserId.toString())
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}

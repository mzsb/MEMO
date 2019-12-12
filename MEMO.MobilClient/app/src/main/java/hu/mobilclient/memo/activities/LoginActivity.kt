package hu.mobilclient.memo.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import hu.mobilclient.memo.App
import hu.mobilclient.memo.BR
import hu.mobilclient.memo.R
import hu.mobilclient.memo.activities.bases.NetworkActivityBase
import hu.mobilclient.memo.databinding.ActivityLoginBinding
import hu.mobilclient.memo.fragments.NetworkSettingFragment
import hu.mobilclient.memo.fragments.RegistrationFragment
import hu.mobilclient.memo.fragments.TranslationFragment
import hu.mobilclient.memo.fragments.UsageModeFragment
import hu.mobilclient.memo.helpers.Constants
import hu.mobilclient.memo.helpers.EmotionToast
import hu.mobilclient.memo.model.authentication.Login
import hu.mobilclient.memo.model.authentication.TokenHolder
import hu.mobilclient.memo.model.memoapi.Translation
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : NetworkActivityBase() {

    private val login = Login()
    private var checkLoginError = false
    private var registrationFragment: RegistrationFragment? = null
    private var networkFragment: NetworkSettingFragment? = null

    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        setUsageMode()

        binding.setVariable(BR.login, login)
    }

    private fun setUsageMode(){
        val usageMode = getSharedPreferences(Constants.USAGEMODE_DATA, 0).getString(Constants.USAGEMODE, null)
        if(usageMode.isNullOrEmpty()){
            UsageModeFragment {
                connect()
            }.show(supportFragmentManager, Constants.USAGE_MODE_FRAGMENT_TAG)
        }
        else{
            when (usageMode) {
                App.Companion.UsageMode.developer.toString() -> App.usageMode = App.Companion.UsageMode.developer
                App.Companion.UsageMode.tester.toString() -> App.usageMode = App.Companion.UsageMode.tester
            }
            connect()
        }
    }

    private fun connect(){
        App.instance.setNetworkData()
        serviceManager.invalidateApiService()
        serviceManager.connection.connect(::autoLogin) { checkLoginError = true }
    }

    private fun autoLogin(){
        val token = getSharedPreferences(Constants.AUTHENTICATION_DATA, 0).getString(Constants.TOKEN, null)

        if(!token.isNullOrEmpty()){
            serviceManager.authentication.autoLogin(token, callback = ::loginSuccess, checkError = checkLoginError)
        }
    }

    fun loginClick(view: View){
        if(isValid()){
            serviceManager.authentication.login(login, callback = ::loginSuccess, checkError = checkLoginError, errorCallback = {EmotionToast.showError(it)})
        }
    }

    fun registrationClick(view: View){
        val fragment = registrationFragment?: RegistrationFragment()
        registrationFragment = fragment
        fragment.show(supportFragmentManager, Constants.REGISTRATION_FRAGMENT_TAG)
    }

    fun settingsClick(view: View){
        val fragment = networkFragment?: NetworkSettingFragment(ifUsageModeInvalidated = ::setUsageMode)
        networkFragment = fragment
        fragment.show(supportFragmentManager, Constants.NETWORK_SETTINGS_FRAGMENT_TAG)
    }

    private fun EditText.tooLong(maxLength: Int, errorMessage: String): Boolean{
        if(this.text.length > maxLength){
            this.requestFocus()
            this.error = errorMessage + " " + App.instance.getString(R.string.now_dd, this.text.length)
            return true
        }
        return false
    }

    private fun EditText.isNotEmpty(): Boolean{
        if(this.text.isEmpty()){
            this.requestFocus()
            this.error = getString(R.string.required_field)
            return false
        }
        return true
    }

    private fun isValid() = ac_login_et_username.isNotEmpty() &&
                                    !ac_login_et_username.tooLong(Constants.USER_NAME_MAX_LENGTH, getString(R.string.username_too_long, Constants.USER_NAME_MAX_LENGTH)) &&
                                    ac_login_et_password.isNotEmpty() &&
                                    !ac_login_et_password.tooLong(Constants.USER_PASSWORD_MAX_LENGTH, getString(R.string.password_too_long, Constants.USER_PASSWORD_MAX_LENGTH))

    @RequiresApi(Build.VERSION_CODES.M)
    private fun loginSuccess(tokenHolder: TokenHolder) {
        App.instance.refreshToken(tokenHolder.Token)

        if(intent.action.equals(Intent.ACTION_SEND) || intent.action.equals(Intent.ACTION_PROCESS_TEXT)){
            serviceManager.user.get(tokenHolder.UserId, callback = { user ->
                if(user.DictionaryCount.toInt() != 0) {
                    App.currentUser = user

                    val stringExtra = intent.getCharSequenceExtra(if (intent.action.equals(Intent.ACTION_SEND))
                        Intent.EXTRA_TEXT
                    else
                        Intent.EXTRA_PROCESS_TEXT)?.toString()
                            ?: Constants.EMPTY_STRING

                    TranslationFragment(Translation(Translated = stringExtra),
                            FromLogin = true).show(supportFragmentManager, Constants.TRANSLATION_FRAGMENT_TAG)
                }
                else {
                    EmotionToast.showHelp(getString(R.string.create_dictionary_first))
                    finish()
                }
            })
        }
        else {
            intent = Intent(this, NavigationActivity::class.java)
            intent.putExtra(Constants.USERID, tokenHolder.UserId.toString())
            startActivity(intent)
            finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}

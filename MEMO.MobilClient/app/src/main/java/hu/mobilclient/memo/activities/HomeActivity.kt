package hu.mobilclient.memo.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import hu.mobilclient.memo.BR
import hu.mobilclient.memo.R
import hu.mobilclient.memo.activities.bases.NetworkActivityBase
import hu.mobilclient.memo.databinding.ActivityHomeBinding
import hu.mobilclient.memo.helpers.EmotionToast
import hu.mobilclient.memo.model.*
import hu.mobilclient.memo.network.callbacks.User.IGetUserByIdCallBack
import java.util.*

class HomeActivity : NetworkActivityBase(), IGetUserByIdCallBack {

    var user = User()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityHomeBinding = DataBindingUtil.setContentView(this, R.layout.activity_home)

        binding.setVariable(BR.user, user)
        binding.executePendingBindings()

        val userId = intent.getStringExtra("userId")
        serviceManager.user?.get(UUID.fromString(userId))
    }

    fun click(view: View){
        serviceManager.user?.get(user.Id ?: UUID.randomUUID())
    }

    fun logoutClick(view: View){
        getSharedPreferences("authData", 0).edit().clear().apply()
        intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onGetUserByIdSuccess(user: User) {
        this.user = user

        serviceManager.language?.get()
    }

    override fun onGetUserByIdError(errorMessage: String?) {
        EmotionToast.showError(this, errorMessage)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}

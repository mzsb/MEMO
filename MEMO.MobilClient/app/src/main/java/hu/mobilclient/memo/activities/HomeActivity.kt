package hu.mobilclient.memo.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import hu.mobilclient.memo.BR
import hu.mobilclient.memo.R
import hu.mobilclient.memo.activities.bases.NetworkActivityBase
import hu.mobilclient.memo.databinding.ActivityHomeBinding
import hu.mobilclient.memo.helpers.EmotionToast
import hu.mobilclient.memo.model.User
import hu.mobilclient.memo.network.callbacks.IGetUserByIdCallBack
import hu.mobilclient.memo.services.ModelService
import java.util.*

class HomeActivity : NetworkActivityBase(), IGetUserByIdCallBack {

    private var modelService: ModelService? = null

    var user = User()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityHomeBinding = DataBindingUtil.setContentView(this, R.layout.activity_home)

        binding.setVariable(BR.user, user)
        binding.executePendingBindings()

        val userId = intent.getStringExtra("userId")

        modelService = ModelService(this)
        modelService?.getUserById(UUID.fromString(userId))
    }

    fun click(view: View){
        modelService?.getUserById(user.Id)
    }

    fun logoutclick(view: View){
        getSharedPreferences("authData", 0).edit().clear().apply()
        intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onGetUserByIdSuccess(user: User) {
        this.user = user;
    }

    override fun onGetUserByIdError(errorMessage: String?) {
        EmotionToast.showError(this, errorMessage)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}

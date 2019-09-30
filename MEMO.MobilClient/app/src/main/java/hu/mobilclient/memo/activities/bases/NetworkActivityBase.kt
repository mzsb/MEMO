package hu.mobilclient.memo.activities.bases

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import hu.mobilclient.memo.R
import hu.mobilclient.memo.activities.LoginActivity
import hu.mobilclient.memo.helpers.EmotionToast
import hu.mobilclient.memo.network.callbacks.ITokenExpiredCallBack
import hu.mobilclient.memo.network.interfaces.IInternetConnectionListener

abstract class NetworkActivityBase : AppCompatActivity(), IInternetConnectionListener, ITokenExpiredCallBack {

    override fun onInternetUnavailable() {
        EmotionToast.showHelp(this, getString(R.string.check_internet))
    }

    override fun onTokenExpired() {
        intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}

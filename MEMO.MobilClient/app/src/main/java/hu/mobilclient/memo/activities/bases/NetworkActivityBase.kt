package hu.mobilclient.memo.activities.bases

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import hu.mobilclient.memo.App
import hu.mobilclient.memo.R
import hu.mobilclient.memo.activities.LoginActivity
import hu.mobilclient.memo.helpers.EmotionToast
import hu.mobilclient.memo.network.callbacks.Authentication.ITokenExpiredCallBack
import hu.mobilclient.memo.network.interfaces.IInternetConnectionListener
import hu.mobilclient.memo.services.ServiceManager

abstract class NetworkActivityBase : AppCompatActivity(), IInternetConnectionListener, ITokenExpiredCallBack {

    protected var serviceManager: ServiceManager = ServiceManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.instance.setInternetConnectionListener(this)
    }

    override fun onInternetUnavailable() {
        EmotionToast.showHelp(this, getString(R.string.check_internet))
    }

    override fun onTokenExpired() {
        intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}

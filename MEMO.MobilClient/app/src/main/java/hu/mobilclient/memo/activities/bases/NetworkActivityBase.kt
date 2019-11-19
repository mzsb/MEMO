package hu.mobilclient.memo.activities.bases

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import hu.mobilclient.memo.App
import hu.mobilclient.memo.R
import hu.mobilclient.memo.helpers.EmotionToast
import hu.mobilclient.memo.network.interfaces.IInternetConnectionListener
import hu.mobilclient.memo.managers.ServiceManager

abstract class NetworkActivityBase : AppCompatActivity(), IInternetConnectionListener {

    var serviceManager: ServiceManager = ServiceManager(this) {}
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.instance.setInternetConnectionListener(this)
    }

    override fun onInternetUnavailable() {
        EmotionToast.showHelp(getString(R.string.check_internet))
    }
}

package hu.mobilclient.memo.activities.bases

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import hu.mobilclient.memo.managers.ServiceManager

abstract class NetworkActivityBase : AppCompatActivity() {

    lateinit var serviceManager: ServiceManager
        private set

    lateinit var connectivityManager: ConnectivityManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        serviceManager = ServiceManager(this) {}
        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }
}

package hu.mobilclient.memo.services

import android.app.Activity
import android.content.Context
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.google.gson.annotations.SerializedName
import hu.mobilclient.memo.App
import hu.mobilclient.memo.R
import hu.mobilclient.memo.activities.LoginActivity
import hu.mobilclient.memo.activities.bases.NetworkActivityBase
import hu.mobilclient.memo.fragments.NetworkSettingFragment
import hu.mobilclient.memo.helpers.Constants
import hu.mobilclient.memo.helpers.EmotionToast
import hu.mobilclient.memo.helpers.ProblemDetails
import hu.mobilclient.memo.network.ApiService
import hu.mobilclient.memo.services.bases.ServiceBase
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Response

class ConnectionService(val activity: Activity, private val errorCallback: (String) -> Unit) : ServiceBase(activity)  {

    private val defaultIp= activity.getSharedPreferences(Constants.NETWORK_DATA, 0)
                                        .getString(Constants.IP, ApiService.DEFAULTSERVERIP)
                                        ?: ApiService.DEFAULTSERVERIP

    private val defaultPort = activity.getSharedPreferences(Constants.NETWORK_DATA, 0)
                                            .getString(Constants.PORT, ApiService.DEFAULTSERVERPORT)
                                            ?: ApiService.DEFAULTSERVERPORT

    fun connect(ifConnected: () -> Unit = {}, ifNotConnected: () -> Unit = {}){
        if(isNetworkAvailable()) {
            if (!isEmulator() || App.usageMode == App.Companion.UsageMode.tester) {
                activity.ac_login_ll_network_progress.visibility = View.VISIBLE
                val baseURL = when(App.usageMode){
                    App.Companion.UsageMode.tester -> "${App.AZURE_CONNECTION_BASE_URL}connection"
                    App.Companion.UsageMode.developer -> "https://${defaultIp}:${defaultPort}/api/connection"
                }
                connect(baseURL,
                        callback = {
                            activity.ac_login_ll_network_progress.visibility = View.GONE
                            ifConnected()
                        },
                        errorCallback = {
                            if(App.usageMode == App.Companion.UsageMode.developer) {
                                scanNetwork(ifConnected,
                                        ifNotConnected)
                            }
                            else{
                                EmotionToast.showError(it)
                                activity.ac_login_ll_network_progress.visibility = View.GONE
                            }
                        })
            } else {
                setConnectionData(ApiService.DEFAULTSERVERIP,ApiService.DEFAULTSERVERPORT)
                activity.ac_login_ll_network_progress.visibility = View.GONE
                ifConnected()
            }
        }
        else{
            activity.ac_login_ll_network_progress.visibility = View.GONE
        }
    }

    fun setConnectionData(ip: String? = defaultIp, port: String? = defaultPort){
        activity.getSharedPreferences(Constants.NETWORK_DATA, 0).edit()
                .putString(Constants.IP, ip?: defaultIp)
                .putString(Constants.PORT, port?: defaultPort)
                .apply()

        App.instance.setNetworkData()
        (activity as NetworkActivityBase).serviceManager.invalidateApiService()
    }

    private fun scanNetwork(ifConnected: () -> Unit = {}, ifNotConnected: () -> Unit = {}){
        val myWifiManager = App.instance.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val myWifiInfo: WifiInfo = myWifiManager.connectionInfo
        val subnet = getSubnetAddress(myWifiInfo.ipAddress)

        var failCounter = 0
        for(i in 0..255) {
            if(isNetworkAvailable(false)) {
                connect("https://${subnet}.${i}:5001/api/connection", callback = {
                    if (it.Ip != Constants.EMPTY_STRING) {
                        activity.ac_login_ll_network_progress.visibility = View.GONE
                        setConnectionData(ip = it.Ip)
                        EmotionToast.showHelp(activity.getString(R.string.server_connection_on))
                        ifConnected()
                    }
                }, errorCallback =
                {
                    if (failCounter == 255) {
                        activity.ac_login_ll_network_progress.visibility = View.GONE

                        EmotionToast.showError(activity.getString(R.string.server_connection_off))
                        EmotionToast.showHelp(activity.getString(R.string.manual_configuration_can_help))
                        NetworkSettingFragment().show((activity as FragmentActivity).supportFragmentManager, "TAG")

                        ifNotConnected()
                    }
                    failCounter += 1
                })
            }
            else{
                activity.ac_login_ll_network_progress.visibility = View.GONE
                ifConnected()
            }
        }
    }

    private fun connect(url: String,
                        callback: (Connection) -> Unit,
                        errorCallback: (errorMessage: String) -> Unit = this.errorCallback,
                        checkError: Boolean = false) =
                    createRequest(
                            request = apiService::getConnect,
                            requestParameter = url,
                            onSuccess =
                            fun (response: Response<Connection>) {
                                if (response.isSuccessful && response.code() == 200) {
                                    callback(response.body()
                                            ?: return errorCallback(ProblemDetails(response.errorBody()?.string()).detail))
                                } else {
                                    errorCallback(ProblemDetails(response.errorBody()?.string()).detail)
                                }
                            },
                            onFailure = {errorCallback(it.message?:Constants.EMPTY_STRING)},
                            checkError = checkError)

    private fun getSubnetAddress(address: Int): String {
        return String.format(
                "%d.%d.%d",
                address and 0xff,
                address shr 8 and 0xff,
                address shr 16 and 0xff)
    }

    private fun isEmulator() = (Build.FINGERPRINT.startsWith("generic")
                                         || Build.FINGERPRINT.startsWith("unknown")
                                         || Build.MODEL.contains("google_sdk")
                                         || Build.MODEL.contains("Emulator")
                                         || Build.MODEL.contains("Android SDK built for x86")
                                         || Build.MANUFACTURER.contains("Genymotion")
                                         || Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")
                                         || "google_sdk" == Build.PRODUCT)

    inner class Connection(@SerializedName("ip")var Ip: String = Constants.EMPTY_STRING)
}
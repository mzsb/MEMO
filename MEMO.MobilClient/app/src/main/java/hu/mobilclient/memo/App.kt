package hu.mobilclient.memo

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.os.Handler
import android.os.Looper
import com.google.gson.Gson
import hu.mobilclient.memo.helpers.Constants
import hu.mobilclient.memo.model.User
import hu.mobilclient.memo.network.ApiService
import hu.mobilclient.memo.network.interceptors.InternetConnectionInterceptor
import hu.mobilclient.memo.network.interfaces.IInternetConnectionListener
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.cert.CertificateException
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


/* https://medium.com/@tsaha.cse/advanced-retrofit2-part-1-network-error-handling-response-caching-77483cf68620 */
class App : Application() {

    private lateinit var mInternetConnectionListener: IInternetConnectionListener
    private lateinit var retrofit: Retrofit

    companion object {
        lateinit var instance: App
            private set

        lateinit var apiService: ApiService
            private set

        private val mHandler: Handler = Handler()

        var currentUser: User = User()

        fun isAdmin() = currentUser.Role == Constants.ADMIN

        fun isCurrent(userId : UUID) = currentUser.Id == userId

        fun runOnUiThread(runnable: Runnable) {
            if (Thread.currentThread() === Looper.getMainLooper().thread) {
                runnable.run()
            } else {
                mHandler.post(runnable)
            }
        }
    }

    @SuppressLint("CommitPrefEdits")
    override fun onCreate() {
        super.onCreate()
        instance = this

        setNetworkData()
    }

    fun setInternetConnectionListener(listener: IInternetConnectionListener) {
        mInternetConnectionListener = listener
    }

    fun refreshToken(token: String){
        getSharedPreferences(Constants.AUTHDATA, 0).edit()
            .putString(Constants.TOKEN, token)
            .apply()

        apiService = createApiService()
    }

    fun setNetworkData(){
        ApiService.ServerIP = getSharedPreferences(Constants.NETWORKDATA, 0)
                                .getString(Constants.IP, ApiService.DEFAULTSERVERIP) ?: ApiService.DEFAULTSERVERIP

        ApiService.ServerPort = getSharedPreferences(Constants.NETWORKDATA, 0)
                .getString(Constants.PORT, ApiService.DEFAULTSERVERPORT) ?: ApiService.DEFAULTSERVERPORT

        apiService = createApiService()
    }

    private fun createApiService(): ApiService {
        return provideRetrofit().create(ApiService::class.java)
    }

    private fun provideRetrofit(): Retrofit {
        retrofit = Retrofit.Builder()
                .baseUrl(ApiService.BaseURL)
                .client(provideOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create(Gson()))
                .build()

        return retrofit
    }


    private fun provideOkHttpClient(): OkHttpClient {
        try {
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                @SuppressLint("TrustAllX509TrustManager")
                @Throws(CertificateException::class)
                override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) = Unit

                @SuppressLint("TrustAllX509TrustManager")
                @Throws(CertificateException::class)
                override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) = Unit

                override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> {
                    return arrayOf()
                }
            })

            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, java.security.SecureRandom())

            val sslSocketFactory = sslContext.socketFactory

            val builder = OkHttpClient.Builder()
                    .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)

            builder.hostnameVerifier { _, _ -> true }

            builder.connectTimeout(15, TimeUnit.SECONDS)
            builder.readTimeout(15, TimeUnit.SECONDS)
            builder.writeTimeout(15, TimeUnit.SECONDS)
            builder.addInterceptor(object : InternetConnectionInterceptor() {
                override val isInternetAvailable: Boolean
                    get() {
                        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                        val activeNetworkInfo = connectivityManager.activeNetworkInfo
                        return activeNetworkInfo != null && activeNetworkInfo.isConnected
                    }

                override fun onInternetUnavailable() {
                    mInternetConnectionListener.onInternetUnavailable()
                }
            })
            builder.addInterceptor { chain ->
                val newRequest = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer ${getSharedPreferences(Constants.AUTHDATA, 0).getString(Constants.TOKEN, null)}")
                        .build()
                chain.proceed(newRequest)
            }

            return builder.build()

        } catch (e: Exception) {
            throw RuntimeException(e)
        }

    }
}
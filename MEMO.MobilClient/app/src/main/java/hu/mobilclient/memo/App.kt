package hu.mobilclient.memo

import android.annotation.SuppressLint
import android.app.Application
import android.os.Handler
import android.os.Looper
import com.google.gson.Gson
import hu.mobilclient.memo.helpers.Constants
import hu.mobilclient.memo.model.memoapi.User
import hu.mobilclient.memo.network.ApiService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.cert.CertificateException
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class App : Application() {

    private lateinit var retrofit: Retrofit

    companion object {
        lateinit var instance: App
            private set

        lateinit var apiService: ApiService
            private set

        private val mHandler: Handler = Handler()

        var currentUser: User = User()

        fun getCurrentUserId() = currentUser.Id

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

    fun refreshToken(token: String){
        getSharedPreferences(Constants.AUTHENTICATION_DATA, 0).edit()
            .putString(Constants.TOKEN, token)
            .apply()

        apiService = createApiService()
    }

    fun setNetworkData(){
        ApiService.ServerIP = getSharedPreferences(Constants.NETWORK_DATA, 0)
                                .getString(Constants.IP, ApiService.DEFAULTSERVERIP) ?: ApiService.DEFAULTSERVERIP

        ApiService.ServerPort = getSharedPreferences(Constants.NETWORK_DATA, 0)
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

                override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> = arrayOf()
            })

            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, java.security.SecureRandom())

            val sslSocketFactory = sslContext.socketFactory

            val builder = OkHttpClient.Builder()
                    .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)

            builder.hostnameVerifier { _, _ -> true }

            builder.connectTimeout(30, TimeUnit.SECONDS)
            builder.readTimeout(30, TimeUnit.SECONDS)
            builder.writeTimeout(30, TimeUnit.SECONDS)
            builder.addInterceptor { chain ->
                val newRequest = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer ${getSharedPreferences(Constants.AUTHENTICATION_DATA, 0).getString(Constants.TOKEN, null)}")
                        .build()
                chain.proceed(newRequest)
            }

            return builder.build()

        } catch (e: Exception) {
            throw RuntimeException(e)
        }

    }
}
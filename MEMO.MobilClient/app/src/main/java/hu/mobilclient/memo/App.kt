package hu.mobilclient.memo

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import com.google.gson.Gson
import hu.mobilclient.memo.network.ApiService
import hu.mobilclient.memo.network.interceptors.InternetConnectionInterceptor
import hu.mobilclient.memo.network.interfaces.IInternetConnectionListener
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.security.cert.CertificateException
import java.util.concurrent.TimeUnit
import javax.net.ssl.*

/* https://medium.com/@tsaha.cse/advanced-retrofit2-part-1-network-error-handling-response-caching-77483cf68620 */
class App : Application() {

    private var apiService: ApiService? = null
    private var mInternetConnectionListener: IInternetConnectionListener? = null
    private var retrofit: Retrofit? = null

    @SuppressLint("CommitPrefEdits")
    override fun onCreate() {
        super.onCreate()
    }

    fun setInternetConnectionListener(listener: IInternetConnectionListener) {
        mInternetConnectionListener = listener
    }

    fun refreshToken(token: String){
        getSharedPreferences("authData", 0).edit()
            .putString("token", token)
            .apply()

        apiService = getApiService()
    }

    fun getApiService(): ApiService? {
        if (apiService == null) {
            apiService = provideRetrofit(ApiService.BaseURL)?.create(ApiService::class.java)
        }
        return apiService
    }

    private fun provideRetrofit(url: String): Retrofit? {
        retrofit = Retrofit.Builder()
                .baseUrl(url)
                .client(provideOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create(Gson()))
                .build()

        return retrofit
    }

    private fun provideOkHttpClient(): OkHttpClient {

        /* https://stackoverflow.com/questions/37686625/disable-ssl-certificate-check-in-retrofit-library */
        try {
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                @Throws(CertificateException::class)
                override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {
                }

                @Throws(CertificateException::class)
                override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {
                }

                override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> {
                    return arrayOf()
                }
            })

            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, java.security.SecureRandom())

            val sslSocketFactory = sslContext.socketFactory

            val builder = OkHttpClient.Builder()
            builder.sslSocketFactory(sslSocketFactory)
            builder.hostnameVerifier { hostname, session -> true }

            builder.connectTimeout(30, TimeUnit.SECONDS)
            builder.readTimeout(30, TimeUnit.SECONDS)
            builder.writeTimeout(30, TimeUnit.SECONDS)
            builder.addInterceptor(object : InternetConnectionInterceptor() {
                override val isInternetAvailable: Boolean
                    get() {
                        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                        val activeNetworkInfo = connectivityManager.activeNetworkInfo
                        return activeNetworkInfo != null && activeNetworkInfo.isConnected
                    }

                override fun onInternetUnavailable() {
                    mInternetConnectionListener!!.onInternetUnavailable()
                }
            })
            builder.addInterceptor { chain ->
                val newRequest = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer ${getSharedPreferences("authData", 0).getString("token", null)}")
                        .build()
                chain.proceed(newRequest)
            }

            return builder.build()

        } catch (e: Exception) {
            throw RuntimeException(e)
        }

    }
}
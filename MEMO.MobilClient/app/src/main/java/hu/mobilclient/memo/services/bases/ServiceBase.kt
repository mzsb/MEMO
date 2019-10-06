package hu.mobilclient.memo.services.bases

import android.app.Activity
import hu.mobilclient.memo.App
import hu.mobilclient.memo.R
import hu.mobilclient.memo.activities.bases.NetworkActivityBase
import hu.mobilclient.memo.helpers.EmotionToast
import hu.mobilclient.memo.model.TokenHolder
import hu.mobilclient.memo.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.ConnectException
import java.net.SocketTimeoutException

abstract class ServiceBase(private val activity: Activity) {

    var apiService: ApiService = App.apiService

    protected fun <K>createRequest(request:() -> Call<K>,
                                     onSuccess:(Response<K>) -> Unit = fun(_){},
                                     onFailure:(Throwable) -> Unit = fun(_){} ) {
        request.invoke().enqueue(object : Callback<K> {
            override fun onResponse(call: Call<K>, response: Response<K>) {
                onSuccess(response)

                if (response.code() == 401){
                    refreshToken(request)
                }
            }

            override fun onFailure(call: Call<K>, t: Throwable) {
                checkError(t)
                onFailure(t)
            }
        })
    }

    protected fun <T,K>createRequest(request:(T) -> Call<K>, requestParameter: T,
                                     onSuccess:(Response<K>) -> Unit = fun(_){},
                                     onFailure:(Throwable) -> Unit = fun(_){} ) {
        request.invoke(requestParameter).enqueue(object : Callback<K> {
            override fun onResponse(call: Call<K>, response: Response<K>) {
                onSuccess(response)

                if (response.code() == 401){
                    refreshToken(request, requestParameter)
                }
            }

            override fun onFailure(call: Call<K>, t: Throwable) {
                checkError(t)
                onFailure(t)
            }
        })
    }

    private fun <K>refreshToken(function:() -> Call<K>) {

        val token = activity.getSharedPreferences("authData", 0).getString("token", null) ?: ""

        apiService.refreshToken(token)
                .enqueue(object : Callback<TokenHolder> {
                    override fun onResponse(call: Call<TokenHolder>, response: Response<TokenHolder>) {
                        if (response.isSuccessful && response.code() == 200) {
                            App.instance.refreshToken(response.body()?.Token
                                    ?: return EmotionToast.showError(activity,activity.getString(R.string.error_occurred)))
                            function()
                        }
                        else{
                            (activity as NetworkActivityBase).onTokenExpired()
                        }
                    }

                    override fun onFailure(call: Call<TokenHolder>, t: Throwable) {
                        checkError(t)
                        (activity as NetworkActivityBase).onTokenExpired()
                    }
                })
    }

    private fun <T,K>refreshToken(function:(T) -> Call<K>, param: T) {

        val token = activity.getSharedPreferences("authData", 0).getString("token", null) ?: ""

        apiService.refreshToken(token)
                .enqueue(object : Callback<TokenHolder> {
                    override fun onResponse(call: Call<TokenHolder>, response: Response<TokenHolder>) {
                        if (response.isSuccessful && response.code() == 200) {
                            App.instance.refreshToken(response.body()?.Token
                                        ?: return EmotionToast.showError(activity,activity.getString(R.string.error_occurred)))
                            function(param)
                        }
                        else{
                            (activity as NetworkActivityBase).onTokenExpired()
                        }
                    }

                    override fun onFailure(call: Call<TokenHolder>, t: Throwable) {
                        checkError(t)
                        (activity as NetworkActivityBase).onTokenExpired()
                    }
                })
    }

    private fun checkError(throwable: Throwable){
        val message = when(throwable){
            is SocketTimeoutException -> activity.getString(R.string.server_not_available)
            is ConnectException -> activity.getString(R.string.network_error)
            else -> throwable.message
        }

        EmotionToast.showError(activity, message )
    }
}
package hu.mobilclient.memo.services

import android.annotation.SuppressLint
import android.app.Activity
import hu.mobilclient.memo.App
import hu.mobilclient.memo.R
import hu.mobilclient.memo.activities.HomeActivity
import hu.mobilclient.memo.activities.LoginActivity
import hu.mobilclient.memo.activities.RegistrationActivity
import hu.mobilclient.memo.activities.bases.NetworkActivityBase
import hu.mobilclient.memo.model.Login
import hu.mobilclient.memo.model.Registration
import hu.mobilclient.memo.model.TokenHolder
import hu.mobilclient.memo.model.User
import hu.mobilclient.memo.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException
import java.util.*
import hu.mobilclient.memo.helpers.EmotionToast
import hu.mobilclient.memo.helpers.ProblemDetails
import java.net.ConnectException


class ModelService @SuppressLint("CommitPrefEdits")
constructor(private val activity: Activity) {

    private var apiService: ApiService? = null

    init {
        apiService = (activity.application as App).getApiService()
    }

    fun login(login: Login) {
        apiService!!.login(login)
                .enqueue(object : Callback<TokenHolder> {
                    override fun onResponse(call: Call<TokenHolder>, response: Response<TokenHolder>) {
                        if (response.isSuccessful && response.code() == 200) {
                            (activity.application as App).refreshToken(response.body()!!.Token)
                            (activity as LoginActivity).onLoginSuccess(response.body()!!)
                        }
                        else{
                            (activity as LoginActivity).onLoginError(ProblemDetails.getDetail(response.errorBody()?.string()))
                        }
                    }

                    override fun onFailure(call: Call<TokenHolder>, t: Throwable) {
                        checkError(t)
                    }
                })
    }

    fun autoLogin(token: String) {
        apiService!!.autoLogin(token)
                .enqueue(object : Callback<TokenHolder> {
                    override fun onResponse(call: Call<TokenHolder>, response: Response<TokenHolder>) {
                        if (response.isSuccessful && response.code() == 200) {
                            (activity as LoginActivity).onLoginSuccess(response.body()!!)
                        }
                        else {
                            if (response.code() == 401) {
                                refreshToken(this@ModelService::autoLogin, token)
                            }
                        }
                    }

                    override fun onFailure(call: Call<TokenHolder>, t: Throwable) {
                        checkError(t)
                    }
                })
    }

    fun registration(registration: Registration) {
        apiService!!.registration(registration)
                .enqueue(object : Callback<TokenHolder> {
                    override fun onResponse(call: Call<TokenHolder>, response: Response<TokenHolder>) {
                        if (response.isSuccessful && response.code() == 200) {
                            (activity.application as App).refreshToken(response.body()!!.Token)
                            (activity as RegistrationActivity).onRegistrationSuccess(response.body()!!)
                        }
                        else{
                            (activity as RegistrationActivity).onRegistrationError(ProblemDetails.getDetail(response.errorBody()?.string()))
                        }
                    }

                    override fun onFailure(call: Call<TokenHolder>, t: Throwable) {
                        checkError(t)
                    }
                })
    }

    fun <T>refreshToken(function:(T) -> Unit, param: T) {
        val token = activity.getSharedPreferences("authData", 0).getString("token", null)

        apiService!!.refreshToken(token!!)
                .enqueue(object : Callback<TokenHolder> {
                    override fun onResponse(call: Call<TokenHolder>, response: Response<TokenHolder>) {
                        if (response.isSuccessful && response.code() == 200) {
                            (activity.application as App).refreshToken(response.body()!!.Token)
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

    fun getUserById(id: UUID) {
        apiService!!.getUserById(id)
                .enqueue(object : Callback<User> {
                    override fun onResponse(call: Call<User>, response: Response<User>) {
                        if (response.isSuccessful && response.code() == 200) {
                           (activity as HomeActivity).onGetUserByIdSuccess(response.body()!!)
                        }
                        else {
                            if (response.code() == 401){
                                refreshToken(this@ModelService::getUserById, id)
                            } else{
                                (activity as HomeActivity).onGetUserByIdError(ProblemDetails.getDetail(response.errorBody()?.string()))
                            }
                        }
                    }

                    override fun onFailure(call: Call<User>, t: Throwable) {
                        checkError(t)
                    }
                })
    }

    fun checkError(throwable: Throwable){
        val message = when(throwable){
            is SocketTimeoutException -> activity.getString(R.string.server_not_available)
            is ConnectException -> activity.getString(R.string.network_error)
            else -> throwable.message
        }

        EmotionToast.showError(activity, message )
    }
}
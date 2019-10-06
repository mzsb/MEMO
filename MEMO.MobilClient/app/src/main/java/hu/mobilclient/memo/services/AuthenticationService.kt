package hu.mobilclient.memo.services

import android.app.Activity
import hu.mobilclient.memo.App
import hu.mobilclient.memo.R
import hu.mobilclient.memo.helpers.ProblemDetails
import hu.mobilclient.memo.model.Login
import hu.mobilclient.memo.model.Registration
import hu.mobilclient.memo.model.TokenHolder
import hu.mobilclient.memo.network.callbacks.Authentication.ILoginCallBack
import hu.mobilclient.memo.network.callbacks.Authentication.IRegistrationCallBack
import hu.mobilclient.memo.services.bases.ServiceBase
import retrofit2.Response

class AuthenticationService(private val activity: Activity) : ServiceBase(activity) {

    fun login(login: Login) = createRequest(
            request = apiService::login,
            requestParameter = login,
            onSuccess =
            fun (response: Response<TokenHolder>) {
                if(activity is ILoginCallBack) {
                    if (response.isSuccessful && response.code() == 200) {
                        App.instance.refreshToken(response.body()?.Token
                                    ?: return activity.onLoginError(ProblemDetails(response.errorBody()?.string()).detail))
                        activity.onLoginSuccess(response.body()!!)

                    } else {
                        activity.onLoginError(ProblemDetails(response.errorBody()?.string()).detail)
                    }
                }
                else{
                    throw RuntimeException(activity.getString(R.string.invalid_activity_type))
                }
            })

    fun autoLogin(token: String) = createRequest(
            request = apiService::autoLogin,
            requestParameter = token,
            onSuccess =
            fun (response: Response<TokenHolder>) {
                if(activity is ILoginCallBack) {
                    if (response.isSuccessful && response.code() == 200) {
                        activity.onLoginSuccess(response.body()
                                ?: return activity.onLoginError(ProblemDetails(response.errorBody()?.string()).detail))
                    }
                }
                else{
                    throw RuntimeException(activity.getString(R.string.invalid_activity_type))
                }
            })

    fun registration(registration: Registration) = createRequest(
            request = apiService::registration,
            requestParameter = registration,
            onSuccess =
            fun (response: Response<TokenHolder>) {
                if(activity is IRegistrationCallBack) {
                    if (response.isSuccessful && response.code() == 200) {
                        App.instance.refreshToken(response.body()!!.Token)
                        activity.onRegistrationSuccess(response.body()
                                ?: return activity.onRegistrationError(ProblemDetails(response.errorBody()?.string()).detail))
                    } else {
                        activity.onRegistrationError(ProblemDetails(response.errorBody()?.string()).detail)
                    }
                }
                else{
                    throw RuntimeException(activity.getString(R.string.invalid_activity_type))
                }
            })
}
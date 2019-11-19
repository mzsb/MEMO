package hu.mobilclient.memo.services

import android.app.Activity
import hu.mobilclient.memo.App
import hu.mobilclient.memo.helpers.Constants
import hu.mobilclient.memo.helpers.ProblemDetails
import hu.mobilclient.memo.model.Login
import hu.mobilclient.memo.model.Registration
import hu.mobilclient.memo.model.TokenHolder
import hu.mobilclient.memo.services.bases.ServiceBase
import retrofit2.Response

class AuthenticationService(activity: Activity, private val errorCallback: (String) -> Unit) : ServiceBase(activity) {

    fun login(login: Login,
              callback: (TokenHolder) -> Unit,
              errorCallback: (errorMessage: String) -> Unit = this.errorCallback,
              checkError: Boolean = false) =
        createRequest(
            request = apiService::login,
            requestParameter = login,
            onSuccess =
            fun (response: Response<TokenHolder>) {
                if (response.isSuccessful && response.code() == 200) {
                    App.instance.refreshToken(response.body()?.Token
                                ?: return errorCallback(ProblemDetails(response.errorBody()?.string()).detail))
                    callback(response.body()
                                ?: return errorCallback(ProblemDetails(response.errorBody()?.string()).detail))
                } else {
                    errorCallback(ProblemDetails(response.errorBody()?.string()).detail)
                }
            },
            onFailure = {errorCallback(it.message?:Constants.EMPTYSTRING)},
            checkError = checkError)

    fun autoLogin(token: String,
                  callback: (TokenHolder) -> Unit,
                  errorCallback: (errorMessage: String) -> Unit = this.errorCallback,
                  checkError: Boolean = false) =
        createRequest(
            request = apiService::autoLogin,
            requestParameter = token,
            onSuccess =
            fun (response: Response<TokenHolder>) {
                if (response.isSuccessful && response.code() == 200) {
                    callback(response.body()
                            ?: return errorCallback(ProblemDetails(response.errorBody()?.string()).detail))
                } else {
                    errorCallback(ProblemDetails(response.errorBody()?.string()).detail)
                }
            },
            onFailure = {errorCallback(it.message?:Constants.EMPTYSTRING)},
            checkError = checkError)

    fun registration(registration: Registration,
                     callback: (TokenHolder) -> Unit,
                     errorCallback: (errorMessage: String) -> Unit = this.errorCallback,
                     checkError: Boolean = false) =
        createRequest(
            request = apiService::registration,
            requestParameter = registration,
            onSuccess =
            fun (response: Response<TokenHolder>) {
                if (response.isSuccessful && response.code() == 200) {
                    App.instance.refreshToken(response.body()?.Token
                            ?: return errorCallback(ProblemDetails(response.errorBody()?.string()).detail))
                    callback(response.body()
                            ?: return errorCallback(ProblemDetails(response.errorBody()?.string()).detail))
                } else {
                    errorCallback(ProblemDetails(response.errorBody()?.string()).detail)
                }
            },
            onFailure = {errorCallback(it.message?:Constants.EMPTYSTRING)},
            checkError = checkError)
}
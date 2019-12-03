package hu.mobilclient.memo.services

import android.app.Activity
import hu.mobilclient.memo.helpers.Constants
import hu.mobilclient.memo.helpers.ProblemDetails
import hu.mobilclient.memo.model.memoapi.Language
import hu.mobilclient.memo.services.bases.ServiceBase
import retrofit2.Response

class LanguageService(activity: Activity, private val errorCallback: (String) -> Unit) : ServiceBase(activity) {

    fun get(callback: (List<Language>) -> Unit,
            errorCallback: (errorMessage: String) -> Unit = this.errorCallback,
            checkError: Boolean = false) =
        createRequest(
                request = apiService::getLanguages,
                onSuccess =
                fun(response: Response<List<Language>>) {
                    if (response.isSuccessful && response.code() == 200) {
                        callback(response.body()
                                ?: return errorCallback(ProblemDetails(response.errorBody()?.string()).detail))
                    } else {
                        errorCallback(ProblemDetails(response.errorBody()?.string()).detail)
                    }
                },
                onFailure = {errorCallback(it.message?:Constants.EMPTY_STRING)},
                checkError = checkError)
}
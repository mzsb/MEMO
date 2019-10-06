package hu.mobilclient.memo.services

import android.app.Activity
import hu.mobilclient.memo.R
import hu.mobilclient.memo.helpers.ProblemDetails
import hu.mobilclient.memo.model.Language
import hu.mobilclient.memo.network.callbacks.Language.IGetLanguagesCallBack
import hu.mobilclient.memo.services.bases.ServiceBase
import retrofit2.Response

class LanguageService(private val activity: Activity) : ServiceBase(activity) {

    fun get() {
        createRequest(
                request = apiService::getLanguages,
                onSuccess =
                fun(response: Response<List<Language>>) {

                    if (activity is IGetLanguagesCallBack) {
                        if (response.isSuccessful && response.code() == 200) {
                            activity.onGetLanguagesSuccess(response.body()
                                    ?: return activity.onGetLanguagesError(ProblemDetails(response.errorBody()?.string()).detail))
                        } else {
                            activity.onGetLanguagesError(ProblemDetails(response.errorBody()?.string()).detail)
                        }
                    } else {
                        throw RuntimeException(activity.getString(R.string.invalid_activity_type))
                    }

                })
    }
}
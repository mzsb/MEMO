package hu.mobilclient.memo.services

import android.app.Activity
import hu.mobilclient.memo.helpers.Constants
import hu.mobilclient.memo.helpers.ProblemDetails
import hu.mobilclient.memo.model.Translation
import hu.mobilclient.memo.services.bases.ServiceBase
import retrofit2.Response
import java.util.*

class TranslationService(activity: Activity, private val errorCallback: (String) -> Unit) : ServiceBase(activity) {

    fun get(callback: (List<Translation>) -> Unit,
            errorCallback: (errorMessage: String) -> Unit = this.errorCallback,
            checkError: Boolean = false) =
        createRequest(
                request = apiService::getTranslations,
                onSuccess =
                fun(response: Response<List<Translation>>) {
                    if (response.isSuccessful && response.code() == 200) {
                        callback(response.body()
                                ?: return errorCallback(ProblemDetails(response.errorBody()?.string()).detail))
                    } else {
                        errorCallback(ProblemDetails(response.errorBody()?.string()).detail)
                    }
                },
                onFailure = {errorCallback(it.message?:Constants.EMPTYSTRING)},
                checkError = checkError)

    fun get(id: UUID,
            callback: (Translation) -> Unit,
            errorCallback: (errorMessage: String) -> Unit = this.errorCallback,
            checkError: Boolean = false) =
        createRequest(
                request = apiService::getTranslationById,
                requestParameter = id,
                onSuccess =
                fun(response: Response<Translation>) {
                    if (response.isSuccessful && response.code() == 200) {
                        callback(response.body()
                                ?: return errorCallback(ProblemDetails(response.errorBody()?.string()).detail))
                    } else {
                        errorCallback(ProblemDetails(response.errorBody()?.string()).detail)
                    }
                },
                onFailure = {errorCallback(it.message?:Constants.EMPTYSTRING)},
                checkError = checkError)

    fun insert(translation: Translation,
               callback: (Translation) -> Unit,
               errorCallback: (errorMessage: String) -> Unit = this.errorCallback,
               checkError: Boolean = false) =
        createRequest(
                request = apiService::insertTranslation,
                requestParameter = translation,
                onSuccess =
                fun(response: Response<Translation>) {
                    if (response.isSuccessful && response.code() == 201) {
                        callback(response.body()
                                ?: return errorCallback(ProblemDetails(response.errorBody()?.string()).detail))
                    } else {
                        errorCallback(ProblemDetails(response.errorBody()?.string()).detail)
                    }
                },
                onFailure = {errorCallback(it.message?:Constants.EMPTYSTRING)},
                checkError = checkError)

    fun update(translation: Translation,
               callback: () -> Unit,
               errorCallback: (errorMessage: String) -> Unit = this.errorCallback,
               checkError: Boolean = false) =
        createRequest(
                request = apiService::updateTranslation,
                requestParameter = translation,
                onSuccess =
                fun(response: Response<Void>) {
                    if (response.isSuccessful && response.code() == 200) {
                        callback()
                    } else {
                        errorCallback(ProblemDetails(response.errorBody()?.string()).detail)
                    }
                },
                onFailure = {errorCallback(it.message?:Constants.EMPTYSTRING)},
                checkError = checkError)

    fun delete(id: UUID,
               callback: () -> Unit,
               errorCallback: (errorMessage: String) -> Unit = this.errorCallback,
               checkError: Boolean = false) =
        createRequest(
                request = apiService::deleteTranslation,
                requestParameter = id,
                onSuccess =
                fun(response: Response<Void>) {
                    if (response.isSuccessful && response.code() == 200) {
                        callback()
                    } else {
                        errorCallback(ProblemDetails(response.errorBody()?.string()).detail)
                    }
                },
                onFailure = {errorCallback(it.message?:Constants.EMPTYSTRING)},
                checkError = checkError)

    fun getByDictionaryId(id: UUID,
                          callback: (List<Translation>) -> Unit,
                          errorCallback: (errorMessage: String) -> Unit = this.errorCallback,
                          checkError: Boolean = false) =
        createRequest(
                request = apiService::getTranslationsByDictionaryId,
                requestParameter = id,
                onSuccess =
                fun(response: Response<List<Translation>>) {
                    if (response.isSuccessful && response.code() == 200) {
                        callback(response.body()
                                ?: return errorCallback(ProblemDetails(response.errorBody()?.string()).detail))
                    } else {
                        errorCallback(ProblemDetails(response.errorBody()?.string()).detail)
                    }
                },
                onFailure = {errorCallback(it.message?:Constants.EMPTYSTRING)},
                checkError = checkError)
}
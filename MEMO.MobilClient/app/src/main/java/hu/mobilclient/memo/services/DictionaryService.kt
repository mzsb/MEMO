package hu.mobilclient.memo.services

import android.app.Activity
import hu.mobilclient.memo.helpers.Constants
import hu.mobilclient.memo.helpers.ProblemDetails
import hu.mobilclient.memo.model.memoapi.Dictionary
import hu.mobilclient.memo.services.bases.ServiceBase
import retrofit2.Response
import java.util.*

class DictionaryService(activity: Activity, private val errorCallback: (String) -> Unit) : ServiceBase(activity) {

    fun get(callback: (List<Dictionary>) -> Unit,
            errorCallback: (errorMessage: String) -> Unit = this.errorCallback,
            checkError: Boolean = false) =
        createRequest(
                request = apiService::getDictionaries,
                onSuccess =
                fun(response: Response<List<Dictionary>>) {
                    if (response.isSuccessful && response.code() == 200) {
                        callback(response.body()
                                ?: return errorCallback(ProblemDetails(response.errorBody()?.string()).detail))
                    } else {
                        errorCallback(ProblemDetails(response.errorBody()?.string()).detail)
                    }
                },
                onFailure = {errorCallback(it.message?:Constants.EMPTY_STRING)},
                checkError = checkError)

    fun get(id: UUID,
            callback: (Dictionary) -> Unit,
            errorCallback: (errorMessage: String) -> Unit = this.errorCallback,
            checkError: Boolean = false) =
        createRequest(
                request = apiService::getDictionaryById,
                requestParameter = id,
                onSuccess =
                fun(response: Response<Dictionary>) {
                    if (response.isSuccessful && response.code() == 200) {
                        callback(response.body()
                                ?: return errorCallback(ProblemDetails(response.errorBody()?.string()).detail))
                    } else {
                        errorCallback(ProblemDetails(response.errorBody()?.string()).detail)
                    }
                },
                onFailure = {errorCallback(it.message?:Constants.EMPTY_STRING)},
                checkError = checkError)

    fun insert(dictionary: Dictionary,
               callback: (Dictionary) -> Unit,
               errorCallback: (errorMessage: String) -> Unit = this.errorCallback,
               checkError: Boolean = false) =
        createRequest(
                request = apiService::insertDictionary,
                requestParameter = dictionary,
                onSuccess =
                fun(response: Response<Dictionary>) {
                    if (response.isSuccessful && response.code() == 201) {
                        callback(response.body()
                                ?: return errorCallback(ProblemDetails(response.errorBody()?.string()).detail))
                    } else {
                        errorCallback(ProblemDetails(response.errorBody()?.string()).detail)
                    }
                },
                onFailure = {errorCallback(it.message?:Constants.EMPTY_STRING)},
                checkError = checkError)

    fun update(dictionary: Dictionary,
               callback: () -> Unit,
               errorCallback: (errorMessage: String) -> Unit = this.errorCallback,
               checkError: Boolean = false) =
        createRequest(
                request = apiService::updateDictionary,
                requestParameter = dictionary,
                onSuccess =
                fun(response: Response<Void>) {
                    if (response.isSuccessful && response.code() == 200) {
                        callback()
                    } else {
                        errorCallback(ProblemDetails(response.errorBody()?.string()).detail)
                    }
                },
                onFailure = {errorCallback(it.message?:Constants.EMPTY_STRING)},
                checkError = checkError)

    fun delete(id: UUID,
               callback: () -> Unit,
               errorCallback: (errorMessage: String) -> Unit = this.errorCallback,
               checkError: Boolean = false) =
        createRequest(
                request = apiService::deleteDictionary,
                requestParameter = id,
                onSuccess =
                fun(response: Response<Void>) {
                    if (response.isSuccessful && response.code() == 200) {
                        callback()
                    } else {
                        errorCallback(ProblemDetails(response.errorBody()?.string()).detail)
                    }
                },
                onFailure = {errorCallback(it.message?:Constants.EMPTY_STRING)},
                checkError = checkError)

    fun getByUserId(id: UUID,
                    callback: (List<Dictionary>) -> Unit,
                    errorCallback: (errorMessage: String) -> Unit = this.errorCallback,
                    checkError: Boolean = false)  =
        createRequest(
                request = apiService::getDictionariesByUserId,
                requestParameter = id,
                onSuccess =
                fun(response: Response<List<Dictionary>>) {
                    if (response.isSuccessful && response.code() == 200) {
                        callback(response.body()
                                ?: return errorCallback(ProblemDetails(response.errorBody()?.string()).detail))
                    } else {
                        errorCallback(ProblemDetails(response.errorBody()?.string()).detail)
                    }
                },
                onFailure = {errorCallback(it.message?:Constants.EMPTY_STRING)},
                checkError = checkError)

    fun getPublic(userId: UUID,
                  callback: (List<Dictionary>) -> Unit,
                  errorCallback: (errorMessage: String) -> Unit = this.errorCallback,
                  checkError: Boolean = false) =
        createRequest(
                request = apiService::getDictionariesPublic,
                requestParameter = userId,
                onSuccess =
                fun(response: Response<List<Dictionary>>) {
                    if (response.isSuccessful && response.code() == 200) {
                        callback(response.body()
                                ?: return errorCallback(ProblemDetails(response.errorBody()?.string()).detail))
                    } else {
                        errorCallback(ProblemDetails(response.errorBody()?.string()).detail)
                    }
                },
                onFailure = {errorCallback(it.message?:Constants.EMPTY_STRING)},
                checkError = checkError)

    fun getFastAccessible(id: UUID,
                          callback: (List<Dictionary>) -> Unit,
                          errorCallback: (errorMessage: String) -> Unit = this.errorCallback,
                          checkError: Boolean = false) =
            createRequest(
                    request = apiService::getDictionariesFastAccessible,
                    requestParameter = id,
                    onSuccess =
                    fun(response: Response<List<Dictionary>>) {
                        if (response.isSuccessful && response.code() == 200) {
                            callback(response.body()
                                    ?: return errorCallback(ProblemDetails(response.errorBody()?.string()).detail))
                        } else {
                            errorCallback(ProblemDetails(response.errorBody()?.string()).detail)
                        }
                    },
                    onFailure = {errorCallback(it.message?:Constants.EMPTY_STRING)},
                    checkError = checkError)

    fun subscribe(userId: UUID,
                  dictionary: Dictionary,
                  callback: () -> Unit,
                  errorCallback: (errorMessage: String) -> Unit = this.errorCallback,
                  checkError: Boolean = false) =
            createRequest(
                    request = apiService::subscribe,
                    firstRequestParameter = userId,
                    secondRequestParameter = dictionary,
                    onSuccess =
                    fun(response: Response<Void>) {
                        if (response.isSuccessful && response.code() == 200) {
                            callback()
                        } else {
                            errorCallback(ProblemDetails(response.errorBody()?.string()).detail)
                        }
                    },
                    onFailure = {errorCallback(it.message?:Constants.EMPTY_STRING)},
                    checkError = checkError)

    fun unsubscribe(userId: UUID,
                    dictionary: Dictionary,
                    callback: () -> Unit,
                    errorCallback: (errorMessage: String) -> Unit = this.errorCallback,
                    checkError: Boolean = false) =
            createRequest(
                    request = apiService::unsubscribe,
                    firstRequestParameter = userId,
                    secondRequestParameter = dictionary,
                    onSuccess =
                    fun(response: Response<Void>) {
                        if (response.isSuccessful && response.code() == 200) {
                            callback()
                        } else {
                            errorCallback(ProblemDetails(response.errorBody()?.string()).detail)
                        }
                    },
                    onFailure = {errorCallback(it.message?:Constants.EMPTY_STRING)},
                    checkError = checkError)
}
package hu.mobilclient.memo.services

import android.app.Activity
import hu.mobilclient.memo.R
import hu.mobilclient.memo.helpers.ProblemDetails
import hu.mobilclient.memo.model.Dictionary
import hu.mobilclient.memo.network.callbacks.Dictionary.*
import hu.mobilclient.memo.services.bases.ServiceBase
import retrofit2.Response
import java.util.*

class DictionaryService(private val activity: Activity) : ServiceBase(activity) {

    fun get() {
        createRequest(
                request = apiService::getDictionaries,
                onSuccess =
                fun(response: Response<List<Dictionary>>) {

                    if (activity is IGetDictionariesCallBack) {
                        if (response.isSuccessful && response.code() == 200) {
                            activity.onGetDictionariesSuccess(response.body()
                                    ?: return activity.onGetDictionariesError(ProblemDetails(response.errorBody()?.string()).detail))
                        } else {
                            activity.onGetDictionariesError(ProblemDetails(response.errorBody()?.string()).detail)
                        }
                    } else {
                        throw RuntimeException(activity.getString(R.string.invalid_activity_type))
                    }

                })
    }

    fun get(id: UUID) {
        createRequest(
                request = apiService::getDictionaryById,
                requestParameter = id,
                onSuccess =
                fun(response: Response<Dictionary>) {

                    if (activity is IGetDictionaryByIdCallBack) {
                        if (response.isSuccessful && response.code() == 200) {
                            activity.onGetDictionaryByIdSuccess(response.body()
                                    ?: return activity.onGetDictionaryByIdError(ProblemDetails(response.errorBody()?.string()).detail))
                        } else {
                            activity.onGetDictionaryByIdError(ProblemDetails(response.errorBody()?.string()).detail)
                        }
                    } else {
                        throw RuntimeException(activity.getString(R.string.invalid_activity_type))
                    }

                })
    }

    fun insert(dictionary: Dictionary) {
        createRequest(
                request = apiService::insertDictionary,
                requestParameter = dictionary,
                onSuccess =
                fun(response: Response<Dictionary>) {

                    if (activity is IInsertDictionaryCallBack) {
                        if (response.isSuccessful && response.code() == 201) {
                            activity.onInsertDictionarySuccess(response.body()
                                    ?: return activity.onInsertDictionaryError(ProblemDetails(response.errorBody()?.string()).detail))
                        } else {
                            activity.onInsertDictionaryError(ProblemDetails(response.errorBody()?.string()).detail)
                        }
                    } else {
                        throw RuntimeException(activity.getString(R.string.invalid_activity_type))
                    }

                })
    }

    fun update(dictionary: Dictionary) {
        createRequest(
                request = apiService::updateDictionary,
                requestParameter = dictionary,
                onSuccess =
                fun(response: Response<Void>) {

                    if (activity is IUpdateDictionaryCallBack) {
                        if (response.isSuccessful && response.code() == 200) {
                            activity.onUpdateDictionarySuccess()
                        } else {
                            activity.onUpdateDictionaryError(ProblemDetails(response.errorBody()?.string()).detail)
                        }
                    } else {
                        throw RuntimeException(activity.getString(R.string.invalid_activity_type))
                    }

                })
    }

    fun delete(id: UUID) {
        createRequest(
                request = apiService::deleteDictionary,
                requestParameter = id,
                onSuccess =
                fun(response: Response<Void>) {

                    if (activity is IDeleteDictionaryCallBack) {
                        if (response.isSuccessful && response.code() == 200) {
                            activity.onDeleteDictionarySuccess()
                        } else {
                            activity.onDeleteDictionaryError(ProblemDetails(response.errorBody()?.string()).detail)
                        }
                    } else {
                        throw RuntimeException(activity.getString(R.string.invalid_activity_type))
                    }

                })
    }

    fun getByUserId(id: UUID) {
        createRequest(
                request = apiService::getDictionariesByUserId,
                requestParameter = id,
                onSuccess =
                fun(response: Response<List<Dictionary>>) {

                    if (activity is IGetDictionariesByUserIdCallBack) {
                        if (response.isSuccessful && response.code() == 200) {
                            activity.onGetDictionariesByUserIdSuccess(response.body()
                                    ?: return activity.onGetDictionariesByUserIdError(ProblemDetails(response.errorBody()?.string()).detail))
                        } else {
                            activity.onGetDictionariesByUserIdError(ProblemDetails(response.errorBody()?.string()).detail)
                        }
                    } else {
                        throw RuntimeException(activity.getString(R.string.invalid_activity_type))
                    }

                })
    }

    fun getPublic(id: UUID) {
        createRequest(
                request = apiService::getDictionariesPublic,
                requestParameter = id,
                onSuccess =
                fun(response: Response<List<Dictionary>>) {

                    if (activity is IGetDictionariesPublicCallBack) {
                        if (response.isSuccessful && response.code() == 200) {
                            activity.onGetDictionariesPublicSuccess(response.body()
                                    ?: return activity.onGetDictionariesPublicError(ProblemDetails(response.errorBody()?.string()).detail))
                        } else {
                            activity.onGetDictionariesPublicError(ProblemDetails(response.errorBody()?.string()).detail)
                        }
                    } else {
                        throw RuntimeException(activity.getString(R.string.invalid_activity_type))
                    }

                })
    }
}
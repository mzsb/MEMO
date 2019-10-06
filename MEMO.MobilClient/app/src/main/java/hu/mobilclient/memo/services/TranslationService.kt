package hu.mobilclient.memo.services

import android.app.Activity
import hu.mobilclient.memo.R
import hu.mobilclient.memo.helpers.ProblemDetails
import hu.mobilclient.memo.model.Translation
import hu.mobilclient.memo.network.callbacks.Translation.IGetTranslationsByDictionaryIdCallBack
import hu.mobilclient.memo.network.callbacks.Translation.*
import hu.mobilclient.memo.services.bases.ServiceBase
import retrofit2.Response
import java.util.*

class TranslationService(private val activity: Activity) : ServiceBase(activity) {

    fun get() {
        createRequest(
                request = apiService::getTranslations,
                onSuccess =
                fun(response: Response<List<Translation>>) {

                    if (activity is IGetTranslationsCallBack) {
                        if (response.isSuccessful && response.code() == 200) {
                            activity.onGetTranslationsSuccess(response.body()
                                    ?: return activity.onGetTranslationsError(ProblemDetails(response.errorBody()?.string()).detail))
                        } else {
                            activity.onGetTranslationsError(ProblemDetails(response.errorBody()?.string()).detail)
                        }
                    } else {
                        throw RuntimeException(activity.getString(R.string.invalid_activity_type))
                    }

                })
    }

    fun get(id: UUID) {
        createRequest(
                request = apiService::getTranslationById,
                requestParameter = id,
                onSuccess =
                fun(response: Response<Translation>) {

                    if (activity is IGetTranslationByIdCallBack) {
                        if (response.isSuccessful && response.code() == 200) {
                            activity.onGetTranslationByIdSuccess(response.body()
                                    ?: return activity.onGetTranslationByIdError(ProblemDetails(response.errorBody()?.string()).detail))
                        } else {
                            activity.onGetTranslationByIdError(ProblemDetails(response.errorBody()?.string()).detail)
                        }
                    } else {
                        throw RuntimeException(activity.getString(R.string.invalid_activity_type))
                    }

                })
    }

    fun insert(translation: Translation) {
        createRequest(
                request = apiService::insertTranslation,
                requestParameter = translation,
                onSuccess =
                fun(response: Response<Translation>) {

                    if (activity is IInsertTranslationCallBack) {
                        if (response.isSuccessful && response.code() == 201) {
                            activity.onInsertTranslationSuccess(response.body()
                                    ?: return activity.onInsertTranslationError(ProblemDetails(response.errorBody()?.string()).detail))
                        } else {
                            activity.onInsertTranslationError(ProblemDetails(response.errorBody()?.string()).detail)
                        }
                    } else {
                        throw RuntimeException(activity.getString(R.string.invalid_activity_type))
                    }

                })
    }

    fun update(translation: Translation) {
        createRequest(
                request = apiService::updateTranslation,
                requestParameter = translation,
                onSuccess =
                fun(response: Response<Void>) {

                    if (activity is IUpdateTranslationCallBack) {
                        if (response.isSuccessful && response.code() == 200) {
                            activity.onUpdateTranslationSuccess()
                        } else {
                            activity.onUpdateTranslationError(ProblemDetails(response.errorBody()?.string()).detail)
                        }
                    } else {
                        throw RuntimeException(activity.getString(R.string.invalid_activity_type))
                    }

                })
    }

    fun delete(id: UUID) {
        createRequest(
                request = apiService::deleteTranslation,
                requestParameter = id,
                onSuccess =
                fun(response: Response<Void>) {

                    if (activity is IDeleteTranslationCallBack) {
                        if (response.isSuccessful && response.code() == 200) {
                            activity.onDeleteTranslationSuccess()
                        } else {
                            activity.onDeleteTranslationError(ProblemDetails(response.errorBody()?.string()).detail)
                        }
                    } else {
                        throw RuntimeException(activity.getString(R.string.invalid_activity_type))
                    }

                })
    }

    fun getByDictionaryId(id: UUID) {
        createRequest(
                request = apiService::getTranslationsByDictionaryId,
                requestParameter = id,
                onSuccess =
                fun(response: Response<List<Translation>>) {

                    if (activity is IGetTranslationsByDictionaryIdCallBack) {
                        if (response.isSuccessful && response.code() == 200) {
                            activity.onGetTranslationsByDictionaryIdSuccess(response.body()
                                    ?: return activity.onGetTranslationsByDictionaryIdError(ProblemDetails(response.errorBody()?.string()).detail))
                        } else {
                            activity.onGetTranslationsByDictionaryIdError(ProblemDetails(response.errorBody()?.string()).detail)
                        }
                    } else {
                        throw RuntimeException(activity.getString(R.string.invalid_activity_type))
                    }

                })
    }
}
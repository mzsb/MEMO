package hu.mobilclient.memo.services

import android.app.Activity
import hu.mobilclient.memo.helpers.Constants
import hu.mobilclient.memo.helpers.ProblemDetails
import hu.mobilclient.memo.model.memoapi.Attribute
import hu.mobilclient.memo.services.bases.ServiceBase
import retrofit2.Response
import java.util.*

class AttributeService(activity: Activity, private val errorCallback: (String) -> Unit) : ServiceBase(activity) {

    fun get(callback: (List<Attribute>) -> Unit,
            errorCallback: (errorMessage: String) -> Unit = this.errorCallback,
            checkError: Boolean = false) =
            createRequest(
                    request = apiService::getAttributes,
                    onSuccess =
                    fun(response: Response<List<Attribute>>) {
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
            callback: (Attribute) -> Unit,
            errorCallback: (errorMessage: String) -> Unit = this.errorCallback,
            checkError: Boolean = false) =
            createRequest(
                    request = apiService::getAttributeById,
                    requestParameter = id,
                    onSuccess =
                    fun(response: Response<Attribute>) {
                        if (response.isSuccessful && response.code() == 200) {
                            callback(response.body()
                                    ?: return errorCallback(ProblemDetails(response.errorBody()?.string()).detail))
                        } else {
                            errorCallback(ProblemDetails(response.errorBody()?.string()).detail)
                        }
                    },
                    onFailure = {errorCallback(it.message?:Constants.EMPTY_STRING)},
                    checkError = checkError)

    fun insert(dictionary: Attribute,
               callback: (Attribute) -> Unit,
               errorCallback: (errorMessage: String) -> Unit = this.errorCallback,
               checkError: Boolean = false) =
            createRequest(
                    request = apiService::insertAttribute,
                    requestParameter = dictionary,
                    onSuccess =
                    fun(response: Response<Attribute>) {
                        if (response.isSuccessful && response.code() == 201) {
                            callback(response.body()
                                    ?: return errorCallback(ProblemDetails(response.errorBody()?.string()).detail))
                        } else {
                            errorCallback(ProblemDetails(response.errorBody()?.string()).detail)
                        }
                    },
                    onFailure = {errorCallback(it.message?:Constants.EMPTY_STRING)},
                    checkError = checkError)

    fun update(dictionary: Attribute,
               callback: () -> Unit,
               errorCallback: (errorMessage: String) -> Unit = this.errorCallback,
               checkError: Boolean = false) =
            createRequest(
                    request = apiService::updateAttribute,
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
                    request = apiService::deleteAttribute,
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
                    callback: (List<Attribute>) -> Unit,
                    errorCallback: (errorMessage: String) -> Unit = this.errorCallback,
                    checkError: Boolean = false)  =
            createRequest(
                    request = apiService::getAttributesByUserId,
                    requestParameter = id,
                    onSuccess =
                    fun(response: Response<List<Attribute>>) {
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
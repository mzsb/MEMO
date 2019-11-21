package hu.mobilclient.memo.services

import android.app.Activity
import hu.mobilclient.memo.R
import hu.mobilclient.memo.helpers.Constants
import hu.mobilclient.memo.helpers.ProblemDetails
import hu.mobilclient.memo.model.User
import hu.mobilclient.memo.services.bases.ServiceBase
import retrofit2.Response
import java.util.*

class UserService(activity: Activity, private val errorCallback: (String) -> Unit) : ServiceBase(activity) {

    fun get(callback: (List<User>) -> Unit,
            errorCallback: (errorMessage: String) -> Unit = this.errorCallback,
            checkError: Boolean = false) =
        createRequest(
                request = apiService::getUsers,
                onSuccess =
                fun (response: Response<List<User>>) {
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
            callback: (User) -> Unit,
            errorCallback: (errorMessage: String) -> Unit = this.errorCallback,
            checkError: Boolean = false) =
        createRequest(
                request = apiService::getUserById,
                requestParameter = id,
                onSuccess =
                fun (response: Response<User>) {
                    if (response.isSuccessful && response.code() == 200) {
                        callback(response.body()
                                ?: return errorCallback(ProblemDetails(response.errorBody()?.string()).detail))
                    } else {
                        errorCallback(ProblemDetails(response.errorBody()?.string()).detail)
                    }
                },
                onFailure = {errorCallback(it.message?:Constants.EMPTYSTRING)},
                checkError = checkError)

    fun update(user: User,
               callback: () -> Unit,
               errorCallback: (errorMessage: String) -> Unit = this.errorCallback,
               checkError: Boolean = false) =
        createRequest(
                request = apiService::updateUser,
                requestParameter = user,
                onSuccess =
                fun (response: Response<Void>) {
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
                request = apiService::deleteUser,
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

    fun getViewersByUserId(id: UUID,
                           callback: (List<User>) -> Unit,
                           errorCallback: (errorMessage: String) -> Unit = this.errorCallback,
                           checkError: Boolean = false) =
                    createRequest(
                            request = apiService::getViewersByUserId,
                            requestParameter = id,
                            onSuccess =
                            fun (response: Response<List<User>>) {
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
package hu.mobilclient.memo.services

import android.app.Activity
import hu.mobilclient.memo.R
import hu.mobilclient.memo.helpers.ProblemDetails
import hu.mobilclient.memo.model.User
import hu.mobilclient.memo.network.callbacks.User.IDeleteUserCallBack
import hu.mobilclient.memo.network.callbacks.User.IGetUserByIdCallBack
import hu.mobilclient.memo.network.callbacks.User.IGetUsersCallBack
import hu.mobilclient.memo.network.callbacks.User.IUpdateUserCallBack
import hu.mobilclient.memo.services.bases.ServiceBase
import retrofit2.Response
import java.util.*

class UserService(private val activity: Activity) : ServiceBase(activity) {

    fun get() {
        createRequest(
                request = apiService::getUsers,
                onSuccess =
                fun (response: Response<List<User>>) {

                    if (activity is IGetUsersCallBack) {
                        if (response.isSuccessful && response.code() == 200) {
                            activity.onGetUsersSuccess(response.body()
                                    ?: return activity.onGetUsersError(ProblemDetails(response.errorBody()?.string()).detail))
                        } else {
                            activity.onGetUsersError(ProblemDetails(response.errorBody()?.string()).detail)
                        }
                    }
                    else{
                        throw RuntimeException(activity.getString(R.string.invalid_activity_type))
                    }

                })
    }

    fun get(id: UUID) {
        createRequest(
                request = apiService::getUserById,
                requestParameter = id,
                onSuccess =
                fun (response: Response<User>) {

                    if (activity is IGetUserByIdCallBack) {
                        if (response.isSuccessful && response.code() == 200) {
                            activity.onGetUserByIdSuccess(response.body()
                                    ?: return activity.onGetUserByIdError(ProblemDetails(response.errorBody()?.string()).detail))
                        } else {
                            activity.onGetUserByIdError(ProblemDetails(response.errorBody()?.string()).detail)
                        }
                    }
                    else{
                        throw RuntimeException(activity.getString(R.string.invalid_activity_type))
                    }

                })
    }

    fun update(user: User) {
        createRequest(
                request = apiService::updateUser,
                requestParameter = user,
                onSuccess =
                fun (response: Response<Void>) {

                    if (activity is IUpdateUserCallBack) {
                        if (response.isSuccessful && response.code() == 200) {
                            activity.onUpdateUserSuccess()
                        } else {
                            activity.onUpdateUserError(ProblemDetails(response.errorBody()?.string()).detail)
                        }
                    }
                    else{
                        throw RuntimeException(activity.getString(R.string.invalid_activity_type))
                    }

                })
    }

    fun delete(id: UUID) {
        createRequest(
                request = apiService::deleteUser,
                requestParameter = id,
                onSuccess =
                fun(response: Response<Void>) {

                    if (activity is IDeleteUserCallBack) {
                        if (response.isSuccessful && response.code() == 200) {
                            activity.onDeleteUserSuccess()
                        } else {
                            activity.onDeleteUserError(ProblemDetails(response.errorBody()?.string()).detail)
                        }
                    } else {
                        throw RuntimeException(activity.getString(R.string.invalid_activity_type))
                    }

                })
    }
}
package hu.mobilclient.memo.network.callbacks.User

import hu.mobilclient.memo.model.User

interface IGetUserByIdCallBack {
    fun onGetUserByIdSuccess(user: User)

    fun onGetUserByIdError(errorMessage: String?)
}
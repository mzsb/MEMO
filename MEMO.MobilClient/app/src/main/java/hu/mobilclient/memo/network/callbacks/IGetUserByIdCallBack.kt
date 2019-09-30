package hu.mobilclient.memo.network.callbacks

import hu.mobilclient.memo.model.User

interface IGetUserByIdCallBack {
    fun onGetUserByIdSuccess(user: User)

    fun onGetUserByIdError(errorMessage: String?)
}
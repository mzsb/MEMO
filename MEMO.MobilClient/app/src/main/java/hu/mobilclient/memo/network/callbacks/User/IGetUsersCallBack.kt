package hu.mobilclient.memo.network.callbacks.User

import hu.mobilclient.memo.model.User

interface IGetUsersCallBack {
    fun onGetUsersSuccess(users: List<User>)

    fun onGetUsersError(errorMessage: String?)
}
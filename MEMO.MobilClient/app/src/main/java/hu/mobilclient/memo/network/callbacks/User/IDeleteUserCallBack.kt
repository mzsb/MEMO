package hu.mobilclient.memo.network.callbacks.User

interface IDeleteUserCallBack {
    fun onDeleteUserSuccess()

    fun onDeleteUserError(errorMessage: String?)
}
package hu.mobilclient.memo.network.callbacks.User

interface IUpdateUserCallBack {
    fun onUpdateUserSuccess()

    fun onUpdateUserError(errorMessage: String?)
}
package hu.mobilclient.memo.network.callbacks

import hu.mobilclient.memo.model.TokenHolder

interface ILoginCallBack {
    fun onLoginSuccess(tokenHolder: TokenHolder)

    fun onLoginError(errorMessage: String?)
}
package hu.mobilclient.memo.network.callbacks.Authentication

import hu.mobilclient.memo.model.TokenHolder

interface IRegistrationCallBack {
    fun onRegistrationSuccess(tokenHolder: TokenHolder)

    fun onRegistrationError(errorMessage: String?)
}
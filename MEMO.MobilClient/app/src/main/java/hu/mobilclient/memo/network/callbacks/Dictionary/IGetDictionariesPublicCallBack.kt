package hu.mobilclient.memo.network.callbacks.Dictionary

import hu.mobilclient.memo.model.Dictionary

interface IGetDictionariesPublicCallBack {
    fun onGetDictionariesPublicSuccess(dictionaries: List<Dictionary>)

    fun onGetDictionariesPublicError(errorMessage: String?)
}

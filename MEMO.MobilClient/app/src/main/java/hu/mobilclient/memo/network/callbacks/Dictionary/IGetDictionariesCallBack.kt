package hu.mobilclient.memo.network.callbacks.Dictionary

import hu.mobilclient.memo.model.Dictionary

interface IGetDictionariesCallBack {
    fun onGetDictionariesSuccess(dictionaries: List<Dictionary>)

    fun onGetDictionariesError(errorMessage: String?)
}

package hu.mobilclient.memo.network.callbacks.Dictionary

import hu.mobilclient.memo.model.Dictionary

interface IGetDictionariesByUserIdCallBack {
    fun onGetDictionariesByUserIdSuccess(dictionaries: List<Dictionary>)

    fun onGetDictionariesByUserIdError(errorMessage: String?)
}

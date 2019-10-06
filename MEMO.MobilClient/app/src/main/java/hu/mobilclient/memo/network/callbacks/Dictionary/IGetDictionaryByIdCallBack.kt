package hu.mobilclient.memo.network.callbacks.Dictionary

import hu.mobilclient.memo.model.Dictionary

interface IGetDictionaryByIdCallBack {
    fun onGetDictionaryByIdSuccess(dictionary: Dictionary)

    fun onGetDictionaryByIdError(errorMessage: String?)
}

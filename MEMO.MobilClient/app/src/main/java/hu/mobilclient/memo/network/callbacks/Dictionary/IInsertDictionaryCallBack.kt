package hu.mobilclient.memo.network.callbacks.Dictionary

import hu.mobilclient.memo.model.Dictionary

interface IInsertDictionaryCallBack {
    fun onInsertDictionarySuccess(dictionary: Dictionary)

    fun onInsertDictionaryError(errorMessage: String?)
}

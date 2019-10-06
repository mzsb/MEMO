package hu.mobilclient.memo.network.callbacks.Dictionary

interface IDeleteDictionaryCallBack {
    fun onDeleteDictionarySuccess()

    fun onDeleteDictionaryError(errorMessage: String?)
}

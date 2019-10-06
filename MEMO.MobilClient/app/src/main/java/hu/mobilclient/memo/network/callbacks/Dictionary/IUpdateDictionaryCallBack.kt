package hu.mobilclient.memo.network.callbacks.Dictionary

interface IUpdateDictionaryCallBack {
    fun onUpdateDictionarySuccess()

    fun onUpdateDictionaryError(errorMessage: String?)
}

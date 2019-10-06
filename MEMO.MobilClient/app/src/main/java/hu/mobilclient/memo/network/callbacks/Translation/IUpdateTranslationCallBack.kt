package hu.mobilclient.memo.network.callbacks.Translation

interface IUpdateTranslationCallBack {
    fun onUpdateTranslationSuccess()

    fun onUpdateTranslationError(errorMessage: String?)
}

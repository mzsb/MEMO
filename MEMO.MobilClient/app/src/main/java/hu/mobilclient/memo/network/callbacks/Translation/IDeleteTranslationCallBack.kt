package hu.mobilclient.memo.network.callbacks.Translation

interface IDeleteTranslationCallBack {
    fun onDeleteTranslationSuccess()

    fun onDeleteTranslationError(errorMessage: String?)
}

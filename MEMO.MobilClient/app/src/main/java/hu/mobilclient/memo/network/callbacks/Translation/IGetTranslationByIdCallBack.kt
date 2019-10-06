package hu.mobilclient.memo.network.callbacks.Translation

import hu.mobilclient.memo.model.Translation

interface IGetTranslationByIdCallBack {
    fun onGetTranslationByIdSuccess(translation: Translation)

    fun onGetTranslationByIdError(errorMessage: String?)
}

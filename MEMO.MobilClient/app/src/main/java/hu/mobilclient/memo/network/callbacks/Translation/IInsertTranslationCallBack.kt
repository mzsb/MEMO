package hu.mobilclient.memo.network.callbacks.Translation

import hu.mobilclient.memo.model.Translation

interface IInsertTranslationCallBack {
    fun onInsertTranslationSuccess(translation: Translation)

    fun onInsertTranslationError(errorMessage: String?)
}

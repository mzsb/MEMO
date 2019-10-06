package hu.mobilclient.memo.network.callbacks.Translation

import hu.mobilclient.memo.model.Translation

interface IGetTranslationsCallBack {
    fun onGetTranslationsSuccess(translations: List<Translation>)

    fun onGetTranslationsError(errorMessage: String?)
}

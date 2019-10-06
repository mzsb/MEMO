package hu.mobilclient.memo.network.callbacks.Translation

import hu.mobilclient.memo.model.Translation

interface IGetTranslationsByDictionaryIdCallBack {
    fun onGetTranslationsByDictionaryIdSuccess(translations: List<Translation>)

    fun onGetTranslationsByDictionaryIdError(errorMessage: String?)
}

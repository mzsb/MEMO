package hu.mobilclient.memo.network.callbacks.Language

import hu.mobilclient.memo.model.Language

interface IGetLanguagesCallBack {
    fun onGetLanguagesSuccess(languages: List<Language>)

    fun onGetLanguagesError(errorMessage: String?)
}

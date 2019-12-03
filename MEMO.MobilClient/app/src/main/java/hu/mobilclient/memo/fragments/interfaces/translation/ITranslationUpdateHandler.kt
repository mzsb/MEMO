package hu.mobilclient.memo.fragments.interfaces.translation

import java.util.*

interface ITranslationUpdateHandler {
    fun onTranslationUpdated(translationId: UUID)
}
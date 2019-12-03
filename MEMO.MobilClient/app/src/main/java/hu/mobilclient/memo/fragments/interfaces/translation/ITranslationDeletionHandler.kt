package hu.mobilclient.memo.fragments.interfaces.translation

import java.util.*

interface ITranslationDeletionHandler {
    fun onTranslationDeleted(translationId: UUID)
}
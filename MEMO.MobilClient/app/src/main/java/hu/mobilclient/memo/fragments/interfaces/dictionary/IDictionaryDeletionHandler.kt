package hu.mobilclient.memo.fragments.interfaces.dictionary

import java.util.*


interface IDictionaryDeletionHandler {
    fun onDictionaryDeleted(dictionaryId: UUID)
}
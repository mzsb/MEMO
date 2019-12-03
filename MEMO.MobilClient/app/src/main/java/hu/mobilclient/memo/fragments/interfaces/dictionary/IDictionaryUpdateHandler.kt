package hu.mobilclient.memo.fragments.interfaces.dictionary

import java.util.*

interface IDictionaryUpdateHandler {
    fun onDictionaryUpdated(dictionaryId: UUID)
}
package hu.mobilclient.memo.model.practice

import androidx.databinding.ObservableBoolean
import hu.mobilclient.memo.helpers.Constants
import java.util.*

class MemoryCard(val TranslationId: UUID = UUID(0,0),
                 val Value: String = Constants.EMPTY_STRING,
                 var IsFlipped: ObservableBoolean = ObservableBoolean(false),
                 var IsFound: Boolean = false)
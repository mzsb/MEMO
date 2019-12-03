package hu.mobilclient.memo.model.enums

import hu.mobilclient.memo.App
import hu.mobilclient.memo.R

enum class PracticeType {
    MEMORYGAME;

    override fun toString() = when(this){
        MEMORYGAME -> App.instance.getString(R.string.memorygame)
    }
}
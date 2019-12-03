package hu.mobilclient.memo.data.memoryGame

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import hu.mobilclient.memo.helpers.Constants

@Entity(tableName = "memoryCard")
class MemoryCardEntity(@PrimaryKey(autoGenerate = true) var Id: Long?,
                       @ColumnInfo(name = "translationId") val TranslationId: String = Constants.EMPTY_STRING,
                       @ColumnInfo(name = "value") val Value: String = Constants.EMPTY_STRING,
                       @ColumnInfo(name = "isFlipped") var IsFlipped: String = Constants.EMPTY_STRING,
                       @ColumnInfo(name = "isFound") var IsFound: String = Constants.EMPTY_STRING)
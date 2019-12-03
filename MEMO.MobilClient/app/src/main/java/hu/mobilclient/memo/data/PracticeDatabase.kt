package hu.mobilclient.memo.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import hu.mobilclient.memo.data.memoryGame.MemoryCardDao
import hu.mobilclient.memo.data.memoryGame.MemoryCardEntity

@Database(entities = [MemoryCardEntity::class], version = 1)
abstract class PracticeDatabase : RoomDatabase() {

    abstract fun memoryCardDao(): MemoryCardDao

    companion object {
        private var INSTANCE: PracticeDatabase? = null
        fun getInstance(context: Context): PracticeDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.applicationContext,
                        PracticeDatabase::class.java, "practice.db").build()
            }
            return INSTANCE!!
        }
    }
}
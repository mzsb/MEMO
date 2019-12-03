package hu.mobilclient.memo.data.memoryGame

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MemoryCardDao {
    @Query("SELECT * FROM memoryCard")
    fun getMemoryCards(): List<MemoryCardEntity>

    @Insert
    fun insertMemoryCards(memoryCards: List<MemoryCardEntity>)

    @Query("DELETE FROM memoryCard")
    fun deleteMemoryCards()
}
package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AceFlashcardDao {

    @Query("SELECT * FROM ace_flashcards ORDER BY serviceCategory, id")
    fun getAllFlashcards(): Flow<List<AceFlashcardEntity>>

    @Query("SELECT * FROM ace_flashcards WHERE serviceCategory = :category ORDER BY id")
    fun getFlashcardsByCategory(category: String): Flow<List<AceFlashcardEntity>>

    @Query("SELECT COUNT(*) FROM ace_flashcards")
    suspend fun getFlashcardCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlashcards(cards: List<AceFlashcardEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlashcard(card: AceFlashcardEntity)

    @Query("UPDATE ace_flashcards SET isMastered = :isMastered, timesReviewed = timesReviewed + 1, lastReviewedTimestamp = :timestamp WHERE id = :id")
    suspend fun updateMastery(id: String, isMastered: Boolean, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE ace_flashcards SET isMastered = 0, timesReviewed = 0")
    suspend fun resetAllMastery()
}

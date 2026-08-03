package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProgressDao {

    @Query("SELECT lessonId FROM completed_lessons")
    fun getCompletedLessonIds(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun markLessonCompleted(entity: CompletedLessonEntity)

    @Query("DELETE FROM completed_lessons WHERE lessonId = :lessonId")
    suspend fun unmarkLessonCompleted(lessonId: String)

    @Query("SELECT * FROM bookmarked_tips ORDER BY savedTimestamp DESC")
    fun getAllBookmarks(): Flow<List<BookmarkedTipEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addBookmark(bookmark: BookmarkedTipEntity)

    @Query("DELETE FROM bookmarked_tips WHERE id = :id")
    suspend fun deleteBookmark(id: String)

    @Query("SELECT * FROM quiz_scores ORDER BY timestamp DESC LIMIT 10")
    fun getQuizScores(): Flow<List<QuizScoreEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun recordQuizScore(score: QuizScoreEntity)
}

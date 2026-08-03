package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "completed_lessons")
data class CompletedLessonEntity(
    @PrimaryKey val lessonId: String,
    val completedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "bookmarked_tips")
data class BookmarkedTipEntity(
    @PrimaryKey val id: String,
    val lessonTitle: String,
    val tipText: String,
    val savedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "quiz_scores")
data class QuizScoreEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val score: Int,
    val totalQuestions: Int,
    val timestamp: Long = System.currentTimeMillis()
)

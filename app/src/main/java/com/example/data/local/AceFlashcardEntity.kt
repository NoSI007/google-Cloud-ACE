package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ace_flashcards")
data class AceFlashcardEntity(
    @PrimaryKey val id: String,
    val serviceName: String,
    val serviceCategory: String, // Compute, Storage & Database, Networking, Security & IAM, DevOps & Operations, Big Data & AI
    val frontPrompt: String,
    val backDefinition: String,
    val examTip: String,
    val keyFeaturesCsv: String,
    val isMastered: Boolean = false,
    val timesReviewed: Int = 0,
    val lastReviewedTimestamp: Long = 0L
)

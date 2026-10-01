package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: String = "active_user",
    val name: String,
    val email: String,
    val selectedClass: Int, // 9, 10, 11, 12
    val selectedBoard: String, // "CBSE", "ICSE", "State Board"
    val avatarId: String = "wizard_owl",
    val xp: Int = 150,
    val level: Int = 1,
    val coins: Int = 250,
    val battlesWon: Int = 0,
    val battlesTotal: Int = 0,
    val streakDays: Int = 3,
    val lastActiveTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "chapter_progress")
data class ChapterProgressEntity(
    @PrimaryKey val chapterId: String,
    val subjectId: String,
    val grade: Int,
    val topicsCompleted: Int = 0,
    val totalTopics: Int = 0,
    val questionsSolved: Int = 0,
    val totalQuestions: Int = 0,
    val quizHighScore: Int = 0,
    val isMastered: Boolean = false
)

@Entity(tableName = "offline_bookmarks")
data class OfflineBookmarkEntity(
    @PrimaryKey val id: String,
    val itemType: String, // "NOTE", "QUESTION", "PYQ"
    val title: String,
    val subtitle: String,
    val contentSnippet: String,
    val chapterId: String,
    val grade: Int,
    val savedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "personal_notes")
data class PersonalNoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val topicId: String,
    val topicTitle: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "duel_history")
data class DuelHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val opponentName: String,
    val opponentAvatar: String,
    val userScore: Int,
    val opponentScore: Int,
    val isWin: Boolean,
    val xpGained: Int,
    val timestamp: Long = System.currentTimeMillis()
)

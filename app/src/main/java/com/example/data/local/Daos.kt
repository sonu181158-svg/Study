package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user_profile WHERE id = 'active_user' LIMIT 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserProfile(profile: UserProfileEntity)

    @Query("UPDATE user_profile SET xp = xp + :xpGained, coins = coins + :coinsGained, level = :newLevel WHERE id = 'active_user'")
    suspend fun addRewards(xpGained: Int, coinsGained: Int, newLevel: Int)

    @Query("UPDATE user_profile SET battlesTotal = battlesTotal + 1, battlesWon = battlesWon + :wonInc WHERE id = 'active_user'")
    suspend fun recordBattle(wonInc: Int)

    @Query("UPDATE user_profile SET selectedClass = :newClass, selectedBoard = :newBoard WHERE id = 'active_user'")
    suspend fun updateClassAndBoard(newClass: Int, newBoard: String)
}

@Dao
interface ProgressDao {
    @Query("SELECT * FROM chapter_progress WHERE grade = :grade")
    fun getProgressForGrade(grade: Int): Flow<List<ChapterProgressEntity>>

    @Query("SELECT * FROM chapter_progress WHERE chapterId = :chapterId LIMIT 1")
    fun getChapterProgress(chapterId: String): Flow<ChapterProgressEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveChapterProgress(progress: ChapterProgressEntity)
}

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM offline_bookmarks ORDER BY savedTimestamp DESC")
    fun getAllBookmarks(): Flow<List<OfflineBookmarkEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM offline_bookmarks WHERE id = :id)")
    fun isBookmarked(id: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: OfflineBookmarkEntity)

    @Query("DELETE FROM offline_bookmarks WHERE id = :id")
    suspend fun deleteBookmark(id: String)
}

@Dao
interface PersonalNoteDao {
    @Query("SELECT * FROM personal_notes WHERE topicId = :topicId ORDER BY timestamp DESC")
    fun getNotesForTopic(topicId: String): Flow<List<PersonalNoteEntity>>

    @Query("SELECT * FROM personal_notes ORDER BY timestamp DESC")
    fun getAllPersonalNotes(): Flow<List<PersonalNoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: PersonalNoteEntity)

    @Query("DELETE FROM personal_notes WHERE id = :id")
    suspend fun deleteNote(id: Long)
}

@Dao
interface DuelDao {
    @Query("SELECT * FROM duel_history ORDER BY timestamp DESC LIMIT 20")
    fun getRecentDuels(): Flow<List<DuelHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun recordDuel(duel: DuelHistoryEntity)
}

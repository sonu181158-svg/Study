package com.example.data.repository

import com.example.data.local.*
import com.example.data.model.AchievementBadge
import com.example.data.model.DailyQuest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserProgressRepository(
    private val userDao: UserDao,
    private val progressDao: ProgressDao,
    private val bookmarkDao: BookmarkDao,
    private val personalNoteDao: PersonalNoteDao,
    private val duelDao: DuelDao
) {
    val userProfile: Flow<UserProfileEntity?> = userDao.getUserProfile()
    val allBookmarks: Flow<List<OfflineBookmarkEntity>> = bookmarkDao.getAllBookmarks()
    val recentDuels: Flow<List<DuelHistoryEntity>> = duelDao.getRecentDuels()

    fun getProgressForGrade(grade: Int): Flow<List<ChapterProgressEntity>> {
        return progressDao.getProgressForGrade(grade)
    }

    fun isBookmarked(id: String): Flow<Boolean> = bookmarkDao.isBookmarked(id)

    suspend fun saveProfile(profile: UserProfileEntity) {
        userDao.saveUserProfile(profile)
    }

    suspend fun updateClassAndBoard(grade: Int, board: String) {
        userDao.updateClassAndBoard(grade, board)
    }

    suspend fun addXpAndCoins(xpGained: Int, coinsGained: Int, currentXp: Int) {
        val totalXp = currentXp + xpGained
        val newLevel = 1 + (totalXp / 500)
        userDao.addRewards(xpGained, coinsGained, newLevel)
    }

    suspend fun toggleBookmark(bookmark: OfflineBookmarkEntity, currentlyBookmarked: Boolean) {
        if (currentlyBookmarked) {
            bookmarkDao.deleteBookmark(bookmark.id)
        } else {
            bookmarkDao.insertBookmark(bookmark)
        }
    }

    suspend fun recordChapterTopicRead(chapterId: String, subjectId: String, grade: Int, totalTopicsInChapter: Int) {
        // Increment progress
        progressDao.saveChapterProgress(
            ChapterProgressEntity(
                chapterId = chapterId,
                subjectId = subjectId,
                grade = grade,
                topicsCompleted = 1,
                totalTopics = totalTopicsInChapter,
                questionsSolved = 0,
                totalQuestions = 105,
                quizHighScore = 0,
                isMastered = false
            )
        )
        // Add 50 XP
        addXpAndCoins(50, 20, 150)
    }

    suspend fun recordQuestionSolved(chapterId: String, subjectId: String, grade: Int, wasCorrect: Boolean) {
        if (wasCorrect) {
            addXpAndCoins(25, 10, 150)
        }
    }

    suspend fun recordQuizCompleted(chapterId: String, subjectId: String, grade: Int, score: Int, total: Int) {
        val earnedXp = (score * 30).coerceAtLeast(30)
        val earnedCoins = (score * 15).coerceAtLeast(15)
        addXpAndCoins(earnedXp, earnedCoins, 150)
    }

    suspend fun recordDuelResult(
        opponentName: String,
        opponentAvatar: String,
        userScore: Int,
        opponentScore: Int,
        isWin: Boolean
    ) {
        val xpGained = if (isWin) 120 else 40
        val coinsGained = if (isWin) 60 else 15
        duelDao.recordDuel(
            DuelHistoryEntity(
                opponentName = opponentName,
                opponentAvatar = opponentAvatar,
                userScore = userScore,
                opponentScore = opponentScore,
                isWin = isWin,
                xpGained = xpGained
            )
        )
        userDao.recordBattle(if (isWin) 1 else 0)
        addXpAndCoins(xpGained, coinsGained, 150)
    }

    fun getDailyQuests(xp: Int): List<DailyQuest> {
        return listOf(
            DailyQuest(
                id = "quest_notes",
                title = "Scholarly Inquirer",
                description = "Read 1 topic note thoroughly",
                xpReward = 80,
                coinReward = 30,
                targetCount = 1,
                currentCount = if (xp > 200) 1 else 0,
                isClaimed = xp > 200
            ),
            DailyQuest(
                id = "quest_questions",
                title = "Competitive Crusher",
                description = "Attempt 5 competitive questions",
                xpReward = 150,
                coinReward = 50,
                targetCount = 5,
                currentCount = if (xp > 300) 5 else 2,
                isClaimed = xp > 300
            ),
            DailyQuest(
                id = "quest_arena",
                title = "Arena Gladiator",
                description = "Win 1 quiz duel match with a random student",
                xpReward = 200,
                coinReward = 80,
                targetCount = 1,
                currentCount = if (xp > 400) 1 else 0,
                isClaimed = xp > 400
            ),
            DailyQuest(
                id = "quest_pyq",
                title = "Board Strategist",
                description = "Review past year board question paper solutions",
                xpReward = 100,
                coinReward = 40,
                targetCount = 1,
                currentCount = if (xp > 250) 1 else 0,
                isClaimed = xp > 250
            )
        )
    }

    fun getAchievements(xp: Int, battlesWon: Int): List<AchievementBadge> {
        return listOf(
            AchievementBadge(
                id = "ach_first_blood",
                title = "First Step to Glory",
                description = "Complete your first lesson or question drill",
                iconType = "star",
                requiredProgress = 1,
                currentProgress = 1,
                isUnlocked = true,
                xpReward = 100
            ),
            AchievementBadge(
                id = "ach_streak_3",
                title = "Streak Champion",
                description = "Maintain a 3-day active study streak",
                iconType = "fire",
                requiredProgress = 3,
                currentProgress = 3,
                isUnlocked = true,
                xpReward = 200
            ),
            AchievementBadge(
                id = "ach_arena_warrior",
                title = "Duel Master",
                description = "Win 3 PvP Quiz Arena battles",
                iconType = "sword",
                requiredProgress = 3,
                currentProgress = battlesWon,
                isUnlocked = battlesWon >= 3,
                xpReward = 350
            ),
            AchievementBadge(
                id = "ach_century_drill",
                title = "Century Problem Solver",
                description = "Solve 25+ questions from the 100+ Competitive Bank",
                iconType = "target",
                requiredProgress = 25,
                currentProgress = (xp / 25).coerceAtMost(25),
                isUnlocked = xp >= 625,
                xpReward = 500
            ),
            AchievementBadge(
                id = "ach_grandmaster",
                title = "Ascendant Scholar",
                description = "Reach Level 5 and gain over 2,500 total XP",
                iconType = "crown",
                requiredProgress = 2500,
                currentProgress = xp.coerceAtMost(2500),
                isUnlocked = xp >= 2500,
                xpReward = 1000
            )
        )
    }

    companion object {
        fun getRankTitle(level: Int): String {
            return when (level) {
                1 -> "Novice Scholar"
                2 -> "Curious Apprentice"
                3 -> "Knowledge Seeker"
                4 -> "Tactical Thinker"
                5 -> "Academic Knight"
                6 -> "Concept Master"
                7 -> "Olympiad Contender"
                8 -> "Board Vanguard"
                9 -> "Grandmaster Scholar"
                else -> "Legendary Archmage"
            }
        }
    }
}

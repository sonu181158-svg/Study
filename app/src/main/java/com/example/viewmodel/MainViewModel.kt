package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.*
import com.example.data.model.*
import com.example.data.remote.GeminiApiClient
import com.example.data.repository.CurriculumRepository
import com.example.data.repository.UserProgressRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.random.Random

sealed class Screen {
    object Login : Screen()
    object Home : Screen()
    data class SubjectDetail(val subjectId: String) : Screen()
    data class TopicNotes(val chapterId: String, val topicId: String? = null) : Screen()
    data class CompetitiveBank(val chapterId: String) : Screen()
    data class InteractiveQuiz(val chapterId: String) : Screen()
    data class PyqPapers(val chapterId: String) : Screen()
    object AiTutor : Screen()
    object QuizArena : Screen()
    object ProgressTracker : Screen()
    object BookmarksShowcase : Screen()
}

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: String, // "user", "tutor"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class ArenaState(
    val status: String = "IDLE", // "IDLE", "MATCHMAKING", "BATTLING", "VICTORY", "DEFEAT"
    val opponentName: String = "",
    val opponentRank: String = "",
    val opponentAvatar: String = "wizard_owl",
    val currentRound: Int = 1,
    val totalRounds: Int = 5,
    val userScore: Int = 0,
    val opponentScore: Int = 0,
    val currentQuestion: QuizQuestion? = null,
    val selectedOptionIndex: Int? = null,
    val hasAnswered: Boolean = false,
    val roundTimerSeconds: Int = 15,
    val userStreak: Int = 0
)

data class QuizState(
    val chapterId: String = "",
    val questions: List<QuizQuestion> = emptyList(),
    val currentIndex: Int = 0,
    val selectedOption: Int? = null,
    val isSubmitted: Boolean = false,
    val score: Int = 0,
    val streak: Int = 0,
    val maxStreak: Int = 0,
    val isFinished: Boolean = false
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    val progressRepo = UserProgressRepository(
        userDao = db.userDao(),
        progressDao = db.progressDao(),
        bookmarkDao = db.bookmarkDao(),
        personalNoteDao = db.personalNoteDao(),
        duelDao = db.duelDao()
    )

    // Navigation Backstack
    private val _screenStack = MutableStateFlow<List<Screen>>(listOf(Screen.Home))
    val currentScreen: StateFlow<Screen> = _screenStack.map { it.lastOrNull() ?: Screen.Home }
        .stateIn(viewModelScope, SharingStarted.Eagerly, Screen.Home)

    // User Profile
    val userProfile: StateFlow<UserProfileEntity?> = progressRepo.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allBookmarks: StateFlow<List<OfflineBookmarkEntity>> = progressRepo.allBookmarks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentDuels: StateFlow<List<DuelHistoryEntity>> = progressRepo.recentDuels
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // AI Tutor Chat
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                sender = "tutor",
                text = "Greetings, Scholar! 🦉 I am Dr. Athena, your personal AI study companion. Ask me any conceptual doubt, request a high-yield study timetable, or ask for board-topper tips!"
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isAiThinking = MutableStateFlow(false)
    val isAiThinking: StateFlow<Boolean> = _isAiThinking.asStateFlow()

    // Arena State
    private val _arenaState = MutableStateFlow(ArenaState())
    val arenaState: StateFlow<ArenaState> = _arenaState.asStateFlow()
    private var arenaTimerJob: Job? = null

    // Quiz State
    private val _quizState = MutableStateFlow(QuizState())
    val quizState: StateFlow<QuizState> = _quizState.asStateFlow()

    init {
        // Initialize default student profile if none exists
        viewModelScope.launch {
            progressRepo.userProfile.firstOrNull().let { existing ->
                if (existing == null) {
                    progressRepo.saveProfile(
                        UserProfileEntity(
                            id = "active_user",
                            name = "Aarav Sharma",
                            email = "scholar.student@example.com",
                            selectedClass = 10,
                            selectedBoard = "CBSE",
                            avatarId = "wizard_owl",
                            xp = 420,
                            level = 1,
                            coins = 350,
                            battlesWon = 1,
                            battlesTotal = 2,
                            streakDays = 4
                        )
                    )
                }
            }
        }
    }

    // --- NAVIGATION ---
    fun navigateTo(screen: Screen) {
        val current = _screenStack.value.toMutableList()
        current.add(screen)
        _screenStack.value = current
    }

    fun navigateBack(): Boolean {
        val current = _screenStack.value.toMutableList()
        if (current.size > 1) {
            current.removeAt(current.size - 1)
            _screenStack.value = current
            return true
        }
        return false
    }

    // --- PROFILE & CLASS MANAGEMENT ---
    fun updateUserProfile(name: String, email: String, grade: Int, board: String, avatarId: String) {
        viewModelScope.launch {
            val current = userProfile.value
            progressRepo.saveProfile(
                UserProfileEntity(
                    id = "active_user",
                    name = name,
                    email = email,
                    selectedClass = grade,
                    selectedBoard = board,
                    avatarId = avatarId,
                    xp = current?.xp ?: 200,
                    level = current?.level ?: 1,
                    coins = current?.coins ?: 300,
                    battlesWon = current?.battlesWon ?: 0,
                    battlesTotal = current?.battlesTotal ?: 0,
                    streakDays = current?.streakDays ?: 1
                )
            )
            navigateTo(Screen.Home)
        }
    }

    fun switchGradeAndBoard(grade: Int, board: String) {
        viewModelScope.launch {
            progressRepo.updateClassAndBoard(grade, board)
        }
    }

    // --- BOOKMARKING ---
    fun toggleBookmark(itemType: String, id: String, title: String, subtitle: String, snippet: String, chapterId: String, grade: Int) {
        viewModelScope.launch {
            val exists = allBookmarks.value.any { it.id == id }
            progressRepo.toggleBookmark(
                OfflineBookmarkEntity(
                    id = id,
                    itemType = itemType,
                    title = title,
                    subtitle = subtitle,
                    contentSnippet = snippet,
                    chapterId = chapterId,
                    grade = grade
                ),
                currentlyBookmarked = exists
            )
        }
    }

    // --- AI TUTOR ---
    fun sendTutorMessage(query: String) {
        if (query.isBlank()) return
        val userMsg = ChatMessage(sender = "user", text = query)
        val updated = _chatMessages.value + userMsg
        _chatMessages.value = updated
        _isAiThinking.value = true

        viewModelScope.launch {
            val grade = userProfile.value?.selectedClass ?: 10
            val board = userProfile.value?.selectedBoard ?: "CBSE"
            val history = updated.takeLast(6).chunked(2).mapNotNull {
                if (it.size == 2 && it[0].sender == "user" && it[1].sender == "tutor") {
                    Pair(it[0].text, it[1].text)
                } else null
            }

            val reply = GeminiApiClient.askTutor(grade, board, query, history)
            _isAiThinking.value = false
            _chatMessages.value = _chatMessages.value + ChatMessage(sender = "tutor", text = reply)
            // Reward 20 XP for learning
            val currentXp = userProfile.value?.xp ?: 150
            progressRepo.addXpAndCoins(20, 10, currentXp)
        }
    }

    fun clearTutorChat() {
        _chatMessages.value = listOf(
            ChatMessage(
                sender = "tutor",
                text = "Chat cleared! What topic or doubt would you like to master next?"
            )
        )
    }

    // --- INTERACTIVE QUIZ ENGINE ---
    fun startChapterQuiz(chapter: Chapter) {
        val questions = chapter.competitiveQuestions.shuffled().take(6).mapIndexed { idx, q ->
            QuizQuestion(
                id = q.id,
                question = q.questionText,
                options = q.options,
                correctIndex = q.correctIndex,
                explanation = q.detailedExplanation,
                points = 100
            )
        }
        _quizState.value = QuizState(
            chapterId = chapter.id,
            questions = questions,
            currentIndex = 0,
            selectedOption = null,
            isSubmitted = false,
            score = 0,
            streak = 0,
            maxStreak = 0,
            isFinished = false
        )
        navigateTo(Screen.InteractiveQuiz(chapter.id))
    }

    fun selectQuizOption(optionIndex: Int) {
        val curr = _quizState.value
        if (curr.isSubmitted || curr.isFinished) return
        _quizState.value = curr.copy(selectedOption = optionIndex)
    }

    fun submitQuizAnswer() {
        val curr = _quizState.value
        if (curr.isSubmitted || curr.selectedOption == null) return
        val currentQ = curr.questions[curr.currentIndex]
        val isCorrect = curr.selectedOption == currentQ.correctIndex

        val newStreak = if (isCorrect) curr.streak + 1 else 0
        val newMax = maxOf(curr.maxStreak, newStreak)
        val addedScore = if (isCorrect) (currentQ.points + (newStreak * 20)) else 0

        _quizState.value = curr.copy(
            isSubmitted = true,
            score = curr.score + addedScore,
            streak = newStreak,
            maxStreak = newMax
        )
    }

    fun nextQuizQuestion() {
        val curr = _quizState.value
        if (curr.currentIndex + 1 < curr.questions.size) {
            _quizState.value = curr.copy(
                currentIndex = curr.currentIndex + 1,
                selectedOption = null,
                isSubmitted = false
            )
        } else {
            _quizState.value = curr.copy(isFinished = true)
            // Reward progress
            viewModelScope.launch {
                val grade = userProfile.value?.selectedClass ?: 10
                progressRepo.recordQuizCompleted(
                    curr.chapterId,
                    "subject",
                    grade,
                    curr.score / 100,
                    curr.questions.size
                )
            }
        }
    }

    // --- ARENA (1v1 PVP DUEL) SIMULATOR ---
    private val randomOpponents = listOf(
        Pair("Priya_JEE26", "Diamond III"),
        Pair("Rohan_Olympiad", "Master I"),
        Pair("Ananya_Topper", "Grandmaster"),
        Pair("Vikram_CBSE100", "Platinum II"),
        Pair("Dev_SciencePro", "Gold I")
    )

    fun startArenaDuel() {
        arenaTimerJob?.cancel()
        _arenaState.value = ArenaState(status = "MATCHMAKING")
        navigateTo(Screen.QuizArena)

        viewModelScope.launch {
            delay(1500) // Matchmaking thrill
            val opp = randomOpponents.random()
            val grade = userProfile.value?.selectedClass ?: 10
            val sampleQuestions = listOf(
                QuizQuestion("aq1", "What is the SI unit of electric potential difference?", listOf("Ampere", "Volt", "Ohm", "Joule"), 1, "Volt (V) is the SI unit of electric potential."),
                QuizQuestion("aq2", "Which organelle is universally known as the powerhouse of the cell?", listOf("Ribosome", "Mitochondria", "Golgi body", "Chloroplast"), 1, "Mitochondria generate cellular ATP."),
                QuizQuestion("aq3", "What is the sum of roots of 2x² - 8x + 6 = 0?", listOf("2", "4", "-4", "3"), 1, "Sum of roots α + β = -(-8)/2 = 4."),
                QuizQuestion("aq4", "Which gas is released when zinc reacts with dilute sulfuric acid?", listOf("Oxygen", "Hydrogen", "Carbon Dioxide", "Nitrogen"), 1, "Zn + H₂SO₄ → ZnSO₄ + H₂↑."),
                QuizQuestion("aq5", "What is the value of sin 30° + cos 60°?", listOf("0", "1", "1/2", "√3/2"), 1, "sin 30° = 1/2, cos 60° = 1/2. Sum = 1.")
            )

            _arenaState.value = ArenaState(
                status = "BATTLING",
                opponentName = opp.first,
                opponentRank = opp.second,
                opponentAvatar = "study_hero_mascot",
                currentRound = 1,
                totalRounds = 5,
                userScore = 0,
                opponentScore = 0,
                currentQuestion = sampleQuestions[0],
                selectedOptionIndex = null,
                hasAnswered = false,
                roundTimerSeconds = 12
            )

            startArenaRoundTimer(sampleQuestions)
        }
    }

    private fun startArenaRoundTimer(questions: List<QuizQuestion>) {
        arenaTimerJob?.cancel()
        arenaTimerJob = viewModelScope.launch {
            var timeLeft = 12
            while (timeLeft > 0) {
                delay(1000)
                timeLeft--
                _arenaState.value = _arenaState.value.copy(roundTimerSeconds = timeLeft)
            }
            // Round time expired
            advanceArenaRound(questions)
        }
    }

    fun submitArenaAnswer(optionIndex: Int) {
        val curr = _arenaState.value
        if (curr.hasAnswered || curr.status != "BATTLING") return
        val isCorrect = optionIndex == curr.currentQuestion?.correctIndex
        val addedUserScore = if (isCorrect) 100 + (curr.roundTimerSeconds * 5) else 0

        // Opponent answers with 70% probability correctly
        val oppCorrect = Random.nextFloat() < 0.70f
        val addedOppScore = if (oppCorrect) 90 + Random.nextInt(20) else 0

        _arenaState.value = curr.copy(
            selectedOptionIndex = optionIndex,
            hasAnswered = true,
            userScore = curr.userScore + addedUserScore,
            opponentScore = curr.opponentScore + addedOppScore
        )
    }

    fun nextArenaRoundManual() {
        val grade = userProfile.value?.selectedClass ?: 10
        val sampleQuestions = listOf(
            QuizQuestion("aq1", "What is the SI unit of electric potential difference?", listOf("Ampere", "Volt", "Ohm", "Joule"), 1, "Volt (V) is the SI unit."),
            QuizQuestion("aq2", "Which organelle is universally known as the powerhouse of the cell?", listOf("Ribosome", "Mitochondria", "Golgi body", "Chloroplast"), 1, "Mitochondria produce ATP."),
            QuizQuestion("aq3", "What is the sum of roots of 2x² - 8x + 6 = 0?", listOf("2", "4", "-4", "3"), 1, "Sum = -(-8)/2 = 4."),
            QuizQuestion("aq4", "Which gas is released when zinc reacts with dilute sulfuric acid?", listOf("Oxygen", "Hydrogen", "Carbon Dioxide", "Nitrogen"), 1, "Zn + H₂SO₄ → ZnSO₄ + H₂↑."),
            QuizQuestion("aq5", "What is the value of sin 30° + cos 60°?", listOf("0", "1", "1/2", "√3/2"), 1, "1/2 + 1/2 = 1.")
        )
        advanceArenaRound(sampleQuestions)
    }

    private fun advanceArenaRound(questions: List<QuizQuestion>) {
        arenaTimerJob?.cancel()
        val curr = _arenaState.value
        if (curr.currentRound < curr.totalRounds) {
            val nextRound = curr.currentRound + 1
            _arenaState.value = curr.copy(
                currentRound = nextRound,
                currentQuestion = questions[nextRound - 1],
                selectedOptionIndex = null,
                hasAnswered = false,
                roundTimerSeconds = 12
            )
            startArenaRoundTimer(questions)
        } else {
            // Battle finished!
            val won = curr.userScore >= curr.opponentScore
            _arenaState.value = curr.copy(
                status = if (won) "VICTORY" else "DEFEAT"
            )
            viewModelScope.launch {
                progressRepo.recordDuelResult(
                    opponentName = curr.opponentName,
                    opponentAvatar = curr.opponentAvatar,
                    userScore = curr.userScore,
                    opponentScore = curr.opponentScore,
                    isWin = won
                )
            }
        }
    }
}

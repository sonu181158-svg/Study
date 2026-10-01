package com.example.data.model

data class Subject(
    val id: String,
    val name: String,
    val iconName: String,
    val colorHex: Long,
    val grade: Int, // 9, 10, 11, 12
    val description: String,
    val totalChapters: Int
)

data class TopicNote(
    val id: String,
    val title: String,
    val subtitle: String,
    val keyConcepts: List<String>,
    val detailedContent: String,
    val realWorldExamples: List<String>,
    val importantFormulasOrDefinitions: List<String>,
    val examTrapsAndTips: List<String>
)

enum class QuestionCategory {
    FOUNDATION_DRILL,
    JEE_NEET_OLYMPIAD,
    HOT_BOARD_TOPPER,
    PAST_YEAR_SOLVED
}

data class CompetitiveQuestion(
    val id: String,
    val chapterId: String,
    val questionText: String,
    val options: List<String>,
    val correctIndex: Int,
    val category: QuestionCategory,
    val difficulty: String, // Easy, Medium, Hard, Legendary
    val targetExam: String, // e.g. "CBSE Board 2024", "JEE Main", "NEET-UG", "NTSE/Olympiad"
    val hint: String,
    val detailedExplanation: String,
    val formulaUsed: String? = null
)

data class PyqPaper(
    val id: String,
    val title: String,
    val board: String, // CBSE, ICSE, State Board
    val year: Int,
    val grade: Int,
    val subjectName: String,
    val durationMinutes: Int,
    val totalMarks: Int,
    val questions: List<PyqQuestionItem>
)

data class PyqQuestionItem(
    val qNumber: Int,
    val marks: Int,
    val section: String,
    val questionText: String,
    val markingSchemeStep1: String,
    val markingSchemeStep2: String,
    val finalAnswer: String
)

data class Chapter(
    val id: String,
    val subjectId: String,
    val number: Int,
    val title: String,
    val summary: String,
    val grade: Int,
    val topicNotes: List<TopicNote>,
    val competitiveQuestions: List<CompetitiveQuestion>,
    val pyqPapers: List<PyqPaper>
)

data class QuizQuestion(
    val id: String,
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String,
    val points: Int = 100
)

data class AchievementBadge(
    val id: String,
    val title: String,
    val description: String,
    val iconType: String,
    val requiredProgress: Int,
    val currentProgress: Int,
    val isUnlocked: Boolean,
    val xpReward: Int
)

data class DailyQuest(
    val id: String,
    val title: String,
    val description: String,
    val xpReward: Int,
    val coinReward: Int,
    val targetCount: Int,
    val currentCount: Int,
    val isClaimed: Boolean
)

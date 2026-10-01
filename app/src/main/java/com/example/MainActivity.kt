package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.ui.screens.*
import com.example.ui.theme.MidnightNavy
import com.example.ui.theme.ScholarQuestTheme
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.Screen

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ScholarQuestTheme(darkTheme = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MidnightNavy
                ) {
                    val currentScreen by viewModel.currentScreen.collectAsState()
                    val profile by viewModel.userProfile.collectAsState()

                    // BackHandler for secondary screens
                    BackHandler(enabled = currentScreen != Screen.Home) {
                        viewModel.navigateBack()
                    }

                    Crossfade(
                        targetState = currentScreen,
                        label = "screen_transition"
                    ) { screen ->
                        when (screen) {
                            is Screen.Login -> {
                                LoginScreen(
                                    currentName = profile?.name ?: "",
                                    currentGrade = profile?.selectedClass ?: 10,
                                    currentBoard = profile?.selectedBoard ?: "CBSE",
                                    onLoginCompleted = { name, email, grade, board, avatar ->
                                        viewModel.updateUserProfile(name, email, grade, board, avatar)
                                    }
                                )
                            }
                            is Screen.Home -> {
                                HomeScreen(
                                    viewModel = viewModel,
                                    profile = profile,
                                    onNavigate = { viewModel.navigateTo(it) }
                                )
                            }
                            is Screen.SubjectDetail -> {
                                SubjectDetailScreen(
                                    subjectId = screen.subjectId,
                                    viewModel = viewModel,
                                    onBack = { viewModel.navigateBack() },
                                    onNavigate = { viewModel.navigateTo(it) }
                                )
                            }
                            is Screen.TopicNotes -> {
                                TopicNotesScreen(
                                    chapterId = screen.chapterId,
                                    initialTopicId = screen.topicId,
                                    viewModel = viewModel,
                                    onBack = { viewModel.navigateBack() },
                                    onNavigateToTutor = { prefill ->
                                        viewModel.navigateTo(Screen.AiTutor)
                                        viewModel.sendTutorMessage(prefill)
                                    }
                                )
                            }
                            is Screen.CompetitiveBank -> {
                                CompetitiveQuestionsScreen(
                                    chapterId = screen.chapterId,
                                    viewModel = viewModel,
                                    onBack = { viewModel.navigateBack() }
                                )
                            }
                            is Screen.InteractiveQuiz -> {
                                InteractiveQuizScreen(
                                    viewModel = viewModel,
                                    onBack = { viewModel.navigateBack() }
                                )
                            }
                            is Screen.PyqPapers -> {
                                PyqScreen(
                                    chapterId = screen.chapterId,
                                    onBack = { viewModel.navigateBack() }
                                )
                            }
                            is Screen.AiTutor -> {
                                AiTutorScreen(
                                    viewModel = viewModel,
                                    onBack = { viewModel.navigateBack() }
                                )
                            }
                            is Screen.QuizArena -> {
                                ArenaScreen(
                                    viewModel = viewModel,
                                    onBack = { viewModel.navigateBack() }
                                )
                            }
                            is Screen.ProgressTracker -> {
                                ProgressTrackerScreen(
                                    viewModel = viewModel,
                                    onBack = { viewModel.navigateBack() }
                                )
                            }
                            is Screen.BookmarksShowcase -> {
                                BookmarksScreen(
                                    viewModel = viewModel,
                                    onBack = { viewModel.navigateBack() },
                                    onNavigate = { viewModel.navigateTo(it) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

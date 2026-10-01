package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.StudyTopBar
import com.example.ui.theme.*
import com.example.viewmodel.MainViewModel

@Composable
fun InteractiveQuizScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val quizState by viewModel.quizState.collectAsState()

    Scaffold(
        topBar = {
            StudyTopBar(
                title = "Interactive Quiz Battle",
                subtitle = if (quizState.isFinished) "Quiz Complete" else "Question ${quizState.currentIndex + 1} of ${quizState.questions.size}",
                onBackClick = onBack
            )
        },
        containerColor = MidnightNavy
    ) { innerPadding ->
        if (quizState.questions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = NeonCyan)
            }
            return@Scaffold
        }

        if (quizState.isFinished) {
            // --- QUIZ RESULTS SUMMARY ---
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Brush.radialGradient(listOf(TrophyGold, FlameOrange))),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(54.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Quiz Completed!",
                    color = TextWhite,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Outstanding performance, Scholar!",
                    color = NeonCyanBright,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardNavy),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BorderSlate, TrophyGold.copy(alpha = 0.5f))))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ResultRow(label = "Final Score", value = "${quizState.score} pts", color = TrophyGold)
                        ResultRow(label = "Max Combo Streak", value = "${quizState.maxStreak} 🔥", color = FlameOrange)
                        ResultRow(label = "XP Earned", value = "+${(quizState.score / 3).coerceAtLeast(60)} XP", color = NeonCyanBright)
                        ResultRow(label = "Coins Awarded", value = "+${(quizState.score / 6).coerceAtLeast(30)} 🪙", color = TrophyGoldBright)
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                Button(
                    onClick = onBack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("quiz_finish_back_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryNeonIndigo)
                ) {
                    Text("Return to Chapters", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        } else {
            // --- ACTIVE QUIZ QUESTION ---
            val currentQ = quizState.questions[quizState.currentIndex]

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Progress and Streak Status Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Score: ",
                            color = TextMuted,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "${quizState.score}",
                            color = TrophyGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    if (quizState.streak > 1) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(FlameOrange.copy(alpha = 0.2f))
                                .border(1.dp, FlameOrange, RoundedCornerShape(12.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Streak x${quizState.streak} 🔥 (+${quizState.streak * 20} bonus)",
                                color = FlameOrange,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Progress Bar
                LinearProgressIndicator(
                    progress = { (quizState.currentIndex + 1) / quizState.questions.size.toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = NeonCyan,
                    trackColor = BorderSlate
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Question Box
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardNavy),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BorderSlate, PrimaryNeonIndigo.copy(alpha = 0.4f))))
                ) {
                    Text(
                        text = currentQ.question,
                        color = TextWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 22.sp,
                        modifier = Modifier.padding(18.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Options List
                currentQ.options.forEachIndexed { optIndex, optionText ->
                    val isSelected = quizState.selectedOption == optIndex
                    val isSubmitted = quizState.isSubmitted
                    val isCorrect = optIndex == currentQ.correctIndex

                    val borderColor = when {
                        isSubmitted && isCorrect -> EmeraldVictory
                        isSubmitted && isSelected && !isCorrect -> DangerRed
                        isSelected -> NeonCyanBright
                        else -> BorderSlate
                    }

                    val bgColor = when {
                        isSubmitted && isCorrect -> EmeraldVictory.copy(alpha = 0.18f)
                        isSubmitted && isSelected && !isCorrect -> DangerRed.copy(alpha = 0.18f)
                        isSelected -> PrimaryNeonIndigo.copy(alpha = 0.2f)
                        else -> CardNavy
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(bgColor)
                            .border(1.5.dp, borderColor, RoundedCornerShape(12.dp))
                            .clickable(enabled = !isSubmitted) {
                                viewModel.selectQuizOption(optIndex)
                            }
                            .padding(14.dp)
                            .testTag("quiz_opt_$optIndex")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val optLetter = ('A' + optIndex).toString()
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(borderColor.copy(alpha = 0.3f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = optLetter,
                                    color = TextWhite,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = optionText,
                                color = TextWhite,
                                fontSize = 13.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Explanation on submission
                if (quizState.isSubmitted) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = BorderSlate.copy(alpha = 0.4f)),
                        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(NeonCyan.copy(alpha = 0.4f), NeonCyan.copy(alpha = 0.4f))))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = if (quizState.selectedOption == currentQ.correctIndex) "🎉 Correct Answer!" else "❌ Incorrect",
                                color = if (quizState.selectedOption == currentQ.correctIndex) EmeraldVictory else DangerRed,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = currentQ.explanation,
                                color = TextWhite,
                                fontSize = 12.sp,
                                lineHeight = 17.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Submit or Next Button
                if (!quizState.isSubmitted) {
                    Button(
                        onClick = { viewModel.submitQuizAnswer() },
                        enabled = quizState.selectedOption != null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("quiz_submit_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryNeonIndigo)
                    ) {
                        Text(
                            text = "Submit Answer",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    Button(
                        onClick = { viewModel.nextQuizQuestion() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("quiz_next_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                    ) {
                        Text(
                            text = if (quizState.currentIndex + 1 < quizState.questions.size) "Next Question" else "View Results",
                            color = MidnightNavy,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ResultRow(label: String, value: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = TextMuted, fontSize = 13.sp)
        Text(text = value, color = color, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

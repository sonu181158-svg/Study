package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CompetitiveQuestion
import com.example.data.model.QuestionCategory
import com.example.data.repository.CurriculumRepository
import com.example.ui.components.StudyTopBar
import com.example.ui.theme.*
import com.example.viewmodel.MainViewModel

@Composable
fun CompetitiveQuestionsScreen(
    chapterId: String,
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val chapter = CurriculumRepository.getChapterById(chapterId)
    val allQuestions = chapter?.competitiveQuestions ?: emptyList()

    var selectedCategory by remember { mutableStateOf<QuestionCategory?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredQuestions = remember(selectedCategory, searchQuery, allQuestions) {
        allQuestions.filter { q ->
            val matchCat = selectedCategory == null || q.category == selectedCategory
            val matchSearch = searchQuery.isBlank() ||
                    q.questionText.contains(searchQuery, ignoreCase = true) ||
                    q.targetExam.contains(searchQuery, ignoreCase = true)
            matchCat && matchSearch
        }
    }

    Scaffold(
        topBar = {
            StudyTopBar(
                title = "Competitive Question Bank",
                subtitle = "${allQuestions.size} Questions • ${chapter?.title ?: ""}",
                onBackClick = onBack
            )
        },
        containerColor = MidnightNavy
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Category Filter Chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    CategoryChip(
                        label = "All (${allQuestions.size})",
                        isSelected = selectedCategory == null,
                        onClick = { selectedCategory = null }
                    )
                }
                item {
                    CategoryChip(
                        label = "JEE / NEET / Olympiad",
                        isSelected = selectedCategory == QuestionCategory.JEE_NEET_OLYMPIAD,
                        onClick = { selectedCategory = QuestionCategory.JEE_NEET_OLYMPIAD }
                    )
                }
                item {
                    CategoryChip(
                        label = "HOT Topper",
                        isSelected = selectedCategory == QuestionCategory.HOT_BOARD_TOPPER,
                        onClick = { selectedCategory = QuestionCategory.HOT_BOARD_TOPPER }
                    )
                }
                item {
                    CategoryChip(
                        label = "Past Year Solved",
                        isSelected = selectedCategory == QuestionCategory.PAST_YEAR_SOLVED,
                        onClick = { selectedCategory = QuestionCategory.PAST_YEAR_SOLVED }
                    )
                }
                item {
                    CategoryChip(
                        label = "Foundation Drills",
                        isSelected = selectedCategory == QuestionCategory.FOUNDATION_DRILL,
                        onClick = { selectedCategory = QuestionCategory.FOUNDATION_DRILL }
                    )
                }
            }

            // Question Count & List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Showing ${filteredQuestions.size} Problems",
                            color = NeonCyanBright,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Tap options to check answers",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }

                itemsIndexed(filteredQuestions, key = { _, q -> q.id }) { index, question ->
                    CompetitiveQuestionCard(
                        question = question,
                        displayNumber = index + 1,
                        onBookmarkClick = {
                            viewModel.toggleBookmark(
                                itemType = "QUESTION",
                                id = question.id,
                                title = "Problem #${index + 1}: ${question.targetExam}",
                                subtitle = question.questionText.take(50),
                                snippet = question.detailedExplanation,
                                chapterId = chapterId,
                                grade = chapter?.grade ?: 10
                            )
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun CategoryChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) Brush.linearGradient(listOf(PrimaryNeonIndigo, NeonCyan))
                else Brush.linearGradient(listOf(CardNavy, CardNavy))
            )
            .border(
                1.dp,
                if (isSelected) NeonCyanBright else BorderSlate,
                RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 7.dp)
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.White else TextMuted,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
fun CompetitiveQuestionCard(
    question: CompetitiveQuestion,
    displayNumber: Int,
    onBookmarkClick: () -> Unit
) {
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var isAnswerRevealed by remember { mutableStateOf(false) }
    var showHint by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .testTag("question_card_${question.id}"),
        colors = CardDefaults.cardColors(containerColor = CardNavy),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BorderSlate, BorderSlate)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Badges Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Category Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(PrimaryNeonIndigo.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = question.targetExam,
                            color = PrimaryLightIndigo,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Difficulty Badge
                    val diffColor = when (question.difficulty) {
                        "Easy" -> EmeraldVictory
                        "Medium" -> NeonCyan
                        "Hard" -> FlameOrange
                        else -> DangerRed
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(diffColor.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = question.difficulty,
                            color = diffColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                IconButton(
                    onClick = onBookmarkClick,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.BookmarkBorder,
                        contentDescription = "Bookmark Question",
                        tint = TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Question Text
            Text(
                text = "$displayNumber. ${question.questionText}",
                color = TextWhite,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Interactive Options
            question.options.forEachIndexed { optIndex, optText ->
                val isSelected = selectedOption == optIndex
                val isCorrect = optIndex == question.correctIndex

                val borderColor = when {
                    isAnswerRevealed && isCorrect -> EmeraldVictory
                    isAnswerRevealed && isSelected && !isCorrect -> DangerRed
                    isSelected -> NeonCyanBright
                    else -> BorderSlate
                }

                val bgColor = when {
                    isAnswerRevealed && isCorrect -> EmeraldVictory.copy(alpha = 0.15f)
                    isAnswerRevealed && isSelected && !isCorrect -> DangerRed.copy(alpha = 0.15f)
                    isSelected -> PrimaryNeonIndigo.copy(alpha = 0.15f)
                    else -> BorderSlate.copy(alpha = 0.2f)
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(bgColor)
                        .border(1.dp, borderColor, RoundedCornerShape(10.dp))
                        .clickable {
                            selectedOption = optIndex
                            isAnswerRevealed = true
                        }
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val optLetter = ('A' + optIndex).toString()
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(borderColor.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = optLetter,
                                color = if (isAnswerRevealed && isCorrect) EmeraldVictory else TextWhite,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = optText,
                            color = TextWhite,
                            fontSize = 12.sp,
                            lineHeight = 17.sp,
                            modifier = Modifier.weight(1f)
                        )
                        if (isAnswerRevealed && isCorrect) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint = EmeraldVictory,
                                modifier = Modifier.size(18.dp)
                            )
                        } else if (isAnswerRevealed && isSelected && !isCorrect) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = null,
                                tint = DangerRed,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons: Hint & Detailed Solution
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = { showHint = !showHint },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = TrophyGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (showHint) "Hide Hint" else "Reveal Hint",
                        color = TrophyGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                TextButton(
                    onClick = { isAnswerRevealed = !isAnswerRevealed },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isAnswerRevealed) "Hide Solution" else "Step-by-Step Solution",
                        color = NeonCyanBright,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = if (isAnswerRevealed) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = NeonCyanBright,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Hint Drawer
            AnimatedVisibility(visible = showHint) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = TrophyGold.copy(alpha = 0.12f)),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(TrophyGold.copy(alpha = 0.4f), TrophyGold.copy(alpha = 0.4f))))
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            Icons.Default.HelpOutline,
                            contentDescription = null,
                            tint = TrophyGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = question.hint,
                            color = TrophyGoldBright,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // Detailed Solution Drawer
            AnimatedVisibility(visible = isAnswerRevealed) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = BorderSlate.copy(alpha = 0.35f)),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(NeonCyan.copy(alpha = 0.3f), NeonCyan.copy(alpha = 0.3f))))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Correct Answer: Option ${('A' + question.correctIndex)}",
                            color = EmeraldVictory,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = question.detailedExplanation,
                            color = TextWhite,
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                        if (question.formulaUsed != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Formula: ${question.formulaUsed}",
                                color = NeonCyanBright,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

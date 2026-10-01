package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Chapter
import com.example.data.repository.CurriculumRepository
import com.example.ui.components.StudyTopBar
import com.example.ui.theme.*
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.Screen

@Composable
fun SubjectDetailScreen(
    subjectId: String,
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onNavigate: (Screen) -> Unit
) {
    val grade = viewModel.userProfile.value?.selectedClass ?: 10
    val subjects = CurriculumRepository.getSubjectsForGrade(grade)
    val subject = subjects.find { it.id == subjectId } ?: subjects.first()
    val chapters = CurriculumRepository.getChaptersForSubject(subjectId)

    Scaffold(
        topBar = {
            StudyTopBar(
                title = subject.name,
                subtitle = "Class $grade • ${chapters.size} Comprehensive Chapters",
                onBackClick = onBack
            )
        },
        containerColor = MidnightNavy
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header stats banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardNavy),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BorderSlate, Color(subject.colorHex).copy(alpha = 0.5f))))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Course Overview & Curriculum",
                            color = TextWhite,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = subject.description,
                            color = TextMuted,
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            StatChip(label = "Chapters", value = "${chapters.size}", color = NeonCyanBright)
                            StatChip(label = "Questions", value = "100+ / Ch", color = TrophyGold)
                            StatChip(label = "PYQ Papers", value = "2020-2024", color = EmeraldVictory)
                            StatChip(label = "Offline Ready", value = "YES", color = PrimaryLightIndigo)
                        }
                    }
                }
            }

            // Chapter list items
            items(chapters) { chapter ->
                ChapterItemCard(
                    chapter = chapter,
                    subjectColor = Color(subject.colorHex),
                    onNotesClick = {
                        val firstTopicId = chapter.topicNotes.firstOrNull()?.id
                        onNavigate(Screen.TopicNotes(chapter.id, firstTopicId))
                    },
                    onQuestionsClick = {
                        onNavigate(Screen.CompetitiveBank(chapter.id))
                    },
                    onQuizClick = {
                        viewModel.startChapterQuiz(chapter)
                    },
                    onPyqClick = {
                        onNavigate(Screen.PyqPapers(chapter.id))
                    }
                )
            }
        }
    }
}

@Composable
fun StatChip(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = color,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 13.sp
        )
        Text(
            text = label,
            color = TextMuted,
            fontSize = 10.sp
        )
    }
}

@Composable
fun ChapterItemCard(
    chapter: Chapter,
    subjectColor: Color,
    onNotesClick: () -> Unit,
    onQuestionsClick: () -> Unit,
    onQuizClick: () -> Unit,
    onPyqClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .testTag("chapter_card_${chapter.id}"),
        colors = CardDefaults.cardColors(containerColor = CardNavy),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BorderSlate, BorderSlate)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(subjectColor.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "CHAPTER ${chapter.number}",
                        color = subjectColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = EmeraldVictory,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Complete",
                        color = EmeraldVictory,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = chapter.title,
                color = TextWhite,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = chapter.summary,
                color = TextMuted,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Action buttons row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ChapterActionButton(
                    icon = Icons.Default.MenuBook,
                    label = "Notes",
                    color = NeonCyan,
                    modifier = Modifier.weight(1f),
                    onClick = onNotesClick
                )

                ChapterActionButton(
                    icon = Icons.Default.FitnessCenter,
                    label = "100+ Qs",
                    color = TrophyGold,
                    modifier = Modifier.weight(1f),
                    onClick = onQuestionsClick
                )

                ChapterActionButton(
                    icon = Icons.Default.Bolt,
                    label = "Quiz",
                    color = PrimaryNeonIndigo,
                    modifier = Modifier.weight(1f),
                    onClick = onQuizClick
                )

                ChapterActionButton(
                    icon = Icons.Default.HistoryEdu,
                    label = "PYQs",
                    color = EmeraldVictory,
                    modifier = Modifier.weight(1f),
                    onClick = onPyqClick
                )
            }
        }
    }
}

@Composable
fun ChapterActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(38.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(color.copy(alpha = 0.15f))
            .border(1.dp, color.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .testTag("action_${label.lowercase()}"),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(15.dp)
            )
            Text(
                text = label,
                color = color,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

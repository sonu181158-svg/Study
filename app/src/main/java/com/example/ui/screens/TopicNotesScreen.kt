package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.example.data.local.OfflineBookmarkEntity
import com.example.data.model.TopicNote
import com.example.data.repository.CurriculumRepository
import com.example.ui.components.StudyTopBar
import com.example.ui.theme.*
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.Screen

@Composable
fun TopicNotesScreen(
    chapterId: String,
    initialTopicId: String?,
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onNavigateToTutor: (prefillPrompt: String) -> Unit
) {
    val chapter = CurriculumRepository.getChapterById(chapterId)
    val topicNotes = chapter?.topicNotes ?: emptyList()
    var selectedTopicId by remember {
        mutableStateOf(initialTopicId ?: topicNotes.firstOrNull()?.id ?: "")
    }

    val currentTopic = topicNotes.find { it.id == selectedTopicId } ?: topicNotes.firstOrNull()
    val isBookmarked by viewModel.progressRepo.isBookmarked(currentTopic?.id ?: "").collectAsState(initial = false)

    var showPersonalNotesDialog by remember { mutableStateOf(false) }
    var personalNoteText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            StudyTopBar(
                title = chapter?.title ?: "Topic Notes",
                subtitle = "Insanely Detailed Offline Study Notes",
                onBackClick = onBack,
                actions = {
                    if (currentTopic != null) {
                        IconButton(
                            onClick = {
                                viewModel.toggleBookmark(
                                    itemType = "NOTE",
                                    id = currentTopic.id,
                                    title = currentTopic.title,
                                    subtitle = chapter?.title ?: "",
                                    snippet = currentTopic.subtitle,
                                    chapterId = chapterId,
                                    grade = chapter?.grade ?: 10
                                )
                            },
                            modifier = Modifier.testTag("bookmark_note_button")
                        ) {
                            Icon(
                                imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Bookmark Note",
                                tint = if (isBookmarked) TrophyGold else TextWhite
                            )
                        }
                    }
                }
            )
        },
        containerColor = MidnightNavy
    ) { innerPadding ->
        if (currentTopic == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No notes available for this chapter yet.", color = TextMuted)
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Topic Switcher Tabs (if more than 1 topic)
            if (topicNotes.size > 1) {
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(topicNotes) { t ->
                            val isSelected = t.id == selectedTopicId
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
                                    .clickable { selectedTopicId = t.id }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = t.title.take(25) + if (t.title.length > 25) "..." else "",
                                    color = if (isSelected) Color.White else TextMuted,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            // Title & Subtitle Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardNavy),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BorderSlate, NeonCyan.copy(alpha = 0.5f))))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = currentTopic.title,
                            color = TextWhite,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = currentTopic.subtitle,
                            color = NeonCyanBright,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AssistChip(
                                onClick = {
                                    onNavigateToTutor("Explain ${currentTopic.title} from ${chapter?.title} with real life examples and common mistakes.")
                                },
                                label = { Text("Ask Dr. Athena AI", fontSize = 11.sp, color = TextWhite) },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.SmartToy,
                                        contentDescription = null,
                                        tint = NeonCyan,
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = NeonCyan.copy(alpha = 0.15f)
                                )
                            )

                            AssistChip(
                                onClick = { showPersonalNotesDialog = true },
                                label = { Text("My Notes", fontSize = 11.sp, color = TextWhite) },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.EditNote,
                                        contentDescription = null,
                                        tint = TrophyGold,
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = TrophyGold.copy(alpha = 0.15f)
                                )
                            )
                        }
                    }
                }
            }

            // 1. Key Concept Highlights
            item {
                NoteSectionContainer(
                    title = "Key Concepts & Fundamentals",
                    icon = Icons.Default.Lightbulb,
                    accentColor = TrophyGold
                ) {
                    currentTopic.keyConcepts.forEach { concept ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = "• ",
                                color = TrophyGold,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = concept,
                                color = TextWhite,
                                fontSize = 13.sp,
                                lineHeight = 19.sp
                            )
                        }
                    }
                }
            }

            // 2. Insanely Detailed Conceptual Content
            item {
                NoteSectionContainer(
                    title = "Deep Conceptual Analysis & Mechanisms",
                    icon = Icons.Default.AutoStories,
                    accentColor = NeonCyan
                ) {
                    Text(
                        text = currentTopic.detailedContent,
                        color = TextWhite,
                        fontSize = 13.sp,
                        lineHeight = 21.sp
                    )
                }
            }

            // 3. Real-World Applications & Examples
            if (currentTopic.realWorldExamples.isNotEmpty()) {
                item {
                    NoteSectionContainer(
                        title = "Real-World Examples & Daily Life Analogies",
                        icon = Icons.Default.Public,
                        accentColor = EmeraldVictory
                    ) {
                        currentTopic.realWorldExamples.forEachIndexed { idx, example ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = BorderSlate.copy(alpha = 0.35f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text(
                                        text = "${idx + 1}. ",
                                        color = EmeraldVictory,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = example,
                                        color = TextWhite,
                                        fontSize = 12.sp,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 4. Important Formulas & Definitions
            if (currentTopic.importantFormulasOrDefinitions.isNotEmpty()) {
                item {
                    NoteSectionContainer(
                        title = "Key Formulas & Rigorous Definitions",
                        icon = Icons.Default.Functions,
                        accentColor = PrimaryLightIndigo
                    ) {
                        currentTopic.importantFormulasOrDefinitions.forEach { formula ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = PrimaryNeonIndigo.copy(alpha = 0.15f)),
                                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(PrimaryNeonIndigo.copy(alpha = 0.4f), PrimaryNeonIndigo.copy(alpha = 0.4f))))
                            ) {
                                Text(
                                    text = formula,
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                    }
                }
            }

            // 5. Exam Traps & Topper Tips
            if (currentTopic.examTrapsAndTips.isNotEmpty()) {
                item {
                    NoteSectionContainer(
                        title = "Common Board Traps & Topper Tips",
                        icon = Icons.Default.WarningAmber,
                        accentColor = DangerRed
                    ) {
                        currentTopic.examTrapsAndTips.forEach { tip ->
                            val isTrap = tip.startsWith("TRAP")
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isTrap) DangerRed.copy(alpha = 0.15f) else EmeraldVictory.copy(alpha = 0.15f)
                                ),
                                border = CardDefaults.outlinedCardBorder().copy(
                                    brush = Brush.linearGradient(
                                        listOf(
                                            if (isTrap) DangerRed.copy(alpha = 0.4f) else EmeraldVictory.copy(alpha = 0.4f),
                                            if (isTrap) DangerRed.copy(alpha = 0.4f) else EmeraldVictory.copy(alpha = 0.4f)
                                        )
                                    )
                                )
                            ) {
                                Text(
                                    text = tip,
                                    color = if (isTrap) Color(0xFFFF8A8A) else Color(0xFFA7F3D0),
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    // Dialog for student to write personal notes on this topic
    if (showPersonalNotesDialog) {
        AlertDialog(
            onDismissRequest = { showPersonalNotesDialog = false },
            title = {
                Text(
                    text = "My Personal Topic Notes",
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column {
                    Text(
                        text = "Notes saved locally for offline revision.",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = personalNoteText,
                        onValueChange = { personalNoteText = it },
                        placeholder = { Text("Write your mnemonic, summary, or doubt here...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .testTag("personal_note_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = BorderSlate,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showPersonalNotesDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryNeonIndigo)
                ) {
                    Text("Save Offline")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPersonalNotesDialog = false }) {
                    Text("Cancel", color = TextMuted)
                }
            },
            containerColor = CardNavy
        )
    }
}

@Composable
fun NoteSectionContainer(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardNavy),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BorderSlate, accentColor.copy(alpha = 0.35f))))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(accentColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text(
                    text = title,
                    color = TextWhite,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            content()
        }
    }
}

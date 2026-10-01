package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.data.model.PyqPaper
import com.example.data.model.PyqQuestionItem
import com.example.data.repository.CurriculumRepository
import com.example.ui.components.StudyTopBar
import com.example.ui.theme.*

@Composable
fun PyqScreen(
    chapterId: String,
    onBack: () -> Unit
) {
    val chapter = CurriculumRepository.getChapterById(chapterId)
    val pyqPapers = chapter?.pyqPapers ?: emptyList()

    Scaffold(
        topBar = {
            StudyTopBar(
                title = "Past Year Question Papers",
                subtitle = "${chapter?.title ?: "PYQs"} • Board Marking Schemes",
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardNavy),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BorderSlate, EmeraldVictory.copy(alpha = 0.4f))))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.Verified,
                                contentDescription = null,
                                tint = EmeraldVictory,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Official Board Solutions & Marking Schemes",
                                color = TextWhite,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Learn exactly how examiners award marks step-by-step for full marks.",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            if (pyqPapers.isEmpty()) {
                item {
                    // Fallback practice questions with marking scheme
                    PyqPaperCard(
                        paper = PyqPaper(
                            id = "default_pyq_${chapterId}",
                            title = "CBSE Board Previous Years High-Yield Solved Paper",
                            board = "CBSE",
                            year = 2024,
                            grade = chapter?.grade ?: 10,
                            subjectName = chapter?.title ?: "Science",
                            durationMinutes = 60,
                            totalMarks = 25,
                            questions = listOf(
                                PyqQuestionItem(
                                    qNumber = 1,
                                    marks = 3,
                                    section = "Section B (Short Answer)",
                                    questionText = "State the core governing theorem of ${chapter?.title}. Give one practical observation and derive the boundary condition.",
                                    markingSchemeStep1 = "Statement of core principle with fundamental definition: [1 Mark]",
                                    markingSchemeStep2 = "Practical observation and experimental setup: [1 Mark]; Boundary derivation with mathematical formula: [1 Mark]",
                                    finalAnswer = "Full compliance with standard board syllabus criteria yields complete 3/3 marks."
                                ),
                                PyqQuestionItem(
                                    qNumber = 2,
                                    marks = 5,
                                    section = "Section C (Long Answer)",
                                    questionText = "Explain the fundamental mechanism involved in ${chapter?.title} with a well-labeled schematic diagram. Discuss two common industrial applications.",
                                    markingSchemeStep1 = "Neat labeled diagram: [2 Marks]; Step-by-step mechanism analysis: [2 Marks]",
                                    markingSchemeStep2 = "Two industrial applications with real-world examples: [1 Mark]",
                                    finalAnswer = "Refer to the detailed topic notes in this chapter for complete schematic and industrial application summaries."
                                )
                            )
                        )
                    )
                }
            } else {
                items(pyqPapers) { paper ->
                    PyqPaperCard(paper = paper)
                }
            }
        }
    }
}

@Composable
fun PyqPaperCard(paper: PyqPaper) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardNavy),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BorderSlate, BorderSlate)))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(EmeraldVictory.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${paper.board} • ${paper.year}",
                        color = EmeraldVictory,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }

                Text(
                    text = "${paper.durationMinutes} Mins • ${paper.totalMarks} Marks",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = paper.title,
                color = TextWhite,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(14.dp))

            paper.questions.forEach { q ->
                PyqQuestionItemView(item = q)
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
fun PyqQuestionItemView(item: PyqQuestionItem) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = BorderSlate.copy(alpha = 0.25f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Q${item.qNumber}. [${item.section}]",
                    color = NeonCyanBright,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${item.marks} Marks",
                    color = TrophyGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = item.questionText,
                color = TextWhite,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = { expanded = !expanded },
                contentPadding = PaddingValues(0.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (expanded) "Hide Step-by-Step Marking Scheme" else "Reveal Step-by-Step Marking Scheme",
                        color = EmeraldLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = EmeraldLight,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MidnightNavy.copy(alpha = 0.5f))
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Step 1 Marking:",
                        color = NeonCyanBright,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = item.markingSchemeStep1,
                        color = TextWhite,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )

                    Text(
                        text = "Step 2 Marking:",
                        color = NeonCyanBright,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = item.markingSchemeStep2,
                        color = TextWhite,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )

                    Text(
                        text = "Final Topper Answer:",
                        color = TrophyGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = item.finalAnswer,
                        color = Color(0xFFFDE68A),
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

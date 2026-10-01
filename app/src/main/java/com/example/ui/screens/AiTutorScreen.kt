package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.StudyTopBar
import com.example.ui.theme.*
import com.example.viewmodel.ChatMessage
import com.example.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@Composable
fun AiTutorScreen(
    viewModel: MainViewModel,
    prefillPrompt: String? = null,
    onBack: () -> Unit
) {
    val messages by viewModel.chatMessages.collectAsState()
    val isThinking by viewModel.isAiThinking.collectAsState()
    val profile by viewModel.userProfile.collectAsState()
    val grade = profile?.selectedClass ?: 10
    val board = profile?.selectedBoard ?: "CBSE"

    var inputQuery by remember { mutableStateOf(prefillPrompt ?: "") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    val suggestionChips = listOf(
        "📅 7-Day Study Plan for Class $grade",
        "⚡ Explain Ohm's Law & Circuits",
        "🌿 Photosynthesis Light & Dark Reactions",
        "📐 Quadratic Formula & Roots",
        "🧪 Redox: Oxidation vs Reduction",
        "🎯 Board Exam Topper Secrets"
    )

    Scaffold(
        topBar = {
            StudyTopBar(
                title = "Dr. Athena AI Tutor",
                subtitle = "Personalized Study Plans & Complex Doubts",
                onBackClick = onBack,
                actions = {
                    IconButton(
                        onClick = { viewModel.clearTutorChat() },
                        modifier = Modifier.testTag("clear_chat_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Clear Chat",
                            tint = TextWhite
                        )
                    }
                }
            )
        },
        containerColor = MidnightNavy
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Chat history list
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                contentPadding = PaddingValues(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header Mascot Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = CardNavy),
                        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BorderSlate, NeonCyan.copy(alpha = 0.4f))))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(Brush.radialGradient(listOf(NeonCyanBright, PrimaryNeonIndigo)))
                                    .border(1.5.dp, NeonCyan, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.study_hero_mascot_1790871898580),
                                    contentDescription = "Dr. Athena AI",
                                    modifier = Modifier.size(50.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = "Dr. Athena • AI Study Mentor",
                                    color = TextWhite,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Trained for Class $grade ($board). Ask questions, request personalized timetables, or verify tricky derivations.",
                                    color = TextMuted,
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }

                items(messages, key = { it.id }) { msg ->
                    ChatBubbleItem(message = msg)
                }

                if (isThinking) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = NeonCyanBright,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Dr. Athena is analyzing the concept...",
                                color = NeonCyanBright,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Suggestion chips row
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(suggestionChips) { chipText ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(CardNavy)
                            .border(1.dp, PrimaryNeonIndigo.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .clickable {
                                viewModel.sendTutorMessage(chipText)
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = chipText,
                            color = NeonCyanBright,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Chat input bar
            Surface(
                color = CardNavy,
                tonalElevation = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputQuery,
                        onValueChange = { inputQuery = it },
                        placeholder = { Text("Ask doubt or ask for a study plan...", fontSize = 13.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("ai_tutor_input"),
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = BorderSlate,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            if (inputQuery.isNotBlank()) {
                                val q = inputQuery.trim()
                                inputQuery = ""
                                viewModel.sendTutorMessage(q)
                            }
                        },
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(PrimaryNeonIndigo, NeonCyan)))
                            .testTag("ai_tutor_send_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubbleItem(message: ChatMessage) {
    val isUser = message.sender == "user"

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(PrimaryNeonIndigo),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.study_hero_mascot_1790871898580),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Box(
            modifier = Modifier
                .widthIn(max = 300.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 14.dp,
                        topEnd = 14.dp,
                        bottomStart = if (isUser) 14.dp else 2.dp,
                        bottomEnd = if (isUser) 2.dp else 14.dp
                    )
                )
                .background(
                    if (isUser) Brush.linearGradient(listOf(PrimaryNeonIndigo, PrimaryLightIndigo))
                    else Brush.linearGradient(listOf(CardNavy, CardNavy))
                )
                .border(
                    1.dp,
                    if (isUser) PrimaryLightIndigo else BorderSlate,
                    RoundedCornerShape(14.dp)
                )
                .padding(12.dp)
        ) {
            Text(
                text = message.text,
                color = TextWhite,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }
    }
}

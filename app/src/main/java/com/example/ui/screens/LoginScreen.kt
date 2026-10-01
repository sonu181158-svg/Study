package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    currentName: String = "",
    currentGrade: Int = 10,
    currentBoard: String = "CBSE",
    onLoginCompleted: (name: String, email: String, grade: Int, board: String, avatar: String) -> Unit
) {
    var name by remember { mutableStateOf(if (currentName.isNotBlank()) currentName else "Scholar Cadet") }
    var email by remember { mutableStateOf("cadet@studyg.edu") }
    var selectedGrade by remember { mutableIntStateOf(currentGrade) }
    var selectedBoard by remember { mutableStateOf(currentBoard) }
    var selectedAvatar by remember { mutableStateOf("grad_cap") }

    val boards = listOf("CBSE", "ICSE / ISC", "State Board")
    val classes = listOf(9, 10, 11, 12)

    val avatars = listOf(
        Pair("grad_cap", "🎓 Topper"),
        Pair("robot_owl", "🦉 Athena"),
        Pair("galaxy_bot", "🚀 Cosmo"),
        Pair("scholar_fox", "🦊 Spark")
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MidnightNavy)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // App Emblem & Title
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(Brush.radialGradient(listOf(NeonCyanBright, PrimaryNeonIndigo)))
                    .border(2.dp, NeonCyan, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.studyg_grad_hat_icon_1790872155610),
                    contentDescription = "STUDYg Graduation Cap",
                    modifier = Modifier.size(72.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "STUDYg",
                color = TextWhite,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )

            Text(
                text = "Gamified Learning Portal • Classes 9th to 12th",
                color = NeonCyanBright,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Card Form
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardNavy),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BorderSlate, PrimaryNeonIndigo.copy(alpha = 0.5f))))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Customize Your Scholar Profile",
                        color = TextWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Text(
                        text = "Content will be strictly scoped to your selected class.",
                        color = TextMuted,
                        fontSize = 12.sp,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Name Input
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Student Name") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = NeonCyan) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = BorderSlate,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("student_name_input")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Class Selector (9, 10, 11, 12)
                    Text(
                        text = "Select Your Class:",
                        color = TextWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        classes.forEach { gradeNum ->
                            val isSelected = selectedGrade == gradeNum
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) Brush.linearGradient(listOf(PrimaryNeonIndigo, NeonCyan))
                                        else Brush.linearGradient(listOf(BorderSlate.copy(alpha = 0.3f), BorderSlate.copy(alpha = 0.3f)))
                                    )
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) NeonCyanBright else BorderSlate,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { selectedGrade = gradeNum }
                                    .testTag("class_select_${gradeNum}"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Class $gradeNum",
                                    color = if (isSelected) Color.White else TextMuted,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Board Selector
                    Text(
                        text = "Select Your Board:",
                        color = TextWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        boards.forEach { boardName ->
                            val isSelected = selectedBoard == boardName
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) PrimaryNeonIndigo.copy(alpha = 0.35f)
                                        else BorderSlate.copy(alpha = 0.2f)
                                    )
                                    .border(
                                        width = if (isSelected) 1.5.dp else 1.dp,
                                        color = if (isSelected) NeonCyan else BorderSlate,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { selectedBoard = boardName }
                                    .testTag("board_select_${boardName.replace(" ", "_")}"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = boardName,
                                    color = if (isSelected) NeonCyanBright else TextMuted,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Avatar Picker
                    Text(
                        text = "Choose Your Avatar:",
                        color = TextWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        avatars.forEach { (avatarKey, label) ->
                            val isChosen = selectedAvatar == avatarKey
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { selectedAvatar = avatarKey }
                                    .padding(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(52.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isChosen) Brush.linearGradient(listOf(PrimaryNeonIndigo, NeonCyan))
                                            else Brush.linearGradient(listOf(BorderSlate, BorderSlate))
                                        )
                                        .border(
                                            2.dp,
                                            if (isChosen) TrophyGold else Color.Transparent,
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label.take(2),
                                        fontSize = 20.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = label.substringAfter(" "),
                                    color = if (isChosen) TextWhite else TextMuted,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Submit Button
                    Button(
                        onClick = {
                            val finalName = if (name.isBlank()) "Scholar" else name.trim()
                            onLoginCompleted(finalName, email, selectedGrade, selectedBoard, selectedAvatar)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("enter_studyg_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        contentPadding = PaddingValues()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(PrimaryNeonIndigo, NeonCyan)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.School,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                                Text(
                                    text = "Enter STUDYg Class $selectedGrade",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CloudOff,
                    contentDescription = null,
                    tint = EmeraldVictory,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Offline Mode Enabled for Remote Learners",
                    color = EmeraldVictory,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

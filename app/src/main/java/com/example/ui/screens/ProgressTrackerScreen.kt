package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AchievementBadge
import com.example.data.repository.CurriculumRepository
import com.example.data.repository.UserProgressRepository
import com.example.ui.components.StudyTopBar
import com.example.ui.components.XpProgressBar
import com.example.ui.theme.*
import com.example.viewmodel.MainViewModel

@Composable
fun ProgressTrackerScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val profile by viewModel.userProfile.collectAsState()
    val grade = profile?.selectedClass ?: 10
    val xp = profile?.xp ?: 200
    val battlesWon = profile?.battlesWon ?: 0
    val achievements = viewModel.progressRepo.getAchievements(xp, battlesWon)
    val subjects = CurriculumRepository.getSubjectsForGrade(grade)

    Scaffold(
        topBar = {
            StudyTopBar(
                title = "Study Growth & Achievements",
                subtitle = "Visualize Your Academic Evolution",
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
            // Level & Rank Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = CardNavy),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(PrimaryNeonIndigo, NeonCyan)))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = profile?.name ?: "Scholar",
                                    color = TextWhite,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Class $grade (${profile?.selectedBoard ?: "CBSE"})",
                                    color = NeonCyanBright,
                                    fontSize = 12.sp
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(TrophyGold.copy(alpha = 0.2f))
                                    .border(1.dp, TrophyGold, RoundedCornerShape(12.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "Total XP: $xp",
                                    color = TrophyGold,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        XpProgressBar(
                            currentXp = xp,
                            level = profile?.level ?: 1,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Streak Calendar Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardNavy),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BorderSlate, FlameOrange.copy(alpha = 0.4f))))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = FlameOrange)
                                Text(text = "${profile?.streakDays ?: 1}-Day Active Study Streak!", color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                            Text(text = "Keep going!", color = FlameOrange, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        val days = listOf("M", "T", "W", "T", "F", "S", "S")
                        val activeDays = (profile?.streakDays ?: 1).coerceAtMost(7)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            days.forEachIndexed { idx, dayLetter ->
                                val isActive = idx < activeDays
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(if (isActive) FlameOrange else BorderSlate.copy(alpha = 0.3f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isActive) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                        } else {
                                            Text(text = dayLetter, color = TextMuted, fontSize = 11.sp)
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = dayLetter, color = if (isActive) FlameOrange else TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Subject Mastery Radar
            item {
                Text(
                    text = "Subject Mastery Progress",
                    color = TextWhite,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            items(subjects) { subject ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CardNavy),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BorderSlate, Color(subject.colorHex).copy(alpha = 0.3f))))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = subject.name, color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(text = "78% Mastered", color = Color(subject.colorHex), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { 0.78f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = Color(subject.colorHex),
                            trackColor = BorderSlate
                        )
                    }
                }
            }

            // Achievements Showroom
            item {
                Text(
                    text = "Achievements Showroom",
                    color = TextWhite,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            items(achievements) { badge ->
                AchievementCardItem(badge = badge)
            }
        }
    }
}

@Composable
fun AchievementCardItem(badge: AchievementBadge) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardNavy),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(
                listOf(
                    BorderSlate,
                    if (badge.isUnlocked) TrophyGold.copy(alpha = 0.45f) else BorderSlate.copy(alpha = 0.2f)
                )
            )
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(
                        if (badge.isUnlocked) Brush.radialGradient(listOf(TrophyGold, FlameOrange))
                        else Brush.radialGradient(listOf(BorderSlate, BorderSlate))
                    ),
                contentAlignment = Alignment.Center
            ) {
                val icon = when (badge.iconType) {
                    "star" -> Icons.Default.Star
                    "fire" -> Icons.Default.LocalFireDepartment
                    "sword" -> Icons.Default.SportsKabaddi
                    "target" -> Icons.Default.TrackChanges
                    else -> Icons.Default.EmojiEvents
                }
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (badge.isUnlocked) Color.White else TextMuted,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = badge.title,
                    color = if (badge.isUnlocked) TextWhite else TextMuted,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = badge.description,
                    color = TextMuted,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "+${badge.xpReward} XP",
                    color = if (badge.isUnlocked) TrophyGoldBright else TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (badge.isUnlocked) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(EmeraldVictory.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "UNLOCKED",
                        color = EmeraldVictory,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            } else {
                Text(
                    text = "${badge.currentProgress}/${badge.requiredProgress}",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

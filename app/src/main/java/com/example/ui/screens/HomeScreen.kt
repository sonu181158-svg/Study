package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.UserProfileEntity
import com.example.data.model.Subject
import com.example.data.repository.CurriculumRepository
import com.example.ui.components.GamifiedStatBadge
import com.example.ui.components.XpProgressBar
import com.example.ui.theme.*
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.Screen

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    profile: UserProfileEntity?,
    onNavigate: (Screen) -> Unit
) {
    val grade = profile?.selectedClass ?: 10
    val board = profile?.selectedBoard ?: "CBSE"
    val subjects = CurriculumRepository.getSubjectsForGrade(grade)
    val quests = viewModel.progressRepo.getDailyQuests(profile?.xp ?: 200)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MidnightNavy),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // --- TOP GAMIFIED HEADER ---
        item {
            Surface(
                color = MidnightNavy,
                tonalElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(Brush.radialGradient(listOf(NeonCyanBright, PrimaryNeonIndigo)))
                                    .border(1.5.dp, NeonCyan, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.studyg_grad_hat_icon_1790872155610),
                                    contentDescription = "STUDYg Logo",
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "STUDYg",
                                    color = TextWhite,
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.5.sp
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable { onNavigate(Screen.Login) }
                                ) {
                                    Text(
                                        text = "Class $grade • $board",
                                        color = NeonCyanBright,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "Switch Class",
                                        tint = NeonCyanBright,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        // Stats: Streaks & Coins
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            GamifiedStatBadge(
                                icon = Icons.Default.LocalFireDepartment,
                                text = "${profile?.streakDays ?: 1}d",
                                color = FlameOrange
                            )
                            GamifiedStatBadge(
                                icon = Icons.Default.MonetizationOn,
                                text = "${profile?.coins ?: 100}",
                                color = TrophyGold
                            )
                            IconButton(
                                onClick = { onNavigate(Screen.Login) },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(BorderSlate)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Profile",
                                    tint = TextWhite,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // XP Progress & Level
                    XpProgressBar(
                        currentXp = profile?.xp ?: 150,
                        level = profile?.level ?: 1,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // --- HERO BANNER ---
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.5.dp, PrimaryLightIndigo.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.scholar_quest_hero_banner_1790871885992),
                    contentDescription = "Cosmic Study Arena",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    contentScale = ContentScale.Crop
                )

                // Dark gradient overlay for text legibility
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, MidnightNavy.copy(alpha = 0.92f))
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(14.dp)
                ) {
                    Text(
                        text = "100+ Competitive Questions & Live Duels",
                        color = TextWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Board topper notes, PYQs with marking schemes & AI tutor.",
                        color = NeonCyanBright,
                        fontSize = 11.sp
                    )
                }

                Button(
                    onClick = { viewModel.startArenaDuel() },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryNeonIndigo),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp)
                        .height(36.dp)
                        .testTag("hero_arena_duel_button")
                ) {
                    Icon(
                        Icons.Default.SportsKabaddi,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "1v1 Duel",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // --- QUICK GAME LAUNCHER CHIPS ---
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickActionCard(
                    title = "AI Tutor",
                    subtitle = "Dr. Athena",
                    icon = Icons.Default.SmartToy,
                    color = NeonCyan,
                    modifier = Modifier.weight(1f)
                ) {
                    onNavigate(Screen.AiTutor)
                }

                QuickActionCard(
                    title = "Quiz Arena",
                    subtitle = "Live 1v1",
                    icon = Icons.Default.Bolt,
                    color = TrophyGold,
                    modifier = Modifier.weight(1f)
                ) {
                    viewModel.startArenaDuel()
                }

                QuickActionCard(
                    title = "Growth",
                    subtitle = "Achievements",
                    icon = Icons.Default.EmojiEvents,
                    color = EmeraldVictory,
                    modifier = Modifier.weight(1f)
                ) {
                    onNavigate(Screen.ProgressTracker)
                }

                QuickActionCard(
                    title = "Offline",
                    subtitle = "Saved",
                    icon = Icons.Default.Bookmark,
                    color = PrimaryLightIndigo,
                    modifier = Modifier.weight(1f)
                ) {
                    onNavigate(Screen.BookmarksShowcase)
                }
            }
        }

        // --- DAILY QUESTS SECTION ---
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(
                            imageVector = Icons.Default.Flag,
                            contentDescription = null,
                            tint = TrophyGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Daily Quests",
                            color = TextWhite,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "Reset in 14h",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(quests) { quest ->
                        QuestCardItem(quest = quest)
                    }
                }
            }
        }

        // --- SUBJECTS HEADER ---
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Class $grade Subjects",
                        color = TextWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Detailed notes • 100+ Competitive bank • Past year papers",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // --- SUBJECT CARDS ---
        items(subjects) { subject ->
            SubjectListItemCard(
                subject = subject,
                onClick = { onNavigate(Screen.SubjectDetail(subject.id)) }
            )
        }
    }
}

@Composable
fun QuickActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .testTag("quick_${title.lowercase().replace(" ", "_")}"),
        colors = CardDefaults.cardColors(containerColor = CardNavy),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BorderSlate, color.copy(alpha = 0.4f))))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                color = TextWhite,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Text(
                text = subtitle,
                color = TextMuted,
                fontSize = 9.sp,
                maxLines = 1
            )
        }
    }
}

@Composable
fun QuestCardItem(quest: com.example.data.model.DailyQuest) {
    Card(
        modifier = Modifier
            .width(220.dp)
            .clip(RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = CardNavy),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BorderSlate, PrimaryNeonIndigo.copy(alpha = 0.3f))))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = quest.title,
                    color = TextWhite,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                if (quest.isClaimed) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(EmeraldVictory.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "CLAIMED",
                            color = EmeraldVictory,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = quest.description,
                color = TextMuted,
                fontSize = 11.sp,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "+${quest.xpReward} XP",
                        color = NeonCyanBright,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "+${quest.coinReward} 🪙",
                        color = TrophyGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "${quest.currentCount}/${quest.targetCount}",
                    color = TextWhite,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun SubjectListItemCard(
    subject: Subject,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag("subject_card_${subject.id}"),
        colors = CardDefaults.cardColors(containerColor = CardNavy),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BorderSlate, Color(subject.colorHex).copy(alpha = 0.4f))))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(subject.colorHex).copy(alpha = 0.2f))
                    .border(1.dp, Color(subject.colorHex).copy(alpha = 0.5f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                val icon = when (subject.iconName) {
                    "atom" -> Icons.Default.Science
                    "flask" -> Icons.Default.Biotech
                    "calculator" -> Icons.Default.Calculate
                    "leaf" -> Icons.Default.Eco
                    "code" -> Icons.Default.Terminal
                    "globe" -> Icons.Default.Public
                    else -> Icons.Default.MenuBook
                }
                Icon(
                    imageVector = icon,
                    contentDescription = subject.name,
                    tint = Color(subject.colorHex),
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = subject.name,
                    color = TextWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = subject.description,
                    color = TextMuted,
                    fontSize = 12.sp,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "${subject.totalChapters} Chapters",
                        color = NeonCyanBright,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "• 100+ Questions Each",
                        color = TrophyGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Open Subject",
                tint = TextMuted,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

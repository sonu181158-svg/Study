package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.StudyTopBar
import com.example.ui.theme.*
import com.example.viewmodel.MainViewModel

@Composable
fun ArenaScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val arenaState by viewModel.arenaState.collectAsState()
    val duelsHistory by viewModel.recentDuels.collectAsState()
    val profile by viewModel.userProfile.collectAsState()

    Scaffold(
        topBar = {
            StudyTopBar(
                title = "Quiz Duel Arena",
                subtitle = "1v1 Live Knowledge Battle with Peers",
                onBackClick = onBack
            )
        },
        containerColor = MidnightNavy
    ) { innerPadding ->
        when (arenaState.status) {
            "MATCHMAKING" -> {
                MatchmakingView(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }
            "BATTLING" -> {
                BattlingView(
                    arenaState = arenaState,
                    userName = profile?.name ?: "You",
                    onSelectOption = { viewModel.submitArenaAnswer(it) },
                    onNextRound = { viewModel.nextArenaRoundManual() },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }
            "VICTORY", "DEFEAT" -> {
                BattleOutcomeView(
                    arenaState = arenaState,
                    userName = profile?.name ?: "You",
                    onPlayAgain = { viewModel.startArenaDuel() },
                    onExit = onBack,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }
            else -> {
                // IDLE Home Lobby
                ArenaLobbyView(
                    battlesWon = profile?.battlesWon ?: 0,
                    battlesTotal = profile?.battlesTotal ?: 0,
                    duels = duelsHistory,
                    onStartDuel = { viewModel.startArenaDuel() },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }
        }
    }
}

@Composable
fun MatchmakingView(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "radar")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radar_angle"
    )

    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(140.dp)
                .clip(CircleShape)
                .background(CardNavy)
                .border(2.dp, NeonCyan.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .rotate(angle)
                    .clip(CircleShape)
                    .background(
                        Brush.sweepGradient(
                            listOf(Color.Transparent, NeonCyan.copy(alpha = 0.35f), NeonCyan)
                        )
                    )
            )

            Icon(
                imageVector = Icons.Default.SportsKabaddi,
                contentDescription = null,
                tint = TextWhite,
                modifier = Modifier.size(48.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Searching for Live Challenger...",
            color = TextWhite,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Connecting to fellow board candidates across cities...",
            color = NeonCyanBright,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun BattlingView(
    arenaState: com.example.viewmodel.ArenaState,
    userName: String,
    onSelectOption: (Int) -> Unit,
    onNextRound: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Duel Verses Bar
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardNavy),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(PrimaryNeonIndigo, TrophyGold)))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // User Player
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Brush.radialGradient(listOf(NeonCyanBright, PrimaryNeonIndigo))),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.studyg_grad_hat_icon_1790872155610),
                            contentDescription = null,
                            modifier = Modifier.size(34.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = userName, color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(text = "${arenaState.userScore} pts", color = NeonCyanBright, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                }

                // VS Badge & Timer
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(TrophyGold.copy(alpha = 0.2f))
                            .border(1.dp, TrophyGold, RoundedCornerShape(10.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(text = "ROUND ${arenaState.currentRound}/${arenaState.totalRounds}", color = TrophyGold, fontSize = 11.sp, fontWeight = FontWeight.Black)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "⏱️ ${arenaState.roundTimerSeconds}s",
                        color = if (arenaState.roundTimerSeconds <= 4) DangerRed else TextWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Opponent Player
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Brush.radialGradient(listOf(FlameOrange, DangerRed))),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.study_hero_mascot_1790871898580),
                            contentDescription = null,
                            modifier = Modifier.size(38.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = arenaState.opponentName, color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(text = "${arenaState.opponentScore} pts", color = TrophyGoldBright, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Question Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardNavy),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BorderSlate, BorderSlate)))
        ) {
            Text(
                text = arenaState.currentQuestion?.question ?: "Loading duel question...",
                color = TextWhite,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 22.sp,
                modifier = Modifier.padding(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Options
        val opts = arenaState.currentQuestion?.options ?: emptyList()
        val correctIdx = arenaState.currentQuestion?.correctIndex ?: -1

        opts.forEachIndexed { idx, optText ->
            val isSelected = arenaState.selectedOptionIndex == idx
            val isRevealed = arenaState.hasAnswered
            val isCorrect = idx == correctIdx

            val borderColor = when {
                isRevealed && isCorrect -> EmeraldVictory
                isRevealed && isSelected && !isCorrect -> DangerRed
                isSelected -> NeonCyanBright
                else -> BorderSlate
            }

            val bgColor = when {
                isRevealed && isCorrect -> EmeraldVictory.copy(alpha = 0.18f)
                isRevealed && isSelected && !isCorrect -> DangerRed.copy(alpha = 0.18f)
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
                    .clickable(enabled = !arenaState.hasAnswered) {
                        onSelectOption(idx)
                    }
                    .padding(14.dp)
                    .testTag("arena_opt_$idx")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val optLetter = ('A' + idx).toString()
                    Text(
                        text = "$optLetter.",
                        color = if (isRevealed && isCorrect) EmeraldVictory else TextMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = optText,
                        color = TextWhite,
                        fontSize = 13.sp,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        if (arenaState.hasAnswered) {
            Button(
                onClick = onNextRound,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("arena_next_round_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryNeonIndigo)
            ) {
                Text(
                    text = if (arenaState.currentRound < arenaState.totalRounds) "Next Round ⚡" else "Finish Duel 🏆",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun BattleOutcomeView(
    arenaState: com.example.viewmodel.ArenaState,
    userName: String,
    onPlayAgain: () -> Unit,
    onExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isWin = arenaState.status == "VICTORY"

    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        if (isWin) listOf(TrophyGold, FlameOrange) else listOf(BorderSlate, DangerRed)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isWin) Icons.Default.EmojiEvents else Icons.Default.Shield,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(52.dp)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = if (isWin) "VICTORY!" else "HONORABLE DUEL!",
            color = if (isWin) TrophyGold else TextWhite,
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp
        )

        Text(
            text = if (isWin) "You outsmarted ${arenaState.opponentName} in battle!" else "Close match against ${arenaState.opponentName}!",
            color = NeonCyanBright,
            fontSize = 13.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardNavy),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BorderSlate, BorderSlate)))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ResultRow(label = "$userName Score", value = "${arenaState.userScore} pts", color = NeonCyanBright)
                ResultRow(label = "${arenaState.opponentName} Score", value = "${arenaState.opponentScore} pts", color = TrophyGold)
                ResultRow(label = "XP Earned", value = if (isWin) "+120 XP" else "+40 XP", color = EmeraldVictory)
                ResultRow(label = "Coins Earned", value = if (isWin) "+60 🪙" else "+15 🪙", color = TrophyGoldBright)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onPlayAgain,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("play_another_duel_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryNeonIndigo)
        ) {
            Text("Find Another Challenger ⚔️", color = Color.White, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = onExit,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Return to Hub", color = TextWhite)
        }
    }
}

@Composable
fun ArenaLobbyView(
    battlesWon: Int,
    battlesTotal: Int,
    duels: List<com.example.data.local.DuelHistoryEntity>,
    onStartDuel: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
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
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "1v1 Knowledge Battles",
                        color = TextWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Compete in 5-round rapid fire quizzes with random students from your class.",
                        color = TextMuted,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "$battlesWon", color = TrophyGold, fontSize = 22.sp, fontWeight = FontWeight.Black)
                            Text(text = "Battles Won", color = TextMuted, fontSize = 11.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "$battlesTotal", color = NeonCyanBright, fontSize = 22.sp, fontWeight = FontWeight.Black)
                            Text(text = "Total Duels", color = TextMuted, fontSize = 11.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            val winRate = if (battlesTotal > 0) ((battlesWon * 100) / battlesTotal) else 100
                            Text(text = "$winRate%", color = EmeraldVictory, fontSize = 22.sp, fontWeight = FontWeight.Black)
                            Text(text = "Win Rate", color = TextMuted, fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = onStartDuel,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("start_matchmaking_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryNeonIndigo)
                    ) {
                        Icon(Icons.Default.FlashOn, contentDescription = null, tint = TrophyGold)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Find Match Now ⚔️", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
            }
        }

        item {
            Text(
                text = "Recent Battles",
                color = TextWhite,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (duels.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = BorderSlate.copy(alpha = 0.2f))
                ) {
                    Text(
                        text = "No recent duels yet. Tap 'Find Match Now' to jump into your first 1v1 quiz battle!",
                        color = TextMuted,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            items(duels) { duel ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CardNavy),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.linearGradient(
                            listOf(BorderSlate, if (duel.isWin) EmeraldVictory.copy(alpha = 0.4f) else DangerRed.copy(alpha = 0.4f))
                        )
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "vs ${duel.opponentName}",
                                color = TextWhite,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${duel.userScore} - ${duel.opponentScore} pts",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (duel.isWin) EmeraldVictory.copy(alpha = 0.2f) else DangerRed.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (duel.isWin) "WIN (+${duel.xpGained} XP)" else "LOSS",
                                color = if (duel.isWin) EmeraldVictory else DangerRed,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.OfflineBookmarkEntity
import com.example.ui.components.StudyTopBar
import com.example.ui.theme.*
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.Screen

@Composable
fun BookmarksScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onNavigate: (Screen) -> Unit
) {
    val bookmarks by viewModel.allBookmarks.collectAsState()

    Scaffold(
        topBar = {
            StudyTopBar(
                title = "Offline Knowledge Vault",
                subtitle = "${bookmarks.size} Bookmarked Items (100% Offline)",
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
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardNavy),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BorderSlate, EmeraldVictory.copy(alpha = 0.4f))))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(EmeraldVictory.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.CloudOff,
                                contentDescription = null,
                                tint = EmeraldVictory,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Zero Internet Required",
                                color = TextWhite,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "All bookmarked topics and questions are stored on-device for uninterrupted remote learning.",
                                color = TextMuted,
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }

            if (bookmarks.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = CardNavy)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.BookmarkBorder,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "No Offline Bookmarks Yet",
                                color = TextWhite,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Tap the bookmark icon on any Topic Note or Competitive Question to save it here for offline revision!",
                                color = TextMuted,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(bookmarks) { item ->
                    BookmarkListItem(
                        item = item,
                        onClick = {
                            if (item.itemType == "NOTE") {
                                onNavigate(Screen.TopicNotes(item.chapterId, item.id))
                            } else {
                                onNavigate(Screen.CompetitiveBank(item.chapterId))
                            }
                        },
                        onDelete = {
                            viewModel.toggleBookmark(
                                itemType = item.itemType,
                                id = item.id,
                                title = item.title,
                                subtitle = item.subtitle,
                                snippet = item.contentSnippet,
                                chapterId = item.chapterId,
                                grade = item.grade
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun BookmarkListItem(
    item: OfflineBookmarkEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = CardNavy),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(BorderSlate, BorderSlate)))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (item.itemType == "NOTE") NeonCyan.copy(alpha = 0.2f) else TrophyGold.copy(alpha = 0.2f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (item.itemType == "NOTE") Icons.Default.MenuBook else Icons.Default.HelpOutline,
                    contentDescription = null,
                    tint = if (item.itemType == "NOTE") NeonCyan else TrophyGold,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    color = TextWhite,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Text(
                    text = item.subtitle,
                    color = TextMuted,
                    fontSize = 11.sp,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.contentSnippet,
                    color = NeonCyanBright,
                    fontSize = 10.sp,
                    maxLines = 2
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Remove Bookmark",
                    tint = DangerRed,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

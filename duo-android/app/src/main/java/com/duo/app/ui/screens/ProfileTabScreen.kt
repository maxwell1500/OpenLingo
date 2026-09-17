package com.duo.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duo.app.ui.Achievement

/**
 * Profile tab: identity header, lifetime stats from local tables,
 * answer-accuracy bar, and the achievements trophy shelf.
 * Stateless — all values are collected by the caller.
 */
@Composable
fun ProfileTabScreen(
    userName: String,
    courseName: String,
    points: Int,
    streak: Int,
    completedLessons: Int,
    kanaCount: Int,
    accuracyPercent: Int,
    achievements: List<Pair<Achievement, Boolean>>,
    typeStats: List<com.duo.app.data.local.entities.ExerciseTypeStatsEntity> = emptyList(),
    modifier: Modifier = Modifier,
) {
    val unlocked = achievements.count { it.second }
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Identity header
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1CB0F6)),
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = "🦫", fontSize = 48.sp)
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = userName,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                    Text(
                        text = "Learning $courseName • Free forever",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.85f),
                    )
                }
            }
        }

        // Stats grid
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(emoji = "⚡", value = "$points", label = "Total XP", modifier = Modifier.weight(1f))
            StatCard(emoji = "🔥", value = "$streak", label = "Day streak", modifier = Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(emoji = "📖", value = "$completedLessons", label = "Lessons done", modifier = Modifier.weight(1f))
            StatCard(emoji = "🈁", value = "$kanaCount", label = "Kana mastered", modifier = Modifier.weight(1f))
        }

        // Accuracy
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F7F7)),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(text = "🎯 Answer accuracy", fontWeight = FontWeight.Bold)
                    Text(
                        text = "$accuracyPercent%",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF58CC02),
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { accuracyPercent / 100f },
                    modifier = Modifier.fillMaxWidth().height(10.dp),
                    color = Color(0xFF58CC02),
                    trackColor = Color(0xFFE5E5E5),
                )
            }
        }

        // Skill Strengths & Weak Areas
        if (typeStats.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F7F7)),
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "📊 Exercise Type Accuracy",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall,
                    )
                    val sortedStats = typeStats.sortedBy { it.accuracyPercent }
                    sortedStats.forEach { stat ->
                        val readableName = when (stat.type) {
                            "SELECT" -> "Multiple Choice"
                            "ASSIST" -> "Vocabulary Assist"
                            "WORD_BANK" -> "Sentence Builder"
                            "LISTEN" -> "Listening Comprehension"
                            "MATCH_PAIRS" -> "Matching Pairs"
                            "STORY" -> "Story Comprehension"
                            else -> stat.type
                        }
                        val acc = stat.accuracyPercent
                        val barColor = when {
                            acc >= 80 -> Color(0xFF58CC02)
                            acc >= 60 -> Color(0xFFFFC800)
                            else -> Color(0xFFFF4B4B)
                        }
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(text = readableName, fontSize = 12.sp, color = Color(0xFF4B4B4B))
                                Text(
                                    text = "$acc% (${stat.correct}/${stat.attempts})",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = barColor,
                                )
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            LinearProgressIndicator(
                                progress = { acc / 100f },
                                modifier = Modifier.fillMaxWidth().height(6.dp),
                                color = barColor,
                                trackColor = Color(0xFFE5E5E5),
                            )
                        }
                    }
                }
            }
        }

        // Trophy shelf
        Text(
            text = "🏆 Achievements ($unlocked/${achievements.size})",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        achievements.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { (achievement, isUnlocked) ->
                    AchievementCard(
                        achievement = achievement,
                        isUnlocked = isUnlocked,
                        modifier = Modifier.weight(1f),
                    )
                }
                if (row.size == 1) Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun StatCard(emoji: String, value: String, label: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F7F7)),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = emoji, fontSize = 28.sp)
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF777777),
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun AchievementCard(
    achievement: Achievement,
    isUnlocked: Boolean,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.alpha(if (isUnlocked) 1f else 0.55f),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) Color(0xFFFFF6DB) else Color(0xFFF7F7F7),
        ),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = if (isUnlocked) achievement.emoji else "🔒", fontSize = 30.sp)
            Text(
                text = achievement.title,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
            )
            Text(
                text = achievement.description,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF777777),
                textAlign = TextAlign.Center,
            )
        }
    }
}

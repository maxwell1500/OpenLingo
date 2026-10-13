package com.openlingo.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.openlingo.app.data.local.entities.CourseEntity
import com.openlingo.app.data.local.entities.UserProgressEntity
import com.openlingo.app.data.repository.LocalProgressRepository
import com.openlingo.app.ui.MainTab
import com.openlingo.app.ui.screens.HeartRefillSheet

@Composable
internal fun OpenLingoBottomNavigationBar(
    currentTab: MainTab,
    isJapanese: Boolean,
    onSelectTab: (MainTab) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // 1. Learn Tab
            val isLearn = currentTab is MainTab.Learn
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { onSelectTab(MainTab.Learn) }
                    .padding(horizontal = 20.dp, vertical = 4.dp),
            ) {
                Text(text = "🏠", fontSize = 22.sp)
                Text(
                    text = "Learn",
                    fontSize = 12.sp,
                    fontWeight = if (isLearn) FontWeight.Bold else FontWeight.Medium,
                    color = if (isLearn) Color(0xFF2E9E6B) else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            // 2. Characters Tab (Japanese only)
            if (isJapanese) {
                val isChars = currentTab is MainTab.Characters
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { onSelectTab(MainTab.Characters) }
                        .padding(horizontal = 20.dp, vertical = 4.dp),
                ) {
                    Text(
                        text = "あ",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isChars) Color(0xFF3D7EA6) else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = "Characters",
                        fontSize = 12.sp,
                        fontWeight = if (isChars) FontWeight.Bold else FontWeight.Medium,
                        color = if (isChars) Color(0xFF3D7EA6) else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            // 3. Practice / Dumbbell Tab
            val isPractice = currentTab is MainTab.Practice
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { onSelectTab(MainTab.Practice) }
                    .padding(horizontal = 20.dp, vertical = 4.dp),
            ) {
                Text(text = "🏋️", fontSize = 22.sp)
                Text(
                    text = "Practice",
                    fontSize = 12.sp,
                    fontWeight = if (isPractice) FontWeight.Bold else FontWeight.Medium,
                    color = if (isPractice) Color(0xFF3D7EA6) else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            // 4. Profile Tab
            val isProfile = currentTab is MainTab.Profile
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { onSelectTab(MainTab.Profile) }
                    .padding(horizontal = 20.dp, vertical = 4.dp),
            ) {
                Text(text = "👤", fontSize = 22.sp)
                Text(
                    text = "Profile",
                    fontSize = 12.sp,
                    fontWeight = if (isProfile) FontWeight.Bold else FontWeight.Medium,
                    color = if (isProfile) Color(0xFF3D7EA6) else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

// -------------------------------------------------------------------------
// Top App Bar: Course Switcher, Streak, XP, Hearts
// -------------------------------------------------------------------------
@Composable
internal fun OpenLingoTopAppBar(
    courses: List<CourseEntity>,
    userProgress: UserProgressEntity?,
    mistakeCount: Int,
    onSelectCourse: (Int) -> Unit,
    onRefillHearts: () -> Unit,
    onStartMistakePractice: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    val activeCourseId = userProgress?.activeCourseId ?: 1
    val hearts = userProgress?.hearts ?: 5
    val points = userProgress?.points ?: 0
    val streak = userProgress?.streak ?: 1
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                courses.forEach { course ->
                    val isSelected = course.id == activeCourseId
                    Box(
                        modifier = Modifier
                            .background(
                                color = if (isSelected) Color(0xFFE5F5FF) else MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(12.dp),
                            )
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) Color(0xFF3D7EA6) else MaterialTheme.colorScheme.outlineVariant,
                                shape = RoundedCornerShape(12.dp),
                            )
                            .clickable { onSelectCourse(course.id) }
                            .padding(horizontal = 8.dp, vertical = 5.dp),
                    ) {
                        Text(
                            text = "${course.imageSrc} ${course.title}",
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color(0xFF1899D6) else MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            }

            // Stats: Points & Hearts
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                // Streak Flame Badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🔥", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$streak",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE67E22),
                        fontSize = 15.sp,
                    )
                }

                // XP
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "⚡", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$points",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE8A93C),
                        fontSize = 15.sp,
                    )
                }

                // Hearts. A pill that reads `❤️ 5/5` and opens a sheet — no green
                // `+` badge sitting on the emoji and no 1.25x pulse. The pulse was
                // an attention trick that fired on every frame the learner spent on
                // this bar, and the `+` overlapped the glyph it was annotating, so
                // a refilled heart still looked half-empty. Refilling is free and
                // always available, so the badge's job is to *explain* hearts, not
                // to beg.
                var showHearts by remember { mutableStateOf(false) }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .background(
                            color = Color(0xFFFDEDEC),
                            shape = RoundedCornerShape(999.dp),
                        )
                        .clickable { showHearts = true }
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                ) {
                    Text(text = "❤️", fontSize = 14.sp)
                    Text(
                        text = "$hearts/${LocalProgressRepository.MAX_HEARTS}",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFC0392B),
                        fontSize = 14.sp,
                    )
                }

                if (showHearts) {
                    HeartRefillSheet(
                        hearts = hearts,
                        mistakeCount = mistakeCount,
                        onRefill = {
                            showHearts = false
                            onRefillHearts()
                        },
                        onPracticeMistakes = {
                            showHearts = false
                            onStartMistakePractice()
                        },
                        onDismiss = { showHearts = false },
                    )
                }

                // Settings gear
                Box(
                    modifier = Modifier
                        .background(Color(0xFFF0F0F0), CircleShape)
                        .clickable(onClick = onOpenSettings)
                        .padding(horizontal = 6.dp, vertical = 4.dp),
                ) {
                    Text(text = "⚙️", fontSize = 14.sp)
                }
            }
        }
    }
}

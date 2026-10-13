package com.openlingo.app.ui.screens


import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.widthIn
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.openlingo.app.data.local.entities.LessonEntity
import com.openlingo.app.data.local.entities.UnitWithLessons
import com.openlingo.app.data.repository.DailyQuest

// -------------------------------------------------------------------------
// Screen 1: Lesson Map (Units, Lesson Nodes, Offline Info Banner)
// -------------------------------------------------------------------------

private enum class LessonState {
    COMPLETED,
    ACTIVE,
    LOCKED,
}

@Composable
internal fun LessonMapScreen(
    unitsWithLessons: List<UnitWithLessons>,
    unitRuleTexts: Map<Int, List<String>>,
    unitVocabCounts: Map<Int, Int>,
    onOpenUnitVocabulary: (Int) -> Unit,
    unitDrillCounts: Map<Int, Int>,
    onOpenDrills: (Int) -> Unit,
    completedChallengeIds: Set<Int>,
    completedLessonIds: Set<Int>,
    onStartLesson: (Int) -> Unit,
    quest: DailyQuest,
    brokenStreak: Int,
    courseId: Int,
    onStartCheckpoint: (String) -> Unit,
) {
    val scrollState = rememberScrollState()
    val allLessons = remember(unitsWithLessons) {
        unitsWithLessons.sortedBy { it.unit.orderIndex }
            .flatMap { it.lessons.sortedBy { l -> l.orderIndex } }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .widthIn(max = 640.dp)
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        // Daily quest card
        val questDone = quest.isComplete
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (questDone) Color(0xFFD7FFB8) else Color(0xFFFFF6DB),
            ),
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = if (questDone) "✅ Daily quest complete!" else "🎯 Daily quest",
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "${quest.xp}/${quest.goal} XP",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E9E6B),
                    )
                }
                LinearProgressIndicator(
                    progress = { (quest.xp.toFloat() / quest.goal).coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth().height(10.dp),
                    color = Color(0xFF2E9E6B),
                    trackColor = Color(0xFFE5E5E5),
                )
            }
        }

        // Streak repair card: earn the broken streak back with a replay.
        if (brokenStreak > 0) {
            val repairLessonId = unitsWithLessons.firstOrNull()?.lessons?.firstOrNull()?.id
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFDFE0)),
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "💔 Repair your $brokenStreak-day streak",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFC0392B),
                    )
                    Text(
                        text = "Replay the first lesson to earn it back — no tap-to-fix.",
                        fontSize = 13.sp,
                        color = Color(0xFF4B4B4B),
                    )
                    if (repairLessonId != null) {
                        Button(
                            onClick = { onStartLesson(repairLessonId) },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC0392B)),
                        ) {
                            Text(text = "START REPAIR LESSON", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
        unitsWithLessons.forEach { unitWithLessons ->
            UnitSection(
                unitWithLessons = unitWithLessons,
                ruleTexts = unitRuleTexts[unitWithLessons.unit.id].orEmpty(),
                vocabCount = unitVocabCounts[unitWithLessons.unit.id] ?: 0,
                onOpenVocabulary = { onOpenUnitVocabulary(unitWithLessons.unit.id) },
                drillCount = unitDrillCounts[unitWithLessons.unit.id] ?: 0,
                onOpenDrills = { onOpenDrills(unitWithLessons.unit.id) },
                allLessons = allLessons,
                completedLessonIds = completedLessonIds,
                onStartLesson = onStartLesson,
            )
        }

        // Checkpoint tests section
        val levels = if (courseId == 1) listOf("A1", "A2", "B1") else listOf("N5", "N4")
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
            ),
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "📝 Checkpoint Tests",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = "Test your mastery of each level. Missed questions are added to your practice list.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    levels.forEach { level ->
                        Button(
                            onClick = { onStartCheckpoint(level) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        ) {
                            Text(text = "$level Test", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Offline data banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F7F7)),
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(text = "💾", fontSize = 24.sp)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "100% Offline",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF4B4B4B),
                    )
                    Text(
                        text = "All XP, hearts, and lessons save automatically to your phone. Export a backup in Settings to move them to another device.",
                        fontSize = 12.sp,
                        color = Color(0xFF777777),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun UnitSection(
    unitWithLessons: UnitWithLessons,
    ruleTexts: List<String>,
    vocabCount: Int,
    onOpenVocabulary: () -> Unit,
    drillCount: Int,
    onOpenDrills: () -> Unit,
    allLessons: List<LessonEntity>,
    completedLessonIds: Set<Int>,
    onStartLesson: (Int) -> Unit,
) {
    val unit = unitWithLessons.unit
    val lessons = unitWithLessons.lessons
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Unit Header Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2E9E6B)),
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = unit.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
                Spacer(modifier = Modifier.height(4.dp))
                // WI-01b: once a unit's challenges carry rule text, the header explains the
                // grammar up front instead of only naming the unit. Units with no authored
                // rule yet render the one-line description exactly as before.
                if (ruleTexts.isEmpty()) {
                    Text(
                        text = unit.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFD7FFB8),
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "GRAMMAR",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE8FFD0),
                        )
                        ruleTexts.forEach { rule ->
                            Text(
                                text = rule,
                                style = MaterialTheme.typography.bodySmall,
                                lineHeight = 17.sp,
                                color = Color(0xFFFFFFFF),
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                // WI-12: the unit's vocabulary was previously only reachable as implicit
                // challenge options and FSRS rows. A unit with nothing scheduled for it
                // says so here rather than offering a tap that opens an empty screen.
                // FlowRow, not Row: this header now carries two chips under a rule
                // block that can be several lines long, so at a large font scale a
                // plain Row would overflow the card and clip. FlowRow wraps the second
                // chip onto its own line instead, at any font scale and any card width.
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF4CAF00))
                            .clickable(onClick = onOpenVocabulary)
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                    ) {
                        Text(
                            text = "📖",
                            fontSize = 13.sp,
                        )
                        Text(
                            text = if (vocabCount > 0) {
                                "VOCABULARY · $vocabCount ${if (vocabCount == 1) "word" else "words"}"
                            } else {
                                "VOCABULARY · NOT YET ADDED"
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                        )
                    }
                    // The generated conjugation drill, beside the vocabulary list because
                    // both are study surfaces this unit's own challenges make possible.
                    // A unit that teaches no drillable form says so here rather than
                    // offering a tap that opens an empty table.
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF4CAF00))
                            .clickable(onClick = onOpenDrills)
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                    ) {
                        Text(
                            text = "🔤",
                            fontSize = 13.sp,
                        )
                        Text(
                            text = if (drillCount > 0) {
                                "DRILLS · $drillCount ${if (drillCount == 1) "form" else "forms"}"
                            } else {
                                "DRILLS · NONE YET"
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                        )
                    }
                }
            }
        }

        // Lessons in this Unit (S-curve winding path)
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            val xOffsets = listOf(0.dp, (-46).dp, 0.dp, 46.dp)
            lessons.forEach { lesson ->
                val lessonIndex = allLessons.indexOfFirst { it.id == lesson.id }.coerceAtLeast(0)
                val horizontalOffset = xOffsets[lessonIndex % xOffsets.size]
                val state = when {
                    completedLessonIds.contains(lesson.id) -> LessonState.COMPLETED
                    lessonIndex <= 0 -> LessonState.ACTIVE
                    allLessons.getOrNull(lessonIndex - 1)?.let { completedLessonIds.contains(it.id) } == true -> LessonState.ACTIVE
                    else -> LessonState.LOCKED
                }
                LessonNode(
                    lesson = lesson,
                    state = state,
                    horizontalOffset = horizontalOffset,
                    onStart = { onStartLesson(lesson.id) },
                )
            }
        }
    }
}

@Composable
private fun LessonNode(
    lesson: LessonEntity,
    state: LessonState,
    horizontalOffset: androidx.compose.ui.unit.Dp,
    onStart: () -> Unit,
) {
    val context = LocalContext.current
    val pulse = rememberInfiniteTransition(label = "activePulse")
    val scale by pulse.animateFloat(
        initialValue = 1f,
        targetValue = if (state == LessonState.ACTIVE) 1.08f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900),
            repeatMode = androidx.compose.animation.core.RepeatMode.Reverse,
        ),
        label = "lessonScale",
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .offset(x = horizontalOffset)
            .padding(vertical = 4.dp),
    ) {
        // Floating "START" chip for active lesson
        if (state == LessonState.ACTIVE) {
            Box(
                modifier = Modifier
                    .background(Color(0xFF2E9E6B), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 2.dp),
            ) {
                Text(
                    text = "START",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 10.sp,
                    letterSpacing = 1.sp,
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
        }

        Box(
            modifier = Modifier
                .size(74.dp)
                .scale(scale)
                .background(
                    color = when (state) {
                        LessonState.COMPLETED -> Color(0xFFE8A93C)
                        LessonState.ACTIVE -> Color(0xFF2E9E6B)
                        LessonState.LOCKED -> Color(0xFFE5E5E5)
                    },
                    shape = CircleShape,
                )
                .border(
                    width = 4.dp,
                    color = when (state) {
                        LessonState.COMPLETED -> Color(0xFFE5A800)
                        LessonState.ACTIVE -> Color(0xFF46A302)
                        LessonState.LOCKED -> Color(0xFFCCCCCC)
                    },
                    shape = CircleShape,
                )
                .clickable {
                    when (state) {
                        LessonState.COMPLETED, LessonState.ACTIVE -> onStart()
                        LessonState.LOCKED -> {
                            android.widget.Toast.makeText(
                                context,
                                "Complete previous lesson to unlock 🔒",
                                android.widget.Toast.LENGTH_SHORT,
                            ).show()
                        }
                    }
                },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = when (state) {
                    LessonState.COMPLETED -> "👑"
                    LessonState.ACTIVE -> "⭐"
                    LessonState.LOCKED -> "🔒"
                },
                fontSize = 32.sp,
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = lesson.title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (state == LessonState.ACTIVE) FontWeight.ExtraBold else FontWeight.Bold,
            color = when (state) {
                LessonState.LOCKED -> Color(0xFFAAAAAA)
                LessonState.COMPLETED -> Color(0xFF4B4B4B)
                LessonState.ACTIVE -> Color(0xFF3D7EA6)
            },
        )
        if (state == LessonState.COMPLETED) {
            Text(
                text = "Completed ✓",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF2E9E6B),
            )
        }
    }
}

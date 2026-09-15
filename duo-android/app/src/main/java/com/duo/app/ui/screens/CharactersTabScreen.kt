package com.duo.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
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
import com.duo.app.data.local.character.JapaneseCharacter
import com.duo.app.data.local.character.KanaRepository
import com.duo.app.data.local.character.ScriptType
import com.duo.app.ui.components.StrokeDrawingCanvas

@Composable
fun CharactersTabScreen(
    onSelectCharacter: (JapaneseCharacter) -> Unit,
    mastered: Set<String> = emptySet(),
    modifier: Modifier = Modifier,
) {
    var selectedScript by remember { mutableStateOf(ScriptType.HIRAGANA) }
    var showBlitz by remember { mutableStateOf(false) }
    val characterList = remember(selectedScript) {
        if (selectedScript == ScriptType.HIRAGANA) KanaRepository.hiraganaList else KanaRepository.katakanaList
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
            .padding(horizontal = 20.dp, vertical = 14.dp),
    ) {
        // Script Selector Tabs: [ Hiragana ] [ Katakana ]
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFFECECEC),
        ) {
            Row(
                modifier = Modifier.padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                // Hiragana Pill
                val isHiragana = selectedScript == ScriptType.HIRAGANA
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            color = if (isHiragana) Color.White else Color.Transparent,
                            shape = RoundedCornerShape(12.dp),
                        )
                        .clickable { selectedScript = ScriptType.HIRAGANA }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Hiragana (ひらがな)",
                        fontSize = 13.sp,
                        fontWeight = if (isHiragana) FontWeight.Bold else FontWeight.Medium,
                        color = if (isHiragana) Color(0xFF1CB0F6) else Color(0xFF777777),
                    )
                }

                // Katakana Pill
                val isKatakana = selectedScript == ScriptType.KATAKANA
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            color = if (isKatakana) Color.White else Color.Transparent,
                            shape = RoundedCornerShape(12.dp),
                        )
                        .clickable { selectedScript = ScriptType.KATAKANA }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Katakana (カタカナ)",
                        fontSize = 13.sp,
                        fontWeight = if (isKatakana) FontWeight.Bold else FontWeight.Medium,
                        color = if (isKatakana) Color(0xFF1CB0F6) else Color(0xFF777777),
                    )
                }
            }
        }

        // Kana Blitz Speed Quiz Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showBlitz = true },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF6DB)),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFFC800)),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(text = "⚡", fontSize = 24.sp)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Kana Blitz (60s Speed Quiz)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFFB45309),
                    )
                    Text(
                        text = "Test your rapid recall speed against the clock",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF78350F),
                    )
                }
                Text(text = "START →", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFFD97706))
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Syllabary Progress & Title
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = if (selectedScript == ScriptType.HIRAGANA) "All 46 Hiragana" else "Foundational Katakana",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4B4B4B),
            )
            Text(
                text = "${characterList.count { it.character in mastered }}⭐ / ${characterList.size}",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF58CC02),
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Character Grid (4 columns)
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            itemsIndexed(characterList) { _, char ->
                CharacterPracticeCard(
                    character = char,
                    mastered = char.character in mastered,
                    onClick = { onSelectCharacter(char) },
                )
            }
        }
        if (showBlitz) {
            KanaBlitzDialog(
                characterList = characterList,
                onDismiss = { showBlitz = false },
            )
        }
    }
}

@Composable
private fun KanaBlitzDialog(
    characterList: List<JapaneseCharacter>,
    onDismiss: () -> Unit,
) {
    var timeLeft by remember { mutableIntStateOf(60) }
    var score by remember { mutableIntStateOf(0) }
    var questionCount by remember { mutableIntStateOf(0) }

    val currentTarget = remember(questionCount) {
        characterList.random(kotlin.random.Random(System.currentTimeMillis() + questionCount))
    }
    val currentOptions = remember(currentTarget) {
        val distractors = characterList
            .filter { it.character != currentTarget.character }
            .shuffled()
            .take(3)
            .map { it.romaji }
        (distractors + currentTarget.romaji).shuffled()
    }

    LaunchedEffect(Unit) {
        while (timeLeft > 0) {
            kotlinx.coroutines.delay(1000)
            timeLeft--
        }
    }

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth().padding(8.dp),
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                if (timeLeft > 0) {
                    // Blitz Header (Timer & Score)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "⏱️ ${timeLeft}s",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = if (timeLeft <= 10) Color(0xFFFF4B4B) else Color(0xFF1CB0F6),
                        )
                        Text(
                            text = "⚡ $score pts",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = Color(0xFFFF9600),
                        )
                        Text(
                            text = "✕",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF777777),
                            modifier = Modifier.clickable(onClick = onDismiss).padding(4.dp),
                        )
                    }

                    // Character Display
                    Box(
                        modifier = Modifier
                            .size(130.dp)
                            .background(Color(0xFFF7F7F7), CircleShape)
                            .border(3.dp, Color(0xFFE5E5E5), CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = currentTarget.character,
                            fontSize = 68.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B),
                        )
                    }

                    Text(
                        text = "Pick the matching romaji:",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF64748B),
                    )

                    // 2x2 Option Buttons
                    currentOptions.chunked(2).forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            row.forEach { romajiOption ->
                                Button(
                                    onClick = {
                                        if (romajiOption == currentTarget.romaji) {
                                            score++
                                        }
                                        questionCount++
                                    },
                                    modifier = Modifier.weight(1f).height(52.dp),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1CB0F6)),
                                ) {
                                    Text(
                                        text = romajiOption,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // Time's Up Results View
                    Text(text = "⏱️", fontSize = 56.sp)
                    Text(
                        text = "Time's Up!",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B),
                    )
                    Text(
                        text = "You scored $score correct answers in 60s!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFF64748B),
                    )
                    Text(
                        text = when {
                            score >= 25 -> "⚡ Lightning Fast! Capybara God Mode"
                            score >= 15 -> "🏆 Fluent Recall! Super Sharp"
                            else -> "🦫 Great Practice! Keep it chill"
                        },
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF58CC02),
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Button(
                            onClick = {
                                score = 0
                                questionCount = 0
                                timeLeft = 60
                            },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF58CC02)),
                        ) {
                            Text(text = "PLAY AGAIN", fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE5E5E5)),
                        ) {
                            Text(text = "DONE", color = Color(0xFF4B4B4B), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CharacterPracticeCard(
    character: JapaneseCharacter,
    mastered: Boolean,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFE5E5E5)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(
                        if (mastered) Color(0xFFFFD900) else Color(0xFFE5F5FF),
                        CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = character.character,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1899D6),
                )
                if (mastered) {
                    Text(
                        text = "⭐",
                        fontSize = 12.sp,
                        modifier = Modifier.align(Alignment.TopEnd),
                    )
                }
            }

            Text(
                text = character.romaji,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4B4B4B),
            )

            Text(
                text = "${character.strokes.size} st.",
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp,
                color = Color(0xFF888888),
            )
        }
    }
}

@Composable
fun CharacterDrawingScreen(
    character: JapaneseCharacter,
    completedStrokes: Int,
    isCompleted: Boolean,
    onStrokeCompleted: (Int) -> Unit,
    onExit: () -> Unit,
) {
    val totalStrokes = character.strokes.size
    val progress = (completedStrokes.toFloat() / totalStrokes.toFloat()).coerceIn(0f, 1f)

    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 2.dp,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Text(
                        text = "✕",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFAFAFAF),
                        modifier = Modifier.clickable(onClick = onExit),
                    )

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .weight(1f)
                            .height(12.dp)
                            .background(Color(0xFFE5E5E5), RoundedCornerShape(6.dp)),
                        color = Color(0xFF58CC02),
                        trackColor = Color(0xFFE5E5E5),
                    )
                }
            }
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 8.dp,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(16.dp),
                ) {
                    if (isCompleted) {
                        Button(
                            onClick = onExit,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF58CC02)),
                        ) {
                            Text(
                                text = "EXCELLENT! CONTINUE",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                            )
                        }
                    } else {
                        Text(
                            text = "Follow stroke ${completedStrokes + 1} of $totalStrokes",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF777777),
                            modifier = Modifier.align(Alignment.Center),
                        )
                    }
                }
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Character Title and Pronunciation
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = "Draw: ${character.character}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4B4B4B),
                )
                Text(
                    text = "${character.scriptType.name.lowercase().replaceFirstChar { it.uppercase() }} • '${character.romaji}' (${character.group})",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF1CB0F6),
                    fontWeight = FontWeight.SemiBold,
                )
            }

            // Interactive Tracing Canvas
            StrokeDrawingCanvas(
                character = character,
                completedStrokes = completedStrokes,
                onStrokeCompleted = onStrokeCompleted,
                modifier = Modifier.fillMaxWidth(0.9f),
            )

            if (isCompleted) {
                androidx.compose.animation.AnimatedVisibility(
                    visible = true,
                    enter = androidx.compose.animation.scaleIn(
                        animationSpec = androidx.compose.animation.core.spring(
                            dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
                        ),
                    ) + androidx.compose.animation.fadeIn(),
                ) {
                    Text(
                        text = "🎉 Character mastered! +10 XP",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF58CC02),
                    )
                }
            }
        }
    }
}

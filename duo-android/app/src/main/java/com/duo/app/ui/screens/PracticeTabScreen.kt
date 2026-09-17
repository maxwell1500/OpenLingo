package com.duo.app.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class VocabWord(
    val foreign: String,
    val romaji: String? = null,
    val translation: String,
    val audioSrc: String? = null,
    val category: String,
)

val practiceVocabBank: List<VocabWord> = listOf(
    VocabWord("Hola", null, "Hello", "asset:///audio/es/hola.ogg", "Spanish Essentials"),
    VocabWord("Buenos días", null, "Good morning", "asset:///audio/es/buenos_dias.ogg", "Spanish Essentials"),
    VocabWord("Gracias", null, "Thank you", "asset:///audio/es/gracias.ogg", "Spanish Essentials"),
    VocabWord("Un café, por favor", null, "A coffee, please", "asset:///audio/es/un_cafe_por_favor.ogg", "Food & Dining"),
    VocabWord("La cuenta, por favor", null, "The bill, please", "asset:///audio/es/la_cuenta.ogg", "Food & Dining"),
    VocabWord("Yo hablo español", null, "I speak Spanish", "asset:///audio/es/yo_hablo_espanol.ogg", "Action Verbs"),
    VocabWord("こんにちは", "Konnichiwa", "Hello / Good day", "asset:///audio/ja/konnichiwa.ogg", "Japanese Greetings"),
    VocabWord("おはようございます", "Ohayou gozaimasu", "Good morning", "asset:///audio/ja/ohayou.ogg", "Japanese Greetings"),
    VocabWord("お水", "Mizu", "Water", "asset:///audio/ja/mizu.ogg", "Food & Refreshments"),
    VocabWord("コーヒー", "Koohii", "Coffee", "asset:///audio/ja/koohii.ogg", "Katakana Loanwords"),
    VocabWord("パン", "Pan", "Bread", "asset:///audio/ja/pan.ogg", "Katakana Loanwords"),
    VocabWord("たべます", "Tabemasu", "To eat", "asset:///audio/ja/tabemasu.ogg", "Daily Verbs"),
    VocabWord("のみます", "Nomimasu", "To drink", "asset:///audio/ja/nomimasu.ogg", "Daily Verbs"),
    VocabWord("すみません", "Sumimasen", "Excuse me / Sorry", "asset:///audio/ja/sumimasen.ogg", "Polite Expressions"),
)

@Composable
fun PracticeTabScreen(
    onPlayVoice: (String) -> Unit,
    onStartPractice: () -> Unit,
    mistakes: List<com.duo.app.data.local.entities.MistakeEntry> = emptyList(),
    onClearAllMistakes: () -> Unit = {},
    courseComplete: Boolean = false,
    vocabList: List<com.duo.app.data.local.entities.VocabScheduleEntity> = emptyList(),
    dueVocabCount: Int = 0,
    onReviewVocab: (com.duo.app.data.local.entities.VocabScheduleEntity, Int) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier,
) {
    var showFlashcards by remember { mutableStateOf(false) }
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        // Course complete celebration — shown when all lessons done and no mistakes.
        if (courseComplete && mistakes.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF58CC02)),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .background(Color(0xFF58CC02), CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(text = "🎉", fontSize = 28.sp)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Course mastered!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF3E8E01),
                        )
                        Text(
                            text = "All lessons complete and no pending mistakes. Keep reviewing with flashcards to stay sharp.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF4B4B4B),
                        )
                    }
                }
            }
        }
        // Primary action: practice weaknesses (missed challenges) or general review.
        if (mistakes.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onStartPractice() },
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFF9600)),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .background(Color(0xFFFF9600), CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(text = "🎯", fontSize = 28.sp)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Practice your ${mistakes.size} weakness${if (mistakes.size == 1) "" else "es"}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE67E22),
                        )
                        Text(
                            text = "Only the challenges you missed — no heart penalty, clears on success",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF4B4B4B),
                        )
                    }
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF58CC02), RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "GO",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                        )
                    }
                }
            }
        }

        // Flashcards / vocab review banner (always available).
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showFlashcards = true },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE5F5FF)),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF1CB0F6)),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .background(Color(0xFF1CB0F6), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = "🃏", fontSize = 28.sp)
                }

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = "FSRS Spaced Repetition",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1899D6),
                        )
                        if (dueVocabCount > 0) {
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFFF9600), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp),
                            ) {
                                Text(
                                    text = "$dueVocabCount DUE",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                    }
                    Text(
                        text = if (dueVocabCount > 0) "$dueVocabCount cards due for optimal memory retention" else "All cards caught up! Practice ahead anytime.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF4B4B4B),
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Tap to review",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1CB0F6),
                )
            }
        }
        // Section: Mistakes to review (SRS dumbbell) — retry clears the entry.
        if (mistakes.isNotEmpty()) {
            MistakesReviewCard(
                mistakes = mistakes,
                onClearAllMistakes = onClearAllMistakes,
            )
        }

        // Section Title: Learned Vocabulary Bank
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Vocabulary Bank",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4B4B4B),
            )
            Text(
                text = "${practiceVocabBank.size} words unlocked",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF58CC02),
            )
        }

        // Vocabulary List
        // Vocabulary List
        val displayList = if (vocabList.isNotEmpty()) vocabList else practiceVocabBank.map {
            com.duo.app.data.local.entities.VocabScheduleEntity(
                id = it.foreign,
                language = "es",
                foreign = it.foreign,
                romaji = it.romaji,
                translation = it.translation,
                audioSrc = it.audioSrc,
                category = it.category,
            )
        }
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            items(displayList) { item ->
                VocabScheduleCard(item = item, onPlayVoice = onPlayVoice)
            }
        }
        if (showFlashcards) {
            VocabFlashcardsDialog(
                vocabList = displayList,
                onPlayVoice = onPlayVoice,
                onReviewVocab = onReviewVocab,
                onDismiss = { showFlashcards = false },
            )
        }
    }
}

@Composable
private fun VocabFlashcardsDialog(
    vocabList: List<com.duo.app.data.local.entities.VocabScheduleEntity>,
    onPlayVoice: (String) -> Unit,
    onReviewVocab: (com.duo.app.data.local.entities.VocabScheduleEntity, Int) -> Unit,
    onDismiss: () -> Unit,
) {
    var currentIndex by remember { mutableIntStateOf(0) }
    var isFlipped by remember { mutableStateOf(false) }

    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(400),
        label = "cardFlip",
    )

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
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "🃏 Flashcards (${currentIndex + 1}/${vocabList.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                    )
                    Text(
                        text = "✕",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF777777),
                        modifier = Modifier.clickable(onClick = onDismiss).padding(4.dp),
                    )
                }

                if (currentIndex < vocabList.size) {
                    val currentWord = vocabList[currentIndex]

                    // 3D Flip Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .graphicsLayer {
                                rotationY = rotation
                                cameraDistance = 12f * density
                            }
                            .clickable { isFlipped = !isFlipped },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (rotation <= 90f) Color(0xFFF0F9FF) else Color(0xFFFFFBEB),
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            2.dp,
                            if (rotation <= 90f) Color(0xFF1CB0F6) else Color(0xFFF59E0B),
                        ),
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (rotation <= 90f) {
                                // Front of card
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = currentWord.foreign,
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1E293B),
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    if (currentWord.audioSrc != null) {
                                        Button(
                                            onClick = { onPlayVoice(currentWord.audioSrc) },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1CB0F6)),
                                            shape = CircleShape,
                                            contentPadding = androidx.compose.foundation.layout.PaddingValues(8.dp),
                                            modifier = Modifier.size(40.dp),
                                        ) {
                                            Text(text = "🔊", fontSize = 16.sp)
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                    }
                                    Text(
                                        text = "Tap to flip 🔄",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF94A3B8),
                                    )
                                }
                            } else {
                                // Back of card (mirrored for natural readability)
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.graphicsLayer { rotationY = 180f },
                                ) {
                                    Text(
                                        text = currentWord.translation,
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFB45309),
                                    )
                                    if (!currentWord.romaji.isNullOrBlank()) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = currentWord.romaji,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color(0xFFD97706),
                                            fontWeight = FontWeight.SemiBold,
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Category: ${currentWord.category}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFF78350F),
                                    )
                                }
                            }
                        }
                    }
                    // FSRS 4-button Answer Actions (Again, Hard, Good, Easy)
                    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Button(
                                onClick = {
                                    onReviewVocab(currentWord, com.duo.app.data.fsrs.FsrsScheduler.RATING_AGAIN)
                                    isFlipped = false
                                    if (currentIndex + 1 < vocabList.size) currentIndex++ else onDismiss()
                                },
                                modifier = Modifier.weight(1f).height(44.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFDFE0)),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(4.dp),
                            ) {
                                Text(text = "Again ✕", color = Color(0xFFFF4B4B), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                            Button(
                                onClick = {
                                    onReviewVocab(currentWord, com.duo.app.data.fsrs.FsrsScheduler.RATING_HARD)
                                    isFlipped = false
                                    if (currentIndex + 1 < vocabList.size) currentIndex++ else onDismiss()
                                },
                                modifier = Modifier.weight(1f).height(44.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFF3C4)),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(4.dp),
                            ) {
                                Text(text = "Hard", color = Color(0xFFD97706), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Button(
                                onClick = {
                                    onReviewVocab(currentWord, com.duo.app.data.fsrs.FsrsScheduler.RATING_GOOD)
                                    isFlipped = false
                                    if (currentIndex + 1 < vocabList.size) currentIndex++ else onDismiss()
                                },
                                modifier = Modifier.weight(1f).height(44.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE8F5E9)),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(4.dp),
                            ) {
                                Text(text = "Good ✓", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                            Button(
                                onClick = {
                                    onReviewVocab(currentWord, com.duo.app.data.fsrs.FsrsScheduler.RATING_EASY)
                                    isFlipped = false
                                    if (currentIndex + 1 < vocabList.size) currentIndex++ else onDismiss()
                                },
                                modifier = Modifier.weight(1f).height(44.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0F2FE)),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(4.dp),
                            ) {
                                Text(text = "Easy ✨", color = Color(0xFF0284C7), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun VocabScheduleCard(
    item: com.duo.app.data.local.entities.VocabScheduleEntity,
    onPlayVoice: (String) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E5E5)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = item.foreign,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4B4B4B),
                    )
                    if (item.reps > 0) {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFE8F5E9), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                        ) {
                            Text(
                                text = "${item.reps} reps",
                                color = Color(0xFF2E7D32),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
                if (!item.romaji.isNullOrBlank()) {
                    Text(
                        text = item.romaji,
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 12.sp,
                        color = Color(0xFF1CB0F6),
                    )
                }
                Text(
                    text = "${item.translation} • ${item.category}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF888888),
                )
            }

            if (item.audioSrc != null) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFFE5F5FF), CircleShape)
                        .clickable { onPlayVoice(item.audioSrc) },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = "🔊", fontSize = 18.sp)
                }
            }
        }
    }
}

@Composable
private fun VocabCard(
    item: VocabWord,
    onPlayVoice: (String) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E5E5)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.foreign,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4B4B4B),
                )
                if (!item.romaji.isNullOrBlank()) {
                    Text(
                        text = item.romaji,
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 12.sp,
                        color = Color(0xFF1CB0F6),
                    )
                }
                Text(
                    text = "${item.translation} • ${item.category}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF888888),
                )
            }

            if (item.audioSrc != null) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFFE5F5FF), CircleShape)
                        .clickable { onPlayVoice(item.audioSrc) },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = "🔊", fontSize = 18.sp)
                }
            }
        }
    }
}

@Composable
private fun MistakesReviewCard(
    mistakes: List<com.duo.app.data.local.entities.MistakeEntry>,
    onClearAllMistakes: () -> Unit = {},
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEDEF)),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFF9600)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(text = "📋", fontSize = 24.sp)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Your missed challenges (${mistakes.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4B4B4B),
                    )
                    Text(
                        text = "Answer them correctly in a practice session to clear them",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF777777),
                    )
                }
            }

            mistakes.take(5).forEach { mistake ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = "•",
                        fontSize = 14.sp,
                        color = Color(0xFFFF9600),
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = mistake.question,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF4B4B4B),
                            maxLines = 2,
                        )
                        if (mistake.lessonName.isNotBlank()) {
                            Text(
                                text = mistake.lessonName,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF999999),
                                maxLines = 1,
                            )
                        }
                    }
                }
            }
            if (mistakes.size > 5) {
                Text(
                    text = "+${mistakes.size - 5} more…",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF999999),
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                Text(
                    text = "Clear all",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF999999),
                    modifier = Modifier.clickable { onClearAllMistakes() },
                )
            }
        }
    }
}

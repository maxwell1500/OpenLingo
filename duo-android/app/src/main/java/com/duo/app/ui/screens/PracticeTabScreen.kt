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
import androidx.compose.foundation.layout.wrapContentWidth
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
import com.duo.app.grammar.GrammarFocus


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
    unlockedWords: List<com.duo.app.ui.UnitWord> = emptyList(),
    dictionary: com.duo.app.dictionary.Dictionary = com.duo.app.dictionary.Dictionary.EMPTY,
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
                        // `fill = false` and `weight` is the whole fix for the badge.
                        // A Row measures its *unweighted* children first, so the badge
                        // below is measured against this Row's full max width and takes
                        // the width its text needs; only what is left over reaches this
                        // title, which then wraps. With the title unweighted it claimed
                        // the whole row first, the badge was measured with a max width of
                        // zero, and its text laid out one character per line down a
                        // zero-width pill: a count nobody could read.
                        Text(
                            text = "FSRS Spaced Repetition",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1899D6),
                            modifier = Modifier.weight(1f, fill = false),
                        )
                        if (dueVocabCount > 0) {
                            DueBadge(dueVocabCount)
                        }
                    }
                    Text(
                        text = fsrsBannerSummary(vocabList.size, dueVocabCount),
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

        // Section: what this learner has actually met, and nothing else.
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
                text = "${unlockedWords.size} ${if (unlockedWords.size == 1) "word" else "words"} unlocked",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF58CC02),
            )
        }

        // The count above and the list below are the same derivation, so they cannot
        // disagree: [com.duo.app.ui.LearnerVocabulary.wordsMet] over completed challenges.
        // A fresh install has completed nothing, so both are empty and the section says so
        // rather than claiming words the learner has not met.
        if (unlockedWords.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F7F7)),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = "No words unlocked yet",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4B4B4B),
                    )
                    Text(
                        text = "Every word you answer correctly in a lesson lands here, so " +
                            "this list starts empty and fills up as you learn.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF777777),
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                items(unlockedWords) { word ->
                    UnlockedWordCard(
                        word = word,
                        dictionary = dictionary,
                        onPlayVoice = onPlayVoice,
                    )
                }
            }
        }
        if (showFlashcards) {
            VocabFlashcardsDialog(
                vocabList = vocabList,
                onPlayVoice = onPlayVoice,
                onReviewVocab = onReviewVocab,
                onDismiss = { showFlashcards = false },
            )
        }
    }
}

/**
 * The due count as a badge: a pill with the number and the word DUE on one line.
 *
 * `wrapContentWidth(unbounded = true)` is the second half of the fix, and it
 * holds whatever the row around it does: the badge is measured against an
 * unbounded width, so its `Text` is laid out on a single line at its intrinsic
 * width instead of wrapping to one character per line. A badge is the one thing
 * in this card that must never be the thing that gives way, so it takes its own
 * width and the title wraps around it rather than the other way round.
 */
@Composable
private fun DueBadge(dueVocabCount: Int) {
    Box(
        modifier = Modifier
            .wrapContentWidth(unbounded = true)
            .background(Color(0xFFFF9600), RoundedCornerShape(8.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp),
    ) {
        Text(
            text = dueBadgeLabel(dueVocabCount),
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

/**
 * The line under the FSRS banner's title.
 *
 * Three states, and the empty one is the one the screen used to get wrong: with
 * no words met there are no cards, which is not the same as cards that are all
 * caught up, and the learner is told which one they are looking at. The counts
 * arrive as arguments, so the sentence and the badge beside it cannot come from
 * two different places.
 */
internal fun fsrsBannerSummary(vocabCount: Int, dueCount: Int): String = when {
    vocabCount == 0 -> "No cards yet — every word you meet in a lesson becomes one."
    dueCount > 0 -> "$dueCount cards due for optimal memory retention"
    else -> "All cards caught up! Practice ahead anytime."
}

/**
 * The badge's own text. Kept beside [fsrsBannerSummary] for the same reason: one
 * count, one place that turns it into words, so the pill and the sentence can
 * never print different numbers.
 */
internal fun dueBadgeLabel(dueCount: Int): String = "$dueCount DUE"

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
                        text = if (vocabList.isEmpty()) {
                            "🃏 Flashcards"
                        } else {
                            "🃏 Flashcards (${currentIndex + 1}/${vocabList.size})"
                        },
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
                } else {
                    // No cards yet. A learner who has met no words gets this, not
                    // "1/0" over an empty panel: the deck is empty because there
                    // is nothing in it for them yet, and that is the honest
                    // reason to print here.
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = "No cards yet",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF4B4B4B),
                        )
                        Text(
                            text = "Every word you answer correctly in a lesson becomes a " +
                                "review card, so this fills up as you learn.",
                            fontSize = 13.sp,
                            color = Color(0xFF888888),
                        )
                    }
                }
            }
        }
    }
}

/**
 * One word the learner has met, with the gloss and the clip the offline dictionary
 * supplies. The dictionary is the only place the app states a meaning outright, so a
 * word it cannot gloss renders without one rather than with a guess.
 */
@Composable
private fun UnlockedWordCard(
    word: com.duo.app.ui.UnitWord,
    dictionary: com.duo.app.dictionary.Dictionary,
    onPlayVoice: (String) -> Unit,
) {
    val entry = remember(word, dictionary) { dictionary.lookup(word.term) }
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
                    text = word.term,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4B4B4B),
                )
                val romaji = word.romaji ?: entry?.romaji
                if (!romaji.isNullOrBlank()) {
                    Text(
                        text = romaji,
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 12.sp,
                        color = Color(0xFF1CB0F6),
                    )
                }
                val gloss = entry?.gloss
                if (!gloss.isNullOrBlank()) {
                    Text(
                        text = gloss,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF888888),
                    )
                }
                Text(
                    text = word.lessonTitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFAAAAAA),
                )
            }

            val audioSrc = word.audioSrc ?: entry?.audioSrc
            if (audioSrc != null) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFFE5F5FF), CircleShape)
                        .clickable { onPlayVoice(audioSrc) },
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
                        text = "Each one shows its rule first — read it, then retry",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF777777),
                    )
                }
            }

            mistakes.take(5).forEach { mistake ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
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
                        // WI-16: the review entry names the lesson and the grammar
                        // point, then shows the rule itself. A re-ask with no
                        // explanation is the memorization loop this card exists
                        // to break, so the rule is readable *before* the retry,
                        // while the learner is still deciding what to answer.
                        MistakeProvenance(mistake)
                        // A mistake on a challenge written before the grammar
                        // overhaul has no ruleText. It still clears, and the
                        // provenance line above is all it claims — an empty
                        // rule card would be worse than none.
                        mistake.ruleText?.takeIf { it.isNotBlank() }?.let { rule ->
                            Text(
                                text = rule,
                                style = MaterialTheme.typography.bodySmall,
                                lineHeight = 16.sp,
                                color = Color(0xFF7A5C5C),
                                modifier = Modifier.padding(top = 2.dp),
                                maxLines = 4,
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

/** Lesson and grammar point a mistake came from, or nothing at all if unknown. */
@Composable
private fun MistakeProvenance(mistake: com.duo.app.data.local.entities.MistakeEntry) {
    val focus = GrammarFocus.label(mistake.grammaticalFocus)
    val lesson = mistake.lessonName.takeIf { it.isNotBlank() }
    val line = when {
        lesson != null && focus != null -> "$lesson • $focus"
        lesson != null -> lesson
        focus != null -> focus
        else -> return
    }
    Text(
        text = line,
        style = MaterialTheme.typography.bodySmall,
        color = Color(0xFF999999),
        maxLines = 1,
    )
}

package com.openlingo.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.openlingo.app.dictionary.Dictionary
import com.openlingo.app.dictionary.DictionaryEntry
import com.openlingo.app.dictionary.NO_GLOSS_NOTICE

/**
 * WI-11: the offline definition sheet.
 *
 * A lookup is a study surface, not an attempt. Nothing here writes: no mistake
 * is queued, no challenge is marked done, no exercise-type stat moves. The
 * learner can look a word up in the middle of a graded answer without that
 * lookup being scored, which is the only way a reference can exist inside a
 * test without contaminating it.
 *
 * The sheet also never fills a blank. A word the app has no English for still
 * opens, and says so in one line, rather than borrowing a gloss from a
 * neighbouring word — the alternative is a confident wrong answer the learner
 * has no way to detect.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DictionarySheet(
    entry: DictionaryEntry,
    onPlayVoice: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = entry.term,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4B4B4B),
            )
            if (!entry.romaji.isNullOrBlank()) {
                Text(
                    text = entry.romaji,
                    style = MaterialTheme.typography.titleSmall,
                    color = Color(0xFF3D7EA6),
                )
            }
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                entry.partOfSpeech?.let { pos -> FactChip(text = pos) }
                entry.category?.let { FactChip(text = it) }
                entry.audioSrc?.let { src ->
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFFE5F5FF), CircleShape)
                            .clickable { onPlayVoice(src) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(text = "🔊", fontSize = 18.sp)
                    }
                }
            }

            Text(
                text = entry.gloss ?: NO_GLOSS_NOTICE,
                style = MaterialTheme.typography.titleMedium,
                color = if (entry.gloss != null) Color(0xFF4B4B4B) else Color(0xFF8A8A8A),
            )

            entry.example?.let { example ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "IN A SENTENCE",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF9A9A9A),
                )
                Text(
                    text = example,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF4B4B4B),
                )
                entry.exampleTranslation?.let { translation ->
                    Text(
                        text = translation,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF888888),
                    )
                }
            }

            Text(
                text = "Offline · from the exercises in this app",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFFAAAAAA),
            )
            TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.End)) {
                Text(text = "Close", fontWeight = FontWeight.Bold, color = Color(0xFF3D7EA6))
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FactChip(text: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFFF0F7FF),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFB8DCFF)),
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1B6FB8),
        )
    }
}

/**
 * A [androidx.compose.material3.Text] that opens the definition sheet when the
 * learner taps a word it can resolve.
 *
 * The tap is turned into a character offset with the laid-out
 * [TextLayoutResult], and that offset is handed to [Dictionary.resolveTap] — so
 * the question of "which word did they mean" is answered in one tested place
 * rather than guessed here. A tap that resolves to nothing does nothing: there
 * is no fallback, because a sheet that opened on the wrong word would be worse
 * than no sheet.
 *
 * [preferWholeTerm] is for the per-unit vocabulary list, where the rendered
 * string is the term rather than a sentence containing it. See
 * [Dictionary.resolveTerm].
 *
 * [longPressOnly] is for the surfaces that already use a tap for something
 * else. On an answer option a tap selects the answer — that is the lesson, and
 * it is not this feature's to take away — so a hold looks the word up instead.
 */
@Composable
fun LookupText(
    text: String,
    dictionary: Dictionary,
    onLookup: (DictionaryEntry) -> Unit,
    modifier: Modifier = Modifier,
    longPressOnly: Boolean = false,
    preferWholeTerm: Boolean = false,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
    color: Color = Color(0xFF4B4B4B),
    fontWeight: FontWeight? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
) {
    val layout = remember(text) { TextLayoutBox() }
    Text(
        text = text,
        style = style,
        color = color,
        fontWeight = fontWeight,
        lineHeight = lineHeight,
        onTextLayout = { layout.result = it },
        modifier = modifier.pointerInput(text, dictionary, longPressOnly, preferWholeTerm) {
            // detectTapGestures reports the position in its own coordinates,
            // which is exactly what TextLayoutResult.getOffsetForPosition wants.
            // Deciding *which* word was meant is resolveTap's job, not this
            // composable's.
            detectTapGestures(
                onTap = { position ->
                    if (!longPressOnly) {
                        resolveAt(layout.result, text, dictionary, position, onLookup, preferWholeTerm)
                    }
                },
                onLongPress = { position ->
                    if (longPressOnly) {
                        resolveAt(layout.result, text, dictionary, position, onLookup, preferWholeTerm)
                    }
                },
            )
        },
    )
}

private fun resolveAt(
    result: TextLayoutResult?,
    text: String,
    dictionary: Dictionary,
    position: androidx.compose.ui.geometry.Offset,
    onLookup: (DictionaryEntry) -> Unit,
    preferWholeTerm: Boolean = false,
) {
    val laidOut = result ?: return
    val offset = laidOut.getOffsetForPosition(position)
    val hit = if (preferWholeTerm) dictionary.resolveTerm(text, offset) else dictionary.resolveTap(text, offset)
    hit?.let { onLookup(it.entry) }
}

/**
 * Holds the laid-out text without going through snapshot state, so recording a
 * layout on every frame does not recompose the exercise around it.
 */
private class TextLayoutBox {
    var result: TextLayoutResult? = null
}

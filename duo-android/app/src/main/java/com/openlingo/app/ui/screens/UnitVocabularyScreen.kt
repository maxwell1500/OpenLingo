package com.openlingo.app.ui.screens


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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.openlingo.app.data.local.entities.UnitEntity
import com.openlingo.app.ui.LookupText

/**
 * The words one unit teaches, grouped by the lesson that teaches them.
 *
 * A study surface, like the grammar drills beside it: opening it reads the corpus and
 * writes nothing. `dictionary` supplies the gloss and the clip, because the offline
 * dictionary is the one place the app states a meaning outright — the unit list itself
 * claims no translation for a word the corpus never glosses.
 */
@Composable
internal fun UnitVocabularyScreen(
    unit: UnitEntity?,
    words: List<com.openlingo.app.ui.UnitWord>,
    dictionary: com.openlingo.app.dictionary.Dictionary,
    onLookup: (com.openlingo.app.dictionary.DictionaryEntry) -> Unit,
    onPlayVoice: (String) -> Unit,
    onBack: () -> Unit,
) {
    val grouped = remember(words) { words.groupBy { it.lessonTitle } }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .widthIn(max = 640.dp)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "←",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3D7EA6),
                modifier = Modifier.clickable(onClick = onBack).padding(8.dp),
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = unit?.title ?: "Vocabulary",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4B4B4B),
            )
        }

        if (words.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F7F7)),
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "📖 No vocabulary here",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4B4B4B),
                    )
                    Text(
                        text = "None of this unit's lessons give a correct answer in the language " +
                            "you are learning, so there is nothing to list. This is what the unit " +
                            "actually teaches — not a list still waiting to be filled in.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF777777),
                    )
                }
            }
        } else {
            Text(
                text = "${words.size} ${if (words.size == 1) "word" else "words"} this unit teaches · tap a word for its definition, 🔊 to hear it",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF888888),
            )
            grouped.forEach { (lessonTitle, lessonWords) ->
                Text(
                    text = lessonTitle.uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF888888),
                )
                lessonWords.forEach { word ->
                    UnitVocabularyRow(
                        word = word,
                        dictionary = dictionary,
                        onLookup = onLookup,
                        onPlayVoice = onPlayVoice,
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun UnitVocabularyRow(
    word: com.openlingo.app.ui.UnitWord,
    dictionary: com.openlingo.app.dictionary.Dictionary,
    onLookup: (com.openlingo.app.dictionary.DictionaryEntry) -> Unit,
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
        ) {
            Column(modifier = Modifier.weight(1f)) {
                LookupText(
                    text = word.term,
                    dictionary = dictionary,
                    onLookup = onLookup,
                    preferWholeTerm = true,
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
                        color = Color(0xFF3D7EA6),
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
            }
            // The clip the teaching option carried, or the one the dictionary found. Never
            // a generated path and never a URL: audio is a bundled asset or it is nothing.
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

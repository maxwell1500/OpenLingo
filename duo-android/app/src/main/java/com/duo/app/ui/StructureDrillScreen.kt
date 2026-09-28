package com.duo.app.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duo.app.feedback.Haptics
import com.duo.app.grammar.AnswerGrader
import com.duo.app.grammar.ErrorHint
import com.duo.app.grammar.drills.Paradigm
import com.duo.app.grammar.drills.ParadigmEntry

/**
 * The generated conjugation drill: browse a paradigm, hear its forms, quiz on them.
 *
 * Nothing here is authored content and nothing here is graded by the progress tables.
 * The screen renders what [com.duo.app.grammar.drills.StructureDrill] extracted from the
 * lessons the learner already has, and [ParadigmEntry.accepts] is the same comparison the
 * lesson uses, so a form that a lesson would have accepted is a form the drill accepts.
 * It is a study surface: no answer submitted here reaches `challenge_progress`,
 * `mistakes` or `exercise_type_stats`, so it cannot pollute adaptive difficulty.
 *
 * [paradigms] is the generated list for the unit, so a newly authored paradigm appears
 * here the next time this composable recomposes — there is no per-focus code in this file
 * and no per-focus branch anywhere below.
 */
@Composable
fun StructureDrillScreen(
    unitTitle: String,
    paradigms: List<Paradigm>,
    onPlayVoice: (audioSrc: String, fallbackText: String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Null means "browsing the tables"; a non-null index means "quizzing that paradigm".
    var quizFocus by remember { mutableStateOf<String?>(null) }
    var selectedFocus by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .widthIn(max = 640.dp)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        DrillHeader(title = "Conjugation Drills", subtitle = unitTitle, onBack = onBack)

        val quiz = paradigms.firstOrNull { it.focus == quizFocus }
        if (quiz != null && !quiz.isEmpty) {
            ParadigmQuiz(paradigm = quiz, onPlayVoice = onPlayVoice, onExit = { quizFocus = null })
            Spacer(modifier = Modifier.height(16.dp))
            return@Column
        }

        val totalForms = paradigms.sumOf { it.entries.size }
        Text(
            text = if (totalForms == 0) {
                "Generated from the grammar this unit teaches"
            } else {
                "$totalForms forms across ${paradigms.size} patterns · generated from this unit's " +
                    "lessons · tap 🔊 to hear one"
            },
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF888888),
        )

        if (paradigms.isEmpty()) {
            ParadigmEmptyState()
        } else {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(paradigms, key = { it.focus }) { paradigm ->
                    DrillChip(
                        text = paradigm.heading,
                        isSelected = paradigm.focus == paradigms[selectedFocus.coerceIn(paradigms.indices)].focus,
                        onClick = { selectedFocus = paradigms.indexOf(paradigm) },
                    )
                }
            }

            val selected = paradigms[selectedFocus.coerceIn(paradigms.indices)]
            if (selected.isEmpty) {
                ParadigmEmptyState()
            } else {
                ParadigmTable(
                    paradigm = selected,
                    onPlayVoice = onPlayVoice,
                    onPractise = { quizFocus = selected.focus },
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

/** The drill's chrome: a back arrow and what is being drilled, as on the vocabulary list. */
@Composable
private fun DrillHeader(title: String, subtitle: String, onBack: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "←",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1CB0F6),
            modifier = Modifier.clickable(onClick = onBack).padding(8.dp),
        )
        Spacer(modifier = Modifier.width(4.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4B4B4B),
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF888888),
            )
        }
    }
}

/**
 * One paradigm as a table: the person or sentence on the left, the form it takes on the
 * right, a speaker to hear it. Rows whose prompt named a lemma are grouped under that
 * lemma, so a verb's forms sit together instead of being scattered by lesson order.
 */
@Composable
private fun ParadigmTable(
    paradigm: Paradigm,
    onPlayVoice: (String, String) -> Unit,
    onPractise: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE5E5E5)),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = paradigm.heading,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4B4B4B),
                    )
                    Text(
                        text = "${paradigm.entries.size} " +
                            (if (paradigm.entries.size == 1) "form" else "forms"),
                        fontSize = 12.sp,
                        color = Color(0xFF888888),
                    )
                }
                Button(
                    onClick = onPractise,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF58CC02)),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                ) {
                    Text(
                        text = "PRACTISE",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                    )
                }
            }

            val groups = paradigm.lemmas.map { it to paradigm.entriesFor(it) } +
                listOf<Pair<String?, List<ParadigmEntry>>>(null to paradigm.entriesFor(null))
            groups.filter { it.second.isNotEmpty() }.forEach { (lemma, entries) ->
                if (lemma != null) {
                    Text(
                        text = lemma,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1CB0F6),
                    )
                }
                entries.forEach { entry -> ParadigmRow(entry = entry, onPlayVoice = onPlayVoice) }
            }
        }
    }
}

/** One row of the table: the slot it fills, the form that fills it, and a way to hear it. */
@Composable
private fun ParadigmRow(entry: ParadigmEntry, onPlayVoice: (String, String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = entry.cue,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF888888),
            )
            if (!entry.romaji.isNullOrBlank()) {
                Text(text = entry.romaji, fontSize = 12.sp, color = Color(0xFF1CB0F6))
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = entry.form,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4B4B4B),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color(0xFFE5F5FF), CircleShape)
                .clickable { onPlayVoice(entry.audioSrc.orEmpty(), entry.form) },
            contentAlignment = Alignment.Center,
        ) {
            Text(text = "🔊", fontSize = 18.sp)
        }
    }
}

/**
 * The quiz. One form at a time, typed by the learner and graded by
 * [ParadigmEntry.accepts] — the same [AnswerGrader] comparison `FILL_BLANK` uses, so a
 * missing accent and a full-width IME are forgiven and a Japanese dakuten is not.
 *
 * The near-misses the curriculum tagged are shown with the reason it gives, which is why
 * a wrong answer can say "Right tense, wrong person" instead of only "incorrect".
 */
@Composable
private fun ParadigmQuiz(
    paradigm: Paradigm,
    onPlayVoice: (String, String) -> Unit,
    onExit: () -> Unit,
) {
    val context = LocalContext.current
    val entries = paradigm.entries
    var index by remember { mutableIntStateOf(0) }
    var typed by remember { mutableStateOf("") }
    var graded by remember { mutableStateOf(false) }
    var correctCount by remember { mutableIntStateOf(0) }

    val entry = entries[index.coerceIn(entries.indices)]
    val isCorrect = graded && entry.accepts(typed)
    val missed = graded && !isCorrect
    val typedDistractor = if (missed) {
        entry.distractors.firstOrNull { AnswerGrader.normalize(it.form) == AnswerGrader.normalize(typed) }
    } else {
        null
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "←",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1CB0F6),
            modifier = Modifier.clickable(onClick = onExit).padding(8.dp),
        )
        Spacer(modifier = Modifier.width(4.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = paradigm.heading,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4B4B4B),
            )
            Text(
                text = "Form ${index + 1} of ${entries.size} · nothing is saved",
                fontSize = 12.sp,
                color = Color(0xFF888888),
            )
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE5E5E5)),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            entry.lemma?.let {
                Text(text = it, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1CB0F6))
            }
            Text(
                text = promptWithBlank(entry.prompt),
                style = MaterialTheme.typography.titleMedium,
                lineHeight = 26.sp,
                color = Color(0xFF4B4B4B),
            )
            OutlinedTextField(
                value = typed,
                onValueChange = { if (!graded) typed = it },
                enabled = !graded,
                singleLine = true,
                placeholder = { Text("Type the form") },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }

    if (graded) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = if (isCorrect) Color(0xFFD7FFB8) else Color(0xFFFFDFE0),
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isCorrect) "✓ Correct!" else "✗ Correct answer: ${entry.form}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF4B4B4B),
                        modifier = Modifier.weight(1f),
                    )
                    // Hearing the right form immediately is the point of a conjugation
                    // drill: the near-miss and the answer differ by one suffix.
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color.White, CircleShape)
                            .clickable { onPlayVoice(entry.audioSrc.orEmpty(), entry.form) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(text = "🔊", fontSize = 16.sp)
                    }
                }
                if (missed) {
                    // The diagnosis is derived, not authored: ErrorHint says the same
                    // thing here as it does when the learner picks this tile in a
                    // lesson, from the same tag and the same focus.
                    val typedWhy = typedDistractor?.let {
                        ErrorHint.forChoice(it.form, entry.form, it.errorTag, entry.focus)
                    }
                    Text(
                        text = typedWhy ?: ErrorHint.forTypedAnswer(typed, entry.form, entry.focus).orEmpty(),
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 18.sp,
                        color = Color(0xFF4B4B4B),
                    )
                    entry.distractors.filter { it !== typedDistractor }.forEach { distractor ->
                        val why = ErrorHint.forChoice(
                            chosenText = distractor.form,
                            correctText = entry.form,
                            errorTag = distractor.errorTag,
                            focus = entry.focus,
                        )
                        if (why != null) {
                            Text(
                                text = "✗ $why",
                                fontSize = 12.sp,
                                lineHeight = 16.sp,
                                color = Color(0xFF6B4B4B),
                            )
                        }
                    }
                }
            }
        }
    }

    Button(
        onClick = {
            if (graded) {
                if (index + 1 < entries.size) {
                    index += 1
                }
                typed = ""
                graded = false
            } else {
                graded = true
                val correct = entry.accepts(typed)
                if (correct) {
                    correctCount += 1
                    Haptics.correct(context)
                } else {
                    Haptics.incorrect(context)
                }
            }
        },
        enabled = graded || typed.isNotBlank(),
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF58CC02),
            disabledContainerColor = Color(0xFFE5E5E5),
            disabledContentColor = Color(0xFFAFAFAF),
        ),
    ) {
        Text(
            text = when {
                !graded -> "CHECK"
                index + 1 < entries.size -> "NEXT FORM"
                else -> "FINISH · $correctCount/${entries.size}"
            },
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
        )
    }
}

/**
 * A focus that teaches no drillable form. Said plainly, because a table with no rows and
 * no explanation reads as a bug the learner cannot report usefully.
 */
@Composable
private fun ParadigmEmptyState() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F7F7)),
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "📖 No conjugation drill here yet",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4B4B4B),
            )
            Text(
                text = "A drill is generated from the lessons themselves, so this pattern only " +
                    "appears once a lesson in this unit asks for one of its forms. Nothing is " +
                    "missing — there is just no form to table yet.",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF777777),
            )
        }
    }
}

/** The app's lesson-node chip: green when selected, flat grey when not. */
@Composable
private fun DrillChip(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .background(if (isSelected) Color(0xFF58CC02) else Color(0xFFF0F0F0), RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) Color.White else Color(0xFF4B4B4B),
        )
    }
}

/** Renders the authored `___` run in the accent colour, as the lesson prompt does. */
private fun promptWithBlank(prompt: String) = buildAnnotatedString {
    val run = Regex("_{3,}").find(prompt)
    if (run == null) {
        append(prompt)
        return@buildAnnotatedString
    }
    append(prompt.substring(0, run.range.first))
    withStyle(SpanStyle(color = Color(0xFF1CB0F6), fontWeight = FontWeight.Bold)) {
        append(BLANK_PLACEHOLDER)
    }
    append(prompt.substring(run.range.last + 1))
}

private const val BLANK_PLACEHOLDER = "______"

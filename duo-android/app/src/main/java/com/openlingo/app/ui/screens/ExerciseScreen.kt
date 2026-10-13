package com.openlingo.app.ui.screens


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
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
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.rememberScrollState
import com.openlingo.app.ui.MatchPairs
import com.openlingo.app.ui.components.FuriganaParser
import com.openlingo.app.ui.components.FuriganaText
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.openlingo.app.data.local.entities.ChallengeOptionEntity
import com.openlingo.app.data.local.models.ChallengeType
import com.openlingo.app.grammar.BlankPlaceholder
import com.openlingo.app.ui.LookupText
import com.openlingo.app.grammar.GrammarFocus
import com.openlingo.app.ui.ActiveScreen
import com.openlingo.app.ui.FeedbackState
import com.openlingo.app.ui.components.RuleCard

// -------------------------------------------------------------------------
// Screen 2: Interactive Exercise Screen (Question, Options, Check, Feedback)
// -------------------------------------------------------------------------

/** WI-14: discrete playback speeds. 1.0x is the default; 0.5x/0.75x cover
 *  phonemes too fast to catch, which is what the old hardcoded turtle served. */
private val AUDIO_SPEEDS = listOf(0.5f, 0.75f, 1.0f)

/** Rendered in place of an authored `___` run inside a prompt. */
private const val BLANK_PLACEHOLDER = "______"

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun ExerciseScreen(
    exercise: ActiveScreen.Exercise,
    dictionary: com.openlingo.app.dictionary.Dictionary,
    onLookup: (com.openlingo.app.dictionary.DictionaryEntry) -> Unit,
    showRomaji: Boolean,
    isJapanese: Boolean,
    onToggleRomaji: () -> Unit,
    onSelectOption: (Int) -> Unit,
    onSelectWordTile: (Int) -> Unit,
    onSelectPairTile: (Int) -> Unit = {},
    onRemoveWordTile: (Int) -> Unit,
    onPlayVoice: (String, Float, String?) -> Unit = { _, _, _ -> },
    onTypedAnswerChange: (String) -> Unit = {},
    onCheckAnswer: () -> Unit,
    onNextChallenge: () -> Unit,
    onExit: () -> Unit,
    hearts: Int = 5,
    onRefillHearts: () -> Unit = {},
) {
    val challenge = exercise.currentChallenge.challenge
    val options = exercise.currentChallenge.options
    val progressFraction = (exercise.challengeIndex + 1).toFloat() / exercise.totalChallenges.toFloat()
    val isWordBank = challenge.type == ChallengeType.WORD_BANK
    val isListen = challenge.type == ChallengeType.LISTEN
    val isMatchPairs = challenge.type == ChallengeType.MATCH_PAIRS
    val isConjugate = challenge.type == ChallengeType.CONJUGATE
    val isFillBlank = challenge.type == ChallengeType.FILL_BLANK
    val isAssist = challenge.type == ChallengeType.ASSIST
    // An INTRO card has no options, so `selectedOptionId` is always null and the
    // `else` branch below would keep CHECK disabled forever — a card with
    // nothing to choose is always already "answered". `checkAnswer()` clears it
    // as Correct(0, 0) and writes nothing, which is what turns the button into
    // CONTINUE.
    val hasSelection = when {
        challenge.type == ChallengeType.INTRO -> true
        isMatchPairs -> MatchPairs.isComplete(options, exercise.matchedPairIds)
        isWordBank -> exercise.selectedWordTileIds.isNotEmpty()
        isFillBlank -> exercise.typedAnswer.isNotBlank()
        else -> exercise.selectedOptionId != null
    }
    // Transcript fallback for LISTEN: resets with each new challenge.
    var transcriptShown by remember(challenge.id) { mutableStateOf(false) }
    val listenAnswer = options.firstOrNull { it.correct }
    // WI-01a: the rule card is a per-challenge disclosure, dismissed for the
    // rest of this challenge's display and reset by the next one.
    var ruleDismissed by remember(challenge.id) { mutableStateOf(false) }
    // WI-14: playback speed for every audio button on this screen. 1.0x is the
    // default; 0.5x/0.75x cover the "too fast to catch the phonemes" case that
    // the old hardcoded 0.6f turtle button served.
    //
    // Session scope is deliberate and is what `remember` (with no key) already
    // gives: ExerciseScreen is composed only while an exercise session is on
    // screen, so leaving the session (exit, lesson complete, checkpoint result)
    // discards this state and the next session starts at 1.0x. That resets the
    // speed between lessons, so a learner who picked 0.5x in lesson 3 does not
    // silently get 0.5x in lesson 4 with no visible indication why. It is NOT
    // keyed on challenge.id, so the choice persists across the consecutive
    // challenges of one session — re-picking per question would be tedious.
    var audioSpeed by remember { mutableStateOf(1.0f) }
    val playAtChosenSpeed: (String) -> Unit = { src -> onPlayVoice(src, audioSpeed, null) }
    val ruleText = challenge.ruleText
    val showRuleCard = ruleText != null && !ruleDismissed
    val isIntro = challenge.type == ChallengeType.INTRO
    val introOption = options.firstOrNull()
    // The curriculum puts the target in the option when it seeded one (option.text,
    // with the reading in option.romaji); a card seeded with no option carries it
    // in the question instead.
    val introGlyph = introOption?.text?.takeIf { it.isNotBlank() } ?: challenge.question
    // The question is the English meaning only when it is not the glyph itself.
    val introMeaning = if (!introOption?.text.isNullOrBlank()) challenge.question else null
    val introAudioSrc = if (isIntro) challenge.audioSrc ?: introOption?.audioSrc else null
    // An INTRO card is a discovery, not a question: the learner is shown the new
    // word/kana and is expected to hear it before moving on, so the clip plays
    // once as the card appears. Keyed on challenge.id, so it fires on entering
    // the card and never again on recomposition, and the session-scoped
    // `audioSpeed` is read as-is. The glyph is the TTS fallback: a card with no
    // bundled audio is still pronounceable, so it is never silent.
    LaunchedEffect(challenge.id, introAudioSrc) {
        if (isIntro) onPlayVoice(introAudioSrc ?: "", audioSpeed, introGlyph)
    }
    // A LISTEN card is "tap what you hear": with no clip on the challenge the
    // only thing left to pronounce is the correct answer, so the speaker falls
    // back to it through TTS instead of going silent on a blank src.
    val listenAudioSrc = challenge.audioSrc ?: listenAnswer?.audioSrc ?: ""

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = "✕",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFAFAFAF),
                        modifier = Modifier.clickable(onClick = onExit),
                    )
                    LinearProgressIndicator(
                        progress = { progressFraction },
                        modifier = Modifier
                            .weight(1f)
                            .height(12.dp)
                            .background(Color(0xFFE5E5E5), RoundedCornerShape(6.dp)),
                        color = Color(0xFF2E9E6B),
                        trackColor = Color(0xFFE5E5E5),
                    )

                    // Romaji / Furigana Toggle Button (Japanese-course UI).
                    // Japanese-course chrome only: a Spanish lesson has no romaji
                    // to switch, so the toggle must not render there.
                    if (isJapanese) {
                        Surface(
                            modifier = Modifier
                                .clickable(onClick = onToggleRomaji),
                            shape = RoundedCornerShape(8.dp),
                            color = if (showRomaji) Color(0xFFE5F5FF) else Color(0xFFF5F5F5),
                            border = androidx.compose.foundation.BorderStroke(
                                width = 1.dp,
                                color = if (showRomaji) Color(0xFF3D7EA6) else Color(0xFFD5D5D5),
                            ),
                        ) {
                            Text(
                                text = if (showRomaji) "あ/a" else "あ",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (showRomaji) Color(0xFF1899D6) else Color(0xFF888888),
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            ExerciseBottomBar(
                feedback = exercise.feedback,
                hasSelection = hasSelection,
                onCheck = onCheckAnswer,
                onContinue = onNextChallenge,
            )
        },
    ) { padding ->
        if (hearts == 0) {
            OutOfHeartsSheet(
                onRefill = onRefillHearts,
                onExit = onExit,
                modifier = Modifier.padding(padding),
            )
        } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                // A kana match board is eight rows of tiles plus the prompt, the
                // question and the rule card; without scroll the last rows sit
                // under the bottom bar, where no tap can reach them.
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // An INTRO card replaces the whole body: a prompt line above an empty
            // option list reads as a broken question, so it gets its own card
            // instead, and the generic body below is not composed at all.
            if (isIntro) {
                IntroChallengeCard(
                    glyph = introGlyph,
                    reading = introOption?.romaji,
                    meaning = introMeaning,
                    ruleText = ruleText,
                    onPlayAudio = { onPlayVoice(introAudioSrc ?: "", audioSpeed, introGlyph) },
                    modifier = Modifier.fillMaxWidth(),
                )
            } else {
            // Prompt
            Text(
                text = when (challenge.type) {
                    ChallengeType.SELECT -> "Select the correct meaning"
                    ChallengeType.ASSIST -> "Finish the translation"
                    ChallengeType.WORD_BANK -> "Tap the matching tiles"
                    ChallengeType.LISTEN -> "Tap what you hear"
                    ChallengeType.MATCH_PAIRS -> "Tap the matching pairs"
                    ChallengeType.STORY -> "Read the story and answer the question"
                    ChallengeType.CONJUGATE -> "Pick the correct form"
                    ChallengeType.FILL_BLANK -> "Fill in the blank"
                    ChallengeType.INTRO -> "New word"
                },
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF777777),
                fontWeight = FontWeight.Bold,
            )

            // Question Header with Audio Button
            if (isListen) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        AudioSpeakerButton(
                            size = 72.dp,
                            onClick = { onPlayVoice(listenAudioSrc, audioSpeed, listenAnswer?.text) },
                        )
                        // WI-14: the turtle's fixed 0.6x becomes a real speed
                        // stepper, so the same control also reaches SELECT,
                        // STORY and every other challenge that has audio.
                        AudioSpeedSelector(
                            selected = audioSpeed,
                            onSelect = { audioSpeed = it },
                            onPlayAt = { speed -> onPlayVoice(listenAudioSrc, speed, listenAnswer?.text) },
                        )
                    }
                    Text(
                        text = "Tap to listen • pick a speed to replay",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF3D7EA6),
                        fontWeight = FontWeight.SemiBold,
                    )
                    if (!transcriptShown) {
                        Text(
                            text = "🔇 Can't listen right now? Show text",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF777777),
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.clickable { transcriptShown = true },
                        )
                    } else if (listenAnswer != null) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F7F7)),
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Text(
                                    text = "What you'll hear:",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF777777),
                                )
                                LookupText(
                                    text = listenAnswer.text,
                                    dictionary = dictionary,
                                    onLookup = onLookup,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4B4B4B),
                                )
                                if (showRomaji && listenAnswer.romaji != null) {
                                    Text(
                                        text = listenAnswer.romaji,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color(0xFF777777),
                                    )
                                }
                            }
                        }
                    }
                }
            } else if (challenge.type == ChallengeType.STORY) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFBF8EE)),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFE5D8B8)),
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(text = "STORY MODE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF8C6B1F))
                            if (challenge.audioSrc != null) {
                                AudioSpeakerButton(
                                    size = 36.dp,
                                    onClick = { challenge.audioSrc.let(playAtChosenSpeed) },
                                )
                            }
                        }
                        LookupText(
                            text = challenge.question,
                            dictionary = dictionary,
                            onLookup = onLookup,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF2C2C2C),
                            lineHeight = 22.sp,
                        )
                    }
                }
            } else if (isFillBlank || isAssist) {
                // WI-04 / WI-15: both carry their scaffold inside `question` as a
                // `___` run. FILL_BLANK then asks the learner to *type* the missing
                // form; ASSIST offers it as options. The sentence and its blank are
                // rendered identically so the two mechanics differ only in how the
                // answer is produced.
                val scaffold = BlankPlaceholder.parse(challenge.question)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    if (challenge.audioSrc != null) {
                        AudioSpeakerButton(
                            size = 44.dp,
                            onClick = { challenge.audioSrc.let(playAtChosenSpeed) },
                        )
                    }
                    LookupText(
                        text = if (scaffold != null) {
                            scaffold.before.trimEnd() + " " + BLANK_PLACEHOLDER + scaffold.after
                        } else {
                            challenge.question
                        },
                        dictionary = dictionary,
                        onLookup = onLookup,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4B4B4B),
                        lineHeight = 30.sp,
                        modifier = Modifier.weight(1f),
                    )
                }
            } else if (isConjugate) {
                // WI-03: the prompt already names the required form ("the preterite
                // of hablar — yo"); what makes this readable is the lemma at headline
                // scale plus the machine focus tag, humanised, so the learner sees
                // which paradigm they are working in.
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    if (challenge.audioSrc != null) {
                        AudioSpeakerButton(
                            size = 44.dp,
                            onClick = { challenge.audioSrc.let(playAtChosenSpeed) },
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        LookupText(
                            text = challenge.question,
                            dictionary = dictionary,
                            onLookup = onLookup,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4B4B4B),
                            lineHeight = 30.sp,
                        )
                        GrammarFocus.label(challenge.grammaticalFocus)?.let { focus ->
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFF0F7FF),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFB8DCFF)),
                            ) {
                                Text(
                                    text = focus,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1B6FB8),
                                )
                            }
                        }
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    if (challenge.audioSrc != null) {
                        AudioSpeakerButton(
                            size = 44.dp,
                            onClick = { challenge.audioSrc.let(playAtChosenSpeed) },
                        )
                    }
                    LookupText(
                        text = challenge.question,
                        dictionary = dictionary,
                        onLookup = onLookup,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4B4B4B),
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            // WI-14: LISTEN carries the speed stepper inline with its large
            // speaker; every other challenge that has audio gets it here, so
            // the control is not a LISTEN-only privilege.
            challenge.audioSrc?.takeIf { !isListen }?.let { src ->
                AudioSpeedSelector(
                    selected = audioSpeed,
                    onSelect = { audioSpeed = it },
                    onPlayAt = { speed -> onPlayVoice(src, speed, null) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            // WI-11: the lookup is invisible until it is named once. A tap on
            // the prompt, a hold on an answer - the gesture differs because a
            // tap on an answer is the lesson, and that is not this feature's
            // to take away.
            Text(
                text = "Tap a word above to look it up · hold an answer for its definition",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFFAAAAAA),
            )

            // WI-01a: the rule this challenge teaches, above the answers and
            // dismissible. Instruction, never a choice.
            if (showRuleCard) {
                RuleCard(
                    ruleText = ruleText,
                    grammaticalFocus = challenge.grammaticalFocus,
                    onDismiss = { ruleDismissed = true },
                )
            }

            // Exercise Body: WORD_BANK vs Options List
            if (isMatchPairs) {
                MatchPairsContent(
                    options = options,
                    selectedFirstId = exercise.selectedPairFirstId,
                    matchedIds = exercise.matchedPairIds,
                    showRomaji = showRomaji,
                    dictionary = dictionary,
                    onLookup = onLookup,
                    onSelect = onSelectPairTile,
                )
            } else if (isWordBank) {
                WordBankContent(
                    options = options,
                    selectedOptionIds = exercise.selectedWordTileIds,
                    showRomaji = showRomaji,
                    isChecked = exercise.feedback != null,
                    dictionary = dictionary,
                    onLookup = onLookup,
                    onSelectTile = onSelectWordTile,
                    onRemoveTile = onRemoveWordTile,
                )
            } else if (isFillBlank) {
                // WI-04: typed production. The field follows the styling of the
                // backup-import field and is disabled once feedback exists, so
                // the typed answer cannot be edited after it is graded.
                androidx.compose.material3.OutlinedTextField(
                    value = exercise.typedAnswer,
                    onValueChange = onTypedAnswerChange,
                    enabled = exercise.feedback == null,
                    singleLine = true,
                    placeholder = { Text("Type the missing word or ending") },
                    modifier = Modifier.fillMaxWidth(),
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    options.forEach { option ->
                        val isSelected = exercise.selectedOptionId == option.id
                        val isChecked = exercise.feedback != null

                        OptionCard(
                            text = option.text,
                            romaji = option.romaji,
                            showRomaji = showRomaji,
                            isSelected = isSelected,
                            isChecked = isChecked,
                            hasAudio = option.audioSrc != null,
                            dictionary = dictionary,
                            onLookup = onLookup,
                            onClick = { onSelectOption(option.id) },
                            onAudioClick = { option.audioSrc?.let(playAtChosenSpeed) },
                        )
                    }
                }
            }
            }
        }
        }
    }
}

/**
 * Phase 1's ungraded discovery card, given a face of its own.
 *
 * An INTRO challenge has nothing to answer, so the generic exercise body would
 * render a prompt line ("New word") above an empty option list - a screen that
 * reads as broken rather than calm. This card instead states the badge, the
 * glyph at display scale, its reading, its meaning, and the tip, with the audio
 * button as the single interaction. Nothing here is gradeable, so nothing
 * advertises a check: the bottom bar shows CONTINUE.
 */
@Composable
private fun IntroChallengeCard(
    glyph: String,
    reading: String?,
    meaning: String?,
    ruleText: String?,
    onPlayAudio: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // A kana card is short and is announced as a sound; anything longer is a
    // word. The same threshold sets the glyph size, so a two-kana digraph still
    // gets the large display treatment rather than being shrunk as a "word".
    val isKana = glyph.trim().length in 1..2
    // The screen's body column scrolls, so this card needs no scroll of its own;
    // a nested same-axis scroller would eat the gesture the parent should get.
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Surface(
            shape = RoundedCornerShape(999.dp),
            color = Color(0xFFEAF7EF),
        ) {
            Text(
                text = if (isKana) "NEW SOUND" else "NEW WORD",
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E9E6B),
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEAEAEA)),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                if (FuriganaParser.containsKanji(glyph) && !reading.isNullOrBlank()) {
                    FuriganaText(
                        text = glyph,
                        reading = reading,
                        showRuby = true,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = if (isKana) 48.sp else 32.sp,
                            lineHeight = if (isKana) 60.sp else 40.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                        baseColor = Color(0xFF2C2C2C),
                    )
                } else {
                    Text(
                        text = glyph,
                        fontSize = if (isKana) 48.sp else 32.sp,
                        lineHeight = if (isKana) 60.sp else 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2C2C2C),
                        textAlign = TextAlign.Center,
                    )
                    if (!reading.isNullOrBlank()) {
                        Text(
                            text = reading,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF666666),
                            textAlign = TextAlign.Center,
                        )
                    }
                }
                if (!meaning.isNullOrBlank()) {
                    Text(
                        text = meaning,
                        fontSize = 16.sp,
                        color = Color(0xFF4B4B4B),
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }

        // Always present: the glyph is always pronounceable, so a card whose
        // audio asset is missing still has something to say through TTS. Gating
        // this on a non-null clip would leave those cards with no way to hear
        // the new sound at all.
        AudioSpeakerButton(
            size = 56.dp,
            onClick = onPlayAudio,
        )

        if (!ruleText.isNullOrBlank()) {
            // The tip is what the card exists to deliver, so it is part of the
            // card rather than the dismissible RuleCard the graded challenges
            // use: there is nothing here to grade, and a learner who dismisses
            // the only content loses it.
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFF2F8FD),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFB8DCFF)),
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text(text = "💡", fontSize = 16.sp)
                    Text(
                        text = ruleText,
                        fontSize = 14.sp,
                        color = Color(0xFF1B4B6B),
                        lineHeight = 20.sp,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun WordBankContent(
    options: List<ChallengeOptionEntity>,
    selectedOptionIds: List<Int>,
    showRomaji: Boolean,
    dictionary: com.openlingo.app.dictionary.Dictionary,
    onLookup: (com.openlingo.app.dictionary.DictionaryEntry) -> Unit,
    isChecked: Boolean,
    onSelectTile: (Int) -> Unit,
    onRemoveTile: (Int) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        // Assembled Sentence Slot
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 84.dp),
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFFFAFAFA),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFE5E5E5)),
        ) {
            FlowRow(
                modifier = Modifier.padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (selectedOptionIds.isEmpty()) {
                    Text(
                        text = "Tap tiles below to build the sentence",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFAFAFAF),
                        modifier = Modifier.padding(8.dp),
                    )
                } else {
                    selectedOptionIds.forEach { optionId ->
                        val option = options.find { it.id == optionId }
                        if (option != null) {
                            AssembledWordTileChip(
                                text = option.text,
                                romaji = option.romaji,
                                showRomaji = showRomaji,
                                dictionary = dictionary,
                                onLookup = onLookup,
                                onClick = { if (!isChecked) onRemoveTile(optionId) },
                            )
                        }
                    }
                }
            }
        }

        HorizontalDivider(color = Color(0xFFE5E5E5), thickness = 1.dp)

        // Available Word Bank Tiles
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            options.forEach { option ->
                val isPlaced = option.id in selectedOptionIds
                WordTileChip(
                    text = option.text,
                    romaji = option.romaji,
                    showRomaji = showRomaji,
                    isPlaced = isPlaced,
                    dictionary = dictionary,
                    onLookup = onLookup,
                    onClick = { if (!isChecked) onSelectTile(option.id) },
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun WordTileChip(
    text: String,
    romaji: String? = null,
    showRomaji: Boolean = true,
    isPlaced: Boolean = false,
    dictionary: com.openlingo.app.dictionary.Dictionary,
    onLookup: (com.openlingo.app.dictionary.DictionaryEntry) -> Unit,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.combinedClickable(
            onClick = { if (!isPlaced) onClick() },
            onLongClick = { dictionary.lookup(text)?.let(onLookup) },
        ),
        shape = RoundedCornerShape(12.dp),
        color = if (isPlaced) Color(0xFFE5E5E5) else Color.White,
        shadowElevation = if (isPlaced) 0.dp else 2.dp,
        border = androidx.compose.foundation.BorderStroke(
            width = 1.5.dp,
            color = if (isPlaced) Color(0xFFE5E5E5) else Color(0xFFE0E0E0),
        ),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = if (isPlaced) Color.Transparent else Color(0xFF4B4B4B),
            )
            if (showRomaji && !romaji.isNullOrBlank()) {
                Text(
                    text = romaji,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 11.sp,
                    color = if (isPlaced) Color.Transparent else Color(0xFF888888),
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AssembledWordTileChip(
    text: String,
    romaji: String? = null,
    showRomaji: Boolean = true,
    dictionary: com.openlingo.app.dictionary.Dictionary,
    onLookup: (com.openlingo.app.dictionary.DictionaryEntry) -> Unit,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.combinedClickable(
            onClick = onClick,
            onLongClick = { dictionary.lookup(text)?.let(onLookup) },
        ),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFE5F5FF),
        shadowElevation = 2.dp,
        border = androidx.compose.foundation.BorderStroke(
            width = 1.5.dp,
            color = Color(0xFF3D7EA6),
        ),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1899D6),
            )

            if (showRomaji && !romaji.isNullOrBlank()) {
                Text(
                    text = romaji,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 11.sp,
                    color = Color(0xFF3D7EA6),
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MatchPairsContent(
    options: List<ChallengeOptionEntity>,
    selectedFirstId: Int?,
    matchedIds: Set<Int>,
    showRomaji: Boolean,
    dictionary: com.openlingo.app.dictionary.Dictionary,
    onLookup: (com.openlingo.app.dictionary.DictionaryEntry) -> Unit,
    onSelect: (Int) -> Unit,
) {
    Text(
        text = "Tap matching pairs to clear the board",
        style = MaterialTheme.typography.bodySmall,
        color = Color(0xFF777777),
    )
    Spacer(modifier = Modifier.height(12.dp))
    val shuffled = remember(options) { MatchPairs.shuffledBoard(options) }
    shuffled.chunked(2).forEach { row ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            row.forEach { opt ->
                val isMatched = matchedIds.contains(opt.id)
                val isSelected = opt.id == selectedFirstId
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(72.dp)
                        .alpha(if (isMatched) 0.3f else 1f)
                        .combinedClickable(
                            onClick = { if (!isMatched) onSelect(opt.id) },
                            onLongClick = { dictionary.lookup(opt.text)?.let(onLookup) },
                        ),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = when {
                            isMatched -> Color(0xFFE5E5E5)
                            isSelected -> Color(0xFFDDF4FF)
                            else -> Color.White
                        },
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        width = 2.dp,
                        color = when {
                            isMatched -> Color(0xFFCCCCCC)
                            isSelected -> Color(0xFF3D7EA6)
                            else -> Color(0xFFE5E5E5)
                        },
                    ),
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = opt.text,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = if (isMatched) Color(0xFF999999) else Color(0xFF4B4B4B),
                            )
                            if (showRomaji && opt.romaji != null && !isMatched) {
                                Text(
                                    text = opt.romaji,
                                    fontSize = 11.sp,
                                    color = Color(0xFF3D7EA6),
                                )
                            }
                        }
                    }
                }
            }
            if (row.size == 1) Spacer(modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Composable
private fun AudioSpeakerButton(
    size: androidx.compose.ui.unit.Dp = 56.dp,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(size)
            .background(Color(0xFF3D7EA6), CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = "🔊", fontSize = (size.value * 0.45).sp)
    }
}

/**
 * WI-14: discrete playback speeds, replacing the fixed 0.6f turtle button.
 *
 * Tapping a step both selects it (so the main speaker button replays at that
 * speed) and plays the clip once at it, so the learner hears the difference
 * immediately rather than having to select then press play. One control, no
 * competing slow affordance.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AudioSpeedSelector(
    selected: Float,
    onSelect: (Float) -> Unit,
    onPlayAt: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "SPEED",
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF8C6B1F),
        )
        // FlowRow, not Row: the three labels are fixed-width text, so at a large
        // font scale a plain Row would overflow its parent and clip. FlowRow
        // wraps onto a second line instead, so the selector cannot clip at any
        // font scale regardless of how the surrounding row is constrained.
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            AUDIO_SPEEDS.forEach { speed ->
                val isSelected = speed == selected
                Surface(
                    modifier = Modifier.clickable {
                        onSelect(speed)
                        onPlayAt(speed)
                    },
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) Color(0xFFE8A93C) else Color(0xFFFFF6DB),
                    border = androidx.compose.foundation.BorderStroke(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) Color(0xFFB8860B) else Color(0xFFE8A93C),
                    ),
                ) {
                    Text(
                        text = "${speed}x",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = Color(0xFF8C6B1F),
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun OptionCard(
    text: String,
    romaji: String? = null,
    showRomaji: Boolean = true,
    isSelected: Boolean,
    isChecked: Boolean,
    hasAudio: Boolean,
    dictionary: com.openlingo.app.dictionary.Dictionary,
    onLookup: (com.openlingo.app.dictionary.DictionaryEntry) -> Unit,
    onClick: () -> Unit,
    onAudioClick: () -> Unit,
) {
    // WI-11: the long press stays live after the answer is graded, when the
    // learner is most likely to want the definition. `enabled` is deliberately
    // NOT used to gate it — `combinedClickable(enabled = false)` kills
    // onLongClick along with onClick, which took the lookup away from a graded
    // question. Selection is gated inside onClick instead, so a tap after
    // grading still does nothing, and the submitted answer cannot change: the
    // card's highlight is driven by `isSelected`, which only a tap CHECK
    // accepts ever moves, so a long press leaves the graded state looking
    // exactly as it did.
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = { if (!isChecked) onClick() },
                onLongClick = { dictionary.lookup(text)?.let(onLookup) },
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isSelected -> Color(0xFFDDF4FF)
                else -> Color.White
            },
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 2.dp else 1.5.dp,
            color = when {
                isSelected -> Color(0xFF3D7EA6)
                else -> Color(0xFFE5E5E5)
            },
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            if (showRomaji && !romaji.isNullOrBlank() && FuriganaParser.containsJapanese(text)) {
                FuriganaText(
                    text = text,
                    reading = romaji,
                    showRuby = true,
                    modifier = Modifier.weight(1f),
                    isSelected = isSelected,
                )
            } else {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = text,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color(0xFF1899D6) else Color(0xFF4B4B4B),
                    )
                    if (showRomaji && !romaji.isNullOrBlank()) {
                        Text(
                            text = romaji,
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 13.sp,
                            color = if (isSelected) Color(0xFF3D7EA6) else Color(0xFF777777),
                        )
                    }
                }
            }
            if (hasAudio) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(0xFFE5F5FF), CircleShape)
                        .clickable(onClick = onAudioClick),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = "🔊", fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
private fun ExerciseBottomBar(
    feedback: FeedbackState?,
    hasSelection: Boolean,
    onCheck: () -> Unit,
    onContinue: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = when (feedback) {
            is FeedbackState.Correct -> Color(0xFFD7FFB8)
            is FeedbackState.Incorrect -> Color(0xFFFFDFE0)
            null -> Color.White
        },
        shadowElevation = 8.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            when (feedback) {
                is FeedbackState.Correct -> {
                    androidx.compose.animation.AnimatedVisibility(
                        visible = true,
                        enter = scaleIn(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)) + fadeIn(),
                    ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(text = "✓", fontSize = 24.sp, color = Color(0xFF2E9E6B), fontWeight = FontWeight.Bold)
                        Column {
                            Text(
                                text = "Nicely done!",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E9E6B),
                                fontSize = 18.sp,
                            )
                            Text(
                                text = "+${feedback.pointsGained} XP (saved to device)",
                                fontSize = 13.sp,
                                color = Color(0xFF46A302),
                            )
                            if (feedback.combo >= 3) {
                                Text(
                                    text = when {
                                        feedback.combo >= 7 -> "Legendary! 🏆"
                                        feedback.combo >= 5 -> "Unstoppable! ⚡"
                                        else -> "On fire! 🔥"
                                    },
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE67E22),
                                )
                            }
                            if (feedback.streakRepaired) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "🔥 Streak Repaired!",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE67E22),
                                )
                            }
                        }
                    }
                    }
                    Button(
                        onClick = onContinue,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E9E6B)),
                    ) {
                        Text(text = "CONTINUE", fontWeight = FontWeight.Bold)
                    }
                }
                is FeedbackState.Incorrect -> {
                    androidx.compose.animation.AnimatedVisibility(
                        visible = true,
                        enter = scaleIn(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)) + fadeIn(),
                    ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(text = "✕", fontSize = 24.sp, color = Color(0xFFC0392B), fontWeight = FontWeight.Bold)
                        // weight(1f): the hint is a full sentence, so the
                        // column has to wrap rather than push the ✕ off-screen.
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Correct answer:",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFC0392B),
                                fontSize = 16.sp,
                            )
                            Text(
                                text = feedback.correctAnswer,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF4B4B4B),
                            )
                            // WI-09: the error-specific sentence first — the
                            // rule the *picked* answer broke, derived from that
                            // option's errorTag and the challenge's focus. It
                            // answers the learner's actual mistake, which
                            // "Correct answer: X" on its own does not.
                            feedback.hint?.let { hint ->
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = hint,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF8A3A3A),
                                )
                            }
                            // WI-04c: on a rule-bearing challenge the rule is
                            // restated here, so a typed answer that broke it is
                            // explained at the moment it was broken. The hint
                            // above and the rule below are the two halves:
                            // what you did wrong, and what the rule is.
                            feedback.ruleText?.let { rule ->
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = rule,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp,
                                    color = Color(0xFF7A5C5C),
                                )
                            }
                        }
                    }
                    }
                    Button(
                        onClick = onContinue,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC0392B)),
                    ) {
                        Text(text = "CONTINUE", fontWeight = FontWeight.Bold)
                    }
                }
                null -> {
                    Button(
                        onClick = onCheck,
                        enabled = hasSelection,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2E9E6B),
                            disabledContainerColor = Color(0xFFE5E5E5),
                        ),
                    ) {
                        Text(
                            text = "CHECK",
                            fontWeight = FontWeight.Bold,
                            color = if (hasSelection) Color.White else Color(0xFFAFAFAF),
                        )
                    }
                }
            }
        }
    }
}

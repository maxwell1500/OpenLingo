package com.duo.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duo.app.DuoApplication
import com.duo.app.data.local.entities.CourseEntity
import com.duo.app.data.local.entities.LessonEntity
import com.duo.app.data.local.entities.UnitEntity
import com.duo.app.data.local.entities.UserProgressEntity
import com.duo.app.data.local.entities.UnitWithLessons
import com.duo.app.data.local.models.ChallengeType
import com.duo.app.data.repository.AnswerResult
import com.duo.app.data.repository.ChallengeWithOptions
import com.duo.app.data.repository.DailyQuest
import com.duo.app.grammar.AnswerGrader
import com.duo.app.grammar.ErrorHint
import com.duo.app.grammar.drills.StructureDrill
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface MainTab {
    data object Learn : MainTab
    data object Characters : MainTab
    data object Practice : MainTab
    data object Profile : MainTab
}

sealed interface ActiveScreen {
    data object LessonMap : ActiveScreen
    data class Exercise(
        val lessonId: Int,
        val challengeIndex: Int,
        val totalChallenges: Int,
        val currentChallenge: ChallengeWithOptions,
        val selectedOptionId: Int? = null,
        val selectedWordTileIds: List<Int> = emptyList(),
        val selectedPairFirstId: Int? = null,
        val matchedPairIds: Set<Int> = emptySet(),
        val feedback: FeedbackState? = null,
        /** Free text typed by the learner on a FILL_BLANK challenge. */
        val typedAnswer: String = "",
    ) : ActiveScreen
    data class CharacterDrawing(
        val character: com.duo.app.data.local.character.JapaneseCharacter,
        val completedStrokes: Int = 0,
        val isCompleted: Boolean = false,
    ) : ActiveScreen
    data class LessonComplete(val lessonId: Int, val pointsGained: Int, val perfectBonus: Int = 0) : ActiveScreen
    /** WI-12: the browse-and-listen vocabulary list for one unit. */
    data class UnitVocabulary(val unitId: Int) : ActiveScreen
    /**
     * The generated conjugation drill for one unit. A study surface, not a lesson: it
     * is opened from the unit header beside the vocabulary list and closed back to the
     * lesson map the same way.
     */
    data class GrammarDrills(val unitId: Int) : ActiveScreen
    data class CheckpointResult(
        val level: String,
        val correct: Int,
        val total: Int,
    ) : ActiveScreen
    data object Settings : ActiveScreen
}

sealed interface FeedbackState {
    data class Correct(val pointsGained: Int, val combo: Int = 0) : FeedbackState
    /**
     * [ruleText] is carried for challenges that teach a rule (FILL_BLANK above
     * all): the learner sees *why* the answer was wrong at the moment they got
     * it wrong, not after a re-read of the unit.
     *
     * [hint] (WI-09) is the error-specific half of that, derived from the
     * option the learner actually picked: which rule *their* answer broke, not
     * just which rule the challenge teaches. Null when the picked option
     * carries no errorTag, in which case [ruleText] alone explains the miss.
     */
    data class Incorrect(
        val correctAnswer: String,
        val ruleText: String? = null,
        val hint: String? = null,
    ) : FeedbackState
}


@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel(application: Application) : AndroidViewModel(application) {

    /** Longest session a checkpoint may run; held-out items are never dropped to fit. */
    private companion object {
        const val MAX_CHECKPOINT_CHALLENGES = 30
    }

    private val repository = (application as DuoApplication).repository
    private val audioPlayer = (application as DuoApplication).audioPlayer

    val userProgress: StateFlow<UserProgressEntity?> = repository.getUserProgress()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val courses: StateFlow<List<CourseEntity>> = repository.getAllCourses()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unitsWithLessons: StateFlow<List<UnitWithLessons>> = userProgress
        .flatMapLatest { progress ->
            val courseId = progress?.activeCourseId ?: 1
            repository.getUnitsWithLessonsForCourse(courseId)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /**
     * Rule text per unit id, derived from the unit's challenges (WI-01b) rather
     * than stored on `units`, so a header and its lessons can never disagree
     * about what they teach. Reuses the lesson-path challenge query, which
     * already excludes held-out checkpoint items — a rule the learner has not
     * been taught must not advertise itself on the unit header.
     */
    val unitRuleTexts: StateFlow<Map<Int, List<String>>> = unitsWithLessons
        .flatMapLatest { units ->
            flow {
                if (units.isEmpty()) {
                    emit(emptyMap())
                } else {
                    val unitIdByLesson = units.flatMap { u -> u.lessons.map { it.id to u.unit.id } }.toMap()
                    val rules = LinkedHashMap<Int, MutableList<String>>()
                    repository.getChallengesForUnits(units.map { it.unit.id })
                        .forEach { withOptions ->
                            val unitId = unitIdByLesson[withOptions.challenge.lessonId] ?: return@forEach
                            val rule = withOptions.challenge.ruleText?.trim().orEmpty()
                            if (rule.isNotEmpty()) {
                                rules.getOrPut(unitId) { mutableListOf() }.let { if (rule !in it) it.add(rule) }
                            }
                        }
                    emit(rules.mapValues { (_, value) -> value.toList() })
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    val completedChallengeIds: StateFlow<List<Int>> = repository.getCompletedChallengeIds()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val completedLessonIds: StateFlow<Set<Int>> = repository.getCompletedLessonIds()
        .map { it.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val courseComplete: StateFlow<Boolean> = combine(
        unitsWithLessons,
        completedLessonIds,
    ) { units, completed ->
        val allLessonIds = units.flatMap { it.lessons.map { l -> l.id } }
        allLessonIds.isNotEmpty() && allLessonIds.all { completed.contains(it) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)


    private val _activeScreen = MutableStateFlow<ActiveScreen>(ActiveScreen.LessonMap)
    val activeScreen: StateFlow<ActiveScreen> = _activeScreen.asStateFlow()

    private val _currentTab = MutableStateFlow<MainTab>(MainTab.Learn)
    val currentTab: StateFlow<MainTab> = _currentTab.asStateFlow()
    private val _completedLessonCount = MutableStateFlow(0)
    val completedLessonCount: StateFlow<Int> = _completedLessonCount.asStateFlow()

    /** Refreshes Profile aggregates (lesson completions) from local tables. */
    fun refreshProfileStats() {
        viewModelScope.launch {
            _completedLessonCount.value = repository.getCompletedLessonCount()
        }
    }

    val masteredCharacters: StateFlow<Set<String>> = repository.getCharacterMastery()
        .map { list -> list.map { it.character }.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val mistakes: StateFlow<List<com.duo.app.data.local.entities.MistakeEntry>> =
        repository.getMistakes()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val exerciseTypeStats: StateFlow<List<com.duo.app.data.local.entities.ExerciseTypeStatsEntity>> =
        repository.getExerciseTypeStats()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dueVocab: StateFlow<List<com.duo.app.data.local.entities.VocabScheduleEntity>> = userProgress
        .flatMapLatest { progress ->
            val lang = if (progress?.activeCourseId == 2) "ja" else "es"
            repository.getDueVocab(lang)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allVocab: StateFlow<List<com.duo.app.data.local.entities.VocabScheduleEntity>> = userProgress
        .flatMapLatest { progress ->
            val lang = if (progress?.activeCourseId == 2) "ja" else "es"
            repository.getAllVocab(lang)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dueVocabCount: StateFlow<Int> = userProgress
        .flatMapLatest { progress ->
            val lang = if (progress?.activeCourseId == 2) "ja" else "es"
            repository.getDueVocabCount(lang)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    /**
     * WI-11: the offline dictionary, built once from the tables already on the
     * device. Empty until it loads, and an empty dictionary declines every
     * lookup, so a sheet can never open on a half-built index.
     */
    val dictionary: StateFlow<com.duo.app.dictionary.Dictionary> = flow {
        emit(repository.loadDictionary())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), com.duo.app.dictionary.Dictionary.EMPTY)

    /** The headwords each unit teaches, keyed by unit id. */
    private val wordsByUnit: StateFlow<Map<Int, List<UnitWord>>> = unitsWithLessons
        .flatMapLatest { units ->
            flow { emit(UnitVocabularyIndex.wordsByUnit(repository, units)) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    private val openUnitId: StateFlow<Int?> = _activeScreen
        .map { (it as? ActiveScreen.UnitVocabulary)?.unitId }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val openDrillUnitId: StateFlow<Int?> = _activeScreen
        .map { (it as? ActiveScreen.GrammarDrills)?.unitId }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    /**
     * The conjugation paradigms the open unit's lessons generate.
     *
     * Read from the lesson-path query, so held-out checkpoint items are excluded twice
     * over: the query filters them and [StructureDrill] refuses them again. Nothing here
     * is written back — a drill is study, not a lesson, so it records no progress, no
     * mistake and no exercise-type statistic.
     */
    val grammarDrills: StateFlow<List<com.duo.app.grammar.drills.Paradigm>> =
        openDrillUnitId
            .map { unitId ->
                if (unitId == null) {
                    emptyList()
                } else {
                    StructureDrill.paradigms(repository.getChallengesForUnits(listOf(unitId)))
                }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /**
     * Words the open unit teaches. See [UnitVocabularyIndex] for why the unit grouping
     * is derived from the corpus rather than stored, and why the FSRS review schedule
     * does not gate it; a unit that teaches no indexable term shows the empty state.
     */
    val unitVocabulary: StateFlow<List<UnitWord>> =
        combine(openUnitId, wordsByUnit) { unitId, words ->
            if (unitId == null) emptyList() else words[unitId].orEmpty()
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** Word count per unit, for the unit header affordance. */
    val unitVocabCounts: StateFlow<Map<Int, Int>> =
        wordsByUnit
            .map { UnitVocabularyIndex.countsByUnit(it) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    /**
     * Words this learner has actually met, across the whole course.
     *
     * Drives the Practice tab's "words unlocked" count, which used to be a hardcoded
     * 14-row list rendered before the learner had done anything. A fresh install has
     * completed no challenge, so this is empty and the count is zero — the honest answer
     * — and it grows one distinct headword at a time as challenges are completed. See
     * [LearnerVocabulary]; reading progress to describe it writes nothing.
     */
    val unlockedWords: StateFlow<List<UnitWord>> =
        combine(unitsWithLessons, completedChallengeIds) { units, completed ->
            LearnerVocabulary.wordsMet(repository, units, completed.toSet())
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /**
     * Drillable forms per unit, for the unit header chip. Derived the same way the drill
     * itself is — from the lesson-path challenges — so the number on the chip and the
     * table behind it cannot disagree. Units with nothing drillable are left out, so
     * a caller can tell "no drill" from "not loaded yet".
     *
     * Grouping happens **per unit, before** paradigms are built, because that is the
     * only order the two can agree in. `StructureDrill.paradigms` folds two rows for
     * the same form of the same focus into one, and that fold is correct for a table
     * — a form taught twice is one conjugation, not two. Run across every unit at
     * once, though, the fold also collapses a form that unit 9 and unit 10 each teach
     * under the same slug, and the survivor is attributed to whichever unit won, so
     * the other unit silently lost a row. The chip then read "5 forms" above a table
     * with six. Slicing per unit first makes this the same computation the screen
     * performs, and the counts agree by construction rather than by coincidence.
     */
    val unitDrillCounts: StateFlow<Map<Int, Int>> = unitsWithLessons
        .flatMapLatest { units ->
            flow {
                if (units.isEmpty()) {
                    emit(emptyMap())
                } else {
                    val unitIdByLesson = units.flatMap { u -> u.lessons.map { it.id to u.unit.id } }.toMap()
                    val byUnit = repository.getChallengesForUnits(units.map { it.unit.id })
                        .filter { it.challenge.lessonId in unitIdByLesson }
                        .groupBy { unitIdByLesson.getValue(it.challenge.lessonId) }
                    emit(
                        byUnit.mapValues { (_, challenges) ->
                            StructureDrill.paradigms(challenges).sumOf { it.entries.size }
                        },
                    )
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    fun openUnitVocabulary(unitId: Int) {
        _activeScreen.value = ActiveScreen.UnitVocabulary(unitId)
    }

    fun closeUnitVocabulary() {
        _activeScreen.value = ActiveScreen.LessonMap
    }

    fun openGrammarDrills(unitId: Int) {
        _activeScreen.value = ActiveScreen.GrammarDrills(unitId)
    }

    fun closeGrammarDrills() {
        _activeScreen.value = ActiveScreen.LessonMap
    }

    fun reviewVocab(item: com.duo.app.data.local.entities.VocabScheduleEntity, rating: Int) {
        viewModelScope.launch {
            repository.reviewVocab(item, rating)
        }
    }
    // Sound/haptics gates: user settings, defaulting to on for fresh installs.
    private fun soundOn(): Boolean = userProgress.value?.soundEnabled != false
    private fun hapticsOn(): Boolean = userProgress.value?.hapticsEnabled != false

    private fun playVoiceIfEnabled(audioSrc: String, speed: Float = 1.0f, fallbackText: String? = null) {
        val lang = if (userProgress.value?.activeCourseId == 2) "ja" else "es"
        if (soundOn()) audioPlayer.playVoice(audioSrc, speed, fallbackText, lang)
    }

    fun setSoundEnabled(enabled: Boolean) {
        viewModelScope.launch { repository.setSoundEnabled(enabled) }
    }

    fun setHapticsEnabled(enabled: Boolean) {
        viewModelScope.launch { repository.setHapticsEnabled(enabled) }
    }

    fun setThemeAccent(accent: String) {
        viewModelScope.launch { repository.setThemeAccent(accent) }
    }

    fun setThemeMode(mode: String) {
        viewModelScope.launch { repository.setThemeMode(mode) }
    }

    fun setDailyQuestGoal(goal: Int) {
        viewModelScope.launch { repository.setDailyQuestGoal(goal) }
    }

    fun completeOnboarding() {
        viewModelScope.launch { repository.setOnboardingSeen() }
    }

    fun clearAllMistakes() {
        viewModelScope.launch { repository.clearAllMistakes() }
    }

    fun resetAllProgress() {
        viewModelScope.launch {
            repository.resetAllProgress()
            _activeScreen.value = ActiveScreen.LessonMap
        }
    }

    fun openSettings() {
        _activeScreen.value = ActiveScreen.Settings
    }

    fun closeSettings() {
        _activeScreen.value = ActiveScreen.LessonMap
    }
    fun exportBackup(onResult: (String) -> Unit) {
        viewModelScope.launch {
            val json = repository.exportBackupJson()
            onResult(json)
        }
    }

    fun importBackup(jsonString: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repository.importBackupJson(jsonString)
            if (result.isSuccess) {
                refreshProfileStats()
                com.duo.app.widget.OpenLingoWidgetProvider.updateAll(getApplication())
            }
            onResult(result.isSuccess)
        }
    }

    // Exercise state
    private var currentLessonChallenges: List<ChallengeWithOptions> = emptyList()
    private var lessonPointsAccumulated: Int = 0
    private var lessonMistakes: Int = 0
    private var lessonCombo: Int = 0
    val dailyQuest: StateFlow<DailyQuest> = repository.getDailyQuest()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DailyQuest(0, 30))


    fun switchCourse(courseId: Int) {
        viewModelScope.launch {
            repository.switchCourse(courseId)
            _activeScreen.value = ActiveScreen.LessonMap
        }
    }

    fun selectTab(tab: MainTab) {
        _currentTab.value = tab
        if (tab is MainTab.Profile) refreshProfileStats()
        if (_activeScreen.value !is ActiveScreen.Exercise && _activeScreen.value !is ActiveScreen.CharacterDrawing) {
            _activeScreen.value = ActiveScreen.LessonMap
        }
    }

    fun startCharacterDrawing(character: com.duo.app.data.local.character.JapaneseCharacter) {
        _activeScreen.value = ActiveScreen.CharacterDrawing(
            character = character,
            completedStrokes = 0,
            isCompleted = false,
        )
        if (soundOn()) audioPlayer.speakText(character.character, "ja")
    }
    fun onStrokeCompleted(strokeIndex: Int) {
        val current = _activeScreen.value as? ActiveScreen.CharacterDrawing ?: return
        val character = current.character
        val nextStrokes = current.completedStrokes + 1
        val isFinished = nextStrokes >= character.strokes.size
        if (isFinished) {
            if (soundOn()) audioPlayer.playSparkle()
            if (hapticsOn()) com.duo.app.feedback.Haptics.celebrate(getApplication())
            viewModelScope.launch {
                repository.submitAnswer(9000 + character.character.hashCode().coerceAtLeast(0) % 900, true)
                repository.markCharacterMastered(character.character, character.scriptType.name)
            }
            _activeScreen.value = current.copy(
                completedStrokes = nextStrokes,
                isCompleted = true,
            )
        } else {
            if (soundOn()) audioPlayer.playCorrectSound()
            if (hapticsOn()) com.duo.app.feedback.Haptics.tick(getApplication())
            _activeScreen.value = current.copy(
                completedStrokes = nextStrokes,
            )
        }
    }
    fun exitCharacterDrawing() {
        _activeScreen.value = ActiveScreen.LessonMap
    }
    fun resetCharacterDrawing() {
        val current = _activeScreen.value as? ActiveScreen.CharacterDrawing ?: return
        _activeScreen.value = current.copy(
            completedStrokes = 0,
            isCompleted = false,
        )
    }


    fun toggleRomaji() {
        viewModelScope.launch {
            val currentProgress = userProgress.value ?: return@launch
            repository.setShowRomaji(!currentProgress.showRomaji)
        }
    }

    private var isInPracticeSession = false


    private var isInCheckpoint = false
    private var checkpointLevel: String = ""
    private var checkpointCourseId: Int = 0
    private var checkpointCorrect: Int = 0
    private var checkpointMissedIds: List<Int> = emptyList()

    /**
     * Starts a checkpoint assessment for a given level.
     *
     * WI-08: the session draws the level's **held-out** pool first — sentences in the
     * same grammar structures the lessons taught, which never appear on a lesson path —
     * and tops it up with taught items up to `MAX_CHECKPOINT_CHALLENGES`. A2 has four
     * held-out items, B1 eight and N4 ten; A1 and N5 have none yet, so those two levels
     * are drawn entirely from taught items and measure recall rather than generalisation.
     */
    fun startCheckpoint(level: String) {
        val courseId = userProgress.value?.activeCourseId ?: 1
        isInCheckpoint = true
        checkpointLevel = level
        checkpointCourseId = courseId
        checkpointCorrect = 0
        checkpointMissedIds = emptyList()
        viewModelScope.launch {
            // Determine unit IDs for this level
            val unitIds = getUnitIdsForLevel(courseId, level)
            if (unitIds.isEmpty()) return@launch
            val heldOut = repository.getHeldOutChallengesForUnits(unitIds).shuffled()
            val heldOutIds = heldOut.map { it.challenge.id }.toSet()
            val taught = repository.getChallengesForUnits(unitIds)
                .filterNot { it.challenge.id in heldOutIds }
                .shuffled()
            if (heldOut.isEmpty() && taught.isEmpty()) return@launch
            // Every held-out item is included; taught items top the session up to the cap.
            val sample = (heldOut + taught.take((MAX_CHECKPOINT_CHALLENGES - heldOut.size).coerceAtLeast(0)))
                .shuffled()
            isInPracticeSession = true
            currentLessonChallenges = sample
            lessonPointsAccumulated = 0
            lessonMistakes = 0
            lessonCombo = 0
            val firstChallenge = sample[0]
            firstChallenge.challenge.audioSrc?.let { playVoiceIfEnabled(it) }
            _activeScreen.value = ActiveScreen.Exercise(
                lessonId = -1,
                challengeIndex = 0,
                totalChallenges = sample.size,
                currentChallenge = firstChallenge,
            )
        }
    }

    /**
     * Maps a CEFR/JLPT level name to the unit IDs that belong to it.
     *
     * Spanish units are 10-19 and 30-33, Japanese units are 20-29 and 40-43, and the
     * courses never share a unit id, so the level->unit map has to branch on the
     * course. B1 is everything from the irregular preterite onward (units 30-33):
     * the past perfect, the conditional and its connectives in 30-31, then the
     * subjunctive, the imperative, the object and reflexive pronouns and gustar in
     * 32-33. A2 stays where it was, on the regular preterite and imperfecto of
     * units 18-19. N4 is likewise the whole intermediate block: te-form, requests
     * and potential in 28-29, the past, the plain-vs-polite register, the adjective
     * classes, ability, opinion and the giving trio in 40-41, and the passive,
     * the causative and the relative clause in 42-43.
     */
    private fun getUnitIdsForLevel(courseId: Int, level: String): List<Int> {
        return when (level) {
            "A1" -> if (courseId == 1) listOf(10, 11, 12, 13, 14, 15, 16, 17) else emptyList()
            "A2" -> if (courseId == 1) listOf(18, 19) else emptyList()
            "B1" -> if (courseId == 1) listOf(30, 31, 32, 33) else emptyList()
            "N5" -> if (courseId == 2) listOf(20, 21, 22, 23, 24, 25, 26, 27) else emptyList()
            "N4" -> if (courseId == 2) listOf(28, 29, 40, 41, 42, 43) else emptyList()
            else -> emptyList()
        }
    }

    fun startPlacementTest() {
        isInPracticeSession = true
        isInCheckpoint = true
        checkpointCourseId = userProgress.value?.activeCourseId ?: 1
        checkpointLevel = "Placement"
        checkpointCorrect = 0
        checkpointMissedIds = emptyList()

        viewModelScope.launch {
            val courseId = checkpointCourseId
            val allUnitIds = if (courseId == 1) listOf(10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 30, 31, 32, 33)
                            else listOf(20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 40, 41, 42, 43)
            val sample = repository.getChallengesForUnits(allUnitIds).shuffled().take(25)
            if (sample.isNotEmpty()) {
                currentLessonChallenges = sample
                lessonPointsAccumulated = 0
                lessonMistakes = 0
                lessonCombo = 0
                val first = sample[0]
                first.challenge.audioSrc?.let { playVoiceIfEnabled(it) }
                _activeScreen.value = ActiveScreen.Exercise(
                    lessonId = -1,
                    challengeIndex = 0,
                    totalChallenges = sample.size,
                    currentChallenge = first,
                )
            }
        }
    }

    fun applyPlacementScore(correct: Int, total: Int) {
        val percent = if (total > 0) (correct * 100) / total else 0
        viewModelScope.launch {
            when {
                percent >= 80 -> repository.completeChallengesUpToUnit(18) // Jump to A2 / N4
                percent >= 50 -> repository.completeChallengesUpToUnit(14) // Jump to mid-A1 / mid-N5
                else -> { /* Start at beginning */ }
            }
            repository.setOnboardingSeen()
            refreshProfileStats()
            _activeScreen.value = ActiveScreen.LessonMap
        }
    }
    fun startLesson(lessonId: Int) {
        isInPracticeSession = false
        viewModelScope.launch {
            val challenges = repository.getChallengesForLesson(lessonId)
            if (challenges.isNotEmpty()) {
                currentLessonChallenges = challenges
                lessonPointsAccumulated = 0
                lessonMistakes = 0
                lessonCombo = 0
                val firstChallenge = challenges[0]
                firstChallenge.challenge.audioSrc?.let { playVoiceIfEnabled(it) }
                _activeScreen.value = ActiveScreen.Exercise(
                    lessonId = lessonId,
                    challengeIndex = 0,
                    totalChallenges = challenges.size,
                    currentChallenge = firstChallenge,
                )
            }
        }
    }

    fun startMistakePractice() {
        viewModelScope.launch {
            val challenges = repository.getMistakeChallenges()
            if (challenges.isNotEmpty()) {
                isInPracticeSession = true
                currentLessonChallenges = challenges
                lessonPointsAccumulated = 0
                lessonMistakes = 0
                lessonCombo = 0
                val firstChallenge = challenges[0]
                firstChallenge.challenge.audioSrc?.let { playVoiceIfEnabled(it) }
                _activeScreen.value = ActiveScreen.Exercise(
                    lessonId = -1,
                    challengeIndex = 0,
                    totalChallenges = challenges.size,
                    currentChallenge = firstChallenge,
                )
            }
        }
    }
    fun selectOption(optionId: Int) {
        val current = _activeScreen.value as? ActiveScreen.Exercise ?: return
        if (current.feedback != null) return // Already checked
        _activeScreen.value = current.copy(selectedOptionId = optionId)
        // Play pronunciation if option has audio
        val option = current.currentChallenge.options.find { it.id == optionId }
        option?.audioSrc?.let { playVoiceIfEnabled(it) }
    }

    fun selectWordTile(optionId: Int) {
        val current = _activeScreen.value as? ActiveScreen.Exercise ?: return
        if (current.feedback != null) return
        if (current.selectedWordTileIds.contains(optionId)) return
        val option = current.currentChallenge.options.find { it.id == optionId }
        option?.audioSrc?.let { playVoiceIfEnabled(it) }
        _activeScreen.value = current.copy(
            selectedWordTileIds = current.selectedWordTileIds + optionId
        )
    }

    fun selectPairTile(optionId: Int) {
        val current = _activeScreen.value as? ActiveScreen.Exercise ?: return
        if (current.feedback != null) return
        if (current.matchedPairIds.contains(optionId)) return

        val option = current.currentChallenge.options.find { it.id == optionId }
        option?.audioSrc?.let { playVoiceIfEnabled(it) }

        val firstId = current.selectedPairFirstId
        if (firstId == null) {
            _activeScreen.value = current.copy(selectedPairFirstId = optionId)
            return
        }
        if (firstId == optionId) {
            _activeScreen.value = current.copy(selectedPairFirstId = null)
            return
        }

        // Paired by consecutive IDs: (2k, 2k+1) where 2k+1 = 2k + 1
        val isPair = (firstId xor 1) == optionId
        if (isPair) {
            if (hapticsOn()) com.duo.app.feedback.Haptics.tick(getApplication())
            val nextMatched = current.matchedPairIds + setOf(firstId, optionId)
            val allMatched = nextMatched.size >= current.currentChallenge.options.size
            _activeScreen.value = current.copy(
                selectedPairFirstId = null,
                matchedPairIds = nextMatched,
                feedback = if (allMatched) FeedbackState.Correct(pointsGained = 10, combo = 0) else null,
            )
            if (allMatched) {
                viewModelScope.launch {
                    repository.submitAnswer(current.currentChallenge.challenge.id, isCorrect = true)
                }
                if (soundOn()) audioPlayer.playCorrectSound()
                if (hapticsOn()) com.duo.app.feedback.Haptics.correct(getApplication())
                lessonPointsAccumulated += 10
            }
        } else {
            if (hapticsOn()) com.duo.app.feedback.Haptics.incorrect(getApplication())
            _activeScreen.value = current.copy(selectedPairFirstId = null)
        }
    }

    fun removeWordTile(optionId: Int) {
        val current = _activeScreen.value as? ActiveScreen.Exercise ?: return
        if (current.feedback != null) return
        _activeScreen.value = current.copy(
            selectedWordTileIds = current.selectedWordTileIds - optionId
        )
    }

    /** Records the learner's keystrokes on a FILL_BLANK challenge. */
    fun updateTypedAnswer(text: String) {
        val current = _activeScreen.value as? ActiveScreen.Exercise ?: return
        if (current.feedback != null) return
        _activeScreen.value = current.copy(typedAnswer = text)
    }

    fun checkAnswer() {
        val current = _activeScreen.value as? ActiveScreen.Exercise ?: return
        val challenge = current.currentChallenge.challenge

        val isCorrect: Boolean
        val correctSolution: String
        /** WI-09: the option(s) this attempt was built from, for the error hint. */
        val chosenOptions: List<com.duo.app.data.local.entities.ChallengeOptionEntity>

        when (challenge.type) {
            ChallengeType.WORD_BANK -> {
                if (current.selectedWordTileIds.isEmpty()) return
                val correctOptions = current.currentChallenge.options
                    .filter { it.correct }
                    .sortedBy { it.id }
                correctSolution = correctOptions.joinToString(" ") { it.text }
                val chosenSentence = current.selectedWordTileIds.mapNotNull { id ->
                    current.currentChallenge.options.find { it.id == id }?.text
                }.joinToString(" ")

                isCorrect = chosenSentence.trim().equals(correctSolution.trim(), ignoreCase = true)
                chosenOptions = current.selectedWordTileIds.mapNotNull { id ->
                    current.currentChallenge.options.find { it.id == id }
                }
            }
            ChallengeType.FILL_BLANK -> {
                if (current.typedAnswer.isBlank()) return
                // The primary answer stays on the correct option; acceptedAnswers
                // adds the legitimate variants ("soy|estoy"). Accents and
                // full-width IME output are forgiven — see AnswerGrader.
                val primary = current.currentChallenge.options.firstOrNull { it.correct }?.text
                val accepted = listOfNotNull(primary) + AnswerGrader.acceptedVariants(challenge.acceptedAnswers)
                correctSolution = primary.orEmpty()
                isCorrect = AnswerGrader.matches(current.typedAnswer, accepted)
                // Typed answers have no picked option, so there is no tag to
                // read: the hint comes from the focus and the required form.
                chosenOptions = emptyList()
            }
            else -> {
                val selectedId = current.selectedOptionId ?: return
                val chosenOption = current.currentChallenge.options.find { it.id == selectedId } ?: return
                isCorrect = chosenOption.correct
                correctSolution = current.currentChallenge.options.find { it.correct }?.text ?: ""
                chosenOptions = listOf(chosenOption)
            }
        }

        // Derived once, on the miss, so a correct answer pays nothing for it.
        val hint = if (isCorrect) {
            null
        } else {
            errorHint(challenge, correctSolution, chosenOptions, current.typedAnswer)
        }

        viewModelScope.launch {
            val isCheckpoint = isInCheckpoint
            when (val result = repository.submitAnswer(current.currentChallenge.challenge.id, isCorrect, isPractice = isInPracticeSession)) {
                is AnswerResult.Correct -> {
                    if (isCheckpoint) checkpointCorrect += 1
                    if (soundOn()) audioPlayer.playCorrectSound()
                    lessonPointsAccumulated += result.pointsGained
                    lessonCombo += 1
                    if (result.streakRepaired) {
                        android.widget.Toast.makeText(
                            getApplication(),
                            "Streak repaired! 🔥",
                            android.widget.Toast.LENGTH_LONG,
                        ).show()
                    }
                    _activeScreen.value = current.copy(
                        feedback = FeedbackState.Correct(result.pointsGained, lessonCombo)
                    )
                }
                is AnswerResult.Incorrect -> {
                    if (isCheckpoint) checkpointMissedIds = checkpointMissedIds + current.currentChallenge.challenge.id
                    if (soundOn()) audioPlayer.playIncorrectSound()
                    if (hapticsOn()) com.duo.app.feedback.Haptics.incorrect(getApplication())
                    lessonMistakes += 1
                    lessonCombo = 0
                    _activeScreen.value = current.copy(
                        feedback = FeedbackState.Incorrect(correctSolution, challenge.ruleText, hint)
                    )
                }
            }
            com.duo.app.widget.OpenLingoWidgetProvider.updateAll(getApplication())
        }
    }

    /**
     * The error-specific explanation for a miss (WI-09).
     *
     * A picked option carries the reason it is wrong in its own `errorTag`, so
     * the sentence names the rule *this* answer broke. On a WORD_BANK assembly
     * several options are in play; a specific grammar tag is preferred over a
     * plain "different word", because that is the one the learner can act on.
     * With no option at all (FILL_BLANK / ASSIST) the explanation is derived
     * from the focus and the form that fills the slot. Returns null when the
     * data offers nothing to derive, leaving the challenge's `ruleText` to
     * explain the miss on its own.
     */
    private fun errorHint(
        challenge: com.duo.app.data.local.entities.ChallengeEntity,
        correctSolution: String,
        chosenOptions: List<com.duo.app.data.local.entities.ChallengeOptionEntity>,
        typedAnswer: String,
    ): String? {
        if (chosenOptions.isEmpty()) {
            return ErrorHint.forTypedAnswer(typedAnswer, correctSolution, challenge.grammaticalFocus)
        }
        val tagSource = chosenOptions.firstOrNull { !it.errorTag.isNullOrBlank() && it.errorTag != "UNRELATED" }
            ?: chosenOptions.firstOrNull { !it.errorTag.isNullOrBlank() }
        val chosen = tagSource?.text ?: chosenOptions.first().text
        return ErrorHint.forChoice(chosen, correctSolution, tagSource?.errorTag, challenge.grammaticalFocus)
    }

    fun nextChallengeOrFinish() {
        val current = _activeScreen.value as? ActiveScreen.Exercise ?: return
        val nextIndex = current.challengeIndex + 1

        if (nextIndex < currentLessonChallenges.size) {
            val nextChallenge = currentLessonChallenges[nextIndex]
            nextChallenge.challenge.audioSrc?.let { playVoiceIfEnabled(it) }
            _activeScreen.value = ActiveScreen.Exercise(
                lessonId = current.lessonId,
                challengeIndex = nextIndex,
                totalChallenges = currentLessonChallenges.size,
                currentChallenge = nextChallenge,
            )
        } else if (isInCheckpoint) {
            // Checkpoint done — save score, show results.
            isInCheckpoint = false
            isInPracticeSession = false
            val total = currentLessonChallenges.size
            val correct = checkpointCorrect
            val missedIds = checkpointMissedIds
            val level = checkpointLevel
            val courseId = checkpointCourseId
            if (soundOn()) audioPlayer.playFanfare()
            if (hapticsOn()) com.duo.app.feedback.Haptics.celebrate(getApplication())
            refreshProfileStats()
            viewModelScope.launch {
                val userId = userProgress.value?.userId ?: "guest_local"
                repository.saveCheckpointScore(userId, courseId, level, correct, total)
                if (missedIds.isNotEmpty()) {
                    repository.queueMistakes(missedIds)
                }
                _activeScreen.value = ActiveScreen.CheckpointResult(
                    level = level,
                    correct = correct,
                    total = total,
                )
                com.duo.app.widget.OpenLingoWidgetProvider.updateAll(getApplication())
            }
        } else if (isInPracticeSession) {
            // Practice session done — back to Practice tab, no lesson credit.
            isInPracticeSession = false
            if (soundOn()) audioPlayer.playCorrectSound()
            if (hapticsOn()) com.duo.app.feedback.Haptics.correct(getApplication())
            refreshProfileStats()
            _activeScreen.value = ActiveScreen.LessonMap
            _currentTab.value = MainTab.Practice
            com.duo.app.widget.OpenLingoWidgetProvider.updateAll(getApplication())
        } else {
            // Regular lesson completed.
            if (soundOn()) audioPlayer.playFanfare()
            if (hapticsOn()) com.duo.app.feedback.Haptics.celebrate(getApplication())
            refreshProfileStats()
            viewModelScope.launch {
                val bonus = if (lessonMistakes == 0) repository.awardPerfectBonus() else 0
                _activeScreen.value = ActiveScreen.LessonComplete(
                    lessonId = current.lessonId,
                    pointsGained = lessonPointsAccumulated + bonus,
                    perfectBonus = bonus,
                )
                com.duo.app.widget.OpenLingoWidgetProvider.updateAll(getApplication())
            }
        }
    }

    fun playVoice(audioSrc: String, speed: Float = 1.0f, fallbackText: String? = null) {
        playVoiceIfEnabled(audioSrc, speed, fallbackText)
    }

    fun closeCheckpointResult() {
        val current = _activeScreen.value as? ActiveScreen.CheckpointResult
        if (current != null && current.level == "Placement") {
            applyPlacementScore(current.correct, current.total)
        } else {
            _activeScreen.value = ActiveScreen.LessonMap
        }
    }

    fun speakText(text: String, speed: Float = 1.0f) {
        val lang = if (userProgress.value?.activeCourseId == 2) "ja" else "es"
        if (soundOn()) audioPlayer.speakText(text, lang, speed)
    }
    fun stopVoice() {
        audioPlayer.stopVoice()
    }

    fun exitExercise() {
        _activeScreen.value = ActiveScreen.LessonMap
    }

    fun refillHearts() {
        viewModelScope.launch {
            repository.refillHearts()
        }
    }
}

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
import com.duo.app.data.repository.AnswerResult
import com.duo.app.data.repository.ChallengeWithOptions
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
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
    ) : ActiveScreen
    data class CharacterDrawing(
        val character: com.duo.app.data.local.character.JapaneseCharacter,
        val completedStrokes: Int = 0,
        val isCompleted: Boolean = false,
    ) : ActiveScreen
    data class LessonComplete(val lessonId: Int, val pointsGained: Int, val perfectBonus: Int = 0) : ActiveScreen
    data class CheckpointResult(
        val level: String,
        val correct: Int,
        val total: Int,
    ) : ActiveScreen
    data object Settings : ActiveScreen
}

sealed interface FeedbackState {
    data class Correct(val pointsGained: Int, val combo: Int = 0) : FeedbackState
    data class Incorrect(val correctAnswer: String) : FeedbackState
}


@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel(application: Application) : AndroidViewModel(application) {

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
    val todayXp: StateFlow<Int> = repository.getTodayXp()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)


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
     * Loads a shuffled sample of challenges from all units in that level,
     * runs them as a practice session (no XP, no hearts), and shows a score at the end.
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
            val challenges = repository.getChallengesForUnits(unitIds)
            if (challenges.isEmpty()) return@launch
            // Shuffle and cap at 30 for a manageable session
            val sample = if (challenges.size > 30) challenges.shuffled().take(30) else challenges.shuffled()
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

    /** Maps a CEFR/JLPT level name to the unit IDs that belong to it. */
    private fun getUnitIdsForLevel(courseId: Int, level: String): List<Int> {
        return when (level) {
            "A1" -> if (courseId == 1) listOf(1, 2, 12, 13, 14, 15, 16, 17) else emptyList()
            "B1" -> if (courseId == 1) listOf(18, 19) else emptyList()
            "N5" -> if (courseId == 2) listOf(1, 2, 12, 13, 14, 15, 16, 17) else emptyList()
            "N4" -> if (courseId == 2) listOf(18, 19) else emptyList()
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
            val allUnitIds = if (courseId == 1) listOf(10, 11, 12, 13, 14, 15, 16, 17, 18, 19)
                             else listOf(10, 11, 12, 13, 14, 15, 16, 17, 18, 19)
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
                percent >= 80 -> repository.completeChallengesUpToUnit(18) // Jump to B1 / N4
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

    fun checkAnswer() {
        val current = _activeScreen.value as? ActiveScreen.Exercise ?: return
        val isWordBank = current.currentChallenge.challenge.type == "WORD_BANK"

        val isCorrect: Boolean
        val correctSolution: String

        if (isWordBank) {
            if (current.selectedWordTileIds.isEmpty()) return
            val correctOptions = current.currentChallenge.options
                .filter { it.correct }
                .sortedBy { it.id }
            correctSolution = correctOptions.joinToString(" ") { it.text }
            val chosenSentence = current.selectedWordTileIds.mapNotNull { id ->
                current.currentChallenge.options.find { it.id == id }?.text
            }.joinToString(" ")

            isCorrect = chosenSentence.trim().equals(correctSolution.trim(), ignoreCase = true)
        } else {
            val selectedId = current.selectedOptionId ?: return
            val chosenOption = current.currentChallenge.options.find { it.id == selectedId } ?: return
            isCorrect = chosenOption.correct
            correctSolution = current.currentChallenge.options.find { it.correct }?.text ?: ""
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
                        feedback = FeedbackState.Incorrect(correctSolution)
                    )
                }
            }
            com.duo.app.widget.OpenLingoWidgetProvider.updateAll(getApplication())
        }
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

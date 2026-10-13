package com.openlingo.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.WindowInsets
import androidx.core.view.WindowCompat
import com.openlingo.app.ui.theme.OpenLingoTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.openlingo.app.ui.DictionarySheet
import com.openlingo.app.ui.StructureDrillScreen
import com.openlingo.app.ui.ActiveScreen
import com.openlingo.app.ui.MainTab
import com.openlingo.app.ui.MainViewModel
import com.openlingo.app.ui.screens.LessonMapScreen
import com.openlingo.app.ui.screens.UnitVocabularyScreen
import com.openlingo.app.ui.screens.ExerciseScreen
import com.openlingo.app.ui.screens.LessonCompleteScreen
import com.openlingo.app.ui.screens.CheckpointScreen
import com.openlingo.app.ui.screens.SettingsScreen
import com.openlingo.app.ui.screens.OnboardingPager
import com.openlingo.app.ui.screens.OpenLingoBottomNavigationBar
import com.openlingo.app.ui.screens.OpenLingoTopAppBar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setShowWhenLocked(true)
        setTurnScreenOn(true)
        enableEdgeToEdge()
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
        }
        setContent {
            val viewModel: MainViewModel = viewModel()
            val userProgress by viewModel.userProgress.collectAsStateWithLifecycle()
            val courses by viewModel.courses.collectAsStateWithLifecycle()
            val unitsWithLessons by viewModel.unitsWithLessons.collectAsStateWithLifecycle()
            val completedChallengeIds by viewModel.completedChallengeIds.collectAsStateWithLifecycle()
            val unitRuleTexts by viewModel.unitRuleTexts.collectAsStateWithLifecycle()
            val activeScreen by viewModel.activeScreen.collectAsStateWithLifecycle()
            val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
            val masteredCharacters by viewModel.masteredCharacters.collectAsStateWithLifecycle()
            val mistakes by viewModel.mistakes.collectAsStateWithLifecycle()
            val completedLessonCount by viewModel.completedLessonCount.collectAsStateWithLifecycle()
            val dailyQuest by viewModel.dailyQuest.collectAsStateWithLifecycle()
            val completedLessonIds by viewModel.completedLessonIds.collectAsStateWithLifecycle()
            val courseComplete by viewModel.courseComplete.collectAsStateWithLifecycle()
            val allVocab by viewModel.allVocab.collectAsStateWithLifecycle()
            val dueVocabCount by viewModel.dueVocabCount.collectAsStateWithLifecycle()
            val unitVocabulary by viewModel.unitVocabulary.collectAsStateWithLifecycle()
            val unlockedWords by viewModel.unlockedWords.collectAsStateWithLifecycle()
            val unitVocabCounts by viewModel.unitVocabCounts.collectAsStateWithLifecycle()
            val grammarDrills by viewModel.grammarDrills.collectAsStateWithLifecycle()
            val unitDrillCounts by viewModel.unitDrillCounts.collectAsStateWithLifecycle()
            val typeStats by viewModel.exerciseTypeStats.collectAsStateWithLifecycle()
            val dictionary by viewModel.dictionary.collectAsStateWithLifecycle()
            val placementError by viewModel.placementError.collectAsStateWithLifecycle()
            // WI-11: which definition sheet is open, if any. Held here rather
            // than in ActiveScreen because a lookup is an overlay on whatever
            // the learner is already doing, not a place they navigated to.
            var lookupEntry by remember { mutableStateOf<com.openlingo.app.dictionary.DictionaryEntry?>(null) }
            val showRomaji = userProgress?.showRomaji ?: true
            val isJapanese = (userProgress?.activeCourseId ?: 1) == 2

            val currentProgress by viewModel.userProgress.collectAsStateWithLifecycle()
            val themeAccent = remember(currentProgress?.themeAccent) {
                com.openlingo.app.ui.theme.ThemeAccent.fromName(currentProgress?.themeAccent)
            }
            val useDarkTheme = when (com.openlingo.app.ui.theme.ThemeMode.fromName(currentProgress?.themeMode)) {
                com.openlingo.app.ui.theme.ThemeMode.LIGHT -> false
                com.openlingo.app.ui.theme.ThemeMode.DARK -> true
                else -> isSystemInDarkTheme()
            }
            OpenLingoTheme(accent = themeAccent, darkTheme = useDarkTheme) {
                SideEffect {
                    WindowCompat.getInsetsController(window, window.decorView).apply {
                        isAppearanceLightStatusBars = !useDarkTheme
                        isAppearanceLightNavigationBars = !useDarkTheme
                    }
                }
                val context = LocalContext.current
                LaunchedEffect(placementError) {
                    placementError?.let { message ->
                        android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_SHORT).show()
                        viewModel.clearPlacementError()
                    }
                }
                // Refilling is silent apart from a haptic tick: the badge this
                // belongs to re-renders with the new count, and the sheet it was
                // opened from closes itself. A raw Android toast fired over a
                // Compose surface for this read as a system message rather than
                // the app answering a tap.
                val onFreeRefill: () -> Unit = {
                    viewModel.refillHearts()
                    if (userProgress?.hapticsEnabled != false) {
                        com.openlingo.app.feedback.Haptics.tick(context)
                    }
                }
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    contentWindowInsets = WindowInsets(0, 0, 0, 0),
                    topBar = {
                        if (activeScreen is ActiveScreen.LessonMap) {
                            OpenLingoTopAppBar(
                                courses = courses,
                                userProgress = userProgress,
                                mistakeCount = mistakes.size,
                                onSelectCourse = viewModel::switchCourse,
                                onRefillHearts = onFreeRefill,
                                onStartMistakePractice = viewModel::startMistakePractice,
                                onOpenSettings = viewModel::openSettings,
                            )
                        }
                    },
                    bottomBar = {
                        if (activeScreen is ActiveScreen.LessonMap) {
                            OpenLingoBottomNavigationBar(
                                currentTab = currentTab,
                                isJapanese = isJapanese,
                                onSelectTab = viewModel::selectTab,
                            )
                        }
                    },
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                    ) {
                        when (val screen = activeScreen) {
                            is ActiveScreen.LessonMap -> {
                                when (currentTab) {
                                    is MainTab.Learn -> {
                                        Crossfade(
                                            targetState = userProgress?.activeCourseId ?: 1,
                                            label = "CourseCrossfade",
                                        ) { _ ->
                                            LessonMapScreen(
                                                unitVocabCounts = unitVocabCounts,
                                                onOpenUnitVocabulary = viewModel::openUnitVocabulary,
                                                unitDrillCounts = unitDrillCounts,
                                                onOpenDrills = viewModel::openGrammarDrills,
                                                unitRuleTexts = unitRuleTexts,
                                                unitsWithLessons = unitsWithLessons,
                                                completedChallengeIds = completedChallengeIds.toSet(),
                                                completedLessonIds = completedLessonIds,
                                                onStartLesson = viewModel::startLesson,
                                                quest = dailyQuest,
                                                brokenStreak = userProgress?.brokenStreak ?: 0,
                                                courseId = userProgress?.activeCourseId ?: 1,
                                                onStartCheckpoint = viewModel::startCheckpoint,
                                            )
                                        }
                                    }
                                    is MainTab.Characters -> {
                                        com.openlingo.app.ui.screens.CharactersTabScreen(
                                            onSelectCharacter = viewModel::startCharacterDrawing,
                                            mastered = masteredCharacters,
                                        )
                                    }
                                    is MainTab.Practice -> {
                                        com.openlingo.app.ui.screens.PracticeTabScreen(
                                            onPlayVoice = viewModel::playVoice,
                                            onStartPractice = {
                                                if (mistakes.isNotEmpty()) {
                                                    viewModel.startMistakePractice()
                                                } else {
                                                    viewModel.startLesson(if (isJapanese) 200 else 100)
                                                }
                                            },
                                            mistakes = mistakes,
                                            onClearAllMistakes = viewModel::clearAllMistakes,
                                            courseComplete = courseComplete,
                                            vocabList = allVocab,
                                            dueVocabCount = dueVocabCount,
                                            onReviewVocab = viewModel::reviewVocab,
                                            unlockedWords = unlockedWords,
                                            dictionary = dictionary,
                                        )
                                    }
                                    is MainTab.Profile -> {
                                        val correct = completedChallengeIds.size
                                        val wrong = mistakes.size
                                        val accuracy =
                                            if (correct + wrong == 0) 100 else (correct * 100) / (correct + wrong)
                                        com.openlingo.app.ui.screens.ProfileTabScreen(
                                            userName = userProgress?.userName ?: "Guest",
                                            courseName = if (isJapanese) "Japanese" else "Spanish",
                                            points = userProgress?.points ?: 0,
                                            streak = userProgress?.streak ?: 1,
                                            completedLessons = completedLessonCount,
                                            isJapanese = isJapanese,
                                            kanaCount = masteredCharacters.size,
                                            accuracyPercent = accuracy,
                                            achievements = com.openlingo.app.ui.evaluateAchievements(
                                                points = userProgress?.points ?: 0,
                                                streak = userProgress?.streak ?: 1,
                                                kanaCount = masteredCharacters.size,
                                                completedChallenges = completedChallengeIds.size,
                                                completedLessons = completedLessonCount,
                                                isJapanese = isJapanese,
                                            ),
                                            typeStats = typeStats,
                                        )
                                    }
                                }
                                if (userProgress?.onboardingSeen == false) {
                                    OnboardingPager(
                                        onDone = viewModel::completeOnboarding,
                                        onTakePlacement = viewModel::startPlacementTest,
                                    )
                                }
                            }
                            is ActiveScreen.CharacterDrawing -> {
                                com.openlingo.app.ui.screens.CharacterDrawingScreen(
                                    character = screen.character,
                                    completedStrokes = screen.completedStrokes,
                                    isCompleted = screen.isCompleted,
                                    onStrokeCompleted = viewModel::onStrokeCompleted,
                                    onPlayVoice = { text -> viewModel.speakText(text) },
                                    onReset = viewModel::resetCharacterDrawing,
                                    onExit = viewModel::exitCharacterDrawing,
                                )
                            }
                            is ActiveScreen.Exercise -> {
                                ExerciseScreen(
                                    exercise = screen,
                                    dictionary = dictionary,
                                    onLookup = { entry -> lookupEntry = entry },
                                    showRomaji = showRomaji,
                                    onToggleRomaji = viewModel::toggleRomaji,
                                    isJapanese = isJapanese,
                                    onSelectOption = viewModel::selectOption,
                                    onSelectWordTile = viewModel::selectWordTile,
                                    onRemoveWordTile = viewModel::removeWordTile,
                                    onPlayVoice = { src, speed, fallback -> viewModel.playVoice(src, speed, fallback) },
                                    onTypedAnswerChange = viewModel::updateTypedAnswer,
                                    onSelectPairTile = viewModel::selectPairTile,
                                    onCheckAnswer = viewModel::checkAnswer,
                                    onNextChallenge = viewModel::nextChallengeOrFinish,
                                    onExit = viewModel::exitExercise,
                                    hearts = userProgress?.hearts ?: 5,
                                    onRefillHearts = onFreeRefill,
                                )
                            }
                            is ActiveScreen.LessonComplete -> {
                                LessonCompleteScreen(
                                    pointsGained = screen.pointsGained,
                                    perfectBonus = screen.perfectBonus,
                                    onContinue = viewModel::exitExercise,
                                )
                            }
                            is ActiveScreen.CheckpointResult -> {
                                CheckpointScreen(
                                    level = screen.level,
                                    correct = screen.correct,
                                    total = screen.total,
                                    passed = screen.passed,
                                    tierLabel = screen.tierLabel,
                                    onDone = viewModel::closeCheckpointResult,
                                )
                            }
                            is ActiveScreen.UnitVocabulary -> {
                                UnitVocabularyScreen(
                                    unit = unitsWithLessons.firstOrNull { it.unit.id == screen.unitId }?.unit,
                                    words = unitVocabulary,
                                    dictionary = dictionary,
                                    onLookup = { entry -> lookupEntry = entry },
                                    onPlayVoice = { src -> viewModel.playVoice(src) },
                                    onBack = viewModel::closeUnitVocabulary,
                                )
                            }
                            is ActiveScreen.GrammarDrills -> {
                                StructureDrillScreen(
                                    unitTitle = unitsWithLessons
                                        .firstOrNull { it.unit.id == screen.unitId }?.unit?.title
                                        ?: "Conjugation Drills",
                                    paradigms = grammarDrills,
                                    onPlayVoice = { src, fallback ->
                                        viewModel.playVoice(src, fallbackText = fallback)
                                    },
                                    onBack = viewModel::closeGrammarDrills,
                                )
                            }
                            is ActiveScreen.Settings -> {
                                SettingsScreen(
                                    userProgress = userProgress,
                                    onToggleSound = viewModel::setSoundEnabled,
                                    onToggleHaptics = viewModel::setHapticsEnabled,
                                    onToggleRomaji = { viewModel.toggleRomaji() },
                                    onSelectThemeAccent = viewModel::setThemeAccent,
                                    onSelectThemeMode = viewModel::setThemeMode,
                                    onSelectDailyQuestGoal = viewModel::setDailyQuestGoal,
                                    onResetProgress = viewModel::resetAllProgress,
                                    onExportBackup = viewModel::exportBackup,
                                    onImportBackup = viewModel::importBackup,
                                    onBack = viewModel::closeSettings,
                                )
                            }
                        }

                        // WI-11: one definition sheet, reachable from every
                        // surface, and a study surface only — opening it writes
                        // nothing.
                        lookupEntry?.let { entry ->
                            DictionarySheet(
                                entry = entry,
                                onPlayVoice = { src -> viewModel.playVoice(src) },
                                onDismiss = { lookupEntry = null },
                            )
                        }
                    }
                }
            }
        }
    }
    override fun onResume() {
        super.onResume()
        com.openlingo.app.widget.OpenLingoWidgetProvider.updateAll(this)
    }
}

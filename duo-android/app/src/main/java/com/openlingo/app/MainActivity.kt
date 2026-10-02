package com.openlingo.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.WindowInsets
import androidx.core.view.WindowCompat
import androidx.compose.foundation.rememberScrollState
import com.openlingo.app.ui.MatchPairs
import com.openlingo.app.ui.theme.OpenLingoTheme
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.openlingo.app.data.local.entities.ChallengeOptionEntity
import com.openlingo.app.data.local.entities.CourseEntity
import com.openlingo.app.data.local.entities.LessonEntity
import com.openlingo.app.data.local.entities.UnitEntity
import com.openlingo.app.data.local.entities.UnitWithLessons
import com.openlingo.app.data.local.entities.UserProgressEntity
import com.openlingo.app.data.local.models.ChallengeType
import com.openlingo.app.data.repository.ChallengeWithOptions
import com.openlingo.app.data.repository.DailyQuest
import com.openlingo.app.data.repository.LocalProgressRepository
import com.openlingo.app.grammar.BlankPlaceholder
import com.openlingo.app.ui.DictionarySheet
import com.openlingo.app.ui.LookupText
import com.openlingo.app.ui.StructureDrillScreen
import com.openlingo.app.grammar.GrammarFocus
import com.openlingo.app.ui.ActiveScreen
import com.openlingo.app.ui.FeedbackState
import com.openlingo.app.ui.MainTab
import com.openlingo.app.ui.MainViewModel
import com.openlingo.app.ui.components.RuleCard
import kotlinx.coroutines.flow.StateFlow

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
                val onFreeRefill: () -> Unit = {
                    viewModel.refillHearts()
                    if (userProgress?.hapticsEnabled != false) com.openlingo.app.feedback.Haptics.tick(context)
                    android.widget.Toast.makeText(
                        context,
                        "Hearts refilled ❤️ Learn offline. Own your progress.",
                        android.widget.Toast.LENGTH_SHORT,
                    ).show()
                }
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    contentWindowInsets = WindowInsets(0, 0, 0, 0),
                    topBar = {
                        if (activeScreen is ActiveScreen.LessonMap) {
                            OpenLingoTopAppBar(
                                courses = courses,
                                userProgress = userProgress,
                                onSelectCourse = viewModel::switchCourse,
                                onRefillHearts = onFreeRefill,
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
                                    onPlayVoice = { src, speed -> viewModel.playVoice(src, speed) },
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

@Composable
private fun OpenLingoBottomNavigationBar(
    currentTab: MainTab,
    isJapanese: Boolean,
    onSelectTab: (MainTab) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // 1. Learn Tab
            val isLearn = currentTab is MainTab.Learn
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { onSelectTab(MainTab.Learn) }
                    .padding(horizontal = 20.dp, vertical = 4.dp),
            ) {
                Text(text = "🏠", fontSize = 22.sp)
                Text(
                    text = "Learn",
                    fontSize = 12.sp,
                    fontWeight = if (isLearn) FontWeight.Bold else FontWeight.Medium,
                    color = if (isLearn) Color(0xFF2E9E6B) else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            // 2. Characters Tab (Japanese only)
            if (isJapanese) {
                val isChars = currentTab is MainTab.Characters
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { onSelectTab(MainTab.Characters) }
                        .padding(horizontal = 20.dp, vertical = 4.dp),
                ) {
                    Text(
                        text = "あ",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isChars) Color(0xFF3D7EA6) else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = "Characters",
                        fontSize = 12.sp,
                        fontWeight = if (isChars) FontWeight.Bold else FontWeight.Medium,
                        color = if (isChars) Color(0xFF3D7EA6) else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            // 3. Practice / Dumbbell Tab
            val isPractice = currentTab is MainTab.Practice
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { onSelectTab(MainTab.Practice) }
                    .padding(horizontal = 20.dp, vertical = 4.dp),
            ) {
                Text(text = "🏋️", fontSize = 22.sp)
                Text(
                    text = "Practice",
                    fontSize = 12.sp,
                    fontWeight = if (isPractice) FontWeight.Bold else FontWeight.Medium,
                    color = if (isPractice) Color(0xFF3D7EA6) else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            // 4. Profile Tab
            val isProfile = currentTab is MainTab.Profile
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { onSelectTab(MainTab.Profile) }
                    .padding(horizontal = 20.dp, vertical = 4.dp),
            ) {
                Text(text = "👤", fontSize = 22.sp)
                Text(
                    text = "Profile",
                    fontSize = 12.sp,
                    fontWeight = if (isProfile) FontWeight.Bold else FontWeight.Medium,
                    color = if (isProfile) Color(0xFF3D7EA6) else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

// -------------------------------------------------------------------------
// Top App Bar: Course Switcher, Streak, XP, Hearts
// -------------------------------------------------------------------------
@Composable
private fun OpenLingoTopAppBar(
    courses: List<CourseEntity>,
    userProgress: UserProgressEntity?,
    onSelectCourse: (Int) -> Unit,
    onRefillHearts: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    val activeCourseId = userProgress?.activeCourseId ?: 1
    val hearts = userProgress?.hearts ?: 5
    val points = userProgress?.points ?: 0
    val streak = userProgress?.streak ?: 1
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                courses.forEach { course ->
                    val isSelected = course.id == activeCourseId
                    Box(
                        modifier = Modifier
                            .background(
                                color = if (isSelected) Color(0xFFE5F5FF) else MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(12.dp),
                            )
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) Color(0xFF3D7EA6) else MaterialTheme.colorScheme.outlineVariant,
                                shape = RoundedCornerShape(12.dp),
                            )
                            .clickable { onSelectCourse(course.id) }
                            .padding(horizontal = 8.dp, vertical = 5.dp),
                    ) {
                        Text(
                            text = "${course.imageSrc} ${course.title}",
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color(0xFF1899D6) else MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            }

            // Stats: Points & Hearts
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                // Streak Flame Badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🔥", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$streak",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE67E22),
                        fontSize = 15.sp,
                    )
                }

                // XP
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "⚡", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$points",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE8A93C),
                        fontSize = 15.sp,
                    )
                }

                // Hearts (tap to refill FREE when below max — pulses when refillable)
                val heartPulse = rememberInfiniteTransition(label = "heart")
                val heartScale by heartPulse.animateFloat(
                    initialValue = 1f,
                    targetValue = if (hearts < 5) 1.25f else 1f,
                    animationSpec = infiniteRepeatable(animation = tween(600)),
                    label = "heartScale",
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { if (hearts < 5) onRefillHearts() },
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "❤️",
                            fontSize = 16.sp,
                            modifier = Modifier.scale(heartScale),
                        )
                        if (hearts < 5) {
                            Text(
                                text = "+",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier
                                    .background(Color(0xFF2E9E6B), CircleShape)
                                    .padding(horizontal = 3.dp),
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$hearts",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFC0392B),
                        fontSize = 15.sp,
                    )
                }

                // Settings gear
                Box(
                    modifier = Modifier
                        .background(Color(0xFFF0F0F0), CircleShape)
                        .clickable(onClick = onOpenSettings)
                        .padding(horizontal = 6.dp, vertical = 4.dp),
                ) {
                    Text(text = "⚙️", fontSize = 14.sp)
                }
            }
        }
    }
}

// -------------------------------------------------------------------------
// Screen 1: Lesson Map (Units, Lesson Nodes, Offline Info Banner)
// -------------------------------------------------------------------------
/** WI-14: discrete playback speeds. 1.0x is the default; 0.5x/0.75x cover
 *  phonemes too fast to catch, which is what the old hardcoded turtle served. */
private val AUDIO_SPEEDS = listOf(0.5f, 0.75f, 1.0f)

/** Rendered in place of an authored `___` run inside a prompt. */
private const val BLANK_PLACEHOLDER = "______"

private enum class LessonState {
    COMPLETED,
    ACTIVE,
    LOCKED,
}

@Composable
private fun LessonMapScreen(
    unitsWithLessons: List<UnitWithLessons>,
    unitRuleTexts: Map<Int, List<String>>,
    unitVocabCounts: Map<Int, Int>,
    onOpenUnitVocabulary: (Int) -> Unit,
    unitDrillCounts: Map<Int, Int>,
    onOpenDrills: (Int) -> Unit,
    completedChallengeIds: Set<Int>,
    completedLessonIds: Set<Int>,
    onStartLesson: (Int) -> Unit,
    quest: DailyQuest,
    brokenStreak: Int,
    courseId: Int,
    onStartCheckpoint: (String) -> Unit,
) {
    val scrollState = rememberScrollState()
    val allLessons = remember(unitsWithLessons) {
        unitsWithLessons.sortedBy { it.unit.orderIndex }
            .flatMap { it.lessons.sortedBy { l -> l.orderIndex } }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .widthIn(max = 640.dp)
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        // Daily quest card
        val questDone = quest.isComplete
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (questDone) Color(0xFFD7FFB8) else Color(0xFFFFF6DB),
            ),
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = if (questDone) "✅ Daily quest complete!" else "🎯 Daily quest",
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "${quest.xp}/${quest.goal} XP",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E9E6B),
                    )
                }
                LinearProgressIndicator(
                    progress = { (quest.xp.toFloat() / quest.goal).coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth().height(10.dp),
                    color = Color(0xFF2E9E6B),
                    trackColor = Color(0xFFE5E5E5),
                )
            }
        }

        // Streak repair card: earn the broken streak back with a replay.
        if (brokenStreak > 0) {
            val repairLessonId = unitsWithLessons.firstOrNull()?.lessons?.firstOrNull()?.id
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFDFE0)),
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "💔 Repair your $brokenStreak-day streak",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFC0392B),
                    )
                    Text(
                        text = "Replay the first lesson to earn it back — no tap-to-fix.",
                        fontSize = 13.sp,
                        color = Color(0xFF4B4B4B),
                    )
                    if (repairLessonId != null) {
                        Button(
                            onClick = { onStartLesson(repairLessonId) },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC0392B)),
                        ) {
                            Text(text = "START REPAIR LESSON", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
        unitsWithLessons.forEach { unitWithLessons ->
            UnitSection(
                unitWithLessons = unitWithLessons,
                ruleTexts = unitRuleTexts[unitWithLessons.unit.id].orEmpty(),
                vocabCount = unitVocabCounts[unitWithLessons.unit.id] ?: 0,
                onOpenVocabulary = { onOpenUnitVocabulary(unitWithLessons.unit.id) },
                drillCount = unitDrillCounts[unitWithLessons.unit.id] ?: 0,
                onOpenDrills = { onOpenDrills(unitWithLessons.unit.id) },
                allLessons = allLessons,
                completedLessonIds = completedLessonIds,
                onStartLesson = onStartLesson,
            )
        }

        // Checkpoint tests section
        val levels = if (courseId == 1) listOf("A1", "A2", "B1") else listOf("N5", "N4")
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
            ),
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "📝 Checkpoint Tests",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = "Test your mastery of each level. Missed questions are added to your practice list.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    levels.forEach { level ->
                        Button(
                            onClick = { onStartCheckpoint(level) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        ) {
                            Text(text = "$level Test", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Offline data banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F7F7)),
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(text = "💾", fontSize = 24.sp)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "100% Offline",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF4B4B4B),
                    )
                    Text(
                        text = "All XP, hearts, and lessons save automatically to your phone. Export a backup in Settings to move them to another device.",
                        fontSize = 12.sp,
                        color = Color(0xFF777777),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun UnitSection(
    unitWithLessons: UnitWithLessons,
    ruleTexts: List<String>,
    vocabCount: Int,
    onOpenVocabulary: () -> Unit,
    drillCount: Int,
    onOpenDrills: () -> Unit,
    allLessons: List<LessonEntity>,
    completedLessonIds: Set<Int>,
    onStartLesson: (Int) -> Unit,
) {
    val unit = unitWithLessons.unit
    val lessons = unitWithLessons.lessons
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Unit Header Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2E9E6B)),
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = unit.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
                Spacer(modifier = Modifier.height(4.dp))
                // WI-01b: once a unit's challenges carry rule text, the header explains the
                // grammar up front instead of only naming the unit. Units with no authored
                // rule yet render the one-line description exactly as before.
                if (ruleTexts.isEmpty()) {
                    Text(
                        text = unit.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFD7FFB8),
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "GRAMMAR",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE8FFD0),
                        )
                        ruleTexts.forEach { rule ->
                            Text(
                                text = rule,
                                style = MaterialTheme.typography.bodySmall,
                                lineHeight = 17.sp,
                                color = Color(0xFFFFFFFF),
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                // WI-12: the unit's vocabulary was previously only reachable as implicit
                // challenge options and FSRS rows. A unit with nothing scheduled for it
                // says so here rather than offering a tap that opens an empty screen.
                // FlowRow, not Row: this header now carries two chips under a rule
                // block that can be several lines long, so at a large font scale a
                // plain Row would overflow the card and clip. FlowRow wraps the second
                // chip onto its own line instead, at any font scale and any card width.
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF4CAF00))
                            .clickable(onClick = onOpenVocabulary)
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                    ) {
                        Text(
                            text = "📖",
                            fontSize = 13.sp,
                        )
                        Text(
                            text = if (vocabCount > 0) {
                                "VOCABULARY · $vocabCount ${if (vocabCount == 1) "word" else "words"}"
                            } else {
                                "VOCABULARY · NOT YET ADDED"
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                        )
                    }
                    // The generated conjugation drill, beside the vocabulary list because
                    // both are study surfaces this unit's own challenges make possible.
                    // A unit that teaches no drillable form says so here rather than
                    // offering a tap that opens an empty table.
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF4CAF00))
                            .clickable(onClick = onOpenDrills)
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                    ) {
                        Text(
                            text = "🔤",
                            fontSize = 13.sp,
                        )
                        Text(
                            text = if (drillCount > 0) {
                                "DRILLS · $drillCount ${if (drillCount == 1) "form" else "forms"}"
                            } else {
                                "DRILLS · NONE YET"
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                        )
                    }
                }
            }
        }

        // Lessons in this Unit (S-curve winding path)
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            val xOffsets = listOf(0.dp, (-46).dp, 0.dp, 46.dp)
            lessons.forEach { lesson ->
                val lessonIndex = allLessons.indexOfFirst { it.id == lesson.id }.coerceAtLeast(0)
                val horizontalOffset = xOffsets[lessonIndex % xOffsets.size]
                val state = when {
                    completedLessonIds.contains(lesson.id) -> LessonState.COMPLETED
                    lessonIndex <= 0 -> LessonState.ACTIVE
                    allLessons.getOrNull(lessonIndex - 1)?.let { completedLessonIds.contains(it.id) } == true -> LessonState.ACTIVE
                    else -> LessonState.LOCKED
                }
                LessonNode(
                    lesson = lesson,
                    state = state,
                    horizontalOffset = horizontalOffset,
                    onStart = { onStartLesson(lesson.id) },
                )
            }
        }
    }
}

/**
 * The words one unit teaches, grouped by the lesson that teaches them.
 *
 * A study surface, like the grammar drills beside it: opening it reads the corpus and
 * writes nothing. `dictionary` supplies the gloss and the clip, because the offline
 * dictionary is the one place the app states a meaning outright — the unit list itself
 * claims no translation for a word the corpus never glosses.
 */
@Composable
private fun UnitVocabularyScreen(
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

@Composable
private fun LessonNode(
    lesson: LessonEntity,
    state: LessonState,
    horizontalOffset: androidx.compose.ui.unit.Dp,
    onStart: () -> Unit,
) {
    val context = LocalContext.current
    val pulse = rememberInfiniteTransition(label = "activePulse")
    val scale by pulse.animateFloat(
        initialValue = 1f,
        targetValue = if (state == LessonState.ACTIVE) 1.08f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900),
            repeatMode = androidx.compose.animation.core.RepeatMode.Reverse,
        ),
        label = "lessonScale",
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .offset(x = horizontalOffset)
            .padding(vertical = 4.dp),
    ) {
        // Floating "START" chip for active lesson
        if (state == LessonState.ACTIVE) {
            Box(
                modifier = Modifier
                    .background(Color(0xFF2E9E6B), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 2.dp),
            ) {
                Text(
                    text = "START",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 10.sp,
                    letterSpacing = 1.sp,
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
        }

        Box(
            modifier = Modifier
                .size(74.dp)
                .scale(scale)
                .background(
                    color = when (state) {
                        LessonState.COMPLETED -> Color(0xFFE8A93C)
                        LessonState.ACTIVE -> Color(0xFF2E9E6B)
                        LessonState.LOCKED -> Color(0xFFE5E5E5)
                    },
                    shape = CircleShape,
                )
                .border(
                    width = 4.dp,
                    color = when (state) {
                        LessonState.COMPLETED -> Color(0xFFE5A800)
                        LessonState.ACTIVE -> Color(0xFF46A302)
                        LessonState.LOCKED -> Color(0xFFCCCCCC)
                    },
                    shape = CircleShape,
                )
                .clickable {
                    when (state) {
                        LessonState.COMPLETED, LessonState.ACTIVE -> onStart()
                        LessonState.LOCKED -> {
                            android.widget.Toast.makeText(
                                context,
                                "Complete previous lesson to unlock 🔒",
                                android.widget.Toast.LENGTH_SHORT,
                            ).show()
                        }
                    }
                },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = when (state) {
                    LessonState.COMPLETED -> "👑"
                    LessonState.ACTIVE -> "⭐"
                    LessonState.LOCKED -> "🔒"
                },
                fontSize = 32.sp,
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = lesson.title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (state == LessonState.ACTIVE) FontWeight.ExtraBold else FontWeight.Bold,
            color = when (state) {
                LessonState.LOCKED -> Color(0xFFAAAAAA)
                LessonState.COMPLETED -> Color(0xFF4B4B4B)
                LessonState.ACTIVE -> Color(0xFF3D7EA6)
            },
        )
        if (state == LessonState.COMPLETED) {
            Text(
                text = "Completed ✓",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF2E9E6B),
            )
        }
    }
}

// -------------------------------------------------------------------------
// Screen 2: Interactive Exercise Screen (Question, Options, Check, Feedback)
// -------------------------------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ExerciseScreen(
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
    onPlayVoice: (String, Float) -> Unit,
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
    val hasSelection = when {
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
    val playAtChosenSpeed: (String) -> Unit = { src -> onPlayVoice(src, audioSpeed) }
    val ruleText = challenge.ruleText
    val showRuleCard = ruleText != null && !ruleDismissed

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
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Spacer(modifier = Modifier.height(4.dp))

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
                            onClick = { challenge.audioSrc?.let(playAtChosenSpeed) },
                        )
                        // WI-14: the turtle's fixed 0.6x becomes a real speed
                        // stepper, so the same control also reaches SELECT,
                        // STORY and every other challenge that has audio.
                        AudioSpeedSelector(
                            selected = audioSpeed,
                            onSelect = { audioSpeed = it },
                            onPlayAt = { speed -> challenge.audioSrc?.let { onPlayVoice(it, speed) } },
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
                    onPlayAt = { speed -> onPlayVoice(src, speed) },
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

@Composable
private fun OutOfHeartsSheet(
    onRefill: () -> Unit,
    onExit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = "💔", fontSize = 72.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Out of hearts!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFC0392B),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Refill FREE — Learn offline. Own your progress.",
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFF4B4B4B),
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(28.dp))
        Button(
            onClick = onRefill,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC0392B)),
        ) {
            Text(text = "❤️ REFILL HEARTS FREE", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.height(12.dp))
        TextButton(onClick = onExit) {
            Text(
                text = "Exit lesson",
                fontWeight = FontWeight.Bold,
                color = Color(0xFFAFAFAF),
            )
        }
    }
}

// -------------------------------------------------------------------------
// Screen 3: Lesson Complete (Celebration, XP Earned, Saved Locally)
// -------------------------------------------------------------------------
@Composable
private fun LessonCompleteScreen(
    pointsGained: Int,
    perfectBonus: Int,
    onContinue: () -> Unit,
) {
    // XP count-up dopamine: 0 -> pointsGained over ~1s.
    var shownXp by remember { mutableIntStateOf(0) }
    LaunchedEffect(pointsGained) {
        val steps = 20
        repeat(steps) { i ->
            kotlinx.coroutines.delay(50)
            shownXp = (pointsGained * (i + 1)) / steps
        }
    }
    // Trophy pop-in with a bouncy spring.
    val trophyScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow,
        ),
        label = "trophy",
    )
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(112.dp)
                    .scale(trophyScale)
                    .background(Color(0xFFE8A93C), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "🏆", fontSize = 60.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Lesson Complete!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E9E6B),
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "+$shownXp XP earned and saved to your device",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE8A93C),
                textAlign = TextAlign.Center,
            )
            if (perfectBonus > 0) {
                Text(
                    text = "✨ FLAWLESS! +$perfectBonus bonus XP",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE67E22),
                    textAlign = TextAlign.Center,
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E9E6B)),
            ) {
                Text(text = "CONTINUE LEARNING", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
        // Confetti rain overlay (pure Canvas, ~2.5s, zero dependencies).
        CelebrationConfetti(modifier = Modifier.fillMaxSize())
    }
}

// -------------------------------------------------------------------------
// Checkpoint result screen — shows score, pass/fail, and missed count
// -------------------------------------------------------------------------
@Composable
private fun CheckpointScreen(
    level: String,
    correct: Int,
    total: Int,
    passed: Boolean,
    tierLabel: String?,
    onDone: () -> Unit,
) {
    val percent = if (total > 0) (correct * 100) / total else 0
    val missed = total - correct

    val trophyScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow,
        ),
        label = "checkpoint_trophy",
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(112.dp)
                    .scale(trophyScale)
                    .background(if (passed) Color(0xFF2E9E6B) else Color(0xFFE67E22), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = if (passed) "✅" else "📝", fontSize = 56.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "$level Checkpoint",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = if (passed) Color(0xFF2E9E6B) else Color(0xFFE67E22),
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "$correct / $total correct",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "$percent%",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            if (tierLabel != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (passed) "Placed into $tierLabel" else "Starting at $tierLabel",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (passed) Color(0xFF2E9E6B) else Color(0xFFE67E22),
                    textAlign = TextAlign.Center,
                )
            }

            if (missed > 0) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "$missed question${if (missed > 1) "s" else ""} added to your practice list",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onDone,
                modifier = Modifier
                    .height(56.dp)
                    .fillMaxWidth(0.7f),
            ) {
                Text(
                    // No review flow exists — the button only closes the result —
                    // so it must not promise one.
                    text = "Continue",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        CelebrationConfetti(modifier = Modifier.fillMaxSize())
    }
}

private data class ConfettiParticle(
    val x: Float,
    val y0: Float,
    val speed: Float,
    val size: Float,
    val color: Color,
    val drift: Float,
)

@Composable
private fun CelebrationConfetti(modifier: Modifier = Modifier) {
    var lifetime by remember { mutableStateOf(0) }
    val particles = remember {
        val rng = kotlin.random.Random(System.currentTimeMillis())
        val palette = listOf(
            Color(0xFF2E9E6B), Color(0xFF3D7EA6), Color(0xFFE8A93C),
            Color(0xFFC0392B), Color(0xFFCE82FF), Color(0xFFE67E22),
        )
        List(90) {
            ConfettiParticle(
                x = rng.nextFloat(),
                y0 = -rng.nextFloat() * 0.25f,
                speed = 0.35f + rng.nextFloat() * 0.45f,
                size = 8f + rng.nextFloat() * 14f,
                color = palette[rng.nextInt(palette.size)],
                drift = (rng.nextFloat() - 0.5f) * 0.2f,
            )
        }
    }
    LaunchedEffect(Unit) {
        repeat(50) {
            kotlinx.coroutines.delay(50)
            lifetime++
        }
    }
    if (lifetime < 50) {
        Canvas(modifier = modifier) {
            val t = lifetime / 50f
            particles.forEach { p ->
                val y = (p.y0 + t * p.speed) % 1.2f
                drawCircle(
                    color = p.color,
                    radius = p.size,
                    center = androidx.compose.ui.geometry.Offset(
                        x = (p.x + t * p.drift + 1f) % 1f * size.width,
                        y = y * size.height,
                    ),
                    alpha = 1f - t * t,
                )
            }
        }
    }
}

// -------------------------------------------------------------------------
// Screen: Settings (sound, haptics, romaji, reset, about)
// -------------------------------------------------------------------------
@Composable
private fun SettingsScreen(
    userProgress: UserProgressEntity?,
    onToggleSound: (Boolean) -> Unit,
    onToggleHaptics: (Boolean) -> Unit,
    onToggleRomaji: () -> Unit,
    onSelectThemeAccent: (String) -> Unit,
    onSelectThemeMode: (String) -> Unit,
    onSelectDailyQuestGoal: (Int) -> Unit,
    onResetProgress: () -> Unit,
    onExportBackup: ((String) -> Unit) -> Unit,
    onImportBackup: (String, (Boolean) -> Unit) -> Unit,
    onBack: () -> Unit,
) {
    var showResetConfirm by remember { androidx.compose.runtime.mutableStateOf(false) }
    var showImportDialog by remember { androidx.compose.runtime.mutableStateOf(false) }
    var importJsonText by remember { androidx.compose.runtime.mutableStateOf("") }
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("openlingo_settings", android.content.Context.MODE_PRIVATE) }
    var remindersEnabled by remember { androidx.compose.runtime.mutableStateOf(prefs.getBoolean("reminders_enabled", true)) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "←",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3D7EA6),
                modifier = Modifier.clickable(onClick = onBack).padding(8.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
        }

        SettingRow(
            emoji = "🔊",
            title = "Sound effects",
            subtitle = "Kokoro voice, chimes, fanfare",
            checked = userProgress?.soundEnabled ?: true,
            onCheckedChange = onToggleSound,
        )
        SettingRow(
            emoji = "📳",
            title = "Vibration",
            subtitle = "Haptic feedback on answers",
            checked = userProgress?.hapticsEnabled ?: true,
            onCheckedChange = onToggleHaptics,
        )
        SettingRow(
            emoji = "🈁",
            title = "Show romaji",
            subtitle = "Pronunciation hints under Japanese",
            checked = userProgress?.showRomaji ?: true,
            onCheckedChange = { onToggleRomaji() },
        )
        SettingRow(
            emoji = "🔔",
            title = "Daily reminder",
            subtitle = "7 PM nudge if you haven't practiced yet",
            checked = remindersEnabled,
            onCheckedChange = { enabled ->
                remindersEnabled = enabled
                prefs.edit().putBoolean("reminders_enabled", enabled).apply()
            },
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Daily goal (WI-13): the XP the learner has chosen to chase each day.
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = "🎯", fontSize = 24.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Daily goal", fontWeight = FontWeight.Bold)
                Text(
                    text = "XP per day to finish your quest",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF777777),
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            LocalProgressRepository.DAILY_QUEST_XP_OPTIONS.forEach { goal ->
                val isSelected = (userProgress?.dailyQuestGoal ?: LocalProgressRepository.DAILY_QUEST_XP) == goal
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) Color(0xFF2E9E6B).copy(alpha = 0.15f) else Color(0xFFF7F7F7))
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) Color(0xFF2E9E6B) else Color(0xFFE5E5E5),
                            shape = RoundedCornerShape(12.dp),
                        )
                        .clickable { onSelectDailyQuestGoal(goal) }
                        .padding(vertical = 10.dp, horizontal = 4.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "$goal",
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color(0xFF2E9E6B) else Color(0xFF777777),
                    )
                }
            }
        }



        // Theme Accent Selector
        Text(
            text = "Theme Accent",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4B4B4B),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            com.openlingo.app.ui.theme.ThemeAccent.entries.forEach { accent ->
                val isSelected = userProgress?.themeAccent?.equals(accent.name, ignoreCase = true) == true ||
                    (userProgress?.themeAccent == null && accent == com.openlingo.app.ui.theme.ThemeAccent.TEAL)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) accent.swatchColor.copy(alpha = 0.15f) else Color(0xFFF7F7F7))
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) accent.swatchColor else Color(0xFFE5E5E5),
                            shape = RoundedCornerShape(12.dp),
                        )
                        .clickable { onSelectThemeAccent(accent.name) }
                        .padding(vertical = 10.dp, horizontal = 4.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(accent.swatchColor, CircleShape)
                        )
                        Text(
                            text = accent.name.lowercase().replaceFirstChar { it.uppercase() },
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) accent.swatchColor else Color(0xFF777777),
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Appearance (System / Light / Dark)
        Text(
            text = "Appearance",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4B4B4B),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            listOf(
                com.openlingo.app.ui.theme.ThemeMode.SYSTEM to "📱 System",
                com.openlingo.app.ui.theme.ThemeMode.LIGHT to "🌞 Light",
                com.openlingo.app.ui.theme.ThemeMode.DARK to "🌙 Dark",
            ).forEach { (mode, label) ->
                val isSelected = com.openlingo.app.ui.theme.ThemeMode.fromName(userProgress?.themeMode) == mode
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) Color(0xFF3D7EA6).copy(alpha = 0.15f) else Color(0xFFF7F7F7))
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) Color(0xFF3D7EA6) else Color(0xFFE5E5E5),
                            shape = RoundedCornerShape(12.dp),
                        )
                        .clickable { onSelectThemeMode(mode.name) }
                        .padding(vertical = 10.dp, horizontal = 4.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = label,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color(0xFF3D7EA6) else Color(0xFF777777),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Backup and Restore
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            OutlinedButton(
                onClick = {
                    onExportBackup { json ->
                        val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(android.content.Intent.EXTRA_SUBJECT, "OpenLingo Backup")
                            putExtra(android.content.Intent.EXTRA_TEXT, json)
                        }
                        context.startActivity(android.content.Intent.createChooser(shareIntent, "Share OpenLingo Backup"))
                    }
                },
                modifier = Modifier.weight(1f).height(48.dp),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text(text = "📤 Export", fontWeight = FontWeight.Bold)
            }
            OutlinedButton(
                onClick = { showImportDialog = true },
                modifier = Modifier.weight(1f).height(48.dp),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text(text = "📥 Import", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        OutlinedButton(
            onClick = { showResetConfirm = true },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(14.dp),
        ) {
            Text(text = "🗑 Reset all progress", color = Color(0xFFC0392B), fontWeight = FontWeight.Bold)
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F7F7)),
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = "Learn offline. Own your progress.", fontWeight = FontWeight.Bold, color = Color(0xFF2E9E6B))
                Text(
                    text = "No ads, no payments, no account needed. Your progress lives only on this device — export a backup in Settings to move it to another phone.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF777777),
                )
            }
        }
    }

    if (showResetConfirm) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showResetConfirm = false },
            title = { Text(text = "Reset everything?") },
            text = { Text(text = "XP, streak, hearts, mistakes, and kana stars go back to zero. Lessons stay on the device.") },
            confirmButton = {
                TextButton(onClick = { showResetConfirm = false; onResetProgress() }) {
                    Text(text = "RESET", color = Color(0xFFC0392B), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirm = false }) { Text(text = "KEEP MY PROGRESS") }
            },
        )
    }

    if (showImportDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showImportDialog = false },
            title = { Text(text = "Import Backup") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Paste your OpenLingo backup JSON below to restore your XP, streak, and completed lessons.",
                        style = MaterialTheme.typography.bodySmall,
                    )
                    androidx.compose.material3.OutlinedTextField(
                        value = importJsonText,
                        onValueChange = { importJsonText = it },
                        placeholder = { Text("{ \"version\": 2, ... }") },
                        modifier = Modifier.fillMaxWidth().height(140.dp),
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onImportBackup(importJsonText) { success ->
                            if (success) {
                                android.widget.Toast.makeText(context, "Progress restored! 🎉", android.widget.Toast.LENGTH_SHORT).show()
                                showImportDialog = false
                                importJsonText = ""
                            } else {
                                android.widget.Toast.makeText(context, "Invalid backup JSON ❌", android.widget.Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E9E6B)),
                ) {
                    Text(text = "RESTORE", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportDialog = false }) { Text(text = "CANCEL") }
            },
        )
    }
}

@Composable
private fun SettingRow(
    emoji: String,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Text(text = emoji, fontSize = 24.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = title, fontWeight = FontWeight.Bold)
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = Color(0xFF777777))
            }
        }
        androidx.compose.material3.Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

// -------------------------------------------------------------------------
// First-run onboarding: 3 pages, shown once until dismissed.
// -------------------------------------------------------------------------
@Composable
private fun OnboardingPager(
    onDone: () -> Unit,
    onTakePlacement: () -> Unit,
) {
    var page by remember { androidx.compose.runtime.mutableStateOf(0) }
    val pages = listOf(
        Triple("🦫", "Learn offline. Own your progress.", "Spanish + Japanese lessons work 100% offline with OpenLingo. No ads, no payments, no account needed."),
        Triple("❤️", "Hearts refill free", "Mistakes cost a heart. Tap the pulsing ❤️ pill anytime for a free refill — every midnight refills to full too."),
        Triple("💾", "Yours to take anywhere", "All progress lives on your device. Export a backup file from Settings and import it on any phone — no account, no cloud."),
    )
    val (emoji, title, body) = pages[page.coerceIn(pages.indices)]
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Text(
                    text = "Skip",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3D7EA6),
                    modifier = Modifier.clickable(onClick = onDone).padding(8.dp),
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(text = emoji, fontSize = 72.sp)
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = body,
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF777777),
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.weight(1f))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                pages.indices.forEach { i ->
                    Box(
                        modifier = Modifier
                            .size(if (i == page) 10.dp else 8.dp)
                            .background(
                                if (i == page) Color(0xFF2E9E6B) else Color(0xFFE5E5E5),
                                CircleShape,
                            ),
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            if (page == pages.lastIndex) {
                Button(
                    onClick = onTakePlacement,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3D7EA6)),
                ) {
                    Text(
                        text = "🎯 TAKE PLACEMENT TEST",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onDone,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E9E6B)),
                ) {
                    Text(
                        text = "START FROM BEGINNING ✓",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                    )
                }
            } else {
                Button(
                    onClick = { page++ },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E9E6B)),
                ) {
                    Text(
                        text = "NEXT",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                    )
                }
            }
        }
    }
}

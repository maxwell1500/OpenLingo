package com.duo.app

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
import com.duo.app.ui.theme.DuoTheme
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
import com.clerk.ui.auth.AuthView
import com.duo.app.data.local.entities.ChallengeOptionEntity
import com.duo.app.data.local.entities.CourseEntity
import com.duo.app.data.local.entities.LessonEntity
import com.duo.app.data.local.entities.UnitEntity
import com.duo.app.data.local.entities.UnitWithLessons
import com.duo.app.data.local.entities.UserProgressEntity
import com.duo.app.data.repository.ChallengeWithOptions
import com.duo.app.ui.ActiveScreen
import com.duo.app.ui.CloudSyncStatus
import com.duo.app.ui.FeedbackState
import com.duo.app.ui.MainViewModel
import com.duo.app.ui.MainTab
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
            val activeScreen by viewModel.activeScreen.collectAsStateWithLifecycle()
            val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
            val cloudSyncStatus by viewModel.cloudSyncStatus.collectAsStateWithLifecycle()
            val masteredCharacters by viewModel.masteredCharacters.collectAsStateWithLifecycle()
            val mistakes by viewModel.mistakes.collectAsStateWithLifecycle()
            val completedLessonCount by viewModel.completedLessonCount.collectAsStateWithLifecycle()
            val todayXp by viewModel.todayXp.collectAsStateWithLifecycle()
            val completedLessonIds by viewModel.completedLessonIds.collectAsStateWithLifecycle()
            val showRomaji = userProgress?.showRomaji ?: true
            val isJapanese = (userProgress?.activeCourseId ?: 1) == 2

            val currentProgress by viewModel.userProgress.collectAsStateWithLifecycle()
            val themeAccent = remember(currentProgress?.themeAccent) {
                com.duo.app.ui.theme.ThemeAccent.fromName(currentProgress?.themeAccent)
            }
            DuoTheme(accent = themeAccent) {
                val context = LocalContext.current
                val onFreeRefill: () -> Unit = {
                    viewModel.refillHearts()
                    if (userProgress?.hapticsEnabled != false) com.duo.app.feedback.Haptics.tick(context)
                    android.widget.Toast.makeText(
                        context,
                        "Hearts refilled — free forever ❤️",
                        android.widget.Toast.LENGTH_SHORT,
                    ).show()
                }
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    contentWindowInsets = WindowInsets(0, 0, 0, 0),
                    topBar = {
                        if (activeScreen is ActiveScreen.LessonMap) {
                            DuoTopAppBar(
                                courses = courses,
                                userProgress = userProgress,
                                cloudSyncStatus = cloudSyncStatus,
                                onSelectCourse = viewModel::switchCourse,
                                onRefillHearts = onFreeRefill,
                                onOpenSync = viewModel::openCloudSync,
                                onOpenSettings = viewModel::openSettings,
                            )
                        }
                    },
                    bottomBar = {
                        if (activeScreen is ActiveScreen.LessonMap) {
                            DuoBottomNavigationBar(
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
                                                unitsWithLessons = unitsWithLessons,
                                                completedChallengeIds = completedChallengeIds.toSet(),
                                                completedLessonIds = completedLessonIds,
                                                onStartLesson = viewModel::startLesson,
                                                onOpenCloudSync = viewModel::openCloudSync,
                                                todayXp = todayXp,
                                                questGoal = com.duo.app.data.repository.LocalProgressRepository.DAILY_QUEST_XP,
                                                brokenStreak = userProgress?.brokenStreak ?: 0,
                                            )
                                        }
                                    }
                                    is MainTab.Characters -> {
                                        com.duo.app.ui.screens.CharactersTabScreen(
                                            onSelectCharacter = viewModel::startCharacterDrawing,
                                            mastered = masteredCharacters,
                                        )
                                    }
                                    is MainTab.Practice -> {
                                        com.duo.app.ui.screens.PracticeTabScreen(
                                            onPlayVoice = viewModel::playVoice,
                                            onStartPractice = { viewModel.startLesson(if (isJapanese) 200 else 100) },
                                            mistakes = mistakes,
                                            onPracticeMistake = viewModel::startLesson,
                                        )
                                    }
                                    is MainTab.Profile -> {
                                        val correct = completedChallengeIds.size
                                        val wrong = mistakes.size
                                        val accuracy =
                                            if (correct + wrong == 0) 100 else (correct * 100) / (correct + wrong)
                                        com.duo.app.ui.screens.ProfileTabScreen(
                                            userName = userProgress?.userName ?: "Guest",
                                            courseName = if (isJapanese) "Japanese" else "Spanish",
                                            points = userProgress?.points ?: 0,
                                            streak = userProgress?.streak ?: 1,
                                            completedLessons = completedLessonCount,
                                            kanaCount = masteredCharacters.size,
                                            accuracyPercent = accuracy,
                                            achievements = com.duo.app.ui.evaluateAchievements(
                                                points = userProgress?.points ?: 0,
                                                streak = userProgress?.streak ?: 1,
                                                kanaCount = masteredCharacters.size,
                                                completedChallenges = completedChallengeIds.size,
                                                completedLessons = completedLessonCount,
                                            ),
                                        )
                                    }
                                }
                                if (userProgress?.onboardingSeen == false) {
                                    OnboardingPager(onDone = viewModel::completeOnboarding)
                                }
                            }
                            is ActiveScreen.CharacterDrawing -> {
                                com.duo.app.ui.screens.CharacterDrawingScreen(
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
                                    showRomaji = showRomaji,
                                    onToggleRomaji = viewModel::toggleRomaji,
                                    onSelectOption = viewModel::selectOption,
                                    onSelectWordTile = viewModel::selectWordTile,
                                    onRemoveWordTile = viewModel::removeWordTile,
                                    onPlayVoice = viewModel::playVoice,
                                    onPlayVoiceSlow = { src -> viewModel.playVoice(src, 0.6f) },
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
                            is ActiveScreen.CloudAuthSheet -> {
                                CloudSyncSheet(
                                    cloudSyncStatus = cloudSyncStatus,
                                    onClose = viewModel::closeCloudSync,
                                )
                            }
                            is ActiveScreen.Settings -> {
                                SettingsScreen(
                                    userProgress = userProgress,
                                    onToggleSound = viewModel::setSoundEnabled,
                                    onToggleHaptics = viewModel::setHapticsEnabled,
                                    onToggleRomaji = { viewModel.toggleRomaji() },
                                    onSelectThemeAccent = viewModel::setThemeAccent,
                                    onResetProgress = viewModel::resetAllProgress,
                                    onExportBackup = viewModel::exportBackup,
                                    onImportBackup = viewModel::importBackup,
                                    onBack = viewModel::closeSettings,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    override fun onResume() {
        super.onResume()
        com.duo.app.widget.OpenLingoWidgetProvider.updateAll(this)
    }
}

@Composable
private fun DuoBottomNavigationBar(
    currentTab: MainTab,
    isJapanese: Boolean,
    onSelectTab: (MainTab) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 8.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E5E5)),
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
                    color = if (isLearn) Color(0xFF58CC02) else Color(0xFF777777),
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
                        color = if (isChars) Color(0xFF1CB0F6) else Color(0xFF777777),
                    )
                    Text(
                        text = "Characters",
                        fontSize = 12.sp,
                        fontWeight = if (isChars) FontWeight.Bold else FontWeight.Medium,
                        color = if (isChars) Color(0xFF1CB0F6) else Color(0xFF777777),
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
                    color = if (isPractice) Color(0xFF1CB0F6) else Color(0xFF777777),
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
                    color = if (isProfile) Color(0xFF1CB0F6) else Color(0xFF777777),
                )
            }
        }
    }
}

// -------------------------------------------------------------------------
// Top App Bar: Course Switcher, Streak, XP, Hearts, Guest/Synced Badge
// -------------------------------------------------------------------------
@Composable
private fun DuoTopAppBar(
    courses: List<CourseEntity>,
    userProgress: UserProgressEntity?,
    cloudSyncStatus: CloudSyncStatus,
    onSelectCourse: (Int) -> Unit,
    onRefillHearts: () -> Unit,
    onOpenSync: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    val activeCourseId = userProgress?.activeCourseId ?: 1
    val hearts = userProgress?.hearts ?: 5
    val points = userProgress?.points ?: 0
    val streak = userProgress?.streak ?: 1
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
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
                                color = if (isSelected) Color(0xFFE5F5FF) else Color(0xFFF7F7F7),
                                shape = RoundedCornerShape(12.dp),
                            )
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) Color(0xFF1CB0F6) else Color(0xFFE5E5E5),
                                shape = RoundedCornerShape(12.dp),
                            )
                            .clickable { onSelectCourse(course.id) }
                            .padding(horizontal = 8.dp, vertical = 5.dp),
                    ) {
                        Text(
                            text = "${course.imageSrc} ${course.title}",
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color(0xFF1899D6) else Color(0xFF4B4B4B),
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
                        color = Color(0xFFFF9600),
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
                        color = Color(0xFFFFC800),
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
                                    .background(Color(0xFF58CC02), CircleShape)
                                    .padding(horizontal = 3.dp),
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$hearts",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF4B4B),
                        fontSize = 15.sp,
                    )
                }

                // Guest / Cloud Sync Badge
                Box(
                    modifier = Modifier
                        .background(
                            color = when (cloudSyncStatus) {
                                is CloudSyncStatus.Connected -> Color(0xFFE8F5E9)
                                else -> Color(0xFFF0F0F0)
                            },
                            shape = CircleShape,
                        )
                        .clickable(onClick = onOpenSync)
                        .padding(horizontal = 6.dp, vertical = 4.dp),
                ) {
                    Text(
                        text = when (cloudSyncStatus) {
                            is CloudSyncStatus.Connected -> "✓ Synced"
                            else -> "👤 Guest"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (cloudSyncStatus) {
                            is CloudSyncStatus.Connected -> Color(0xFF2E7D32)
                            else -> Color(0xFF777777)
                        },
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
// Screen 1: Lesson Map (Units, Lesson Nodes, Guest Persistence Badge)
// -------------------------------------------------------------------------
private enum class LessonState {
    COMPLETED,
    ACTIVE,
    LOCKED,
}

@Composable
private fun LessonMapScreen(
    unitsWithLessons: List<UnitWithLessons>,
    completedChallengeIds: Set<Int>,
    completedLessonIds: Set<Int>,
    onStartLesson: (Int) -> Unit,
    onOpenCloudSync: () -> Unit,
    todayXp: Int,
    questGoal: Int,
    brokenStreak: Int,
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
        val questDone = todayXp >= questGoal
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
                        text = "$todayXp/$questGoal XP",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF58CC02),
                    )
                }
                LinearProgressIndicator(
                    progress = { (todayXp.toFloat() / questGoal).coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth().height(10.dp),
                    color = Color(0xFF58CC02),
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
                        color = Color(0xFFFF4B4B),
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
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4B4B)),
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
                allLessons = allLessons,
                completedLessonIds = completedLessonIds,
                onStartLesson = onStartLesson,
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Guest Persistence Info Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onOpenCloudSync),
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
                        text = "Guest Mode: 100% Offline",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF4B4B4B),
                    )
                    Text(
                        text = "All XP, hearts, and lessons save automatically to your phone. Tap to link with a cloud account.",
                        fontSize = 12.sp,
                        color = Color(0xFF777777),
                    )
                }
                Text(text = "→", fontSize = 18.sp, color = Color(0xFF1CB0F6))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun UnitSection(
    unitWithLessons: UnitWithLessons,
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
            colors = CardDefaults.cardColors(containerColor = Color(0xFF58CC02)),
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = unit.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = unit.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFD7FFB8),
                )
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
                    .background(Color(0xFF58CC02), RoundedCornerShape(8.dp))
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
                        LessonState.COMPLETED -> Color(0xFFFFC800)
                        LessonState.ACTIVE -> Color(0xFF58CC02)
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
                LessonState.ACTIVE -> Color(0xFF1CB0F6)
            },
        )
        if (state == LessonState.COMPLETED) {
            Text(
                text = "Completed ✓",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF58CC02),
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
    showRomaji: Boolean,
    onToggleRomaji: () -> Unit,
    onSelectOption: (Int) -> Unit,
    onSelectWordTile: (Int) -> Unit,
    onSelectPairTile: (Int) -> Unit = {},
    onRemoveWordTile: (Int) -> Unit,
    onPlayVoice: (String) -> Unit,
    onPlayVoiceSlow: ((String) -> Unit)? = null,
    onCheckAnswer: () -> Unit,
    onNextChallenge: () -> Unit,
    onExit: () -> Unit,
    hearts: Int = 5,
    onRefillHearts: () -> Unit = {},
) {
    val challenge = exercise.currentChallenge.challenge
    val options = exercise.currentChallenge.options
    val progressFraction = (exercise.challengeIndex + 1).toFloat() / exercise.totalChallenges.toFloat()
    val isWordBank = challenge.type == "WORD_BANK"
    val isListen = challenge.type == "LISTEN"
    val isMatchPairs = challenge.type == "MATCH_PAIRS"
    val hasSelection = when {
        isMatchPairs -> exercise.matchedPairIds.size >= options.size
        isWordBank -> exercise.selectedWordTileIds.isNotEmpty()
        else -> exercise.selectedOptionId != null
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
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
                        color = Color(0xFF58CC02),
                        trackColor = Color(0xFFE5E5E5),
                    )

                    // Romaji / Furigana Toggle Button (Duolingo Japanese style)
                    Surface(
                        modifier = Modifier
                            .clickable(onClick = onToggleRomaji),
                        shape = RoundedCornerShape(8.dp),
                        color = if (showRomaji) Color(0xFFE5F5FF) else Color(0xFFF5F5F5),
                        border = androidx.compose.foundation.BorderStroke(
                            width = 1.dp,
                            color = if (showRomaji) Color(0xFF1CB0F6) else Color(0xFFD5D5D5),
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
                    "SELECT" -> "Select the correct meaning"
                    "WORD_BANK" -> "Tap the matching tiles"
                    "LISTEN" -> "Tap what you hear"
                    else -> "Translate this phrase"
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
                            onClick = { challenge.audioSrc?.let(onPlayVoice) },
                        )
                        // Slow button: 0.6x speed for tricky phonemes (🐢)
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(Color(0xFFFFF6DB), CircleShape)
                                .border(2.dp, Color(0xFFFFC800), CircleShape)
                                .clickable {
                                    challenge.audioSrc?.let { src ->
                                        onPlayVoiceSlow?.invoke(src) ?: onPlayVoice(src)
                                    }
                                },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(text = "🐢", fontSize = 26.sp)
                        }
                    }
                    Text(
                        text = "Tap to listen • 🐢 slow",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF1CB0F6),
                        fontWeight = FontWeight.SemiBold,
                    )
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
                            onClick = { challenge.audioSrc.let(onPlayVoice) },
                        )
                    }
                    Text(
                        text = challenge.question,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4B4B4B),
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            // Exercise Body: WORD_BANK vs Options List
            if (isMatchPairs) {
                MatchPairsContent(
                    options = options,
                    selectedFirstId = exercise.selectedPairFirstId,
                    matchedIds = exercise.matchedPairIds,
                    showRomaji = showRomaji,
                    onSelect = onSelectPairTile,
                )
            } else if (isWordBank) {
                WordBankContent(
                    options = options,
                    selectedOptionIds = exercise.selectedWordTileIds,
                    showRomaji = showRomaji,
                    isChecked = exercise.feedback != null,
                    onSelectTile = onSelectWordTile,
                    onRemoveTile = onRemoveWordTile,
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
                            onClick = { onSelectOption(option.id) },
                            onAudioClick = { option.audioSrc?.let(onPlayVoice) },
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
                    onClick = { if (!isChecked) onSelectTile(option.id) },
                )
            }
        }
    }
}

@Composable
private fun WordTileChip(
    text: String,
    romaji: String? = null,
    showRomaji: Boolean = true,
    isPlaced: Boolean = false,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.clickable(enabled = !isPlaced, onClick = onClick),
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

@Composable
private fun AssembledWordTileChip(
    text: String,
    romaji: String? = null,
    showRomaji: Boolean = true,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFE5F5FF),
        shadowElevation = 2.dp,
        border = androidx.compose.foundation.BorderStroke(
            width = 1.5.dp,
            color = Color(0xFF1CB0F6),
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
                    color = Color(0xFF1CB0F6),
                )
            }
        }
    }
}
@Composable
private fun MatchPairsContent(
    options: List<ChallengeOptionEntity>,
    selectedFirstId: Int?,
    matchedIds: Set<Int>,
    showRomaji: Boolean,
    onSelect: (Int) -> Unit,
) {
    Text(
        text = "Tap matching pairs to clear the board",
        style = MaterialTheme.typography.bodySmall,
        color = Color(0xFF777777),
    )
    Spacer(modifier = Modifier.height(12.dp))
    val shuffled = remember(options) { options.shuffled(kotlin.random.Random(options.hashCode())) }
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
                        .clickable(enabled = !isMatched) { onSelect(opt.id) },
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
                            isSelected -> Color(0xFF1CB0F6)
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
                                    color = Color(0xFF1CB0F6),
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
            .background(Color(0xFF1CB0F6), CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = "🔊", fontSize = (size.value * 0.45).sp)
    }
}

@Composable
private fun OptionCard(
    text: String,
    romaji: String? = null,
    showRomaji: Boolean = true,
    isSelected: Boolean,
    isChecked: Boolean,
    hasAudio: Boolean,
    onClick: () -> Unit,
    onAudioClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isChecked, onClick = onClick),
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
                isSelected -> Color(0xFF1CB0F6)
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
                        color = if (isSelected) Color(0xFF1CB0F6) else Color(0xFF777777),
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
                        Text(text = "✓", fontSize = 24.sp, color = Color(0xFF58CC02), fontWeight = FontWeight.Bold)
                        Column {
                            Text(
                                text = "Nicely done!",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF58CC02),
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
                                    color = Color(0xFFFF9600),
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
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF58CC02)),
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
                        Text(text = "✕", fontSize = 24.sp, color = Color(0xFFFF4B4B), fontWeight = FontWeight.Bold)
                        Column {
                            Text(
                                text = "Correct answer:",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFF4B4B),
                                fontSize = 16.sp,
                            )
                            Text(
                                text = feedback.correctAnswer,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF4B4B4B),
                            )
                        }
                    }
                    }
                    Button(
                        onClick = onContinue,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4B4B)),
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
                            containerColor = Color(0xFF58CC02),
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
            color = Color(0xFFFF4B4B),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Refill FREE — no ads, no pay, forever.",
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
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4B4B)),
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
                    .background(Color(0xFFFFC800), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "🏆", fontSize = 60.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Lesson Complete!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF58CC02),
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "+$shownXp XP earned and saved to your device",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFC800),
                textAlign = TextAlign.Center,
            )
            if (perfectBonus > 0) {
                Text(
                    text = "✨ FLAWLESS! +$perfectBonus bonus XP",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF9600),
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
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF58CC02)),
            ) {
                Text(text = "CONTINUE LEARNING", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
        // Confetti rain overlay (pure Canvas, ~2.5s, zero dependencies).
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
            Color(0xFF58CC02), Color(0xFF1CB0F6), Color(0xFFFFC800),
            Color(0xFFFF4B4B), Color(0xFFCE82FF), Color(0xFFFF9600),
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
// Screen 4: Cloud Sync Sheet (Optional Clerk Authentication)
// -------------------------------------------------------------------------
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
                color = Color(0xFF1CB0F6),
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
            com.duo.app.ui.theme.ThemeAccent.entries.forEach { accent ->
                val isSelected = userProgress?.themeAccent?.equals(accent.name, ignoreCase = true) == true ||
                    (userProgress?.themeAccent == null && accent == com.duo.app.ui.theme.ThemeAccent.TEAL)
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
            Text(text = "🗑 Reset all progress", color = Color(0xFFFF4B4B), fontWeight = FontWeight.Bold)
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F7F7)),
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = "Free forever", fontWeight = FontWeight.Bold, color = Color(0xFF58CC02))
                Text(
                    text = "No ads, no payments, no account needed. Guest progress lives only on this device.",
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
                    Text(text = "RESET", color = Color(0xFFFF4B4B), fontWeight = FontWeight.Bold)
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
                        placeholder = { Text("{ \"version\": 1, ... }") },
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
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF58CC02)),
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
private fun OnboardingPager(onDone: () -> Unit) {
    var page by remember { androidx.compose.runtime.mutableStateOf(0) }
    val pages = listOf(
        Triple("🦫", "Learn free, forever", "Spanish + Japanese lessons work 100% offline with OpenLingo. No ads, no payments, no account needed."),
        Triple("❤️", "Hearts refill free", "Mistakes cost a heart. Tap the pulsing ❤️ pill anytime for a free refill — every midnight refills to full too."),
        Triple("☁️", "Cloud sync is optional", "Sign in to back up progress to the web app. Skip it and everything stays on your device."),
    )
    val (emoji, title, body) = pages[page.coerceIn(pages.indices)]
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White,
    ) {
        Column(
            modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Text(
                    text = "Skip",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1CB0F6),
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
                                if (i == page) Color(0xFF58CC02) else Color(0xFFE5E5E5),
                                CircleShape,
                            ),
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { if (page < pages.lastIndex) page++ else onDone() },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF58CC02)),
            ) {
                Text(
                    text = if (page < pages.lastIndex) "NEXT" else "START LEARNING ✓",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                )
            }
        }
    }
}

@Composable
private fun CloudSyncSheet(
    cloudSyncStatus: CloudSyncStatus,
    onClose: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Cloud Sync (Optional)",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4B4B4B),
            )
            TextButton(onClick = onClose) {
                Text(text = "Done", fontWeight = FontWeight.Bold, color = Color(0xFF1CB0F6))
            }
        }

        when (cloudSyncStatus) {
            is CloudSyncStatus.Connected -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "✓ Account Linked", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                        Text(text = "User ID: ${cloudSyncStatus.user.id}", fontSize = 13.sp, color = Color(0xFF1B5E20))
                        if (!cloudSyncStatus.user.email.isNullOrBlank()) {
                            Text(text = "Email: ${cloudSyncStatus.user.email}", fontSize = 13.sp, color = Color(0xFF1B5E20))
                        }
                    }
                }
            }
            is CloudSyncStatus.Connecting -> {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color(0xFF1CB0F6))
                    Text(text = "Syncing local progress to cloud account...", color = Color(0xFF777777))
                }
            }
            is CloudSyncStatus.Guest, is CloudSyncStatus.Error -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F7F7)),
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Save Progress to Cloud",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1CB0F6),
                        )
                        Text(
                            text = "You are currently practicing in 100% offline Guest Mode. All lessons, hearts, and points are saved locally in your phone's SQLite database.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF4B4B4B),
                        )
                        if (cloudSyncStatus is CloudSyncStatus.Error) {
                            Text(
                                text = "Last sync failed: ${cloudSyncStatus.message}. Your local progress is safe — sign in again to retry.",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFF4B4B),
                            )
                        }
                        Text(
                            text = "When you connect a Clerk account, all your local XP and completed lessons will be automatically synced with the Next.js web application.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF4B4B4B),
                        )
                    }
                }

                // If Clerk key is configured, show AuthView
                if (!com.duo.app.BuildConfig.CLERK_PUBLISHABLE_KEY.contains("example.clerk")) {
                    AuthView(isDismissible = true, onDismiss = onClose)
                }
            }
        }
    }
}
}

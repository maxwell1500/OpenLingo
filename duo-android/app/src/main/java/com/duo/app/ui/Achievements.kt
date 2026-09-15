package com.duo.app.ui

/**
 * Achievement rules evaluated purely from local progress totals.
 *
 * Pure function of (points, streak, kana, completions) so the thresholds
 * are unit-testable without a database or ViewModel.
 */
data class Achievement(
    val id: String,
    val emoji: String,
    val title: String,
    val description: String,
)

fun evaluateAchievements(
    points: Int,
    streak: Int,
    kanaCount: Int,
    completedChallenges: Int,
    completedLessons: Int,
): List<Pair<Achievement, Boolean>> = listOf(
    Achievement("first-steps", "🌱", "First steps", "Complete your first challenge") to
        (completedChallenges >= 1),
    Achievement("lesson-done", "📖", "Lesson up", "Finish your first full lesson") to
        (completedLessons >= 1),
    Achievement("century", "💯", "Century club", "Earn 100 total XP") to
        (points >= 100),
    Achievement("scholar", "🎓", "Scholar", "Earn 1,000 total XP") to
        (points >= 1000),
    Achievement("warming-up", "🔥", "Warming up", "Reach a 3-day streak") to
        (streak >= 3),
    Achievement("unstoppable", "⚡", "Unstoppable", "Reach a 7-day streak") to
        (streak >= 7),
    Achievement("kana-start", "🈁", "Kana explorer", "Master your first kana") to
        (kanaCount >= 1),
    Achievement("kana-ten", "⭐", "Rising star", "Master 10 kana") to
        (kanaCount >= 10),
    Achievement("kana-master", "🏆", "Syllabary master", "Master 46 kana — a full script") to
        (kanaCount >= 46),
)

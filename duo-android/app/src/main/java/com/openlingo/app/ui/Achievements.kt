package com.openlingo.app.ui

/**
 * Achievement rules evaluated purely from local progress totals.
 *
 * Pure function of (points, streak, kana, completions, course) so the
 * thresholds are unit-testable without a database or ViewModel. Kana mastery
 * is Japanese-course content: a Spanish learner cannot trace a kana, so the
 * kana shelf is offered only on the Japanese course.
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
    isJapanese: Boolean,
): List<Pair<Achievement, Boolean>> = buildList {
    add(
        Achievement("first-steps", "🌱", "First steps", "Complete your first challenge") to
            (completedChallenges >= 1)
    )
    add(
        Achievement("lesson-done", "📖", "Lesson up", "Finish your first full lesson") to
            (completedLessons >= 1)
    )
    add(
        Achievement("century", "💯", "Century club", "Earn 100 total XP") to
            (points >= 100)
    )
    add(
        Achievement("scholar", "🎓", "Scholar", "Earn 1,000 total XP") to
            (points >= 1000)
    )
    add(
        Achievement("warming-up", "🔥", "Warming up", "Reach a 3-day streak") to
            (streak >= 3)
    )
    add(
        Achievement("unstoppable", "⚡", "Unstoppable", "Reach a 7-day streak") to
            (streak >= 7)
    )
    if (isJapanese) {
        add(
            Achievement("kana-start", "🈁", "Kana explorer", "Master your first kana") to
                (kanaCount >= 1)
        )
        add(
            Achievement("kana-ten", "⭐", "Rising star", "Master 10 kana") to
                (kanaCount >= 10)
        )
        add(
            Achievement("kana-master", "🏆", "Syllabary master", "Master 46 kana — a full script") to
                (kanaCount >= 46)
        )
    }
}

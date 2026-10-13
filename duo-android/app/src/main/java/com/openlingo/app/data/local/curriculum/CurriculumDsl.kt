package com.openlingo.app.data.local.curriculum

import com.openlingo.app.data.local.entities.ChallengeEntity
import com.openlingo.app.data.local.entities.ChallengeOptionEntity
import com.openlingo.app.data.local.entities.LessonEntity
import com.openlingo.app.data.local.entities.UnitEntity
import com.openlingo.app.data.local.models.ChallengeType

/**
 * Curriculum authoring DSL.
 *
 * The curriculum data files are large, repetitive, and must stay byte-identical
 * in their generated form. This DSL removes the mechanical noise:
 *  - lessons and challenges are declared inside their unit's block, so
 *    [UnitEntity.id] and the enclosing unit's id are filled in implicitly;
 *  - options are declared inside their challenge's block, so
 *    [ChallengeOptionEntity.challengeId] is filled in implicitly;
 *  - the `Challenge*`/`Lesson*`/`Unit*` type names are dropped from the call
 *    sites — the helper functions name them.
 *
 * Every helper forwards to the real entity constructor with `lastSynced` left
 * at its default, so the seeded corpus is unchanged.
 *
 * Entity ids stay explicit on purpose: option ids follow per-file allocation
 * patterns (documented in each file's header), and re-deriving them would need a
 * Room migration for zero benefit. The contract is enforced centrally instead:
 * `CurriculumStructureTest` asserts unit/lesson/challenge/option ids are globally
 * unique across the payload files with resolving foreign keys, and
 * `LocalProgressRepositoryTest` asserts the same over the whole seeded corpus
 * (the base curriculum included). A new id block must not collide with either.
 */

/** Builds a [UnitPayload] from a unit block: lessons, challenges, options. */
fun unit(
    id: Int,
    courseId: Int,
    title: String,
    description: String,
    orderIndex: Int,
    block: UnitScope.() -> Unit,
): UnitPayload {
    val scope = UnitScope(id)
    scope.block()
    return UnitPayload(
        unit = UnitEntity(id = id, courseId = courseId, title = title, description = description, orderIndex = orderIndex),
        lessons = scope.lessons,
        challenges = scope.challenges,
        options = scope.options,
    )
}

/** Unit-level scope: collects lessons, challenges and their options. */
class UnitScope(private val unitId: Int) {
    val lessons = mutableListOf<LessonEntity>()
    val challenges = mutableListOf<ChallengeEntity>()
    val options = mutableListOf<ChallengeOptionEntity>()

    /** Adds a lesson to this unit. [unitId] is implied by the enclosing unit. */
    fun lesson(id: Int, title: String, orderIndex: Int) {
        lessons += LessonEntity(id = id, unitId = unitId, title = title, orderIndex = orderIndex)
    }

    /** Adds a challenge to this unit, with a nested option block. */
    fun challenge(
        id: Int,
        lessonId: Int,
        type: ChallengeType,
        question: String,
        orderIndex: Int,
        romaji: String? = null,
        audioSrc: String? = null,
        grammaticalFocus: String? = null,
        ruleText: String? = null,
        acceptedAnswers: String? = null,
        heldOut: Boolean = false,
        block: ChallengeScope.() -> Unit = {},
    ) {
        val scope = ChallengeScope(id)
        scope.block()
        challenges += ChallengeEntity(
            id = id,
            lessonId = lessonId,
            type = type,
            question = question,
            orderIndex = orderIndex,
            romaji = romaji,
            audioSrc = audioSrc,
            grammaticalFocus = grammaticalFocus,
            ruleText = ruleText,
            acceptedAnswers = acceptedAnswers,
            heldOut = heldOut,
        )
        options += scope.options
    }
}

/** Challenge-level scope: collects options for one challenge. */
class ChallengeScope(private val challengeId: Int) {
    val options = mutableListOf<ChallengeOptionEntity>()

    /** Adds an option to this challenge. [challengeId] is implied by the enclosing challenge. */
    fun option(
        id: Int,
        text: String,
        correct: Boolean,
        romaji: String? = null,
        errorTag: String? = null,
        audioSrc: String? = null,
    ) {
        options += ChallengeOptionEntity(
            id = id,
            challengeId = challengeId,
            text = text,
            correct = correct,
            romaji = romaji,
            audioSrc = audioSrc,
            errorTag = errorTag,
        )
    }
}

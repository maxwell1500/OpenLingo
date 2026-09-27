package com.duo.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "challenge_options")
data class ChallengeOptionEntity(
    @PrimaryKey val id: Int,
    val challengeId: Int,
    val text: String,
    val correct: Boolean,
    val romaji: String? = null,
    val imageSrc: String? = null,
    val audioSrc: String? = null,
    /**
     * Machine-readable reason this distractor is wrong, from a fixed vocabulary.
     * The hint shown to the learner is built from this, so a tag must name the
     * error the learner actually made - never a nearby-looking category.
     *
     * - WRONG_TENSE: the tense is wrong. Spanish preterite/imperfect/present swaps,
     *   and the Japanese polite past (ました / でした) against the present.
     * - WRONG_PERSON: right tense, wrong person or number (son after 'yo').
     * - WRONG_FORM: the verb uses a different form - Japanese potential vs plain,
     *   the te-form and the ています frame, a dictionary form or stem that cannot
     *   fill the slot, and the contracted います -> ます. Japanese does not inflect
     *   for tense, so an ability or construction error is never WRONG_TENSE.
     * - WRONG_COPULA: Spanish picked the wrong copula, ser where estar belongs or
     *   the other way round. That is a usage choice, not a register or a person.
     * - WRONG_CLASSIFIER: right numeral, wrong Japanese counter.
     * - WRONG_REGISTER: right grammar, wrong politeness level (です vs だ,
     *   います vs いく, a plain imperative where a polite request is due).
     * - UNRELATED: the distractor is simply a different word.
     *
     * CurriculumIntegrityTest pins this set and checks that every tag is
     * compatible with its challenge's grammaticalFocus.
     * Null means the option is untagged.
     */
    val errorTag: String? = null,
    val lastSynced: Long = System.currentTimeMillis(),
)

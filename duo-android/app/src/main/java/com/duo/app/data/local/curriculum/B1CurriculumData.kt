package com.duo.app.data.local.curriculum

import com.duo.app.data.local.entities.ChallengeEntity
import com.duo.app.data.local.entities.ChallengeOptionEntity
import com.duo.app.data.local.entities.LessonEntity
import com.duo.app.data.local.entities.UnitEntity

/**
 * B1 / N4 threshold units:
 *   Spanish (CEFR B1): Units 9-10 (Pretérito Indefinido, Imperfecto)
 *   Japanese (JLPT N4): Units 9-10 (Te-form & Requests, Potential & Ability)
 *
 * All audio assets are bundled Kokoro-82M Ogg files.
 */
object B1CurriculumData {

    // =========================================================================
    // SPANISH CEFR B1 (Units 9 - 10)
    // =========================================================================
    val spanishB1Units: List<UnitPayload> = listOf(
        // Unit 9: Past Tense — Pretérito Indefinido
        UnitPayload(
            unit = UnitEntity(
                id = 18,
                courseId = 1,
                title = "Unit 9: Past Tense — Pretérito",
                description = "Completed past actions: hablé, comí, viví, llegamos",
                orderIndex = 8,
            ),
            lessons = listOf(
                LessonEntity(id = 116, unitId = 18, title = "Lesson 16: I Did It", orderIndex = 0),
                LessonEntity(id = 117, unitId = 18, title = "Lesson 17: A Trip to Sevilla", orderIndex = 1),
            ),
            challenges = listOf(
                ChallengeEntity(id = 1082, lessonId = 116, type = "SELECT", question = "Which one means 'I spoke with him yesterday'?", audioSrc = "asset:///audio/es/hable_con_el.ogg", orderIndex = 0),
                ChallengeEntity(id = 1083, lessonId = 116, type = "WORD_BANK", question = "Assemble: 'We arrived on time'", orderIndex = 1),
                ChallengeEntity(id = 1084, lessonId = 116, type = "LISTEN", question = "Tap what you hear", audioSrc = "asset:///audio/es/llegamos_a_tiempo.ogg", orderIndex = 2),

                ChallengeEntity(id = 1085, lessonId = 117, type = "SELECT", question = "Which one means 'I bought the train ticket'?", audioSrc = "asset:///audio/es/compre_el_billete.ogg", orderIndex = 0),
                ChallengeEntity(id = 1086, lessonId = 117, type = "WORD_BANK", question = "Assemble: 'I spoke with him yesterday'", orderIndex = 1),
                ChallengeEntity(id = 1087, lessonId = 117, type = "SELECT", question = "Un viaje a Sevilla:\nViajera: \"Compré el billete de tren ayer.\"\nWhat did the traveler do?", audioSrc = "asset:///audio/es/compre_el_billete.ogg", orderIndex = 2),
            ),
            options = listOf(
                ChallengeOptionEntity(id = 10309, challengeId = 1082, text = "Hablé con él ayer", correct = true, audioSrc = "asset:///audio/es/hable_con_el.ogg"),
                ChallengeOptionEntity(id = 10310, challengeId = 1082, text = "Llegamos a tiempo", correct = false),
                ChallengeOptionEntity(id = 10311, challengeId = 1082, text = "Compré el billete", correct = false),

                ChallengeOptionEntity(id = 10312, challengeId = 1083, text = "Llegamos", correct = true),
                ChallengeOptionEntity(id = 10313, challengeId = 1083, text = "a", correct = true),
                ChallengeOptionEntity(id = 10314, challengeId = 1083, text = "tiempo", correct = true),
                ChallengeOptionEntity(id = 10315, challengeId = 1083, text = "compramos", correct = false),

                ChallengeOptionEntity(id = 10316, challengeId = 1084, text = "We arrived on time", correct = true),
                ChallengeOptionEntity(id = 10317, challengeId = 1084, text = "I spoke with him yesterday", correct = false),
                ChallengeOptionEntity(id = 10318, challengeId = 1084, text = "I bought the train ticket", correct = false),

                ChallengeOptionEntity(id = 10319, challengeId = 1085, text = "Compré el billete de tren", correct = true, audioSrc = "asset:///audio/es/compre_el_billete.ogg"),
                ChallengeOptionEntity(id = 10320, challengeId = 1085, text = "Llegamos a tiempo", correct = false),
                ChallengeOptionEntity(id = 10321, challengeId = 1085, text = "Hablé con él ayer", correct = false),

                ChallengeOptionEntity(id = 10322, challengeId = 1086, text = "Hablé", correct = true),
                ChallengeOptionEntity(id = 10323, challengeId = 1086, text = "con", correct = true),
                ChallengeOptionEntity(id = 10324, challengeId = 1086, text = "él", correct = true),
                ChallengeOptionEntity(id = 10325, challengeId = 1086, text = "ayer", correct = true),
                ChallengeOptionEntity(id = 10326, challengeId = 1086, text = "viví", correct = false),

                ChallengeOptionEntity(id = 10327, challengeId = 1087, text = "Bought the train ticket", correct = true),
                ChallengeOptionEntity(id = 10328, challengeId = 1087, text = "Arrived on time", correct = false),
                ChallengeOptionEntity(id = 10329, challengeId = 1087, text = "Spoke with him", correct = false),
            )
        ),

        // Unit 10: Past Tense — Imperfecto
        UnitPayload(
            unit = UnitEntity(
                id = 19,
                courseId = 1,
                title = "Unit 10: Past Tense — Imperfecto",
                description = "Descriptions, habits, and states in the past: era, tenía, vivía",
                orderIndex = 9,
            ),
            lessons = listOf(
                LessonEntity(id = 118, unitId = 19, title = "Lesson 18: When I Was a Child", orderIndex = 0),
                LessonEntity(id = 119, unitId = 19, title = "Lesson 19: Past Habits", orderIndex = 1),
            ),
            challenges = listOf(
                ChallengeEntity(id = 1088, lessonId = 118, type = "SELECT", question = "Which one means 'When I was a child I lived in Madrid'?", audioSrc = "asset:///audio/es/cuando_era_nino.ogg", orderIndex = 0),
                ChallengeEntity(id = 1089, lessonId = 118, type = "WORD_BANK", question = "Assemble: 'When I was a child I lived in Madrid'", orderIndex = 1),
                ChallengeEntity(id = 1090, lessonId = 118, type = "LISTEN", question = "Tap what you hear", audioSrc = "asset:///audio/es/cuando_era_nino.ogg", orderIndex = 2),

                ChallengeEntity(id = 1091, lessonId = 119, type = "SELECT", question = "Which one means 'The weather was nice every day'?", audioSrc = "asset:///audio/es/hacia_buen_tiempo.ogg", orderIndex = 0),
                ChallengeEntity(id = 1092, lessonId = 119, type = "WORD_BANK", question = "Assemble: 'The weather was nice every day'", orderIndex = 1),
                ChallengeEntity(id = 1093, lessonId = 119, type = "LISTEN", question = "Tap what you hear", audioSrc = "asset:///audio/es/hacia_buen_tiempo.ogg", orderIndex = 2),
            ),
            options = listOf(
                ChallengeOptionEntity(id = 10330, challengeId = 1088, text = "Cuando era niño vivía en Madrid", correct = true, audioSrc = "asset:///audio/es/cuando_era_nino.ogg"),
                ChallengeOptionEntity(id = 10331, challengeId = 1088, text = "Hacía buen tiempo todos los días", correct = false),
                ChallengeOptionEntity(id = 10332, challengeId = 1088, text = "Compré el billete de tren", correct = false),

                ChallengeOptionEntity(id = 10333, challengeId = 1089, text = "Cuando", correct = true),
                ChallengeOptionEntity(id = 10334, challengeId = 1089, text = "era", correct = true),
                ChallengeOptionEntity(id = 10335, challengeId = 1089, text = "niño", correct = true),
                ChallengeOptionEntity(id = 10336, challengeId = 1089, text = "vivía", correct = true),
                ChallengeOptionEntity(id = 10337, challengeId = 1089, text = "en", correct = true),
                ChallengeOptionEntity(id = 10338, challengeId = 1089, text = "Madrid", correct = true),
                ChallengeOptionEntity(id = 10339, challengeId = 1089, text = "hablaba", correct = false),

                ChallengeOptionEntity(id = 10340, challengeId = 1090, text = "When I was a child I lived in Madrid", correct = true),
                ChallengeOptionEntity(id = 10341, challengeId = 1090, text = "The weather was nice every day", correct = false),
                ChallengeOptionEntity(id = 10342, challengeId = 1090, text = "I bought the train ticket", correct = false),

                ChallengeOptionEntity(id = 10343, challengeId = 1091, text = "Hacía buen tiempo todos los días", correct = true, audioSrc = "asset:///audio/es/hacia_buen_tiempo.ogg"),
                ChallengeOptionEntity(id = 10344, challengeId = 1091, text = "Cuando era niño vivía en Madrid", correct = false),
                ChallengeOptionEntity(id = 10345, challengeId = 1091, text = "Llegamos a tiempo", correct = false),

                ChallengeOptionEntity(id = 10346, challengeId = 1092, text = "Hacía", correct = true),
                ChallengeOptionEntity(id = 10347, challengeId = 1092, text = "buen", correct = true),
                ChallengeOptionEntity(id = 10348, challengeId = 1092, text = "tiempo", correct = true),
                ChallengeOptionEntity(id = 10349, challengeId = 1092, text = "todos", correct = true),
                ChallengeOptionEntity(id = 10350, challengeId = 1092, text = "los", correct = true),
                ChallengeOptionEntity(id = 10351, challengeId = 1092, text = "días", correct = true),
                ChallengeOptionEntity(id = 10352, challengeId = 1092, text = "vivía", correct = false),

                ChallengeOptionEntity(id = 10353, challengeId = 1093, text = "The weather was nice every day", correct = true),
                ChallengeOptionEntity(id = 10354, challengeId = 1093, text = "When I was a child I lived in Madrid", correct = false),
                ChallengeOptionEntity(id = 10355, challengeId = 1093, text = "We arrived on time", correct = false),
            )
        )
    )

    // =========================================================================
    // JAPANESE JLPT N4 (Units 9 - 10)
    // =========================================================================
    val japaneseB1Units: List<UnitPayload> = listOf(
        // Unit 9: Te-form & Requests (~てください / ~ています)
        UnitPayload(
            unit = UnitEntity(
                id = 28,
                courseId = 2,
                title = "Unit 9: Te-form & Requests",
                description = "Connect verbs, describe ongoing actions, and make polite requests",
                orderIndex = 8,
            ),
            lessons = listOf(
                LessonEntity(id = 215, unitId = 28, title = "Lesson 16: Wait, Please", orderIndex = 0),
                LessonEntity(id = 216, unitId = 28, title = "Lesson 17: Asking Directions", orderIndex = 1),
            ),
            challenges = listOf(
                ChallengeEntity(id = 2081, lessonId = 215, type = "SELECT", question = "How do you say 'Wait a moment, please'?", audioSrc = "asset:///audio/ja/chotto_matte.ogg", orderIndex = 0),
                ChallengeEntity(id = 2082, lessonId = 215, type = "WORD_BANK", question = "Assemble: 'Wait a moment, please'", orderIndex = 1),
                ChallengeEntity(id = 2083, lessonId = 215, type = "LISTEN", question = "Tap what you hear", audioSrc = "asset:///audio/ja/chotto_matte.ogg", orderIndex = 2),

                ChallengeEntity(id = 2084, lessonId = 216, type = "SELECT", question = "How do you say 'Turn right, please'?", audioSrc = "asset:///audio/ja/migi_ni_magatte.ogg", orderIndex = 0),
                ChallengeEntity(id = 2085, lessonId = 216, type = "WORD_BANK", question = "Assemble: 'I'm reading a book right now'", orderIndex = 1),
                ChallengeEntity(id = 2086, lessonId = 216, type = "SELECT", question = "Which one means 'I'm reading a book right now'?", audioSrc = "asset:///audio/ja/ima_tabete_imasu.ogg", orderIndex = 2),
                ChallengeEntity(id = 2087, lessonId = 216, type = "MATCH_PAIRS", question = "Match the te-form phrases", orderIndex = 3),
            ),
            options = listOf(
                ChallengeOptionEntity(id = 20306, challengeId = 2081, text = "ちょっと待ってください", romaji = "chotto matte kudasai", correct = true, audioSrc = "asset:///audio/ja/chotto_matte.ogg"),
                ChallengeOptionEntity(id = 20307, challengeId = 2081, text = "今、本を読んでいます", romaji = "ima hon o yonde imasu", correct = false),
                ChallengeOptionEntity(id = 20308, challengeId = 2081, text = "右に曲がってください", romaji = "migi ni magatte kudasai", correct = false),

                ChallengeOptionEntity(id = 20309, challengeId = 2082, text = "ちょっと", romaji = "chotto", correct = true),
                ChallengeOptionEntity(id = 20310, challengeId = 2082, text = "まって", romaji = "matte", correct = true),
                ChallengeOptionEntity(id = 20311, challengeId = 2082, text = "ください", romaji = "kudasai", correct = true),
                ChallengeOptionEntity(id = 20312, challengeId = 2082, text = "本", romaji = "hon", correct = false),

                ChallengeOptionEntity(id = 20313, challengeId = 2083, text = "Wait a moment, please", correct = true),
                ChallengeOptionEntity(id = 20314, challengeId = 2083, text = "I'm reading a book", correct = false),
                ChallengeOptionEntity(id = 20315, challengeId = 2083, text = "Turn right, please", correct = false),

                ChallengeOptionEntity(id = 20316, challengeId = 2084, text = "右に曲がってください", romaji = "migi ni magatte kudasai", correct = true, audioSrc = "asset:///audio/ja/migi_ni_magatte.ogg"),
                ChallengeOptionEntity(id = 20317, challengeId = 2084, text = "ちょっと待ってください", romaji = "chotto matte kudasai", correct = false),
                ChallengeOptionEntity(id = 20318, challengeId = 2084, text = "まっすぐ行ってください", romaji = "massugu itte kudasai", correct = false),

                ChallengeOptionEntity(id = 20319, challengeId = 2085, text = "今", romaji = "ima", correct = true),
                ChallengeOptionEntity(id = 20320, challengeId = 2085, text = "本を", romaji = "hon o", correct = true),
                ChallengeOptionEntity(id = 20321, challengeId = 2085, text = "読んで", romaji = "yonde", correct = true),
                ChallengeOptionEntity(id = 20322, challengeId = 2085, text = "います", romaji = "imasu", correct = true),
                ChallengeOptionEntity(id = 20323, challengeId = 2085, text = "待って", romaji = "matte", correct = false),

                ChallengeOptionEntity(id = 20324, challengeId = 2086, text = "今、本を読んでいます", romaji = "ima hon o yonde imasu", correct = true, audioSrc = "asset:///audio/ja/ima_tabete_imasu.ogg"),
                ChallengeOptionEntity(id = 20325, challengeId = 2086, text = "ちょっと待ってください", romaji = "chotto matte kudasai", correct = false),
                ChallengeOptionEntity(id = 20326, challengeId = 2086, text = "少し日本語が話せます", romaji = "sukoshi nihongo ga hanasemasu", correct = false),

                // Pair 1: 待ってください <-> Wait, please
                ChallengeOptionEntity(id = 20327, challengeId = 2087, text = "待ってください", romaji = "matte kudasai", correct = true),
                ChallengeOptionEntity(id = 20328, challengeId = 2087, text = "Wait, please", correct = true),
                // Pair 2: 曲がってください <-> Turn, please
                ChallengeOptionEntity(id = 20329, challengeId = 2087, text = "曲がってください", romaji = "magatte kudasai", correct = true),
                ChallengeOptionEntity(id = 20330, challengeId = 2087, text = "Turn, please", correct = true),
                // Pair 3: 読んでいます <-> I'm reading
                ChallengeOptionEntity(id = 20331, challengeId = 2087, text = "読んでいます", romaji = "yonde imasu", correct = true),
                ChallengeOptionEntity(id = 20332, challengeId = 2087, text = "I'm reading", correct = true),
            )
        ),

        // Unit 10: Potential & Ability (~ことができる / ~れる)
        UnitPayload(
            unit = UnitEntity(
                id = 29,
                courseId = 2,
                title = "Unit 10: Potential & Ability",
                description = "Can/can't do: languages, skills (日本語が話せます, 書くことができます)",
                orderIndex = 9,
            ),
            lessons = listOf(
                LessonEntity(id = 217, unitId = 29, title = "Lesson 18: I Can Speak", orderIndex = 0),
                LessonEntity(id = 218, unitId = 29, title = "Lesson 19: I Can Write", orderIndex = 1),
            ),
            challenges = listOf(
                ChallengeEntity(id = 2088, lessonId = 217, type = "SELECT", question = "How do you say 'I can speak a little Japanese'?", audioSrc = "asset:///audio/ja/nihongo_ga_hanasemasu.ogg", orderIndex = 0),
                ChallengeEntity(id = 2089, lessonId = 217, type = "WORD_BANK", question = "Assemble: 'I can speak a little Japanese'", orderIndex = 1),
                ChallengeEntity(id = 2090, lessonId = 217, type = "LISTEN", question = "Tap what you hear", audioSrc = "asset:///audio/ja/nihongo_ga_hanasemasu.ogg", orderIndex = 2),

                ChallengeEntity(id = 2091, lessonId = 218, type = "SELECT", question = "How do you say 'I can write kanji'?", audioSrc = "asset:///audio/ja/kanji_o_kaku_koto_ga_dekimasu.ogg", orderIndex = 0),
                ChallengeEntity(id = 2092, lessonId = 218, type = "WORD_BANK", question = "Assemble: 'I can write kanji'", orderIndex = 1),
                ChallengeEntity(id = 2093, lessonId = 218, type = "MATCH_PAIRS", question = "Match the ability words", orderIndex = 2),
            ),
            options = listOf(
                ChallengeOptionEntity(id = 20333, challengeId = 2088, text = "少し日本語が話せます", romaji = "sukoshi nihongo ga hanasemasu", correct = true, audioSrc = "asset:///audio/ja/nihongo_ga_hanasemasu.ogg"),
                ChallengeOptionEntity(id = 20334, challengeId = 2088, text = "漢字を書くことができます", romaji = "kanji o kaku koto ga dekimasu", correct = false),
                ChallengeOptionEntity(id = 20335, challengeId = 2088, text = "ちょっと待ってください", romaji = "chotto matte kudasai", correct = false),

                ChallengeOptionEntity(id = 20336, challengeId = 2089, text = "少し", romaji = "sukoshi", correct = true),
                ChallengeOptionEntity(id = 20337, challengeId = 2089, text = "日本語が", romaji = "nihongo ga", correct = true),
                ChallengeOptionEntity(id = 20338, challengeId = 2089, text = "話せます", romaji = "hanasemasu", correct = true),
                ChallengeOptionEntity(id = 20339, challengeId = 2089, text = "漢字", romaji = "kanji", correct = false),

                ChallengeOptionEntity(id = 20340, challengeId = 2090, text = "I can speak a little Japanese", correct = true),
                ChallengeOptionEntity(id = 20341, challengeId = 2090, text = "I can write kanji", correct = false),
                ChallengeOptionEntity(id = 20342, challengeId = 2090, text = "I'm reading a book right now", correct = false),

                ChallengeOptionEntity(id = 20343, challengeId = 2091, text = "漢字を書くことができます", romaji = "kanji o kaku koto ga dekimasu", correct = true, audioSrc = "asset:///audio/ja/kanji_o_kaku_koto_ga_dekimasu.ogg"),
                ChallengeOptionEntity(id = 20344, challengeId = 2091, text = "少し日本語が話せます", romaji = "sukoshi nihongo ga hanasemasu", correct = false),
                ChallengeOptionEntity(id = 20345, challengeId = 2091, text = "今、本を読んでいます", romaji = "ima hon o yonde imasu", correct = false),

                ChallengeOptionEntity(id = 20346, challengeId = 2092, text = "漢字", romaji = "kanji", correct = true),
                ChallengeOptionEntity(id = 20347, challengeId = 2092, text = "を", romaji = "o", correct = true),
                ChallengeOptionEntity(id = 20348, challengeId = 2092, text = "書くことが", romaji = "kaku koto ga", correct = true),
                ChallengeOptionEntity(id = 20349, challengeId = 2092, text = "できます", romaji = "dekimasu", correct = true),
                ChallengeOptionEntity(id = 20350, challengeId = 2092, text = "話せます", romaji = "hanasemasu", correct = false),

                // Pair 1: 話せます <-> Can speak
                ChallengeOptionEntity(id = 20351, challengeId = 2093, text = "話せます", romaji = "hanasemasu", correct = true),
                ChallengeOptionEntity(id = 20352, challengeId = 2093, text = "Can speak", correct = true),
                // Pair 2: 書くことができます <-> Can write
                ChallengeOptionEntity(id = 20353, challengeId = 2093, text = "書くことができます", romaji = "kaku koto ga dekimasu", correct = true),
                ChallengeOptionEntity(id = 20354, challengeId = 2093, text = "Can write", correct = true),
                // Pair 3: 待ってください <-> Wait, please
                ChallengeOptionEntity(id = 20355, challengeId = 2093, text = "待ってください", romaji = "matte kudasai", correct = true),
                ChallengeOptionEntity(id = 20356, challengeId = 2093, text = "Wait, please", correct = true),
            )
        )
    )
}

package com.duo.app.data.local.curriculum

import com.duo.app.data.local.entities.ChallengeEntity
import com.duo.app.data.local.entities.ChallengeOptionEntity
import com.duo.app.data.local.entities.LessonEntity
import com.duo.app.data.local.entities.UnitEntity
import com.duo.app.data.local.models.ChallengeType

/**
 * Units 11-12 of the Japanese course, the N4 grammar the roadmap names and
 * `B1CurriculumData.japaneseN4Units` (units 28-29) never reached.
 *
 * Where the shipped Japanese corpus stops at N5-complete plus N4-entry, these two
 * units add the three N4 core points that were missing outright:
 *
 *   - **the past** — the た-form and the ました ending, across godan and ichidan
 *     verbs, plus the ない-form negative. Without a past there is no tense to
 *     conjugate and every earlier verb is stuck in the present.
 *   - **plain against polite** — 断定形 against 丁寧形, だ/です and た/ました. This
 *     is the defining N4 register skill: the same sentence in two registers.
 *   - **adjectives** — い-adjectives take く/い, な-adjectives take で/でした, and
 *     a learner who mixes them up is making a class error, not a tense error.
 *
 * Unit 12 then builds on all of it: ability as ます-form + ことができます (the
 * polite register carried forward from unit 11), opinion as 〜と思います (which
 * drills exactly the plain-form endings unit 11 taught), and the giving/receiving
 * pair あげる/くれる/もらう with から and に.
 *
 * Both units are everyday, transactional and high-frequency, which is the register
 * the rest of the Japanese corpus already uses. Nothing literary is introduced.
 *
 * **No audio.** Every `audioSrc` in the existing corpus points at a bundled Kokoro
 * Ogg file, and there is no clip for a sentence this file teaches. Rather than point
 * at a file that does not exist — or worse, reuse a clip whose audio is a different
 * sentence — every item here is silent, and the `LISTEN` mechanic is left out until
 * a real recording exists.
 *
 * Id layout inside this file, disjoint from every other curriculum file:
 *   - units `40`, `41`          (Spanish has taken 30-31)
 *   - lessons `400`-`405`
 *   - challenges `60000`-`60043`
 *   - options `600001`-`600184` (600157 is not used; every id below it is taken)
 *
 * The six held-out items are 60006, 60014, 60021, 60028, 60036 and 60043. They are
 * seeded like everything else and carry `heldOut = true`, so the lesson path skips
 * them and a checkpoint is the only thing that reaches them.
 */
object JapaneseN4CurriculumData {

    // =========================================================================
    // JAPANESE JLPT N4 (Units 11 - 12)
    // =========================================================================
    val japaneseN4ExtensionUnits: List<UnitPayload> = listOf(
        // Unit 11: Past Tense, Register & Adjectives
        UnitPayload(
            unit = UnitEntity(
                id = 40,
                courseId = 2,
                title = "Unit 11: Past Tense & Adjectives",
                description = "Talk about yesterday: ました, the plain 断定形 against the polite 丁寧形, ない-form, and い/な adjectives",
                orderIndex = 10,
            ),
            lessons = listOf(
                LessonEntity(id = 400, unitId = 40, title = "Lesson 20: Yesterday", orderIndex = 0),
                LessonEntity(id = 401, unitId = 40, title = "Lesson 21: Plain and Polite", orderIndex = 1),
                LessonEntity(id = 402, unitId = 40, title = "Lesson 22: How Was It?", orderIndex = 2),
            ),
            challenges = listOf(
                // --- Lesson 20: Yesterday (ました) --------------------------------
                ChallengeEntity(
                    id = 60000, lessonId = 400, type = ChallengeType.SELECT,
                    question = "How do you say 'I went to Osaka yesterday'?",
                    orderIndex = 0,
                    grammaticalFocus = "ja.past_polite",
                    ruleText = "The polite past is the ます form with た in place of す: 行く → 行きました.\n行きます is the polite present and 行った the plain past — only 行きました is polite *and* past.",
                ),
                ChallengeEntity(
                    id = 60001, lessonId = 400, type = ChallengeType.WORD_BANK,
                    question = "Assemble: 'Yesterday I read a book at home'",
                    orderIndex = 1,
                    grammaticalFocus = "ja.past_polite",
                    ruleText = "A finished action takes た on the ます form: 読む → 読みました.\n読んで is the connective て-form, and 読みません is the polite negative present — neither says the reading is over.",
                ),
                ChallengeEntity(
                    id = 60002, lessonId = 400, type = ChallengeType.CONJUGATE,
                    question = "Which polite past form of 飲む means 'I drank'?",
                    orderIndex = 2,
                    grammaticalFocus = "ja.past_polite",
                    ruleText = "A godan verb makes the polite past by changing the final す to ました: 飲む → 飲みました.\n飲みます is the polite present, 飲む the plain present, and 飲もう the volitional.",
                ),
                ChallengeEntity(
                    id = 60003, lessonId = 400, type = ChallengeType.FILL_BLANK,
                    question = "きのう、えいがを___。",
                    orderIndex = 3,
                    grammaticalFocus = "ja.past_polite",
                    acceptedAnswers = "見ました|みました",
                    ruleText = "The polite past adds た to the ます form: 見る → 見ます → 見ました.\n見ます is the present, 見て the て-form, and 見ません the polite negative — none of them says the film is already over.",
                ),
                ChallengeEntity(
                    id = 60004, lessonId = 400, type = ChallengeType.CONJUGATE,
                    question = "Which plain past form of 待つ means 'I waited'?",
                    orderIndex = 4,
                    grammaticalFocus = "ja.past_polite",
                    ruleText = "A godan verb makes the plain past by changing the final -う to った: 待つ → 待った.\n待ちます is the polite present, 待って the て-form, and 待っています the polite present progressive.",
                ),
                ChallengeEntity(
                    id = 60005, lessonId = 400, type = ChallengeType.SELECT,
                    question = "Which one means 'I wrote a letter yesterday'?",
                    orderIndex = 5,
                    grammaticalFocus = "ja.past_polite",
                    ruleText = "手紙を + 書きます is the plain present; the polite past swaps す for ました, so 書きました.\n書いて is the て-form and 書こう the volitional — neither of them is the past.",
                ),
                ChallengeEntity(
                    id = 60006, lessonId = 400, type = ChallengeType.CONJUGATE,
                    question = "Which polite past form of 帰る means 'I went home'?",
                    orderIndex = 6,
                    heldOut = true,
                    grammaticalFocus = "ja.past_polite",
                    ruleText = "帰る is an ichidan verb: drop る, keep the ます-stem, and attach ました — 帰りました.\n帰ります is the polite present, 帰った the plain past, and 帰って the て-form.",
                ),

                // --- Lesson 21: Plain and Polite (断定形 / 丁寧形) ----------------
                ChallengeEntity(
                    id = 60007, lessonId = 401, type = ChallengeType.SELECT,
                    question = "Which one is the plain, casual way to say 'I bought it'?",
                    orderIndex = 0,
                    grammaticalFocus = "ja.plain_vs_polite",
                    ruleText = "The plain form (断定形) puts た straight onto the verb: 買う → 買った.\n買いました is the polite past, 買います the polite present, and 買って the て-form — the plain form carries neither ます nor でした.",
                ),
                ChallengeEntity(
                    id = 60008, lessonId = 401, type = ChallengeType.FILL_BLANK,
                    question = "きのう、ほんを___。",
                    orderIndex = 1,
                    grammaticalFocus = "ja.plain_vs_polite",
                    acceptedAnswers = "読んだ|よんだ",
                    ruleText = "The plain past drops ます and puts た on the stem: 読む → 読んだ.\n読んでいます is the polite progressive present, 読んで the て-form, and 読みません the polite negative.",
                ),
                ChallengeEntity(
                    id = 60009, lessonId = 401, type = ChallengeType.SELECT,
                    question = "How do you say 'I don't understand'?",
                    orderIndex = 2,
                    grammaticalFocus = "ja.negative",
                    ruleText = "A godan verb makes the polite negative with ません: 分かる → わかりません.\nわからない is the plain negative, わかりませんでした the polite past negative, and わかっています the progressive.",
                ),
                ChallengeEntity(
                    id = 60010, lessonId = 401, type = ChallengeType.WORD_BANK,
                    question = "Assemble: 'I didn't drink coffee'",
                    orderIndex = 3,
                    grammaticalFocus = "ja.negative",
                    ruleText = "Polite negative is ます → ません, and its past is ませんでした: 飲みませんでした.\n飲みません is the negative present and 飲まなかった the plain negative past — only ませんでした says the drinking is already over.",
                ),
                ChallengeEntity(
                    id = 60011, lessonId = 401, type = ChallengeType.CONJUGATE,
                    question = "Which polite negative form of 行く means 'I don't go'?",
                    orderIndex = 4,
                    grammaticalFocus = "ja.negative",
                    ruleText = "行く is irregular: it keeps き and takes ません, so 行きません.\n行かなく is only a stem fragment, 行きます the polite present, and 行きたくない means 'I don't want to go' — a different verb ending entirely.",
                ),
                ChallengeEntity(
                    id = 60012, lessonId = 401, type = ChallengeType.FILL_BLANK,
                    question = "あしたは、あめが___。",
                    orderIndex = 5,
                    grammaticalFocus = "ja.negative",
                    acceptedAnswers = "降りません|ふりません",
                    ruleText = "降ります is an ichidan verb: drop る and the polite negative is 降りません.\n降らない is the plain negative, 降ります the polite present, and 降って the て-form.",
                ),
                ChallengeEntity(
                    id = 60013, lessonId = 401, type = ChallengeType.MATCH_PAIRS,
                    question = "Match the plain and polite forms",
                    orderIndex = 6,
                ),
                ChallengeEntity(
                    id = 60014, lessonId = 401, type = ChallengeType.CONJUGATE,
                    question = "Which plain negative form of 飲む means 'I don't drink'?",
                    orderIndex = 7,
                    heldOut = true,
                    grammaticalFocus = "ja.plain_vs_polite",
                    ruleText = "A godan verb's plain negative drops the final う: 飲む → 飲まない.\n飲みません is the polite negative, 飲む the plain present, and 飲ません is not a form of this verb at all.",
                ),

                // --- Lesson 22: How Was It? (adjectives) -------------------------
                ChallengeEntity(
                    id = 60015, lessonId = 402, type = ChallengeType.SELECT,
                    question = "How do you say 'It was cold yesterday'?",
                    orderIndex = 0,
                    grammaticalFocus = "ja.i_adjective",
                    ruleText = "An い-adjective makes the plain past by putting かった on the stem: 寒い → 寒かった.\n寒くない is the present negative, 寒いです the polite present, and 寒い the plain present — only 寒かった is the past.",
                ),
                ChallengeEntity(
                    id = 60016, lessonId = 402, type = ChallengeType.CONJUGATE,
                    question = "Which form of 新しい means 'it is not new'?",
                    orderIndex = 1,
                    grammaticalFocus = "ja.i_adjective",
                    ruleText = "An い-adjective makes the negative with く + ない: 新しい → 新しくない.\n新しかった is the past, 新しく the bare stem, and 新くない drops the な — the negative always ends くない.",
                ),
                ChallengeEntity(
                    id = 60017, lessonId = 402, type = ChallengeType.SELECT,
                    question = "How do you say 'The room was clean'?",
                    orderIndex = 2,
                    grammaticalFocus = "ja.na_adjective",
                    ruleText = "A な-adjective never changes its stem: きれい stays きれい and the polite past is きれいでした.\nきれいだった is the plain past, and 美しかった conjugates it like an い-adjective — the single mistake this point exists to catch.",
                ),
                ChallengeEntity(
                    id = 60018, lessonId = 402, type = ChallengeType.CONJUGATE,
                    question = "Which form of 静か means 'it was quiet'?",
                    orderIndex = 3,
                    grammaticalFocus = "ja.na_adjective",
                    ruleText = "A な-adjective keeps its stem in every form: 静か → 静かでした, never 静かかったです.\n静かな is the plain form before a noun, 静かです the polite present, and 静かに the adverb.",
                ),
                ChallengeEntity(
                    id = 60019, lessonId = 402, type = ChallengeType.FILL_BLANK,
                    question = "教室はきれい___。",
                    orderIndex = 4,
                    grammaticalFocus = "ja.na_adjective",
                    acceptedAnswers = "でした",
                    ruleText = "A な-adjective does not inflect: きれい + でした, never きれいかったです.\nかった, かったでした and くなかった are all the い-adjective route (cold, big, fun) — きれい takes です and でした and nothing else.",
                ),
                ChallengeEntity(
                    id = 60020, lessonId = 402, type = ChallengeType.SELECT,
                    question = "How do you say 'The book wasn't interesting'?",
                    orderIndex = 5,
                    grammaticalFocus = "ja.na_adjective",
                    ruleText = "A な-adjective makes the plain past negative with くなかった: おもしろい → おもしろくなかった.\nおもしろかった and おもしろくない conjugate it like an い-adjective, and おもしろな is not a form of it.",
                ),
                ChallengeEntity(
                    id = 60021, lessonId = 402, type = ChallengeType.FILL_BLANK,
                    question = "___は安かった。",
                    orderIndex = 6,
                    heldOut = true,
                    grammaticalFocus = "ja.i_adjective",
                    acceptedAnswers = "ビール|びーる",
                    ruleText = "An い-adjective makes the plain past with かった: 安い → 安かった.\nビールス is not a Japanese word and コーヒー is a different drink — the blank wants the drink that was cheap.",
                ),
            ),
            options = listOf(
                ChallengeOptionEntity(id = 600001, challengeId = 60000, text = "昨日大阪へ行きました", romaji = "kinou osaka e ikimashita", correct = true),
                ChallengeOptionEntity(id = 600002, challengeId = 60000, text = "昨日大阪へ行きます", romaji = "kinou osaka e ikimasu", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 600003, challengeId = 60000, text = "昨日大阪へ行こう", romaji = "kinou osaka e ikou", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 600004, challengeId = 60001, text = "昨日", romaji = "kinou", correct = true),
                ChallengeOptionEntity(id = 600005, challengeId = 60001, text = "家で", romaji = "ie de", correct = true),
                ChallengeOptionEntity(id = 600006, challengeId = 60001, text = "本を", romaji = "hon o", correct = true),
                ChallengeOptionEntity(id = 600007, challengeId = 60001, text = "読みました", romaji = "yomimashita", correct = true),
                ChallengeOptionEntity(id = 600008, challengeId = 60001, text = "読みます", romaji = "yomimasu", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 600009, challengeId = 60001, text = "読んで", romaji = "yonde", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 600010, challengeId = 60002, text = "飲みました", romaji = "nomimashita", correct = true),
                ChallengeOptionEntity(id = 600011, challengeId = 60002, text = "飲みます", romaji = "nomimasu", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 600012, challengeId = 60002, text = "飲む", romaji = "nomu", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 600013, challengeId = 60002, text = "飲もう", romaji = "nomou", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 600014, challengeId = 60003, text = "見ました", romaji = "mimashita", correct = true),
                ChallengeOptionEntity(id = 600015, challengeId = 60003, text = "見ます", romaji = "mimasu", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 600016, challengeId = 60003, text = "見て", romaji = "mite", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 600017, challengeId = 60003, text = "見ません", romaji = "mimasen", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 600018, challengeId = 60004, text = "待った", romaji = "matta", correct = true),
                ChallengeOptionEntity(id = 600019, challengeId = 60004, text = "待ちます", romaji = "machimasu", correct = false, errorTag = "WRONG_REGISTER"),
                ChallengeOptionEntity(id = 600020, challengeId = 60004, text = "待って", romaji = "matte", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 600021, challengeId = 60004, text = "待っています", romaji = "matte imasu", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 600022, challengeId = 60005, text = "きのう、手紙を書きました", romaji = "kinou tegami o kakimashita", correct = true),
                ChallengeOptionEntity(id = 600023, challengeId = 60005, text = "きのう、手紙を書きます", romaji = "kinou tegami o kakimasu", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 600024, challengeId = 60005, text = "きのう、手紙を書いて", romaji = "kinou tegami o kaite", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 600025, challengeId = 60005, text = "きのう、手紙を書こう", romaji = "kinou tegami o kakou", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 600026, challengeId = 60006, text = "帰りました", romaji = "kaerimashita", correct = true),
                ChallengeOptionEntity(id = 600027, challengeId = 60006, text = "帰ります", romaji = "kaerimasu", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 600028, challengeId = 60006, text = "帰った", romaji = "kaetta", correct = false, errorTag = "WRONG_REGISTER"),
                ChallengeOptionEntity(id = 600029, challengeId = 60006, text = "帰って", romaji = "kaette", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 600030, challengeId = 60007, text = "買った", romaji = "katta", correct = true),
                ChallengeOptionEntity(id = 600031, challengeId = 60007, text = "買いました", romaji = "kaimashita", correct = false, errorTag = "WRONG_REGISTER"),
                ChallengeOptionEntity(id = 600032, challengeId = 60007, text = "買います", romaji = "kaimasu", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 600033, challengeId = 60007, text = "買って", romaji = "katte", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 600034, challengeId = 60008, text = "読んだ", romaji = "yonda", correct = true),
                ChallengeOptionEntity(id = 600035, challengeId = 60008, text = "読んでいます", romaji = "yonde imasu", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 600036, challengeId = 60008, text = "読んで", romaji = "yonde", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 600037, challengeId = 60008, text = "読みません", romaji = "yomimasen", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 600038, challengeId = 60009, text = "わかりません", romaji = "wakarimasen", correct = true),
                ChallengeOptionEntity(id = 600039, challengeId = 60009, text = "わからない", romaji = "wakaranai", correct = false, errorTag = "WRONG_REGISTER"),
                ChallengeOptionEntity(id = 600040, challengeId = 60009, text = "わかっています", romaji = "wakatte imasu", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 600041, challengeId = 60009, text = "わかりませんでした", romaji = "wakarimasen deshita", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 600042, challengeId = 60010, text = "コーヒーを", romaji = "koohii o", correct = true),
                ChallengeOptionEntity(id = 600043, challengeId = 60010, text = "飲みませんでした", romaji = "nomimasen deshita", correct = true),
                ChallengeOptionEntity(id = 600044, challengeId = 60010, text = "飲みません", romaji = "nomimasen", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 600045, challengeId = 60010, text = "飲まなかった", romaji = "nomanakatta", correct = false, errorTag = "WRONG_REGISTER"),

                ChallengeOptionEntity(id = 600046, challengeId = 60011, text = "行きません", romaji = "ikimasen", correct = true),
                ChallengeOptionEntity(id = 600047, challengeId = 60011, text = "行かなく", romaji = "ikanaku", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 600048, challengeId = 60011, text = "行きます", romaji = "ikimasu", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 600049, challengeId = 60011, text = "行きたくない", romaji = "ikitakunai", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 600050, challengeId = 60012, text = "降りません", romaji = "furimasen", correct = true),
                ChallengeOptionEntity(id = 600051, challengeId = 60012, text = "降ります", romaji = "furimasu", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 600052, challengeId = 60012, text = "降らない", romaji = "furanai", correct = false, errorTag = "WRONG_REGISTER"),
                ChallengeOptionEntity(id = 600053, challengeId = 60012, text = "降って", romaji = "futte", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 600054, challengeId = 60013, text = "食べました", romaji = "tabemashita", correct = true),
                ChallengeOptionEntity(id = 600055, challengeId = 60013, text = "I ate (polite)", correct = true),
                ChallengeOptionEntity(id = 600056, challengeId = 60013, text = "食べる", romaji = "taberu", correct = true),
                ChallengeOptionEntity(id = 600057, challengeId = 60013, text = "I eat (plain)", correct = true),
                ChallengeOptionEntity(id = 600058, challengeId = 60013, text = "飲みました", romaji = "nomimashita", correct = true),
                ChallengeOptionEntity(id = 600059, challengeId = 60013, text = "I drank (polite)", correct = true),
                ChallengeOptionEntity(id = 600060, challengeId = 60013, text = "飲む", romaji = "nomu", correct = true),
                ChallengeOptionEntity(id = 600061, challengeId = 60013, text = "I drink (plain)", correct = true),

                ChallengeOptionEntity(id = 600062, challengeId = 60014, text = "飲まない", romaji = "nomanai", correct = true),
                ChallengeOptionEntity(id = 600063, challengeId = 60014, text = "飲ません", romaji = "nomesen", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 600064, challengeId = 60014, text = "飲みません", romaji = "nomimasen", correct = false, errorTag = "WRONG_REGISTER"),
                ChallengeOptionEntity(id = 600065, challengeId = 60014, text = "飲む", romaji = "nomu", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 600066, challengeId = 60015, text = "昨日は寒かった", romaji = "kinou wa samukatta", correct = true),
                ChallengeOptionEntity(id = 600067, challengeId = 60015, text = "昨日は寒くない", romaji = "kinou wa samuku nai", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 600068, challengeId = 60015, text = "昨日は寒いです", romaji = "kinou wa samu desu", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 600069, challengeId = 60015, text = "昨日は寒い", romaji = "kinou wa samui", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 600070, challengeId = 60016, text = "新しくない", romaji = "atarashiku nai", correct = true),
                ChallengeOptionEntity(id = 600071, challengeId = 60016, text = "新しかった", romaji = "atarashikatta", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 600072, challengeId = 60016, text = "新しく", romaji = "atarashiku", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 600073, challengeId = 60016, text = "新くない", romaji = "atarashiku nai", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 600074, challengeId = 60017, text = "部屋はきれいでした", romaji = "heya wa kirei deshita", correct = true),
                ChallengeOptionEntity(id = 600075, challengeId = 60017, text = "部屋はきれいだった", romaji = "heya wa kirei datta", correct = false, errorTag = "WRONG_REGISTER"),
                ChallengeOptionEntity(id = 600076, challengeId = 60017, text = "部屋は美しかった", romaji = "heya wa utsukushikatta", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 600077, challengeId = 60017, text = "部屋はきれいです", romaji = "heya wa kirei desu", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 600078, challengeId = 60018, text = "静かでした", romaji = "shizuka deshita", correct = true),
                ChallengeOptionEntity(id = 600079, challengeId = 60018, text = "静かな", romaji = "shizuka na", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 600080, challengeId = 60018, text = "静かです", romaji = "shizuka desu", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 600081, challengeId = 60018, text = "静かに", romaji = "shizuka ni", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 600082, challengeId = 60019, text = "でした", romaji = "deshita", correct = true),
                ChallengeOptionEntity(id = 600083, challengeId = 60019, text = "かった", romaji = "katta", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 600084, challengeId = 60019, text = "かったでした", romaji = "katta deshita", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 600085, challengeId = 60019, text = "くなかった", romaji = "ku nakatta", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 600086, challengeId = 60020, text = "本はおもしろくなかった", romaji = "hon wa omoshiroku nakatta", correct = true),
                ChallengeOptionEntity(id = 600087, challengeId = 60020, text = "本はおもしろかった", romaji = "hon wa omoshirokatta", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 600088, challengeId = 60020, text = "本はおもしろくない", romaji = "hon wa omoshiroku nai", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 600089, challengeId = 60020, text = "本はおもしろな", romaji = "hon wa omoshiro na", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 600090, challengeId = 60021, text = "ビール", romaji = "biiru", correct = true),
                ChallengeOptionEntity(id = 600091, challengeId = 60021, text = "ビールス", romaji = "biirusu", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 600092, challengeId = 60021, text = "コーヒー", romaji = "koohii", correct = false),
            ),
        ),

        // Unit 12: Ability, Opinion & Giving
        UnitPayload(
            unit = UnitEntity(
                id = 41,
                courseId = 2,
                title = "Unit 12: Ability, Opinion & Giving",
                description = "〜ことができます, 〜と思います, and the あげる/くれる/もらう trio with から and に",
                orderIndex = 11,
            ),
            lessons = listOf(
                LessonEntity(id = 403, unitId = 41, title = "Lesson 23: I Can Do That", orderIndex = 0),
                LessonEntity(id = 404, unitId = 41, title = "Lesson 24: I Think", orderIndex = 1),
                LessonEntity(id = 405, unitId = 41, title = "Lesson 25: Gifts and Favours", orderIndex = 2),
            ),
            challenges = listOf(
                // --- Lesson 23: I Can Do That (ことができます) ---------------------
                ChallengeEntity(
                    id = 60022, lessonId = 403, type = ChallengeType.SELECT,
                    question = "How do you say 'I can ride a bicycle'?",
                    orderIndex = 0,
                    grammaticalFocus = "ja.ability_polite",
                    ruleText = "A godan verb makes the polite ability by swapping す for せます: 乗る → 乗れます.\n乗ります is the plain present, 乗らない the negative, and 乗らなければ the conditional — only 乗れます says 'can'.",
                ),
                ChallengeEntity(
                    id = 60023, lessonId = 403, type = ChallengeType.WORD_BANK,
                    question = "Assemble: 'I can cook Italian food'",
                    orderIndex = 1,
                    grammaticalFocus = "ja.ability_polite",
                    ruleText = "The ます ability swaps the final す for せます: 作ります → 作れます.\n作れる is the dictionary potential and 作ります the plain present — neither fills the polite ます ability slot.",
                ),
                ChallengeEntity(
                    id = 60024, lessonId = 403, type = ChallengeType.CONJUGATE,
                    question = "Which form of 泳ぐ goes before ことができます?",
                    orderIndex = 2,
                    grammaticalFocus = "ja.ability_polite",
                    ruleText = "Before ことができます the verb stays polite: 泳ぐ → 泳ぎます, so 泳ぐことができます.\n泳げます is the short potential and 泳ぐ the plain present — ことができます takes the ます form.",
                ),
                ChallengeEntity(
                    id = 60025, lessonId = 403, type = ChallengeType.FILL_BLANK,
                    question = "車を___ことができます。",
                    orderIndex = 3,
                    grammaticalFocus = "ja.ability_polite",
                    acceptedAnswers = "運転|うんてん",
                    ruleText = "Before ことができます the ます form drops ます and becomes a noun: 運転します → 運転する → 運転.\n運転します is the polite verb, 運転する keeps the する and 運転した is the plain past — none of them heads the ことができます frame.",
                ),
                ChallengeEntity(
                    id = 60026, lessonId = 403, type = ChallengeType.SELECT,
                    question = "How do you say 'I can't eat spicy food'?",
                    orderIndex = 4,
                    grammaticalFocus = "ja.ability_polite",
                    ruleText = "Polite inability is the potential past negative, ます → られません: 食べます → 食べられません.\n食べません says 'I don't eat', 食べられない is the plain form, and 食べます is the plain positive.",
                ),
                ChallengeEntity(
                    id = 60027, lessonId = 403, type = ChallengeType.MATCH_PAIRS,
                    question = "Match the ability phrases",
                    orderIndex = 5,
                ),
                ChallengeEntity(
                    id = 60028, lessonId = 403, type = ChallengeType.CONJUGATE,
                    question = "Which form of 歌う means 'I can sing'?",
                    orderIndex = 6,
                    heldOut = true,
                    grammaticalFocus = "ja.ability_polite",
                    ruleText = "Ability is the potential, ます → えます: 歌う → 歌えます.\n歌います is the plain present, 歌う the dictionary form, 歌わない the plain negative, and 歌えました the polite past.",
                ),

                // --- Lesson 24: I Think (〜と思います) ---------------------------
                ChallengeEntity(
                    id = 60029, lessonId = 404, type = ChallengeType.SELECT,
                    question = "How do you say 'I think it will be sunny tomorrow'?",
                    orderIndex = 0,
                    grammaticalFocus = "ja.think",
                    ruleText = "A plain verb goes straight in front of と思います: 晴れる + と思います.\n晴れます attaches the ます ending first, 晴れた would make the opinion about today rather than tomorrow, and 晴れるのを見ます is an entirely different construction.",
                ),
                ChallengeEntity(
                    id = 60030, lessonId = 404, type = ChallengeType.CONJUGATE,
                    question = "Which form of 高い goes before と思います?",
                    orderIndex = 1,
                    grammaticalFocus = "ja.think",
                    ruleText = "Before と思います a word keeps its plain form: 高い + と思います.\n高く is the stem that only ます takes, 高かった the past, and 高くない the negative — と思います takes the bare plain form.",
                ),
                ChallengeEntity(
                    id = 60031, lessonId = 404, type = ChallengeType.FILL_BLANK,
                    question = "この本は___と思います。",
                    orderIndex = 2,
                    grammaticalFocus = "ja.think",
                    acceptedAnswers = "おもしろい",
                    ruleText = "The word before と思います is the plain form: おもしろい + と思います.\nおもしろく is the stem that only ます takes, おもしろいでした adds です, and おもしろくない is the negative.",
                ),
                ChallengeEntity(
                    id = 60032, lessonId = 404, type = ChallengeType.SELECT,
                    question = "Which one means 'I think it was cold yesterday'?",
                    orderIndex = 3,
                    grammaticalFocus = "ja.think",
                    ruleText = "The clause before と思います carries its own tense, so it is already past: 寒かったと思います.\n寒くない would be commenting on today, and 寒かったでした adds a second copula to a sentence that has none.",
                ),
                ChallengeEntity(
                    id = 60033, lessonId = 404, type = ChallengeType.WORD_BANK,
                    question = "Assemble: 'I think this shop is cheap'",
                    orderIndex = 4,
                    grammaticalFocus = "ja.think",
                    ruleText = "With です the clause stays polite all the way through: この店は安いです + と思います.\n安い would need だ instead (安いと思います), and 安くでした mixes the い-adjective stem with the な-adjective ending.",
                ),
                ChallengeEntity(
                    id = 60034, lessonId = 404, type = ChallengeType.SELECT,
                    question = "When someone asks your opinion, what does 「〜と思います」 mean?",
                    orderIndex = 5,
                    grammaticalFocus = "ja.think",
                    ruleText = "〜と思います reports a personal opinion: I think, I suppose, in my view.\n「I will」 is the future and 「Let's」 is a proposal — neither is a claim about what is true now.",
                ),
                ChallengeEntity(
                    id = 60035, lessonId = 404, type = ChallengeType.MATCH_PAIRS,
                    question = "Match the opinion phrases",
                    orderIndex = 6,
                ),
                ChallengeEntity(
                    id = 60036, lessonId = 404, type = ChallengeType.FILL_BLANK,
                    question = "きのうは___と思います。",
                    orderIndex = 7,
                    heldOut = true,
                    grammaticalFocus = "ja.think",
                    acceptedAnswers = "あつい",
                    ruleText = "The plain clause goes straight in: あつい + と思います = きのうはあついと思います.\nあつかった is the past and あついです adds です — the opinion takes the uninflected adjective.",
                ),

                // --- Lesson 25: Gifts and Favours (あげる/くれる/もらう) ----------
                ChallengeEntity(
                    id = 60037, lessonId = 405, type = ChallengeType.SELECT,
                    question = "What do you say when a friend gives you a book? (They gave it; you received it.)",
                    orderIndex = 0,
                    grammaticalFocus = "ja.giving_receiving",
                    ruleText = "くれる is 'somebody else does something for me': the giver is the subject with が, so 友達が本をくれました.\nもらいました says 'I got a gift' and あげました says 'I gave' — くれる is the one whose giver is somebody else.",
                ),
                ChallengeEntity(
                    id = 60038, lessonId = 405, type = ChallengeType.CONJUGATE,
                    question = "Which plain past form of くれる means 'they gave me'?",
                    orderIndex = 1,
                    grammaticalFocus = "ja.giving_receiving",
                    ruleText = "くれる is an ichidan verb: the plain past drops る and adds た, so くれた.\nくれて is the て-form, くれ the stem, and くれます the polite present.",
                ),
                ChallengeEntity(
                    id = 60039, lessonId = 405, type = ChallengeType.FILL_BLANK,
                    question = "___にあげました。",
                    orderIndex = 2,
                    grammaticalFocus = "ja.giving_receiving",
                    acceptedAnswers = "ともだち",
                    ruleText = "あげる is 'I give to somebody else', so the receiver takes に: ともだちにあげました.\nともだちました is 友達 + しました, a different verb — the に belongs to あげる, and the blank is what that に points at.",
                ),
                ChallengeEntity(
                    id = 60040, lessonId = 405, type = ChallengeType.SELECT,
                    question = "How do you say 'I got a present from my sister'?",
                    orderIndex = 3,
                    grammaticalFocus = "ja.giving_receiving",
                    ruleText = "もらう is 'I receive', and the source of the gift takes から: 妹からプレゼントをもらいました.\n妹に would fit あげました instead (I give *to* my sister), and 妹が is the wrong subject — あげる and もらう swap exactly those two.",
                ),
                ChallengeEntity(
                    id = 60041, lessonId = 405, type = ChallengeType.CONJUGATE,
                    question = "Which polite past form of あげる means 'I gave it to him'?",
                    orderIndex = 4,
                    grammaticalFocus = "ja.giving_receiving",
                    ruleText = "あげる makes the polite past with ました: あげる → あげました.\nあげます is the polite present, あげない the polite negative, あげる the plain present, and あげろ the plain imperative.",
                ),
                ChallengeEntity(
                    id = 60042, lessonId = 405, type = ChallengeType.FILL_BLANK,
                    question = "___をもらいました。",
                    orderIndex = 5,
                    grammaticalFocus = "ja.giving_receiving",
                    acceptedAnswers = "手紙",
                    ruleText = "もらう is 'I receive something that was given', and the thing received goes before を: 手紙をもらいました.\n手紙を would leave the sentence reading 手紙ををもらいました, and 紙 is the character on its own — the blank is the whole word.",
                ),
                ChallengeEntity(
                    id = 60043, lessonId = 405, type = ChallengeType.CONJUGATE,
                    question = "Which plain past form of もらう means 'I received it'?",
                    orderIndex = 6,
                    heldOut = true,
                    grammaticalFocus = "ja.giving_receiving",
                    ruleText = "もらう makes the plain past with った: もらう → もらった.\nもらいます is the polite present, もらう the plain present, and もらって the て-form.",
                ),
            ),
            options = listOf(
                ChallengeOptionEntity(id = 600093, challengeId = 60022, text = "自転車に乗れます", romaji = "jitensha ni noremasu", correct = true),
                ChallengeOptionEntity(id = 600094, challengeId = 60022, text = "自転車に乗ります", romaji = "jitensha ni norimasu", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 600095, challengeId = 60022, text = "自転車に乗らない", romaji = "jitensha ni noranai", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 600096, challengeId = 60022, text = "自転車に乗らなければ", romaji = "jitensha ni noranakereba", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 600097, challengeId = 60023, text = "イタリア料理を", romaji = "itaria ryori o", correct = true),
                ChallengeOptionEntity(id = 600098, challengeId = 60023, text = "作れます", romaji = "tsukuremasu", correct = true),
                ChallengeOptionEntity(id = 600099, challengeId = 60023, text = "作ります", romaji = "tsukurimasu", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 600100, challengeId = 60023, text = "作れる", romaji = "tsukureru", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 600101, challengeId = 60024, text = "泳ぎます", romaji = "oyogimasu", correct = true),
                ChallengeOptionEntity(id = 600102, challengeId = 60024, text = "泳げます", romaji = "oyogemasu", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 600103, challengeId = 60024, text = "泳ぐ", romaji = "oyogu", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 600104, challengeId = 60024, text = "泳ぎませんでした", romaji = "oyogimasen deshita", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 600105, challengeId = 60025, text = "運転", romaji = "unten", correct = true),
                ChallengeOptionEntity(id = 600106, challengeId = 60025, text = "運転します", romaji = "unten shimasu", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 600107, challengeId = 60025, text = "運転する", romaji = "unten suru", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 600108, challengeId = 60025, text = "運転した", romaji = "unten shita", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 600109, challengeId = 60026, text = "辛いものは食べられません", romaji = "karai mono wa taberaremasen", correct = true),
                ChallengeOptionEntity(id = 600110, challengeId = 60026, text = "辛いものは食べません", romaji = "karai mono wa tabimasen", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 600111, challengeId = 60026, text = "辛いものは食べられない", romaji = "karai mono wa taberarenai", correct = false, errorTag = "WRONG_REGISTER"),
                ChallengeOptionEntity(id = 600112, challengeId = 60026, text = "辛いものは食べます", romaji = "karai mono wa tabemasu", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 600113, challengeId = 60027, text = "日本語が話せます", romaji = "nihongo ga hanasemasu", correct = true),
                ChallengeOptionEntity(id = 600114, challengeId = 60027, text = "I can speak Japanese", correct = true),
                ChallengeOptionEntity(id = 600115, challengeId = 60027, text = "自転車に乗れます", romaji = "jitensha ni noremasu", correct = true),
                ChallengeOptionEntity(id = 600116, challengeId = 60027, text = "I can ride a bicycle", correct = true),
                ChallengeOptionEntity(id = 600117, challengeId = 60027, text = "運転することができます", romaji = "unten suru koto ga dekimasu", correct = true),
                ChallengeOptionEntity(id = 600118, challengeId = 60027, text = "I can drive", correct = true),

                ChallengeOptionEntity(id = 600119, challengeId = 60028, text = "歌えます", romaji = "utaemasu", correct = true),
                ChallengeOptionEntity(id = 600120, challengeId = 60028, text = "歌います", romaji = "utaimasu", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 600121, challengeId = 60028, text = "歌う", romaji = "utau", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 600122, challengeId = 60028, text = "歌えました", romaji = "utaemashita", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 600123, challengeId = 60028, text = "歌わない", romaji = "utawanai", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 600124, challengeId = 60029, text = "明日は晴れると思います", romaji = "ashita wa hareru to omoimasu", correct = true),
                ChallengeOptionEntity(id = 600125, challengeId = 60029, text = "明日は晴れますと思います", romaji = "ashita wa haremasu to omoimasu", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 600126, challengeId = 60029, text = "明日は晴れたと思います", romaji = "ashita wa hareta to omoimasu", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 600127, challengeId = 60029, text = "明日は晴れるのを見ます", romaji = "ashita wa hareru no o mimasu", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 600128, challengeId = 60030, text = "高い", romaji = "takai", correct = true),
                ChallengeOptionEntity(id = 600129, challengeId = 60030, text = "高く", romaji = "taku", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 600130, challengeId = 60030, text = "高かった", romaji = "takatta", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 600131, challengeId = 60030, text = "高くない", romaji = "taku nai", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 600132, challengeId = 60031, text = "おもしろい", romaji = "omoshiroi", correct = true),
                ChallengeOptionEntity(id = 600133, challengeId = 60031, text = "おもしろく", romaji = "omoshiroku", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 600134, challengeId = 60031, text = "おもしろいでした", romaji = "omoshiroi deshita", correct = false, errorTag = "WRONG_REGISTER"),
                ChallengeOptionEntity(id = 600135, challengeId = 60031, text = "おもしろくない", romaji = "omoshiroku nai", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 600136, challengeId = 60032, text = "昨日は寒かったと思います", romaji = "kinou wa samukatta to omoimasu", correct = true),
                ChallengeOptionEntity(id = 600137, challengeId = 60032, text = "昨日は寒くないと思います", romaji = "kinou wa samuku nai to omoimasu", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 600138, challengeId = 60032, text = "昨日は寒かったでしたと思います", romaji = "kinou wa samukatta deshita to omoimasu", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 600139, challengeId = 60032, text = "昨日は寒かったを見ます", romaji = "kinou wa samukatta o mimasu", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 600140, challengeId = 60033, text = "この店は", romaji = "kono mise wa", correct = true),
                ChallengeOptionEntity(id = 600141, challengeId = 60033, text = "安いです", romaji = "yasui desu", correct = true),
                ChallengeOptionEntity(id = 600142, challengeId = 60033, text = "安い", romaji = "yasui", correct = false, errorTag = "WRONG_REGISTER"),
                ChallengeOptionEntity(id = 600143, challengeId = 60033, text = "安くでした", romaji = "yaku deshita", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 600144, challengeId = 60034, text = "I think / I suppose", correct = true),
                ChallengeOptionEntity(id = 600145, challengeId = 60034, text = "I must", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 600146, challengeId = 60034, text = "I will", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 600147, challengeId = 60034, text = "Let's", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 600148, challengeId = 60035, text = "日本語はむずかしいと思います", romaji = "nihongo wa muzukashii to omoimasu", correct = true),
                ChallengeOptionEntity(id = 600149, challengeId = 60035, text = "I think Japanese is difficult", correct = true),
                ChallengeOptionEntity(id = 600150, challengeId = 60035, text = "この店の寿司はおいしいと思います", romaji = "kono mise no sushi wa oishii to omoimasu", correct = true),
                ChallengeOptionEntity(id = 600151, challengeId = 60035, text = "I think the sushi here is delicious", correct = true),
                ChallengeOptionEntity(id = 600152, challengeId = 60035, text = "あの店は高いと思います", romaji = "ano mise wa takai to omoimasu", correct = true),
                ChallengeOptionEntity(id = 600153, challengeId = 60035, text = "I think that shop is expensive", correct = true),

                ChallengeOptionEntity(id = 600154, challengeId = 60036, text = "あつい", romaji = "atsui", correct = true),
                ChallengeOptionEntity(id = 600155, challengeId = 60036, text = "あつかった", romaji = "atsukatta", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 600156, challengeId = 60036, text = "あついです", romaji = "atsui desu", correct = false, errorTag = "WRONG_REGISTER"),

                ChallengeOptionEntity(id = 600158, challengeId = 60037, text = "友達が本をくれました", romaji = "tomodachi ga hon o kuremashita", correct = true),
                ChallengeOptionEntity(id = 600159, challengeId = 60037, text = "友達が本をあげました", romaji = "tomodachi ga hon o agemashita", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 600160, challengeId = 60037, text = "友達が本をもらいました", romaji = "tomodachi ga hon o moraimashita", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 600161, challengeId = 60037, text = "友達が本をあげます", romaji = "tomodachi ga hon o agemasu", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 600162, challengeId = 60038, text = "くれた", romaji = "kureta", correct = true),
                ChallengeOptionEntity(id = 600163, challengeId = 60038, text = "くれます", romaji = "kuremasu", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 600164, challengeId = 60038, text = "くれる", romaji = "kureru", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 600165, challengeId = 60038, text = "くれて", romaji = "kurete", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 600166, challengeId = 60039, text = "ともだち", romaji = "tomodachi", correct = true),
                ChallengeOptionEntity(id = 600167, challengeId = 60039, text = "ともだちました", romaji = "tomodachi mashita", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 600168, challengeId = 60039, text = "友", romaji = "tomo", correct = false),

                ChallengeOptionEntity(id = 600169, challengeId = 60040, text = "妹からプレゼントをもらいました", romaji = "imouto kara purēzento o moraimashita", correct = true),
                ChallengeOptionEntity(id = 600170, challengeId = 60040, text = "妹からプレゼントをあげました", romaji = "imouto kara purēzento o agemashita", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 600171, challengeId = 60040, text = "妹にプレゼントをもらいました", romaji = "imouto ni purēzento o moraimashita", correct = false),
                ChallengeOptionEntity(id = 600172, challengeId = 60040, text = "妹がプレゼントをもらいました", romaji = "imouto ga purēzento o moraimashita", correct = false),

                ChallengeOptionEntity(id = 600173, challengeId = 60041, text = "あげました", romaji = "agemashita", correct = true),
                ChallengeOptionEntity(id = 600174, challengeId = 60041, text = "あげます", romaji = "agemasu", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 600175, challengeId = 60041, text = "あげる", romaji = "ageru", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 600176, challengeId = 60041, text = "あげない", romaji = "agenai", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 600177, challengeId = 60041, text = "あげろ", romaji = "agero", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 600178, challengeId = 60042, text = "手紙", romaji = "tegami", correct = true),
                ChallengeOptionEntity(id = 600179, challengeId = 60042, text = "手紙を", romaji = "tegami o", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 600180, challengeId = 60042, text = "紙", romaji = "kami", correct = false),

                ChallengeOptionEntity(id = 600181, challengeId = 60043, text = "もらった", romaji = "moratta", correct = true),
                ChallengeOptionEntity(id = 600182, challengeId = 60043, text = "もらいます", romaji = "moraimasu", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 600183, challengeId = 60043, text = "もらう", romaji = "morau", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 600184, challengeId = 60043, text = "もらって", romaji = "moratte", correct = false, errorTag = "WRONG_FORM"),
            ),
        ),
    )
}

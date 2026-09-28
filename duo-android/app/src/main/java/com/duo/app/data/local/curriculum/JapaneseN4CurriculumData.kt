package com.duo.app.data.local.curriculum

import com.duo.app.data.local.entities.ChallengeEntity
import com.duo.app.data.local.entities.ChallengeOptionEntity
import com.duo.app.data.local.entities.LessonEntity
import com.duo.app.data.local.entities.UnitEntity
import com.duo.app.data.local.models.ChallengeType

/**
 * Units 11-14 of the Japanese course, the N4 grammar the roadmap names and
 * `B1CurriculumData.japaneseN4Units` (units 28-29) never reached.
 *
 * Where the shipped Japanese corpus stops at N5-complete plus N4-entry, these four
 * units add the N4 core points that were missing outright:
 *
 *   - **the past** — the た-form and the ました ending, across godan and ichidan
 *     verbs, plus the ない-form negative. Without a past there is no tense to
 *     conjugate and every earlier verb is stuck in the present.
 *   - **plain against polite** — 断定形 against 丁寧形, だ/です and た/ました. This
 *     is the defining N4 register skill: the same sentence in two registers.
 *   - **adjectives** — い-adjectives take く/い, な-adjectives take で/でした, and
 *     a learner who mixes them up is making a class error, not a tense error.
 *
 * Unit 12 then builds on all of it: ability as the potential (乗れます) and as
 * the plain verb + ことができます, opinion as 〜と思います (which drills
 * exactly the plain-form endings unit 11 taught), and the giving/receiving
 * pair あげる/くれる/もらう with から and に.

 * **One rule, two routes.** ことができます is 「〜こと が できます」 and こと
 * is the する→す nominaliser, so the verb in front of it takes its *plain*
 * form — 書く ことが できます, 運転する ことが できます — and a ます form can
 * never head it, because 泳ぎますこと is not a word. The other route is the
 * potential on its own: 乗れます, 食べられません, 歌えます. Unit 10 already
 * drilled the こと route under `ja.potential_nominal`; lesson 403 teaches both
 * under `ja.ability_polite`, which is why every ability rule text here names
 * the potential as a separate way of saying 'can'.
 *
 * Both units are everyday, transactional and high-frequency, which is the register
 * the rest of the Japanese corpus already uses. Nothing literary is introduced.
 *
 * **Audio.** 89 distinct clips are wired from these units: 74 on a challenge and 57 on
 * the option that speaks the same text (forty-two clips do both, so the two counts are
 * not disjoint). Units 11-12 alone account for 46 of them, and units 13-14 for the other
 * 43. A SELECT challenge and its correct option share a clip; a CONJUGATE speaks the target
 * form on the challenge alone, so the option grid never plays the answer. A
 * MATCH_PAIRS challenge gets no challenge clip — its prompt is the instruction
 * "Match the ...", not a sentence — so its clips hang off the paired options
 * instead.
 *
 * Challenge 60025 is the one item whose scaffold names the whole target
 * sentence, so it carries the sentence clip rather than a form clip — the same
 * rule a SELECT challenge follows, for the same reason.
 *
 * **`LISTEN`, and the three clips it needed.** `LISTEN` used to be absent here
 * because no audio existed; the clips are there now, so each lesson closes with
 * a listening round. A `LISTEN` challenge carries the clip and its options carry
 * none — `ExerciseScreen` plays `challenge.audioSrc` from the big speaker and
 * withholds it from the generic audio button, so an option-level `audioSrc` on
 * the answer would leak it. For the same reason no `WRONG_*` distractor anywhere
 * in this file carries a clip: audio that plays a form the item calls an error
 * teaches the wrong thing out loud.
 *
 * The distractors are the ones Japanese makes cheap to author and hard to
 * survive: a partner-particle swap (が/を), a plain form against its polite
 * partner (行った/行きました), a class crossover (きれいくでした), a stem where
 * an ending belongs (むずかしくと思います). Japanese is where a listening
 * exercise earns its keep, because the marks that separate the options are
 * audible: the ゛ of 雑誌, the ゜ of が, the っ of 学校, the ー of ビール, the
 * ゃ of 自転車. Every one of those was confirmed in the IPA the acoustic model
 * is handed, not assumed from the script.
 *
 * Two readings were deliberately left out. こうかい is 観光 "sightseeing" and
 * 高 "expensive"; both synthesise to `koːkai`, so a LISTEN item on the pair
 * would be unanswerable by ear and is a writing exercise, not a listening one.
 *
 * Id layout inside this file, disjoint from every other curriculum file:
 *   - units `40`-`43`        (Spanish has taken 30-33)
 *   - lessons `400`-`405` for units 40-41, `406`-`412` for units 42-43
 *   - challenges `60000`-`60043` plus the `LISTEN` block `61000`-`61008` (units 40-41),
 *     then `62000`-`62040` plus the `LISTEN` block `61100`-`61106` (units 42-43)
 *   - options `600001`-`600184` (600157 is not used; every id below it is taken)
 *     plus the `LISTEN` block `6100001`-`6100027`, then options `6300001`-`6300803`
 *     plus the `LISTEN` block `6310001`-`6310063`
 *
 * The six held-out items are 60006, 60014, 60021, 60028, 60036 and 60043, all in
 * units 40-41. Units 42-43 hold nothing out: the N4 pool is fixed at 10 and a held-out
 * item in unit 42 would be selected by no checkpoint. They are
 * seeded like everything else and carry `heldOut = true`, so the lesson path skips
 * them and a checkpoint is the only thing that reaches them. No `LISTEN` item is
 * held out: the held-out pool exists to withhold a *taught form*, and a
 * listening round is the one mechanic a checkpoint cannot substitute for.
 */
object JapaneseN4CurriculumData {

    // =========================================================================
    // JAPANESE JLPT N4 (Units 11 - 14)
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
                    audioSrc = "asset:///audio/ja/kinou_osaka_e_ikimashita.ogg",
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
                    audioSrc = "asset:///audio/ja/nomimashita.ogg",
                    orderIndex = 2,
                    grammaticalFocus = "ja.past_polite",
                    ruleText = "A godan verb makes the polite past by changing the final す to ました: 飲む → 飲みました.\n飲みます is the polite present, 飲む the plain present, and 飲もう the volitional.",
                ),
                ChallengeEntity(
                    id = 60003, lessonId = 400, type = ChallengeType.FILL_BLANK,
                    question = "きのう、えいがを___。",
                    audioSrc = "asset:///audio/ja/kinou_eiga_o_mimashita.ogg",
                    orderIndex = 3,
                    grammaticalFocus = "ja.past_polite",
                    acceptedAnswers = "見ました|みました",
                    ruleText = "The polite past adds た to the ます form: 見る → 見ます → 見ました.\n見ます is the present, 見て the て-form, and 見ません the polite negative — none of them says the film is already over.",
                ),
                ChallengeEntity(
                    id = 60004, lessonId = 400, type = ChallengeType.CONJUGATE,
                    question = "Which plain past form of 待つ means 'I waited'?",
                    audioSrc = "asset:///audio/ja/matta.ogg",
                    orderIndex = 4,
                    grammaticalFocus = "ja.past_polite",
                    ruleText = "A godan verb makes the plain past by changing the final -う to った: 待つ → 待った.\n待ちます is the polite present, 待って the て-form, and 待っています the polite present progressive.",
                ),
                ChallengeEntity(
                    id = 60005, lessonId = 400, type = ChallengeType.SELECT,
                    question = "Which one means 'I wrote a letter yesterday'?",
                    audioSrc = "asset:///audio/ja/kinou_tegami_o_kakimashita.ogg",
                    orderIndex = 5,
                    grammaticalFocus = "ja.past_polite",
                    ruleText = "手紙を + 書きます is the plain present; the polite past swaps す for ました, so 書きました.\n書いて is the て-form and 書こう the volitional — neither of them is the past.",
                ),
                ChallengeEntity(
                    id = 60006, lessonId = 400, type = ChallengeType.CONJUGATE,
                    question = "Which polite past form of 帰る means 'I went home'?",
                    audioSrc = "asset:///audio/ja/kaerimashita.ogg",
                    orderIndex = 6,
                    heldOut = true,
                    grammaticalFocus = "ja.past_polite",
                    ruleText = "帰る is an ichidan verb: drop る, keep the ます-stem, and attach ました — 帰りました.\n帰ります is the polite present, 帰った the plain past, and 帰って the て-form.",
                ),
                ChallengeEntity(
                    id = 61000, lessonId = 400, type = ChallengeType.LISTEN,
                    question = "Tap what you hear",
                    audioSrc = "asset:///audio/ja/kinou_zasshi_o_kaimashita.ogg",
                    orderIndex = 7,
                    grammaticalFocus = "ja.past_polite",
                    ruleText = "The polite past adds ました to the ます form: 買う → 買います → 買いました.\nきのう、雑誌を買います is the polite present, so nothing in it says the buying is over, and きのう、雑誌を買おう is the volitional — 'I suppose I'll buy it' — a different ending on the same stem.",
                ),

                // --- Lesson 21: Plain and Polite (断定形 / 丁寧形) ----------------
                ChallengeEntity(
                    id = 60007, lessonId = 401, type = ChallengeType.SELECT,
                    question = "Which one is the plain, casual way to say 'I bought it'?",
                    audioSrc = "asset:///audio/ja/katta.ogg",
                    orderIndex = 0,
                    grammaticalFocus = "ja.plain_vs_polite",
                    ruleText = "The plain form (断定形) puts た straight onto the verb: 買う → 買った.\n買いました is the polite past, 買います the polite present, and 買って the て-form — the plain form carries neither ます nor でした.",
                ),
                ChallengeEntity(
                    id = 60008, lessonId = 401, type = ChallengeType.FILL_BLANK,
                    question = "きのう、ほんを___。",
                    audioSrc = "asset:///audio/ja/kinou_hon_o_yonda.ogg",
                    orderIndex = 1,
                    grammaticalFocus = "ja.plain_vs_polite",
                    acceptedAnswers = "読んだ|よんだ",
                    ruleText = "The plain past drops ます and puts た on the stem: 読む → 読んだ.\n読んでいます is the polite progressive present, 読んで the て-form, and 読みません the polite negative.",
                ),
                ChallengeEntity(
                    id = 60009, lessonId = 401, type = ChallengeType.SELECT,
                    question = "How do you say 'I don't understand'?",
                    audioSrc = "asset:///audio/ja/wakarimasen.ogg",
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
                    audioSrc = "asset:///audio/ja/ikimasen.ogg",
                    orderIndex = 4,
                    grammaticalFocus = "ja.negative",
                    ruleText = "行く is irregular: it keeps き and takes ません, so 行きません.\n行かなく is only a stem fragment, 行きます the polite present, and 行きたくない means 'I don't want to go' — a different verb ending entirely.",
                ),
                ChallengeEntity(
                    id = 60012, lessonId = 401, type = ChallengeType.FILL_BLANK,
                    question = "あしたは、あめが___。",
                    audioSrc = "asset:///audio/ja/ashita_ame_ga_furimasen.ogg",
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
                    audioSrc = "asset:///audio/ja/nomanai.ogg",
                    orderIndex = 7,
                    heldOut = true,
                    grammaticalFocus = "ja.plain_vs_polite",
                    ruleText = "A godan verb's plain negative drops the final う: 飲む → 飲まない.\n飲みません is the polite negative, 飲む the plain present, and 飲ません is not a form of this verb at all.",
                ),
                ChallengeEntity(
                    id = 61001, lessonId = 401, type = ChallengeType.LISTEN,
                    question = "Tap what you hear",
                    audioSrc = "asset:///audio/ja/kyou_wa_ame_ga_furimasen.ogg",
                    orderIndex = 8,
                    grammaticalFocus = "ja.negative",
                    ruleText = "A polite negative verb ends in ません: 降ります → 降りません, and が marks the rain as the thing that does not fall.\nきょうは、雨を降りません puts を on an intransitive verb — を marks an object, and 雨 is what falls — while きょうは、雨がふっていません builds the progressive ふって + いません, a て-form where this slot wants the plain negative ending.",
                ),
                ChallengeEntity(
                    id = 61002, lessonId = 401, type = ChallengeType.LISTEN,
                    question = "Tap what you hear",
                    audioSrc = "asset:///audio/ja/ashita_gakko_ni_ikimasu.ogg",
                    orderIndex = 9,
                    grammaticalFocus = "ja.plain_vs_polite",
                    ruleText = "The polite present (丁寧形) is the ます form as it already stands: 学校に行きます.\nあした、学校に行きました is the polite past, and あした、学校に行った is the plain past — the 断定形 drops ます altogether, so neither ending is polite *and* present at once.",
                ),

                // --- Lesson 22: How Was It? (adjectives) -------------------------
                ChallengeEntity(
                    id = 60015, lessonId = 402, type = ChallengeType.SELECT,
                    question = "How do you say 'It was cold yesterday'?",
                    audioSrc = "asset:///audio/ja/kinou_wa_samukatta.ogg",
                    orderIndex = 0,
                    grammaticalFocus = "ja.i_adjective",
                    ruleText = "An い-adjective makes the plain past by putting かった on the stem: 寒い → 寒かった.\n寒くない is the present negative, 寒いです the polite present, and 寒い the plain present — only 寒かった is the past.",
                ),
                ChallengeEntity(
                    id = 60016, lessonId = 402, type = ChallengeType.CONJUGATE,
                    question = "Which form of 新しい means 'it is not new'?",
                    audioSrc = "asset:///audio/ja/atarashiku_nai.ogg",
                    orderIndex = 1,
                    grammaticalFocus = "ja.i_adjective",
                    ruleText = "An い-adjective makes the negative with く + ない: 新しい → 新しくない.\n新しかった is the past, 新しく the bare stem, and 新くない drops the な — the negative always ends くない.",
                ),
                ChallengeEntity(
                    id = 60017, lessonId = 402, type = ChallengeType.SELECT,
                    question = "How do you say 'The room was clean'?",
                    audioSrc = "asset:///audio/ja/heya_wa_kirei_deshita.ogg",
                    orderIndex = 2,
                    grammaticalFocus = "ja.na_adjective",
                    ruleText = "A な-adjective never changes its stem: きれい stays きれい and the polite past is きれいでした.\nきれいだった is the plain past, and 美しかった conjugates it like an い-adjective — the single mistake this point exists to catch.",
                ),
                ChallengeEntity(
                    id = 60018, lessonId = 402, type = ChallengeType.CONJUGATE,
                    question = "Which form of 静か means 'it was quiet'?",
                    audioSrc = "asset:///audio/ja/shizuka_deshita.ogg",
                    orderIndex = 3,
                    grammaticalFocus = "ja.na_adjective",
                    ruleText = "A な-adjective keeps its stem in every form: 静か → 静かでした, never 静かかったです.\n静かな is the plain form before a noun, 静かです the polite present, and 静かに the adverb.",
                ),
                ChallengeEntity(
                    id = 60019, lessonId = 402, type = ChallengeType.FILL_BLANK,
                    question = "教室はきれい___。",
                    audioSrc = "asset:///audio/ja/kyoushitsu_wa_kirei_deshita.ogg",
                    orderIndex = 4,
                    grammaticalFocus = "ja.na_adjective",
                    acceptedAnswers = "でした",
                    ruleText = "A な-adjective does not inflect: きれい + でした, never きれいかったです.\nかった, かったでした and くなかった are all the い-adjective route (cold, big, fun) — きれい takes です and でした and nothing else.",
                ),
                ChallengeEntity(
                    id = 60020, lessonId = 402, type = ChallengeType.SELECT,
                    question = "How do you say 'The book wasn't interesting'?",
                    audioSrc = "asset:///audio/ja/hon_wa_omoshiroku_nakatta.ogg",
                    orderIndex = 5,
                    grammaticalFocus = "ja.na_adjective",
                    ruleText = "A な-adjective makes the plain past negative with くなかった: おもしろい → おもしろくなかった.\nおもしろかった and おもしろくない conjugate it like an い-adjective, and おもしろな is not a form of it.",
                ),
                ChallengeEntity(
                    id = 60021, lessonId = 402, type = ChallengeType.FILL_BLANK,
                    question = "___は安かった。",
                    audioSrc = "asset:///audio/ja/biiru_wa_yasukatta.ogg",
                    orderIndex = 6,
                    heldOut = true,
                    grammaticalFocus = "ja.i_adjective",
                    acceptedAnswers = "ビール|びーる",
                    ruleText = "An い-adjective makes the plain past with かった: 安い → 安かった.\nビールス is not a Japanese word and コーヒー is a different drink — the blank wants the drink that was cheap.",
                ),
                ChallengeEntity(
                    id = 61003, lessonId = 402, type = ChallengeType.LISTEN,
                    question = "Tap what you hear",
                    audioSrc = "asset:///audio/ja/biiru_wa_yasukatta.ogg",
                    orderIndex = 7,
                    grammaticalFocus = "ja.i_adjective",
                    ruleText = "An い-adjective makes the plain past on its own stem and stops there: 安い → 安かった, so nothing may follow the かった.\nビールは安いです keeps です and is saying the beer is cheap right now, and ビールは安くなかった adds なかった to a かった that already carries the past — なかった is the な-adjective and verb pattern an い-adjective never uses.",
                ),
                ChallengeEntity(
                    id = 61004, lessonId = 402, type = ChallengeType.LISTEN,
                    question = "Tap what you hear",
                    audioSrc = "asset:///audio/ja/kyoushitsu_wa_kirei_deshita.ogg",
                    orderIndex = 8,
                    grammaticalFocus = "ja.na_adjective",
                    ruleText = "A な-adjective never changes its stem, so きれい + でした is the whole polite past and the sentence ends there.\n教室はきれいだった swaps です for だ and drops the polite ending — that is the plain 断定形 — and 教室はきれいくでした bolts the い-adjective stem く onto the な-adjective ending, which is exactly the class mistake this unit exists to catch.",
                ),
            ),
            options = listOf(
                ChallengeOptionEntity(id = 600001, challengeId = 60000, text = "昨日大阪へ行きました", romaji = "kinou osaka e ikimashita", correct = true, audioSrc = "asset:///audio/ja/kinou_osaka_e_ikimashita.ogg"),
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

                ChallengeOptionEntity(id = 600022, challengeId = 60005, text = "きのう、手紙を書きました", romaji = "kinou tegami o kakimashita", correct = true, audioSrc = "asset:///audio/ja/kinou_tegami_o_kakimashita.ogg"),
                ChallengeOptionEntity(id = 600023, challengeId = 60005, text = "きのう、手紙を書きます", romaji = "kinou tegami o kakimasu", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 600024, challengeId = 60005, text = "きのう、手紙を書いて", romaji = "kinou tegami o kaite", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 600025, challengeId = 60005, text = "きのう、手紙を書こう", romaji = "kinou tegami o kakou", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 600026, challengeId = 60006, text = "帰りました", romaji = "kaerimashita", correct = true),
                ChallengeOptionEntity(id = 600027, challengeId = 60006, text = "帰ります", romaji = "kaerimasu", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 600028, challengeId = 60006, text = "帰った", romaji = "kaetta", correct = false, errorTag = "WRONG_REGISTER"),
                ChallengeOptionEntity(id = 600029, challengeId = 60006, text = "帰って", romaji = "kaette", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 600030, challengeId = 60007, text = "買った", romaji = "katta", correct = true, audioSrc = "asset:///audio/ja/katta.ogg"),
                ChallengeOptionEntity(id = 600031, challengeId = 60007, text = "買いました", romaji = "kaimashita", correct = false, errorTag = "WRONG_REGISTER"),
                ChallengeOptionEntity(id = 600032, challengeId = 60007, text = "買います", romaji = "kaimasu", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 600033, challengeId = 60007, text = "買って", romaji = "katte", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 600034, challengeId = 60008, text = "読んだ", romaji = "yonda", correct = true),
                ChallengeOptionEntity(id = 600035, challengeId = 60008, text = "読んでいます", romaji = "yonde imasu", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 600036, challengeId = 60008, text = "読んで", romaji = "yonde", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 600037, challengeId = 60008, text = "読みません", romaji = "yomimasen", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 600038, challengeId = 60009, text = "わかりません", romaji = "wakarimasen", correct = true, audioSrc = "asset:///audio/ja/wakarimasen.ogg"),
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

                ChallengeOptionEntity(id = 600054, challengeId = 60013, text = "食べました", romaji = "tabemashita", correct = true, audioSrc = "asset:///audio/ja/tabemashita.ogg"),
                ChallengeOptionEntity(id = 600055, challengeId = 60013, text = "I ate (polite)", correct = true),
                ChallengeOptionEntity(id = 600056, challengeId = 60013, text = "食べる", romaji = "taberu", correct = true),
                ChallengeOptionEntity(id = 600057, challengeId = 60013, text = "I eat (plain)", correct = true),
                ChallengeOptionEntity(id = 600058, challengeId = 60013, text = "飲みました", romaji = "nomimashita", correct = true),
                ChallengeOptionEntity(id = 600059, challengeId = 60013, text = "I drank (polite)", correct = true),
                ChallengeOptionEntity(id = 600060, challengeId = 60013, text = "飲む", romaji = "nomu", correct = true, audioSrc = "asset:///audio/ja/nomu.ogg"),
                ChallengeOptionEntity(id = 600061, challengeId = 60013, text = "I drink (plain)", correct = true),

                ChallengeOptionEntity(id = 600062, challengeId = 60014, text = "飲まない", romaji = "nomanai", correct = true),
                ChallengeOptionEntity(id = 600063, challengeId = 60014, text = "飲ません", romaji = "nomesen", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 600064, challengeId = 60014, text = "飲みません", romaji = "nomimasen", correct = false, errorTag = "WRONG_REGISTER"),
                ChallengeOptionEntity(id = 600065, challengeId = 60014, text = "飲む", romaji = "nomu", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 600066, challengeId = 60015, text = "昨日は寒かった", romaji = "kinou wa samukatta", correct = true, audioSrc = "asset:///audio/ja/kinou_wa_samukatta.ogg"),
                ChallengeOptionEntity(id = 600067, challengeId = 60015, text = "昨日は寒くない", romaji = "kinou wa samuku nai", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 600068, challengeId = 60015, text = "昨日は寒いです", romaji = "kinou wa samu desu", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 600069, challengeId = 60015, text = "昨日は寒い", romaji = "kinou wa samui", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 600070, challengeId = 60016, text = "新しくない", romaji = "atarashiku nai", correct = true),
                ChallengeOptionEntity(id = 600071, challengeId = 60016, text = "新しかった", romaji = "atarashikatta", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 600072, challengeId = 60016, text = "新しく", romaji = "atarashiku", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 600073, challengeId = 60016, text = "新くない", romaji = "atarashiku nai", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 600074, challengeId = 60017, text = "部屋はきれいでした", romaji = "heya wa kirei deshita", correct = true, audioSrc = "asset:///audio/ja/heya_wa_kirei_deshita.ogg"),
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

                ChallengeOptionEntity(id = 6100004, challengeId = 61001, text = "きょうは、雨が降りません", romaji = "kyou wa ame ga furimasen", correct = true),
                ChallengeOptionEntity(id = 6100005, challengeId = 61001, text = "きょうは、雨を降りません", romaji = "kyou wa ame o furimasen", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6100006, challengeId = 61001, text = "きょうは、雨がふっていません", romaji = "kyou wa ame ga futte imasen", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 600086, challengeId = 60020, text = "本はおもしろくなかった", romaji = "hon wa omoshiroku nakatta", correct = true, audioSrc = "asset:///audio/ja/hon_wa_omoshiroku_nakatta.ogg"),
                ChallengeOptionEntity(id = 600087, challengeId = 60020, text = "本はおもしろかった", romaji = "hon wa omoshirokatta", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 600088, challengeId = 60020, text = "本はおもしろくない", romaji = "hon wa omoshiroku nai", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 600089, challengeId = 60020, text = "本はおもしろな", romaji = "hon wa omoshiro na", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 600090, challengeId = 60021, text = "ビール", romaji = "biiru", correct = true),
                ChallengeOptionEntity(id = 600091, challengeId = 60021, text = "ビールス", romaji = "biirusu", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 600092, challengeId = 60021, text = "コーヒー", romaji = "koohii", correct = false),

                ChallengeOptionEntity(id = 6100001, challengeId = 61000, text = "きのう、雑誌を買いました", romaji = "kinou zasshi o kaimashita", correct = true),
                ChallengeOptionEntity(id = 6100002, challengeId = 61000, text = "きのう、雑誌を買います", romaji = "kinou zasshi o kaimasu", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 6100003, challengeId = 61000, text = "きのう、雑誌を買おう", romaji = "kinou zasshi o kaou", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 6100007, challengeId = 61002, text = "あした、学校に行きます", romaji = "ashita gakko ni ikimasu", correct = true),
                ChallengeOptionEntity(id = 6100008, challengeId = 61002, text = "あした、学校に行きました", romaji = "ashita gakko ni ikimashita", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 6100009, challengeId = 61002, text = "あした、学校に行った", romaji = "ashita gakko ni itta", correct = false, errorTag = "WRONG_REGISTER"),

                ChallengeOptionEntity(id = 6100010, challengeId = 61003, text = "ビールは安かった", romaji = "biiru wa yasukatta", correct = true),
                ChallengeOptionEntity(id = 6100011, challengeId = 61003, text = "ビールは安いです", romaji = "biiru wa yasui desu", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 6100012, challengeId = 61003, text = "ビールは安くなかった", romaji = "biiru wa yasuku nakatta", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 6100013, challengeId = 61004, text = "教室はきれいでした", romaji = "kyoushitsu wa kirei deshita", correct = true),
                ChallengeOptionEntity(id = 6100014, challengeId = 61004, text = "教室はきれいだった", romaji = "kyoushitsu wa kirei datta", correct = false, errorTag = "WRONG_REGISTER"),
                ChallengeOptionEntity(id = 6100015, challengeId = 61004, text = "教室はきれいくでした", romaji = "kyoushitsu wa kireiku deshita", correct = false, errorTag = "WRONG_FORM"),
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
                    audioSrc = "asset:///audio/ja/jitensha_ni_noremasu.ogg",
                    orderIndex = 0,
                    grammaticalFocus = "ja.ability_polite",
                    ruleText = "A godan verb says 'can' with the potential, る → える: 乗る → 乗れます.\n乗ります is the polite present, 乗らない the plain negative, and 乗らなければ the conditional — only 乗れます is a single word that means 'can'.",
                ),
                ChallengeEntity(
                    id = 60023, lessonId = 403, type = ChallengeType.WORD_BANK,
                    question = "Assemble: 'I can cook Italian food'",
                    orderIndex = 1,
                    grammaticalFocus = "ja.ability_polite",
                    ruleText = "An ichidan verb makes the potential the same way, by dropping る: 作る → 作れる → 作れます, so イタリア料理を作れます 'I can cook Italian food'.\n作れる is the plain potential and 作ります the polite present — this item assembles the polite one.",
                ),
                ChallengeEntity(
                    id = 60024, lessonId = 403, type = ChallengeType.CONJUGATE,
                    question = "Which form of 泳ぐ goes before ことができます?",
                    audioSrc = "asset:///audio/ja/oyogu.ogg",
                    orderIndex = 2,
                    grammaticalFocus = "ja.ability_polite",
                    ruleText = "Before ことができます the verb takes its plain form, because こと is the する→す nominaliser: 泳ぐ + こと + が + できます.\n泳ぎます is the polite present and こと cannot nominalise it — 泳ぎますことができます is not a sentence — while 泳げます is the short potential, the other way to say 'can'.",
                ),
                ChallengeEntity(
                    id = 60025, lessonId = 403, type = ChallengeType.FILL_BLANK,
                    question = "車を___ことができます。",
                    audioSrc = "asset:///audio/ja/kuruma_wo_unten_suru_koto_ga_dekimasu.ogg",
                    orderIndex = 3,
                    grammaticalFocus = "ja.ability_polite",
                    acceptedAnswers = "運転する|うんてんする",
                    ruleText = "ことができます is 「〜こと が できます」: こと turns the verb in front of it into a noun, and a する-verb keeps its する in writing — 運転する ことが できます = 車を運転することができます.\n運転します keeps ます and こと cannot nominalise it, 運転した is the plain past, and 運転 on its own is a noun, not a verb — the blank wants the whole verb.",
                ),
                ChallengeEntity(
                    id = 60026, lessonId = 403, type = ChallengeType.SELECT,
                    question = "How do you say 'I can't eat spicy food'?",
                    audioSrc = "asset:///audio/ja/karai_mono_wa_taberaremasen.ogg",
                    orderIndex = 4,
                    grammaticalFocus = "ja.ability_polite",
                    ruleText = "Polite inability is the polite negative of the potential: 食べる → 食べられない → 食べられません.\n食べません says 'I don't eat' rather than 'I can't eat', 食べられない is the plain potential negative, and 食べます is the polite positive — only 食べられません says 'can't'.",
                ),
                ChallengeEntity(
                    id = 60027, lessonId = 403, type = ChallengeType.MATCH_PAIRS,
                    question = "Match the ability phrases",
                    orderIndex = 5,
                ),
                ChallengeEntity(
                    id = 60028, lessonId = 403, type = ChallengeType.CONJUGATE,
                    question = "Which form of 歌う means 'I can sing'?",
                    audioSrc = "asset:///audio/ja/utaemasu.ogg",
                    orderIndex = 6,
                    heldOut = true,
                    grammaticalFocus = "ja.ability_polite",
                    ruleText = "Ability is the potential: 歌う → 歌える → 歌えます.\n歌います is the polite present, 歌う the dictionary form, 歌わない the plain negative, and 歌えました the polite past — only 歌えます says 'can' in one word.",
                ),
                ChallengeEntity(
                    id = 61005, lessonId = 403, type = ChallengeType.LISTEN,
                    question = "Tap what you hear",
                    audioSrc = "asset:///audio/ja/jitensha_ni_noremasu.ogg",
                    orderIndex = 7,
                    grammaticalFocus = "ja.ability_polite",
                    ruleText = "'Can ride' is the potential in its polite form, one word: 乗る → 乗れる → 乗れます.\n自転車に乗ります is the plain polite verb 'I ride', which says nothing at all about ability, and 自転車に乗れる is the plain potential — 'I can ride' in the 断定形, not the 丁寧形 this item is in.",
                ),
                ChallengeEntity(
                    id = 61006, lessonId = 403, type = ChallengeType.LISTEN,
                    question = "Tap what you hear",
                    audioSrc = "asset:///audio/ja/unten_suru_koto_ga_dekimasu.ogg",
                    orderIndex = 8,
                    grammaticalFocus = "ja.ability_polite",
                    ruleText = "One rule, two routes: ことができます is 「〜こと が できます」, こと is the する→す nominaliser, so the verb in front of it takes its *plain* form — 運転する ことが できます.\n運転しますことができます keeps ます exactly where こと needs a noun, and 運転ができました is the polite past 'I could drive' — a different tense on a different construction.",
                ),

                // --- Lesson 24: I Think (〜と思います) ---------------------------
                ChallengeEntity(
                    id = 60029, lessonId = 404, type = ChallengeType.SELECT,
                    question = "How do you say 'I think it will be sunny tomorrow'?",
                    audioSrc = "asset:///audio/ja/ashita_wa_hareru_to_omoimasu.ogg",
                    orderIndex = 0,
                    grammaticalFocus = "ja.think",
                    ruleText = "A plain verb goes straight in front of と思います: 晴れる + と思います.\n晴れます attaches the ます ending first, 晴れた would make the opinion about today rather than tomorrow, and 晴れるのを見ます is an entirely different construction.",
                ),
                ChallengeEntity(
                    id = 60030, lessonId = 404, type = ChallengeType.CONJUGATE,
                    question = "Which form of 高い goes before と思います?",
                    audioSrc = "asset:///audio/ja/takai.ogg",
                    orderIndex = 1,
                    grammaticalFocus = "ja.think",
                    ruleText = "Before と思います a word keeps its plain form: 高い + と思います.\n高く is the stem that only ます takes, 高かった the past, and 高くない the negative — と思います takes the bare plain form.",
                ),
                ChallengeEntity(
                    id = 60031, lessonId = 404, type = ChallengeType.FILL_BLANK,
                    question = "この本は___と思います。",
                    audioSrc = "asset:///audio/ja/kono_hon_wa_omoshiroi_to_omoimasu.ogg",
                    orderIndex = 2,
                    grammaticalFocus = "ja.think",
                    acceptedAnswers = "おもしろい",
                    ruleText = "The word before と思います is the plain form: おもしろい + と思います.\nおもしろく is the stem that only ます takes, おもしろいでした adds です, and おもしろくない is the negative.",
                ),
                ChallengeEntity(
                    id = 60032, lessonId = 404, type = ChallengeType.SELECT,
                    question = "Which one means 'I think it was cold yesterday'?",
                    audioSrc = "asset:///audio/ja/kinou_wa_samukatta_to_omoimasu.ogg",
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
                ChallengeEntity(
                    id = 61007, lessonId = 404, type = ChallengeType.LISTEN,
                    question = "Tap what you hear",
                    audioSrc = "asset:///audio/ja/nihongo_wa_muzukashii_to_omoimasu.ogg",
                    orderIndex = 8,
                    grammaticalFocus = "ja.think",
                    ruleText = "The word in front of と思います is the bare plain form, adjectives included: むずかしい + と思います.\n日本語はむずかしくと思います hands ます the い-adjective stem く, which belongs to むずかしいです and to nothing before と思います, and 日本語はむずかしかったと思います puts the past on the adjective, so the opinion is about a day already gone.",
                ),

                // --- Lesson 25: Gifts and Favours (あげる/くれる/もらう) ----------
                ChallengeEntity(
                    id = 60037, lessonId = 405, type = ChallengeType.SELECT,
                    question = "What do you say when a friend gives you a book? (They gave it; you received it.)",
                    audioSrc = "asset:///audio/ja/tomodachi_ga_hon_o_kuremashita.ogg",
                    orderIndex = 0,
                    grammaticalFocus = "ja.giving_receiving",
                    ruleText = "くれる is 'somebody else does something for me': the giver is the subject with が, so 友達が本をくれました.\nもらいました says 'I got a gift' and あげました says 'I gave' — くれる is the one whose giver is somebody else.",
                ),
                ChallengeEntity(
                    id = 60038, lessonId = 405, type = ChallengeType.CONJUGATE,
                    question = "Which plain past form of くれる means 'they gave me'?",
                    audioSrc = "asset:///audio/ja/kureta.ogg",
                    orderIndex = 1,
                    grammaticalFocus = "ja.giving_receiving",
                    ruleText = "くれる is an ichidan verb: the plain past drops る and adds た, so くれた.\nくれて is the て-form, くれ the stem, and くれます the polite present.",
                ),
                ChallengeEntity(
                    id = 60039, lessonId = 405, type = ChallengeType.FILL_BLANK,
                    question = "___にあげました。",
                    audioSrc = "asset:///audio/ja/tomodachi_ni_agemashita.ogg",
                    orderIndex = 2,
                    grammaticalFocus = "ja.giving_receiving",
                    acceptedAnswers = "ともだち",
                    ruleText = "あげる is 'I give to somebody else', so the receiver takes に: ともだちにあげました.\nともだちました is 友達 + しました, a different verb — the に belongs to あげる, and the blank is what that に points at.",
                ),
                ChallengeEntity(
                    id = 60040, lessonId = 405, type = ChallengeType.SELECT,
                    question = "How do you say 'I got a present from my sister'?",
                    audioSrc = "asset:///audio/ja/imouto_kara_puresento_o_moraimashita.ogg",
                    orderIndex = 3,
                    grammaticalFocus = "ja.giving_receiving",
                    ruleText = "もらう is 'I receive', and the source of the gift takes から: 妹からプレゼントをもらいました.\n妹に would fit あげました instead (I give *to* my sister), and 妹が is the wrong subject — あげる and もらう swap exactly those two.",
                ),
                ChallengeEntity(
                    id = 60041, lessonId = 405, type = ChallengeType.CONJUGATE,
                    question = "Which polite past form of あげる means 'I gave it to him'?",
                    audioSrc = "asset:///audio/ja/agemashita.ogg",
                    orderIndex = 4,
                    grammaticalFocus = "ja.giving_receiving",
                    ruleText = "あげる makes the polite past with ました: あげる → あげました.\nあげます is the polite present, あげない the polite negative, あげる the plain present, and あげろ the plain imperative.",
                ),
                ChallengeEntity(
                    id = 60042, lessonId = 405, type = ChallengeType.FILL_BLANK,
                    question = "___をもらいました。",
                    audioSrc = "asset:///audio/ja/tegami_o_moraimashita.ogg",
                    orderIndex = 5,
                    grammaticalFocus = "ja.giving_receiving",
                    acceptedAnswers = "手紙",
                    ruleText = "もらう is 'I receive something that was given', and the thing received goes before を: 手紙をもらいました.\n手紙を would leave the sentence reading 手紙ををもらいました, and 紙 is the character on its own — the blank is the whole word.",
                ),
                ChallengeEntity(
                    id = 60043, lessonId = 405, type = ChallengeType.CONJUGATE,
                    question = "Which plain past form of もらう means 'I received it'?",
                    audioSrc = "asset:///audio/ja/moratta.ogg",
                    orderIndex = 6,
                    heldOut = true,
                    grammaticalFocus = "ja.giving_receiving",
                    ruleText = "もらう makes the plain past with った: もらう → もらった.\nもらいます is the polite present, もらう the plain present, and もらって the て-form.",
                ),
                ChallengeEntity(
                    id = 61008, lessonId = 405, type = ChallengeType.LISTEN,
                    question = "Tap what you hear",
                    audioSrc = "asset:///audio/ja/tomodachi_ga_hon_o_kuremashita.ogg",
                    orderIndex = 7,
                    grammaticalFocus = "ja.giving_receiving",
                    ruleText = "くれる is the verb whose giver is somebody else, so the friend takes が and the thing takes を: the friend did something for me.\n友達が本をあげました reverses the direction — あげる needs its receiver under に, never が — and 友達が本をくれませんでした is the polite negative past, so the friend did not in fact give it.",
                ),
            ),
            options = listOf(
                ChallengeOptionEntity(id = 600093, challengeId = 60022, text = "自転車に乗れます", romaji = "jitensha ni noremasu", correct = true, audioSrc = "asset:///audio/ja/jitensha_ni_noremasu.ogg"),
                ChallengeOptionEntity(id = 600094, challengeId = 60022, text = "自転車に乗ります", romaji = "jitensha ni norimasu", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 600095, challengeId = 60022, text = "自転車に乗らない", romaji = "jitensha ni noranai", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 600096, challengeId = 60022, text = "自転車に乗らなければ", romaji = "jitensha ni noranakereba", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 600097, challengeId = 60023, text = "イタリア料理を", romaji = "itaria ryori o", correct = true),
                ChallengeOptionEntity(id = 600098, challengeId = 60023, text = "作れます", romaji = "tsukuremasu", correct = true),
                ChallengeOptionEntity(id = 600099, challengeId = 60023, text = "作ります", romaji = "tsukurimasu", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 600100, challengeId = 60023, text = "作れる", romaji = "tsukureru", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 600101, challengeId = 60024, text = "泳ぎます", romaji = "oyogimasu", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 600102, challengeId = 60024, text = "泳げます", romaji = "oyogemasu", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 600103, challengeId = 60024, text = "泳ぐ", romaji = "oyogu", correct = true),
                ChallengeOptionEntity(id = 600104, challengeId = 60024, text = "泳ぎませんでした", romaji = "oyogimasen deshita", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 600105, challengeId = 60025, text = "運転", romaji = "unten", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 600106, challengeId = 60025, text = "運転します", romaji = "unten shimasu", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 600107, challengeId = 60025, text = "運転する", romaji = "unten suru", correct = true),
                ChallengeOptionEntity(id = 600108, challengeId = 60025, text = "運転した", romaji = "unten shita", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 600109, challengeId = 60026, text = "辛いものは食べられません", romaji = "karai mono wa taberaremasen", correct = true, audioSrc = "asset:///audio/ja/karai_mono_wa_taberaremasen.ogg"),
                ChallengeOptionEntity(id = 600110, challengeId = 60026, text = "辛いものは食べません", romaji = "karai mono wa tabimasen", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 600111, challengeId = 60026, text = "辛いものは食べられない", romaji = "karai mono wa taberarenai", correct = false, errorTag = "WRONG_REGISTER"),
                ChallengeOptionEntity(id = 600112, challengeId = 60026, text = "辛いものは食べます", romaji = "karai mono wa tabemasu", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 600113, challengeId = 60027, text = "日本語が話せます", romaji = "nihongo ga hanasemasu", correct = true, audioSrc = "asset:///audio/ja/nihongo_ga_hanasemasu_bare.ogg"),
                ChallengeOptionEntity(id = 600114, challengeId = 60027, text = "I can speak Japanese", correct = true),
                ChallengeOptionEntity(id = 600115, challengeId = 60027, text = "自転車に乗れます", romaji = "jitensha ni noremasu", correct = true),
                ChallengeOptionEntity(id = 600116, challengeId = 60027, text = "I can ride a bicycle", correct = true),
                ChallengeOptionEntity(id = 600117, challengeId = 60027, text = "運転することができます", romaji = "unten suru koto ga dekimasu", correct = true, audioSrc = "asset:///audio/ja/unten_suru_koto_ga_dekimasu.ogg"),
                ChallengeOptionEntity(id = 600118, challengeId = 60027, text = "I can drive", correct = true),

                ChallengeOptionEntity(id = 600119, challengeId = 60028, text = "歌えます", romaji = "utaemasu", correct = true),
                ChallengeOptionEntity(id = 600120, challengeId = 60028, text = "歌います", romaji = "utaimasu", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 600121, challengeId = 60028, text = "歌う", romaji = "utau", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 600122, challengeId = 60028, text = "歌えました", romaji = "utaemashita", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 600123, challengeId = 60028, text = "歌わない", romaji = "utawanai", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 600124, challengeId = 60029, text = "明日は晴れると思います", romaji = "ashita wa hareru to omoimasu", correct = true, audioSrc = "asset:///audio/ja/ashita_wa_hareru_to_omoimasu.ogg"),
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

                ChallengeOptionEntity(id = 600136, challengeId = 60032, text = "昨日は寒かったと思います", romaji = "kinou wa samukatta to omoimasu", correct = true, audioSrc = "asset:///audio/ja/kinou_wa_samukatta_to_omoimasu.ogg"),
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

                ChallengeOptionEntity(id = 600148, challengeId = 60035, text = "日本語はむずかしいと思います", romaji = "nihongo wa muzukashii to omoimasu", correct = true, audioSrc = "asset:///audio/ja/nihongo_wa_muzukashii_to_omoimasu.ogg"),
                ChallengeOptionEntity(id = 600149, challengeId = 60035, text = "I think Japanese is difficult", correct = true),
                ChallengeOptionEntity(id = 600150, challengeId = 60035, text = "この店の寿司はおいしいと思います", romaji = "kono mise no sushi wa oishii to omoimasu", correct = true, audioSrc = "asset:///audio/ja/kono_mise_no_sushi_wa_oishii_to_omoimasu.ogg"),
                ChallengeOptionEntity(id = 600151, challengeId = 60035, text = "I think the sushi here is delicious", correct = true),
                ChallengeOptionEntity(id = 600152, challengeId = 60035, text = "あの店は高いと思います", romaji = "ano mise wa takai to omoimasu", correct = true, audioSrc = "asset:///audio/ja/ano_mise_wa_takai_to_omoimasu.ogg"),
                ChallengeOptionEntity(id = 600153, challengeId = 60035, text = "I think that shop is expensive", correct = true),

                ChallengeOptionEntity(id = 600154, challengeId = 60036, text = "あつい", romaji = "atsui", correct = true, audioSrc = "asset:///audio/ja/atsui.ogg"),
                ChallengeOptionEntity(id = 600155, challengeId = 60036, text = "あつかった", romaji = "atsukatta", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 600156, challengeId = 60036, text = "あついです", romaji = "atsui desu", correct = false, errorTag = "WRONG_REGISTER"),

                ChallengeOptionEntity(id = 600158, challengeId = 60037, text = "友達が本をくれました", romaji = "tomodachi ga hon o kuremashita", correct = true, audioSrc = "asset:///audio/ja/tomodachi_ga_hon_o_kuremashita.ogg"),
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

                ChallengeOptionEntity(id = 600169, challengeId = 60040, text = "妹からプレゼントをもらいました", romaji = "imouto kara purēzento o moraimashita", correct = true, audioSrc = "asset:///audio/ja/imouto_kara_puresento_o_moraimashita.ogg"),
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

                ChallengeOptionEntity(id = 6100016, challengeId = 61005, text = "自転車に乗れます", romaji = "jitensha ni noremasu", correct = true),
                ChallengeOptionEntity(id = 6100017, challengeId = 61005, text = "自転車に乗ります", romaji = "jitensha ni norimasu", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6100018, challengeId = 61005, text = "自転車に乗れる", romaji = "jitensha ni noreru", correct = false, errorTag = "WRONG_REGISTER"),

                ChallengeOptionEntity(id = 6100019, challengeId = 61006, text = "運転することができます", romaji = "unten suru koto ga dekimasu", correct = true),
                ChallengeOptionEntity(id = 6100020, challengeId = 61006, text = "運転しますことができます", romaji = "unten shimasu koto ga dekimasu", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6100021, challengeId = 61006, text = "運転ができました", romaji = "unten ga dekimashita", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 6100022, challengeId = 61007, text = "日本語はむずかしいと思います", romaji = "nihongo wa muzukashii to omoimasu", correct = true),
                ChallengeOptionEntity(id = 6100023, challengeId = 61007, text = "日本語はむずかしくと思います", romaji = "nihongo wa muzukashiku to omoimasu", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6100024, challengeId = 61007, text = "日本語はむずかしかったと思います", romaji = "nihongo wa muzukashikatta to omoimasu", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 6100025, challengeId = 61008, text = "友達が本をくれました", romaji = "tomodachi ga hon o kuremashita", correct = true),
                ChallengeOptionEntity(id = 6100026, challengeId = 61008, text = "友達が本をあげました", romaji = "tomodachi ga hon o agemashita", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6100027, challengeId = 61008, text = "友達が本をくれませんでした", romaji = "tomodachi ga hon o kuremashimasen deshita", correct = false, errorTag = "WRONG_TENSE"),
            ),
        ),
        // =====================================================================
        // Units 13-14: the three grammar areas the roadmap said the Japanese
        // corpus never reached — 受身, 使役 and 修飾節.
        //
        // Id layout for this appended block, disjoint from units 11-12 above
        // and from every other curriculum file:
        //   - units `42`, `43`
        //   - lessons `406`-`412`
        //   - challenges `62000`-`62040` plus the `LISTEN` block `61100`-`61106`
        //   - options `6300001`-`6300803` plus the `LISTEN` block `6310001`-`6310063`
        //
        // **Honesty about what the passive can do.** 受身 is built on 他動詞 —
        // verbs that take a を-object, because the object is the thing that gets
        // acted on. 行く, 来る, 寝る and the rest are 自動詞 and have no
        // ordinary passive; the corpus says so in 62010's and 62012's rule
        // texts rather than quietly implying every verb can take one. The few
        // 自動詞 that do (存在する, 生きる) are outside this unit and are not
        // claimed.
        //
        // **られる is three words.** 書かれる is the passive, 書ける is the
        // potential, and られる also means 得る ('can get'). Lesson 26 and
        // lesson 27 both turn on that collision, and 62000, 62001, 62006 and
        // 62025 each pit two of them against each other so the learner meets
        // the ambiguity instead of being told to memorise an ending.
        //
        // **させる is not られる.** The causative adds せ to the stem (読ませる,
        // 食べさせる) while the passive adds れ (読まれる, 食べられる); 62014
        // and 62029 are MATCH_PAIRS built on exactly that one-mora difference.
        // する → させる and 来る → 来させる are the two irregulars, and 62024
        // and 62025 name them.
        //
        // **A は cannot head a relative clause.** の is what binds a clause to
        // the noun it modifies (62031-62040), and 62039's rule text says so
        // explicitly because 読みやすいのは日本語の本 is the mistake learners
        // actually make. When the clause's subject is also the sentence topic
        // it takes が (日本語が話せる人), which 62037's rule text covers.
        //
        // **Audio.** 43 new clips, all wired and all referenced: 34 on a
        // challenge and 39 on the option that speaks the same text. Every
        // `LISTEN` challenge carries a clip and none of its options does, the
        // way `ExerciseScreen` needs, and no `WRONG_*` distractor anywhere in
        // this block carries a clip.
        //
        // **Nothing in this block is unanswerable by ear.** The one real risk
        // is られる: 書かれる and 書ける, and 読まれる and 読める, sit one mora
        // apart, so no LISTEN item in this block is built on that pair — 62000,
        // 62001, 62006 and 62025 are SELECT/CONJUGATE read as text, and their
        // clips are deliberately on the correct half only. Each LISTEN's
        // contrast was checked in the IPA the acoustic model is handed:
        // tsukɯɾaɾe masɨ against tsukɯɾimasɨ, hanaseɾɯ against hanashiteimasɨ,
        // saɾeteimasɨ against sase te imasɨ, iɾɯ against ita.
        //
        // No item here is held out: the held-out pool is fixed at A2 4, B1 8,
        // N4 10 and the checkpoints are scoped to units 10-31 plus 40 and 41,
        // so a held-out item in unit 42 would be reachable by nothing.
        UnitPayload(
            unit = UnitEntity(
                id = 42,
                courseId = 2,
                title = "Unit 13: The Passive (受身)",
                description = "Say who did it to what: the される・られる forms, the agent particle に, and 受け身 + ている",
                orderIndex = 12,
            ),
            lessons = listOf(
                LessonEntity(id = 406, unitId = 42, title = "Lesson 26: Passive Forms", orderIndex = 0),
                LessonEntity(id = 407, unitId = 42, title = "Lesson 27: Who Did It", orderIndex = 1),
                LessonEntity(id = 408, unitId = 42, title = "Lesson 28: Right Now", orderIndex = 2),
            ),
            challenges = listOf(
                // --- Lesson 26: Passive Forms (られる) --------------------------
                ChallengeEntity(
                    id = 62000, lessonId = 406, type = ChallengeType.SELECT,
                    question = "How do you say 'It is read (by people)' in the plain passive?",
                    audioSrc = "asset:///audio/ja/g_passive_yomareru.ogg",
                    orderIndex = 0,
                    grammaticalFocus = "ja.passive_formation",
                    ruleText = "A godan verb makes the plain passive by changing its final う to れる: 読む → 読まれる.\n読める is the potential, 'can be read', and 読みます is the polite present of the active verb — neither puts られる on the stem the way 受身 does.",
                ),
                ChallengeEntity(
                    id = 62001, lessonId = 406, type = ChallengeType.CONJUGATE,
                    question = "Which plain passive form of 書く means 'is written'?",
                    audioSrc = "asset:///audio/ja/g_passive_kakareru.ogg",
                    orderIndex = 1,
                    grammaticalFocus = "ja.passive_formation",
                    ruleText = "The passive drops the う and adds れる: 書く → 書かれる. 書ける keeps the け of ける and is the potential, 'can write'.\n書かせられる has the causative せ inside られる and means 'is made to be written'. Only 書かれる is the passive of 書く.",
                ),
                ChallengeEntity(
                    id = 62002, lessonId = 406, type = ChallengeType.FILL_BLANK,
                    question = "きのう、レポートを___。",
                    audioSrc = "asset:///audio/ja/g_passive_ripoto_o_dasare.ogg",
                    orderIndex = 2,
                    grammaticalFocus = "ja.passive_formation",
                    acceptedAnswers = "出されました|だされました",
                    ruleText = "The polite passive past is 出されました: 出す → 出します → 出されます, and レポート keeps を because it is the thing that gets handed in.\n出します is the active polite present, so the report does the handing. 出されませんでした is the polite past negative, so the report was never handed in at all.",
                ),
                ChallengeEntity(
                    id = 62003, lessonId = 406, type = ChallengeType.WORD_BANK,
                    question = "Assemble: 'The room is cleaned every day'",
                    orderIndex = 3,
                    grammaticalFocus = "ja.passive_formation",
                    ruleText = "In the passive the thing acted on becomes the topic and the one doing it takes に: 部屋は毎日掃除されます.\n部屋は毎日掃除します makes the room the one doing the cleaning, and 毎日部屋を掃除されます leaves を on a verb that can no longer take an object, because the object slot is what moved to the topic.",
                ),
                ChallengeEntity(
                    id = 62004, lessonId = 406, type = ChallengeType.MATCH_PAIRS,
                    question = "Match the active verb with its passive",
                    orderIndex = 4,
                ),

                // --- Lesson 27: Who Did It (に, and the acted-on thing) ---------
                ChallengeEntity(
                    id = 62005, lessonId = 407, type = ChallengeType.SELECT,
                    question = "How do you say 'He was scolded by his mother'?",
                    audioSrc = "asset:///audio/ja/g_passive_haha_ni_shikarareta.ogg",
                    orderIndex = 0,
                    grammaticalFocus = "ja.passive_particles",
                    ruleText = "The agent of a passive takes に, never を: 母に叱られました.\n母を叱られました keeps を, the particle an object takes while it is being acted on — but the mother is doing the scolding, not receiving it. 母が叱られました makes the mother the one who got scolded, which is the opposite story.",
                ),
                ChallengeEntity(
                    id = 62006, lessonId = 407, type = ChallengeType.CONJUGATE,
                    question = "Which plain passive form of 見る means 'is seen'?",
                    audioSrc = "asset:///audio/ja/g_passive_mirerareru.ogg",
                    orderIndex = 1,
                    grammaticalFocus = "ja.passive_particles",
                    ruleText = "見る is an ichidan verb, so the passive drops る and adds られる: 見られる.\n見える is the potential, 'can be seen' — られ- is the れる that makes the passive, not the け of ける. 見せる is the causative, 'shows', and carries a せ the passive never has.",
                ),
                ChallengeEntity(
                    id = 62007, lessonId = 407, type = ChallengeType.FILL_BLANK,
                    question = "きのう、せんせいに___。",
                    audioSrc = "asset:///audio/ja/g_passive_sensei_ni_homerareta.ogg",
                    orderIndex = 2,
                    grammaticalFocus = "ja.passive_particles",
                    acceptedAnswers = "褒められました|ほめられました",
                    ruleText = "ほめる is an ichidan verb, so the polite passive past is 褒められました: ほめます → ほめられます, and せんせい takes に because the teacher is the one doing the praising.\nほめました is the plain active past, so the teacher praised and nobody was praised. ほめられません is the polite present, so the praising is still going on.",
                ),
                ChallengeEntity(
                    id = 62008, lessonId = 407, type = ChallengeType.SELECT,
                    question = "How do you say 'The plan hasn't been approved'?",
                    audioSrc = "asset:///audio/ja/g_passive_keikaku_shounin.ogg",
                    orderIndex = 3,
                    grammaticalFocus = "ja.passive_particles",
                    ruleText = "The polite passive negative is この計画は承認されていません: 承認する → 承認します → 承認されていません.\nこの計画は承認していません is the active negative, which has the plan doing the approving — not a thing plans do. この計画は承認しませんでした is the polite past negative, so it answers a question about last week.",
                ),
                ChallengeEntity(
                    id = 62009, lessonId = 407, type = ChallengeType.SELECT,
                    question = "How do you say 'The match was cancelled because of the rain'?",
                    audioSrc = "asset:///audio/ja/g_passive_ame_de_chuushi.ogg",
                    orderIndex = 4,
                    grammaticalFocus = "ja.passive_particles",
                    ruleText = "When something outside the agent brings it about, Japanese puts that something in front: 雨で試合は中止されました.\n雨が試合を中止しました makes the rain the one doing the cancelling, and 雨で試合は中止されません is the polite present, so the match is not being cancelled at all.",
                ),
                ChallengeEntity(
                    id = 61100, lessonId = 407, type = ChallengeType.LISTEN,
                    question = "Tap what you hear",
                    audioSrc = "asset:///audio/ja/g_passive_kuruma_koujou.ogg",
                    orderIndex = 5,
                    grammaticalFocus = "ja.passive_particles",
                    ruleText = "車は工場で作られます is the polite passive present: 作る → 作ります → 作られます, and 車 is the thing being made, so it is the topic.\n車は工場で作ります has the factory building the cars, and 車は工場で作られませんでした is the polite past negative, the opposite of what the clip says.",
                ),
                ChallengeEntity(
                    id = 61101, lessonId = 407, type = ChallengeType.LISTEN,
                    question = "Tap what you hear",
                    audioSrc = "asset:///audio/ja/g_passive_hon_yomareteimasu.ogg",
                    orderIndex = 6,
                    grammaticalFocus = "ja.passive_particles",
                    ruleText = "あの本はとてもよく読まれています is the polite passive: 読まれる + ています, 'is being read', and あの本 is the book being read rather than the reader.\nあの本はとてもよく読みます has the book doing the reading, and あの本はとてもよく読まれませんでした is the polite past negative, so nobody read it at all.",
                ),

                // --- Lesson 28: Right Now (受け身 + ている) and its limits -----
                ChallengeEntity(
                    id = 62010, lessonId = 408, type = ChallengeType.SELECT,
                    question = "How do you say 'This river is being polluted'?",
                    audioSrc = "asset:///audio/ja/g_passive_kawa_kogyou.ogg",
                    orderIndex = 0,
                    grammaticalFocus = "ja.passive_teiru",
                    ruleText = "受け身 + ている says the state is on-going right now, so この川は汚染されています comes from 汚染します → 汚染されます → 汚染されています.\nこの川は汚染しています has the river polluting, and この川は汚染されませんでした is the polite past negative, so the pollution is over rather than current.",
                ),
                ChallengeEntity(
                    id = 62011, lessonId = 408, type = ChallengeType.WORD_BANK,
                    question = "Assemble: 'That shop is closed at the moment'",
                    orderIndex = 1,
                    grammaticalFocus = "ja.passive_teiru",
                    ruleText = "The passive progressive is the polite passive plus ています, so その店は今閉められています comes from 閉めます → 閉められます → 閉められています.\nその店は今閉めています has the shop doing the shutting, and その店は店主が閉めています makes the owner the topic and the shop the thing shut — the other way round.",
                ),
                ChallengeEntity(
                    id = 62012, lessonId = 408, type = ChallengeType.CONJUGATE,
                    question = "Which plain passive progressive of 使う means 'is being used'?",
                    audioSrc = "asset:///audio/ja/g_passive_tsukawareteiru.ogg",
                    orderIndex = 2,
                    grammaticalFocus = "ja.passive_teiru",
                    ruleText = "The progressive on a passive is られる + ている: 使う → 使われる → 使われている.\n使っている is built on the て-form, which is the connective shape and never heads a passive. 使わせられている is the causative passive, 'is being made to be used', and the extra せ is the tell.",
                ),
                ChallengeEntity(
                    id = 62013, lessonId = 408, type = ChallengeType.FILL_BLANK,
                    question = "この道路は現在___。",
                    audioSrc = "asset:///audio/ja/g_passive_douro_seibi.ogg",
                    orderIndex = 3,
                    grammaticalFocus = "ja.passive_teiru",
                    acceptedAnswers = "整備されています|せいびされています",
                    ruleText = "The polite passive progressive of 整備する is 整備されています: 整備します → 整備されます → 整備されています.\n整っています is the progressive of the plain verb 整う, 'is tidy', which is a different word. 整備されていません is the polite negative, so the road is being neglected.",
                ),
                ChallengeEntity(
                    id = 62014, lessonId = 408, type = ChallengeType.MATCH_PAIRS,
                    question = "Match the passive with the causative of the same verb",
                    orderIndex = 4,
                ),
                ChallengeEntity(
                    id = 61102, lessonId = 408, type = ChallengeType.LISTEN,
                    question = "Tap what you hear",
                    audioSrc = "asset:///audio/ja/g_passive_mise_sooji.ogg",
                    orderIndex = 5,
                    grammaticalFocus = "ja.passive_teiru",
                    ruleText = "この店は毎日掃除されています is the polite passive progressive: 掃除します → 掃除されます → 掃除されています.\nこの店は毎日掃除します has the shop doing the sweeping, and この店は毎日掃除されます is the polite passive with no ています, so it does not say the state is on-going.",
                ),
            ),
            options = listOf(
                ChallengeOptionEntity(id = 6300001, challengeId = 62000, text = "読まれる", romaji = "yomareru", correct = true, audioSrc = "asset:///audio/ja/g_passive_yomareru.ogg"),
                ChallengeOptionEntity(id = 6300002, challengeId = 62000, text = "読める", romaji = "yomeru", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6300003, challengeId = 62000, text = "読みます", romaji = "yomimasu", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 6300021, challengeId = 62001, text = "書かれる", romaji = "kakareru", correct = true, audioSrc = "asset:///audio/ja/g_passive_kakareru.ogg"),
                ChallengeOptionEntity(id = 6300022, challengeId = 62001, text = "書ける", romaji = "kakeru", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6300023, challengeId = 62001, text = "書かせられる", romaji = "kakaserareru", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 6300041, challengeId = 62002, text = "出されました", romaji = "dasaremashita", correct = true, audioSrc = "asset:///audio/ja/g_passive_ripoto_o_dasare.ogg"),
                ChallengeOptionEntity(id = 6300042, challengeId = 62002, text = "出します", romaji = "dashimasu", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6300043, challengeId = 62002, text = "出されませんでした", romaji = "dasaremasen deshita", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 6300061, challengeId = 62003, text = "部屋は毎日掃除されます", romaji = "heya wa mainichi souji saremasu", correct = true),
                ChallengeOptionEntity(id = 6300062, challengeId = 62003, text = "部屋は毎日掃除します", romaji = "heya wa mainichi souji shimasu", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6300063, challengeId = 62003, text = "毎日部屋を掃除されます", romaji = "mainichi heya o souji saremasu", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 6300081, challengeId = 62004, text = "読む", romaji = "yomu", correct = true),
                ChallengeOptionEntity(id = 6300082, challengeId = 62004, text = "読まれる", romaji = "yomareru", correct = true, audioSrc = "asset:///audio/ja/g_passive_yomareru.ogg"),
                ChallengeOptionEntity(id = 6300083, challengeId = 62004, text = "使う", romaji = "tsukau", correct = true),
                ChallengeOptionEntity(id = 6300084, challengeId = 62004, text = "使われる", romaji = "tsukawareru", correct = true, audioSrc = "asset:///audio/ja/g_passive_tsukawareru.ogg"),
                ChallengeOptionEntity(id = 6300085, challengeId = 62004, text = "見る", romaji = "miru", correct = true),
                ChallengeOptionEntity(id = 6300086, challengeId = 62004, text = "見られる", romaji = "mirerareru", correct = true, audioSrc = "asset:///audio/ja/g_passive_mirerareru.ogg"),
                ChallengeOptionEntity(id = 6300087, challengeId = 62004, text = "話す", romaji = "hanasu", correct = true),
                ChallengeOptionEntity(id = 6300088, challengeId = 62004, text = "話される", romaji = "hanasareru", correct = true, audioSrc = "asset:///audio/ja/g_passive_hanasareru.ogg"),
                ChallengeOptionEntity(id = 6300089, challengeId = 62004, text = "食べる", romaji = "taberu", correct = true),
                ChallengeOptionEntity(id = 6300090, challengeId = 62004, text = "食べられる", romaji = "taberareru", correct = true, audioSrc = "asset:///audio/ja/g_passive_taberareta.ogg"),
                ChallengeOptionEntity(id = 6300091, challengeId = 62004, text = "書く", romaji = "kaku", correct = true),
                ChallengeOptionEntity(id = 6300092, challengeId = 62004, text = "書かれる", romaji = "kakareru", correct = true, audioSrc = "asset:///audio/ja/g_passive_kakareru.ogg"),

                ChallengeOptionEntity(id = 6300101, challengeId = 62005, text = "母に叱られました", romaji = "haha ni shikararemashita", correct = true, audioSrc = "asset:///audio/ja/g_passive_haha_ni_shikarareta.ogg"),
                ChallengeOptionEntity(id = 6300102, challengeId = 62005, text = "母を叱られました", romaji = "haha o shikararemashita", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6300103, challengeId = 62005, text = "母が叱られました", romaji = "haha ga shikararemashita", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 6300121, challengeId = 62006, text = "見られる", romaji = "mirerareru", correct = true, audioSrc = "asset:///audio/ja/g_passive_mirerareru.ogg"),
                ChallengeOptionEntity(id = 6300122, challengeId = 62006, text = "見える", romaji = "mieru", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6300123, challengeId = 62006, text = "見せる", romaji = "miseru", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 6300141, challengeId = 62007, text = "褒められました", romaji = "homeraremashita", correct = true, audioSrc = "asset:///audio/ja/g_passive_sensei_ni_homerareta.ogg"),
                ChallengeOptionEntity(id = 6300142, challengeId = 62007, text = "ほめました", romaji = "homemashita", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6300143, challengeId = 62007, text = "ほめられません", romaji = "homeraremasen", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 6300161, challengeId = 62008, text = "この計画は承認されていません", romaji = "kono keikaku wa shounin sareteimasen", correct = true, audioSrc = "asset:///audio/ja/g_passive_keikaku_shounin.ogg"),
                ChallengeOptionEntity(id = 6300162, challengeId = 62008, text = "この計画は承認していません", romaji = "kono keikaku wa shounin shiteimasen", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6300163, challengeId = 62008, text = "この計画は承認しませんでした", romaji = "kono keikaku wa shounin shimasen deshita", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 6300181, challengeId = 62009, text = "雨で試合は中止されました", romaji = "ame de shiai wa chuushi sare mashita", correct = true, audioSrc = "asset:///audio/ja/g_passive_ame_de_chuushi.ogg"),
                ChallengeOptionEntity(id = 6300182, challengeId = 62009, text = "雨が試合を中止しました", romaji = "ame ga shiai o chuushi shimashita", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6300183, challengeId = 62009, text = "雨で試合は中止されません", romaji = "ame de shiai wa chuushi saremasen", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 6310001, challengeId = 61100, text = "車は工場で作られます", romaji = "kuruma wa koujou de tsukureraremasu", correct = true),
                ChallengeOptionEntity(id = 6310002, challengeId = 61100, text = "車は工場で作ります", romaji = "kuruma wa koujou de tsukurimasu", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6310003, challengeId = 61100, text = "車は工場で作られませんでした", romaji = "kuruma wa koujou de tsukureraremasen deshita", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 6300201, challengeId = 62010, text = "この川は汚染されています", romaji = "kono kawa wa osen sareteimasu", correct = true, audioSrc = "asset:///audio/ja/g_passive_kawa_kogyou.ogg"),
                ChallengeOptionEntity(id = 6300202, challengeId = 62010, text = "この川は汚染しています", romaji = "kono kawa wa osen shiteimasu", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6300203, challengeId = 62010, text = "この川は汚染されませんでした", romaji = "kono kawa wa osen saremasen deshita", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 6300221, challengeId = 62011, text = "その店は今閉められています", romaji = "sono mise wa ima shimareteimasu", correct = true),
                ChallengeOptionEntity(id = 6300222, challengeId = 62011, text = "その店は今閉めています", romaji = "sono mise wa ima shimemasu", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6300223, challengeId = 62011, text = "その店は店主が閉めています", romaji = "sono mise wa tenchu ga shimemasu", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 6300241, challengeId = 62012, text = "使われている", romaji = "tsukawareteiru", correct = true, audioSrc = "asset:///audio/ja/g_passive_tsukawareteiru.ogg"),
                ChallengeOptionEntity(id = 6300242, challengeId = 62012, text = "使っている", romaji = "tsukatteiru", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6300243, challengeId = 62012, text = "使わせられている", romaji = "tsukawasareteiru", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 6300261, challengeId = 62013, text = "整備されています", romaji = "seibi sareteimasu", correct = true, audioSrc = "asset:///audio/ja/g_passive_douro_seibi.ogg"),
                ChallengeOptionEntity(id = 6300262, challengeId = 62013, text = "整っています", romaji = "tootteimasu", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6300263, challengeId = 62013, text = "整備されていません", romaji = "seibi sareteimasen", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 6300281, challengeId = 62014, text = "読まれる", romaji = "yomareru", correct = true),
                ChallengeOptionEntity(id = 6300282, challengeId = 62014, text = "is read", correct = true),
                ChallengeOptionEntity(id = 6300283, challengeId = 62014, text = "読ませる", romaji = "yomaseru", correct = true),
                ChallengeOptionEntity(id = 6300284, challengeId = 62014, text = "makes read", correct = true),
                ChallengeOptionEntity(id = 6300285, challengeId = 62014, text = "見られる", romaji = "mirerareru", correct = true),
                ChallengeOptionEntity(id = 6300286, challengeId = 62014, text = "is seen", correct = true),
                ChallengeOptionEntity(id = 6300287, challengeId = 62014, text = "見せる", romaji = "miseru", correct = true),
                ChallengeOptionEntity(id = 6300288, challengeId = 62014, text = "shows", correct = true),
                ChallengeOptionEntity(id = 6300289, challengeId = 62014, text = "書かれる", romaji = "kakareru", correct = true),
                ChallengeOptionEntity(id = 6300290, challengeId = 62014, text = "is written", correct = true),
                ChallengeOptionEntity(id = 6300291, challengeId = 62014, text = "書かせる", romaji = "kakaseru", correct = true),
                ChallengeOptionEntity(id = 6300292, challengeId = 62014, text = "makes write", correct = true),
                ChallengeOptionEntity(id = 6300293, challengeId = 62014, text = "食べられる", romaji = "taberareru", correct = true),
                ChallengeOptionEntity(id = 6300294, challengeId = 62014, text = "is eaten", correct = true),
                ChallengeOptionEntity(id = 6300295, challengeId = 62014, text = "食べさせる", romaji = "tabesaseru", correct = true),
                ChallengeOptionEntity(id = 6300296, challengeId = 62014, text = "makes eat", correct = true),

                ChallengeOptionEntity(id = 6310011, challengeId = 61101, text = "あの本はとてもよく読まれています", romaji = "ano hon wa totemo yoku yomareteimasu", correct = true),

                ChallengeOptionEntity(id = 6310012, challengeId = 61101, text = "あの本はとてもよく読みます", romaji = "ano hon wa totemo yoku yomimasu", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6310013, challengeId = 61101, text = "あの本はとてもよく読まれませんでした", romaji = "ano hon wa totemo yoku yomaremasen deshita", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 6310021, challengeId = 61102, text = "この店は毎日掃除されています", romaji = "kono mise wa mainichi souji sareteimasu", correct = true),
                ChallengeOptionEntity(id = 6310022, challengeId = 61102, text = "この店は毎日掃除します", romaji = "kono mise wa mainichi souji shimasu", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6310023, challengeId = 61102, text = "この店は毎日掃除されます", romaji = "kono mise wa mainichi souji saremasu", correct = false, errorTag = "WRONG_FORM"),
            ),
        ),
        UnitPayload(
            unit = UnitEntity(
                id = 43,
                courseId = 2,
                title = "Unit 14: The Causative (使役) and Relative Clauses (修飾節)",
                description = "Make someone do it: 〜させる, the する and 来る irregulars, させる + ている — then the clause that modifies a noun: 〜た人, 〜ている人, 〜ない人, and the plain form + の",
                orderIndex = 13,
            ),
            lessons = listOf(
                LessonEntity(id = 409, unitId = 43, title = "Lesson 29: Making Someone Do", orderIndex = 0),
                LessonEntity(id = 410, unitId = 43, title = "Lesson 30: Being Made To Do It", orderIndex = 1),
                LessonEntity(id = 411, unitId = 43, title = "Lesson 31: The Noun With a Story", orderIndex = 2),
                LessonEntity(id = 412, unitId = 43, title = "Lesson 32: Who's In The Clause", orderIndex = 3),
            ),
            challenges = listOf(
                // --- Lesson 29: Making Someone Do (使役) ------------------------
                ChallengeEntity(
                    id = 62020, lessonId = 409, type = ChallengeType.SELECT,
                    question = "How do you say 'I'll make him eat' in the plain causative?",
                    audioSrc = "asset:///audio/ja/g_causative_tabesaseru.ogg",
                    orderIndex = 0,
                    grammaticalFocus = "ja.causative_formation",
                    ruleText = "The causative adds させる to an ichidan verb's stem: 食べる → 食べさせる, and the thing named by を is the one being made to act.\n食べさせられる carries a ら as well as the せ, which is the passive of 食べさせる. 食べられました is the polite past of the plain passive and never gets anybody to do anything.",
                ),
                ChallengeEntity(
                    id = 62021, lessonId = 409, type = ChallengeType.CONJUGATE,
                    question = "Which plain causative form of 読む means 'makes (someone) read'?",
                    audioSrc = "asset:///audio/ja/g_causative_yomaseru.ogg",
                    orderIndex = 1,
                    grammaticalFocus = "ja.causative_formation",
                    ruleText = "A godan verb makes the causative by changing its final う to せる: 読む → 読ませる.\n読まれる is the passive, 'is read' — せる and れる differ in one mora. 読ませます is the polite form of the very same causative, so it is the right meaning in the wrong register.",
                ),
                ChallengeEntity(
                    id = 62022, lessonId = 409, type = ChallengeType.FILL_BLANK,
                    question = "母が野菜を___。",
                    audioSrc = "asset:///audio/ja/g_causative_tabesasemashita.ogg",
                    orderIndex = 2,
                    grammaticalFocus = "ja.causative_formation",
                    acceptedAnswers = "食べさせました|たべさせました",
                    ruleText = "The polite causative past is させました on the causative stem: 食べさせます → 食べさせました, and 母が is the one making it happen.\n食べられました is the polite past *passive*, so the vegetable is the one being made to eat. 食べました is the ordinary polite past of 食べる, so the mother ate the vegetables herself.",
                ),
                ChallengeEntity(
                    id = 62023, lessonId = 409, type = ChallengeType.WORD_BANK,
                    question = "Assemble: 'The coach makes the team run every morning'",
                    orderIndex = 3,
                    grammaticalFocus = "ja.causative_formation",
                    ruleText = "走る is a godan verb, so its causative is 走らせる: コーチは毎朝チームを走らせます, and チームを is the one being made to run.\nコーチは毎朝チームが走ります leaves the team running on its own, and コーチは毎朝チームの走らせます leaves を on a verb that has become its own action — once a verb turns causative, what it used to act on moves into に.",
                ),
                ChallengeEntity(
                    id = 62024, lessonId = 409, type = ChallengeType.SELECT,
                    question = "Which plain causative of する means 'makes (someone) do'?",
                    audioSrc = "asset:///audio/ja/g_causative_saseru.ogg",
                    orderIndex = 4,
                    grammaticalFocus = "ja.causative_formation",
                    ruleText = "する is irregular here: the causative is させる, not するせる — 勉強する → 勉強させる, and nothing is inserted between.\nせられる is the old honorific causative, kept in a handful of set phrases, and される is the plain passive of する. Only させる is the plain causative stem a learner builds on.",
                ),
                ChallengeEntity(
                    id = 62025, lessonId = 409, type = ChallengeType.SELECT,
                    question = "Which plain causative of 来る means 'makes (someone) come'?",
                    audioSrc = "asset:///audio/ja/g_causative_kuramaseru.ogg",
                    orderIndex = 5,
                    grammaticalFocus = "ja.causative_formation",
                    ruleText = "来る is irregular too, and its causative is 来させる: 友達が来させる. The stem keeps its 来 and takes させる, with nothing inserted.\n来られる is the potential, 'can come', which is the whole reason the two get confused. 来かせました puts a せ in a place させる never puts one, because させる already carries it.",
                ),
                ChallengeEntity(
                    id = 61103, lessonId = 409, type = ChallengeType.LISTEN,
                    question = "Tap what you hear",
                    audioSrc = "asset:///audio/ja/g_causative_shizuka_sasemashita.ogg",
                    orderIndex = 6,
                    grammaticalFocus = "ja.causative_formation",
                    ruleText = "先生は生徒に静かにさせました is the polite causative past: させる → させます → させました, with 生徒に marking the one being made to be quiet.\n先生は生徒に静かにされました has the students quieting the teacher, so the roles are reversed. 先生は生徒に静かにさせません is the polite present, so he never makes them be quiet at all.",
                ),

                // --- Lesson 30: Being Made To Do It (使役 + ている) --------------
                ChallengeEntity(
                    id = 62026, lessonId = 410, type = ChallengeType.SELECT,
                    question = "How do you say 'My mother is making me eat vegetables' (right now)?",
                    audioSrc = "asset:///audio/ja/g_causative_tabesaseteimasu.ogg",
                    orderIndex = 0,
                    grammaticalFocus = "ja.causative_teiru",
                    ruleText = "The causative has a progressive just as the passive does, so 母は私に野菜を食べさせています comes from 食べさせます → 食べさせています, and 私に is the one being made to eat.\n母は私に野菜を食べさせられています is the passive of the causative and reverses the roles. 母は私に野菜を食べさせませんでした is the polite past negative, so she is not doing it.",
                ),
                ChallengeEntity(
                    id = 62027, lessonId = 410, type = ChallengeType.CONJUGATE,
                    question = "Which plain causative progressive of 食べる means 'is making (someone) eat'?",
                    audioSrc = "asset:///audio/ja/g_causative_tabesaserteiru.ogg",
                    orderIndex = 1,
                    grammaticalFocus = "ja.causative_teiru",
                    ruleText = "The progressive on a causative is させる + ている: 食べる → 食べさせる → 食べさせている. The せ stays where it is and ている goes on the end.\n食べられている is the progressive of the plain passive 食べられる, so the eater is being fed rather than made to eat. 食べさせられました is the polite past, so the making is over.",
                ),
                ChallengeEntity(
                    id = 62028, lessonId = 410, type = ChallengeType.FILL_BLANK,
                    question = "先生が生徒に宿題を___。",
                    audioSrc = "asset:///audio/ja/g_causative_shukudai_dasemashita.ogg",
                    orderIndex = 2,
                    grammaticalFocus = "ja.causative_teiru",
                    acceptedAnswers = "出させました|ださせました",
                    ruleText = "出す is a godan verb, so its causative is 出させる and the polite past is 出させました: 先生が生徒に宿題を出させました, with 生徒に marking the one being made to do it.\n出します is the plain polite form and leaves the students handing in their own homework. 出されませんでした is the polite past *negative* of the passive, so it says the homework was not handed in.",
                ),
                ChallengeEntity(
                    id = 62029, lessonId = 410, type = ChallengeType.MATCH_PAIRS,
                    question = "Match the passive with the causative of the same verb",
                    orderIndex = 3,
                ),
                ChallengeEntity(
                    id = 62030, lessonId = 410, type = ChallengeType.SELECT,
                    question = "How do you say 'My father made me clean the room'?",
                    audioSrc = "asset:///audio/ja/g_causative_chichi_souji.ogg",
                    orderIndex = 4,
                    grammaticalFocus = "ja.causative_teiru",
                    ruleText = "父に部屋を掃除させました puts 父に in the slot of the one doing the making and 部屋を in the slot of the one made to clean: 父 + に + 部屋 + を + 掃除させました.\n父に部屋を掃除されます turns the causative back into a passive — 'the room is cleaned by my father' — with nobody doing it to anybody. 父に部屋を掃除します is the plain polite, so the father is doing the cleaning himself.",
                ),
                ChallengeEntity(
                    id = 61104, lessonId = 410, type = ChallengeType.LISTEN,
                    question = "Tap what you hear",
                    audioSrc = "asset:///audio/ja/g_causative_gyunyu_nomaseru.ogg",
                    orderIndex = 5,
                    grammaticalFocus = "ja.causative_teiru",
                    ruleText = "母が私に牛乳を飲ませています is the causative progressive of 飲む: 飲ませます → 飲ませています, and 私に is the one being made to drink.\n母が私に牛乳を飲ませられています is the passive of the causative. 母が私に牛乳を飲ませました is the polite past, so the making is over rather than going on.",
                ),

                // --- Lesson 31: The Noun With a Story (修飾節) ------------------
                ChallengeEntity(
                    id = 62031, lessonId = 411, type = ChallengeType.SELECT,
                    question = "Which one means 'students who study Japanese'?",
                    audioSrc = "asset:///audio/ja/g_rel_benkyousuru_gakusei.ogg",
                    orderIndex = 0,
                    grammaticalFocus = "ja.relative_clause",
                    ruleText = "A relative clause is a verb in the plain form with の standing where the noun goes, so 日本語を勉強する学生 is 日本語を勉強する + 学生. の is what turns the clause into a noun.\n日本語を勉強した学生 is the た-form and points at students who have finished. 日本語を勉強しています学生 is a ます form, which a clause in front of a noun never takes.",
                ),
                ChallengeEntity(
                    id = 62032, lessonId = 411, type = ChallengeType.CONJUGATE,
                    question = "Which plain relative clause means 'the friend who went to Kyoto'?",
                    audioSrc = "asset:///audio/ja/g_rel_kyouto_itta_tomodachi.ogg",
                    orderIndex = 1,
                    grammaticalFocus = "ja.relative_clause",
                    ruleText = "A た-form in front of a noun points at something finished, so 京都に行った友達 is 行った + 友達, and 京都 takes に because 行く is a 移動 verb with no object.\n京都に行きます友達 is the polite present, so it points at a friend who always goes. 京都に行っている友達 is the progressive, so it points at a friend who is there right now.",
                ),
                ChallengeEntity(
                    id = 62033, lessonId = 411, type = ChallengeType.FILL_BLANK,
                    question = "日本語を___人は親切です。",
                    audioSrc = "asset:///audio/ja/g_rel_nihongo_hanaseru_hito.ogg",
                    orderIndex = 2,
                    grammaticalFocus = "ja.relative_clause",
                    acceptedAnswers = "話せる人|はなせるひと",
                    ruleText = "A clause in front of a noun ends in the plain form and 人 is what the clause describes, so 日本語を話せる人 is 日本語を話せる + 人.\n話しています人 is a ます form inside a relative clause. 話した人 is the た-form, so it counts the people who have already finished speaking.",
                ),
                ChallengeEntity(
                    id = 62034, lessonId = 411, type = ChallengeType.WORD_BANK,
                    question = "Assemble: 'the book I read yesterday'",
                    orderIndex = 3,
                    grammaticalFocus = "ja.relative_clause",
                    ruleText = "A past relative clause is a た-form plus の, so きのう読んだ本 is きのう + 読んだ + 本.\nきのう読んでいる本 is the ている form and points at a book being read right now. きのう読めた本 is the potential, 'the book I was able to read', which says nothing about the reading being the thing done.",
                ),
                ChallengeEntity(
                    id = 62035, lessonId = 411, type = ChallengeType.MATCH_PAIRS,
                    question = "Match the relative clause to the noun it describes",
                    orderIndex = 4,
                ),
                ChallengeEntity(
                    id = 61105, lessonId = 411, type = ChallengeType.LISTEN,
                    question = "Tap what you hear",
                    audioSrc = "asset:///audio/ja/g_rel_nihongo_hanaseru_sannin.ogg",
                    orderIndex = 5,
                    grammaticalFocus = "ja.relative_clause",
                    ruleText = "日本語を話せる人は三人です puts a plain-form potential straight in front of 人, and は picks 人 up as the topic of 三人です.\n日本語を話しています人は三人です is a ます form inside a relative clause. 日本語を話した人は三人です is the た-form, so it counts the people who have finished speaking.",
                ),

                // --- Lesson 32: Who's In The Clause (ている / ない / の) ---------
                ChallengeEntity(
                    id = 62036, lessonId = 412, type = ChallengeType.SELECT,
                    question = "Which one means 'the shop that is shut right now'?",
                    audioSrc = "asset:///audio/ja/g_rel_ima_shimatteiru_mise.ogg",
                    orderIndex = 0,
                    grammaticalFocus = "ja.relative_clause",
                    ruleText = "A ている-form in front of a noun describes a state that is on-going at the moment, so 今閉まっている店 is 今 + 閉まっている + 店.\n今閉まった店 is the た-form and points at a shop that has finished shutting. 今閉まります店 is the polite present, so it points at a shop that shuts at this time every day.",
                ),
                ChallengeEntity(
                    id = 62037, lessonId = 412, type = ChallengeType.CONJUGATE,
                    question = "Which relative clause means 'the person who cannot read kanji'?",
                    audioSrc = "asset:///audio/ja/g_rel_kanji_o_yomenai_hito.ogg",
                    orderIndex = 1,
                    grammaticalFocus = "ja.relative_clause",
                    ruleText = "A ない-form in front of a noun says what the noun does not do, so 漢字を読めない人 is 漢字を + 読めない + 人, and 読めない is the plain negative of the potential よめる.\n漢字を読んだ人 is the た-form and points at a person who has finished reading. 漢字を読む人 is the plain positive, so it describes readers. And when the clause's subject is also the sentence topic it takes が, not は: 日本語が話せる人.",
                ),
                ChallengeEntity(
                    id = 62038, lessonId = 412, type = ChallengeType.FILL_BLANK,
                    question = "辞書を___人は少ないです。",
                    audioSrc = "asset:///audio/ja/g_rel_jisho_tsukawanai.ogg",
                    orderIndex = 2,
                    grammaticalFocus = "ja.relative_clause",
                    acceptedAnswers = "使わない人|つかわないひと",
                    ruleText = "辞書を + 使わない + 人 is the whole clause and 人は the subject 少ないです then takes — の is what joins the clause to 人, so nothing is left for は to do inside it.\n使いません人 is a ます form inside a relative clause. 使った人 is the た-form, so it counts the people who used a dictionary and put it away. 使わない人 is the plain ない-form, which is the only one of the three that describes what the person does not do.",
                ),
                ChallengeEntity(
                    id = 62039, lessonId = 412, type = ChallengeType.SELECT,
                    question = "Which one means 'the Japanese book that is easy to read'?",
                    audioSrc = "asset:///audio/ja/g_rel_yomiyasui_nihongo_no_hon.ogg",
                    orderIndex = 3,
                    grammaticalFocus = "ja.relative_clause",
                    ruleText = "An い-adjective in front of a noun keeps い — it never takes く — so 読みやすい日本語の本 is 読みやすい + 日本語の本, and の attaches the whole noun phrase to the last noun.\n読みやすいのは日本語の本 puts は inside the clause, which is where a topic particle cannot go — は belongs to the sentence, の belongs to the clause. 読みやすく日本語の本 uses the く-adverbial form, which cannot modify a noun.",
                ),
                ChallengeEntity(
                    id = 62040, lessonId = 412, type = ChallengeType.WORD_BANK,
                    question = "Assemble: 'the woman in the red coat'",
                    orderIndex = 4,
                    grammaticalFocus = "ja.relative_clause",
                    ruleText = "The clause goes in front of the noun it describes and の binds them, so 赤いコートを着ている女性 is 赤い + コートを着ている + 女性.\n赤いコートを着る女性 is the plain form, which describes what she does generally rather than what she has on right now. 赤いコートの女性 puts の straight after コート, which claims the coat itself owns someone.",
                ),
                ChallengeEntity(
                    id = 61106, lessonId = 412, type = ChallengeType.LISTEN,
                    question = "Tap what you hear",
                    audioSrc = "asset:///audio/ja/g_rel_asoko_ni_iru_ryugakusei.ogg",
                    orderIndex = 5,
                    grammaticalFocus = "ja.relative_clause",
                    ruleText = "あそこにいる人は留学生です is a plain-form いる clause in front of 人, with は picking 人 up as the topic of 留学生です.\nあそこにいた人は留学生です is the た-form, so it counts the people who were there and are not any more. あそこにいます人は留学生です is a ます form inside a relative clause.",
                ),
            ),
            options = listOf(
                ChallengeOptionEntity(id = 6300401, challengeId = 62020, text = "食べさせる", romaji = "tabesaseru", correct = true, audioSrc = "asset:///audio/ja/g_causative_tabesaseru.ogg"),
                ChallengeOptionEntity(id = 6300402, challengeId = 62020, text = "食べさせられる", romaji = "tabesaserareru", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6300403, challengeId = 62020, text = "食べられました", romaji = "taberaremashita", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 6300421, challengeId = 62021, text = "読ませる", romaji = "yomaseru", correct = true, audioSrc = "asset:///audio/ja/g_causative_yomaseru.ogg"),
                ChallengeOptionEntity(id = 6300422, challengeId = 62021, text = "読まれる", romaji = "yomareru", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6300423, challengeId = 62021, text = "読ませます", romaji = "yomasemasu", correct = false, errorTag = "WRONG_REGISTER"),

                ChallengeOptionEntity(id = 6300441, challengeId = 62022, text = "食べさせました", romaji = "tabesasemashita", correct = true, audioSrc = "asset:///audio/ja/g_causative_tabesasemashita.ogg"),
                ChallengeOptionEntity(id = 6300442, challengeId = 62022, text = "食べられました", romaji = "taberaremashita", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 6300443, challengeId = 62022, text = "食べました", romaji = "tabemashita", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 6300461, challengeId = 62023, text = "コーチは毎朝チームを走らせます", romaji = "koochi wa mainichi chiimu o hashirasemasu", correct = true),
                ChallengeOptionEntity(id = 6300462, challengeId = 62023, text = "コーチは毎朝チームが走ります", romaji = "koochi wa mainichi chiimu ga hashirimasu", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6300463, challengeId = 62023, text = "コーチは毎朝チームの走らせます", romaji = "koochi wa mainichi chiimu no hashirasemasu", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 6300481, challengeId = 62024, text = "させる", romaji = "saseru", correct = true, audioSrc = "asset:///audio/ja/g_causative_saseru.ogg"),
                ChallengeOptionEntity(id = 6300482, challengeId = 62024, text = "せられる", romaji = "serareru", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6300483, challengeId = 62024, text = "される", romaji = "sareru", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 6300501, challengeId = 62025, text = "来させる", romaji = "kurasaseru", correct = true, audioSrc = "asset:///audio/ja/g_causative_kuramaseru.ogg"),
                ChallengeOptionEntity(id = 6300502, challengeId = 62025, text = "来られる", romaji = "koreru", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6300503, challengeId = 62025, text = "来かせました", romaji = "kurasemashita", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 6310031, challengeId = 61103, text = "先生は生徒に静かにさせました", romaji = "sensei wa seito ni shizuka ni sasemashita", correct = true),
                ChallengeOptionEntity(id = 6310032, challengeId = 61103, text = "先生は生徒に静かにされました", romaji = "sensei wa seito ni shizuka ni sare mashita", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6310033, challengeId = 61103, text = "先生は生徒に静かにさせません", romaji = "sensei wa seito ni shizuka ni sasemasen", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 6300521, challengeId = 62026, text = "母は私に野菜を食べさせています", romaji = "haha wa watashi ni yasai o tabesaseteimasu", correct = true, audioSrc = "asset:///audio/ja/g_causative_tabesaseteimasu.ogg"),
                ChallengeOptionEntity(id = 6300522, challengeId = 62026, text = "母は私に野菜を食べさせられています", romaji = "haha wa watashi ni yasai o tabesasareteimasu", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6300523, challengeId = 62026, text = "母は私に野菜を食べさせませんでした", romaji = "haha wa watashi ni yasai o tabesasemasen deshita", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 6300541, challengeId = 62027, text = "食べさせている", romaji = "tabesaseteiru", correct = true, audioSrc = "asset:///audio/ja/g_causative_tabesaserteiru.ogg"),
                ChallengeOptionEntity(id = 6300542, challengeId = 62027, text = "食べられている", romaji = "taberareteiru", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6300543, challengeId = 62027, text = "食べさせられました", romaji = "tabesaseraremashita", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 6300561, challengeId = 62028, text = "出させました", romaji = "dasasemashita", correct = true, audioSrc = "asset:///audio/ja/g_causative_shukudai_dasemashita.ogg"),
                ChallengeOptionEntity(id = 6300562, challengeId = 62028, text = "出します", romaji = "dashimasu", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6300563, challengeId = 62028, text = "出されませんでした", romaji = "dasaremasen deshita", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 6300581, challengeId = 62029, text = "休まれる", romaji = "yasumareru", correct = true),
                ChallengeOptionEntity(id = 6300582, challengeId = 62029, text = "休ませる", romaji = "yasumaseru", correct = true, audioSrc = "asset:///audio/ja/g_causative_yasumaseru.ogg"),
                ChallengeOptionEntity(id = 6300583, challengeId = 62029, text = "待たれる", romaji = "matareru", correct = true),
                ChallengeOptionEntity(id = 6300584, challengeId = 62029, text = "待たせる", romaji = "matasaseru", correct = true, audioSrc = "asset:///audio/ja/g_causative_matasaseru.ogg"),
                ChallengeOptionEntity(id = 6300585, challengeId = 62029, text = "飲まれる", romaji = "nomareru", correct = true),
                ChallengeOptionEntity(id = 6300586, challengeId = 62029, text = "飲ませる", romaji = "nomasaseru", correct = true, audioSrc = "asset:///audio/ja/g_causative_nomasaseru.ogg"),
                ChallengeOptionEntity(id = 6300587, challengeId = 62029, text = "書かれる", romaji = "kakareru", correct = true),
                ChallengeOptionEntity(id = 6300588, challengeId = 62029, text = "書かせる", romaji = "kakaseru", correct = true),

                ChallengeOptionEntity(id = 6300601, challengeId = 62030, text = "父に部屋を掃除させました", romaji = "chichi ni heya o souji sasemashita", correct = true, audioSrc = "asset:///audio/ja/g_causative_chichi_souji.ogg"),
                ChallengeOptionEntity(id = 6300602, challengeId = 62030, text = "父に部屋を掃除されます", romaji = "chichi ni heya o souji saremasu", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6300603, challengeId = 62030, text = "父に部屋を掃除します", romaji = "chichi ni heya o souji shimasu", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 6310041, challengeId = 61104, text = "母が私に牛乳を飲ませています", romaji = "haha ga watashi ni gyunyu o nomaseteimasu", correct = true),
                ChallengeOptionEntity(id = 6310042, challengeId = 61104, text = "母が私に牛乳を飲ませられています", romaji = "haha ga watashi ni gyunyu o nomasareteimasu", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6310043, challengeId = 61104, text = "母が私に牛乳を飲ませました", romaji = "haha ga watashi ni gyunyu o nomasemashita", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 6300621, challengeId = 62031, text = "日本語を勉強する学生", romaji = "nihongo o benkyou suru gakusei", correct = true, audioSrc = "asset:///audio/ja/g_rel_benkyousuru_gakusei.ogg"),
                ChallengeOptionEntity(id = 6300622, challengeId = 62031, text = "日本語を勉強した学生", romaji = "nihongo o benkyou shita gakusei", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 6300623, challengeId = 62031, text = "日本語を勉強しています学生", romaji = "nihongo o benkyou shiteimasu gakusei", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 6300641, challengeId = 62032, text = "京都に行った友達", romaji = "kyouto ni itta tomodachi", correct = true, audioSrc = "asset:///audio/ja/g_rel_kyouto_itta_tomodachi.ogg"),
                ChallengeOptionEntity(id = 6300642, challengeId = 62032, text = "京都に行きます友達", romaji = "kyouto ni ikimasu tomodachi", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 6300643, challengeId = 62032, text = "京都に行っている友達", romaji = "kyouto ni itteiru tomodachi", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 6300661, challengeId = 62033, text = "話せる人", romaji = "hanaseru hito", correct = true, audioSrc = "asset:///audio/ja/g_rel_nihongo_hanaseru_hito.ogg"),
                ChallengeOptionEntity(id = 6300662, challengeId = 62033, text = "話しています人", romaji = "hanashiteimasu hito", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6300663, challengeId = 62033, text = "話した人", romaji = "hanashita hito", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 6300681, challengeId = 62034, text = "きのう読んだ本", romaji = "kinou yonda hon", correct = true),
                ChallengeOptionEntity(id = 6300682, challengeId = 62034, text = "きのう読んでいる本", romaji = "kinou yondeiru hon", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6300683, challengeId = 62034, text = "きのう読めた本", romaji = "kinou yometahon", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 6300701, challengeId = 62035, text = "走っている", romaji = "hashitteiru", correct = true, audioSrc = "asset:///audio/ja/g_rel_hashiratteiru.ogg"),
                ChallengeOptionEntity(id = 6300702, challengeId = 62035, text = "ランナー (the runner)", correct = true),
                ChallengeOptionEntity(id = 6300703, challengeId = 62035, text = "走った", romaji = "hashitta", correct = true, audioSrc = "asset:///audio/ja/g_rel_hashitta.ogg"),
                ChallengeOptionEntity(id = 6300704, challengeId = 62035, text = "記録 (the record)", correct = true),
                ChallengeOptionEntity(id = 6300705, challengeId = 62035, text = "歩いている", romaji = "aruiteiru", correct = true, audioSrc = "asset:///audio/ja/g_rel_aruiteru.ogg"),
                ChallengeOptionEntity(id = 6300706, challengeId = 62035, text = "人 (the person)", correct = true),
                ChallengeOptionEntity(id = 6300707, challengeId = 62035, text = "読んだ", romaji = "yonda", correct = true),
                ChallengeOptionEntity(id = 6300708, challengeId = 62035, text = "本 (the book)", correct = true),

                ChallengeOptionEntity(id = 6310051, challengeId = 61105, text = "日本語を話せる人は三人です", romaji = "nihongo o hanaseru hito wa sannin desu", correct = true),
                ChallengeOptionEntity(id = 6310052, challengeId = 61105, text = "日本語を話しています人は三人です", romaji = "nihongo o hanashiteimasu hito wa sannin desu", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6310053, challengeId = 61105, text = "日本語を話した人は三人です", romaji = "nihongo o hanashita hito wa sannin desu", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 6300721, challengeId = 62036, text = "今閉まっている店", romaji = "ima shimatteiru mise", correct = true, audioSrc = "asset:///audio/ja/g_rel_ima_shimatteiru_mise.ogg"),
                ChallengeOptionEntity(id = 6300722, challengeId = 62036, text = "今閉まった店", romaji = "ima shimatta mise", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 6300723, challengeId = 62036, text = "今閉まります店", romaji = "ima shimarimasu mise", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 6300741, challengeId = 62037, text = "漢字を読めない人", romaji = "kanji o yomenai hito", correct = true, audioSrc = "asset:///audio/ja/g_rel_kanji_o_yomenai_hito.ogg"),
                ChallengeOptionEntity(id = 6300742, challengeId = 62037, text = "漢字を読んだ人", romaji = "kanji o yonda hito", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 6300743, challengeId = 62037, text = "漢字を読む人", romaji = "kanji o yomu hito", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 6300761, challengeId = 62038, text = "使わない人", romaji = "tsukawanai hito", correct = true, audioSrc = "asset:///audio/ja/g_rel_jisho_tsukawanai.ogg"),
                ChallengeOptionEntity(id = 6300762, challengeId = 62038, text = "使いません人", romaji = "tsukaimasu hito", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6300763, challengeId = 62038, text = "使った人", romaji = "tsukatta hito", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 6300781, challengeId = 62039, text = "読みやすい日本語の本", romaji = "yomiyasui nihongo no hon", correct = true, audioSrc = "asset:///audio/ja/g_rel_yomiyasui_nihongo_no_hon.ogg"),
                ChallengeOptionEntity(id = 6300782, challengeId = 62039, text = "読みやすいのは日本語の本", romaji = "yomiyasui no wa nihongo no hon", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6300783, challengeId = 62039, text = "読みやすく日本語の本", romaji = "yomiyasuku nihongo no hon", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 6300801, challengeId = 62040, text = "赤いコートを着ている女性", romaji = "akai kooto o kiteiru josei", correct = true),
                ChallengeOptionEntity(id = 6300802, challengeId = 62040, text = "赤いコートを着る女性", romaji = "akai kooto o kiru josei", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6300803, challengeId = 62040, text = "赤いコートの女性", romaji = "akai kooto no josei", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 6310061, challengeId = 61106, text = "あそこにいる人は留学生です", romaji = "asoko ni iru hito wa ryugakusei desu", correct = true),
                ChallengeOptionEntity(id = 6310062, challengeId = 61106, text = "あそこにいた人は留学生です", romaji = "asoko ni ita hito wa ryugakusei desu", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 6310063, challengeId = 61106, text = "あそこにいます人は留学生です", romaji = "asoko ni imasu hito wa ryugakusei desu", correct = false, errorTag = "WRONG_FORM"),
            ),
        ),
    )
}

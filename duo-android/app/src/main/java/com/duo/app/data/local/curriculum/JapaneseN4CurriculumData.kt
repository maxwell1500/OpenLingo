package com.duo.app.data.local.curriculum

import com.duo.app.data.local.entities.ChallengeEntity
import com.duo.app.data.local.entities.ChallengeOptionEntity
import com.duo.app.data.local.entities.LessonEntity
import com.duo.app.data.local.entities.UnitEntity
import com.duo.app.data.local.models.ChallengeType

/**
 * Units 11-16 of the Japanese course, the N4 grammar the roadmap names and
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
 * **Audio.** 165 distinct clips are wired from these units, 46 from units 11-12,
 * 43 from units 13-14, 43 from units 15-16 and 33 from units 17-18. A SELECT
 * challenge and its correct option share a clip; a CONJUGATE speaks the target
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

 * **Units 17-18 (ids 46-47)** add the themed everyday vocabulary — the shop,
 * prices and the home in unit 46; the train, work, the week and the weather in
 * unit 47 — alongside three N4 modality frames the grammar arc had named but
 * never taught: 〜なければなりません (`ja.necessity`), 〜てもいい
 * (`ja.permission`) and 〜たことがあります (`ja.experience`). ようにする /
 * ことにする is still untaught and is listed as missing in the roadmap.
 *
 * **Two known limitations of the bundled voice, recorded rather than hidden.**
 *
 *  1. The Kokoro `jf_alpha` voice renders つ as the phoneme for `i`, not `u`, so
 *     靴 in `ookii.ogg` (この靴は___。) sounds like "k-u-ts-i". This is the
 *     voice's own fixed mapping, not the text: a standalone つ and すし behave
 *     identically, and the clip has a normal /k/-release and a real [ts]
 *     affricate before the vowel, so what is wrong is the vowel's quality and
 *     nothing else. It is a **pre-existing limitation of the shipped audio** —
 *     `atsui.ogg` (あつい) and `matta.ogg` already carry the same つ — and not a
 *     regression introduced by this unit. A TTS voice is not hotfixed here.
 *  2. The `unidic` backend misreads 払えます, 降る and 晴れる, so units 17-18
 *     carry **no synthesised clip for 降る or 晴れる**, and 払える appears only
 *     as untargeted 払う (a clip the word is safe in). The three pre-existing
 *     clips that do speak 降 or 晴 (`ja_cond_ame_ga_futtara_*`,
 *     `ashita_wa_hareru_to_omoimasu`) predate this unit and are left as they are.
 *
 * Id layout inside this file, disjoint from every other curriculum file:
 *   - units `40`-`47`        (Spanish has taken 30-37)
 *   - lessons `400`-`405` for units 40-41, `406`-`412` for units 42-43,
 *     `800`-`805` for units 44-45, `850`-`855` for units 46-47
 *   - challenges `60000`-`60043` plus the `LISTEN` block `61000`-`61008` (units 40-41),
 *     then `62000`-`62040` plus the `LISTEN` block `61100`-`61106` (units 42-43), then
 *     `64000`-`64026` plus the `LISTEN` block `64100`-`64105` (units 44-45), then
 *     `65000`-`65306` (units 46-47)
 *   - options `600001`-`600184` (600157 is not used; every id below it is taken)
 *     plus the `LISTEN` block `6100001`-`6100027`, then options `6300001`-`6300803`
 *     plus the `LISTEN` block `6310001`-`6310063`, then options `6400001` up and the
 *     `LISTEN` block `6410001` up (units 44-45), then options `6500001`-`6500227`
 *     and `7500001`-`7500227` (units 46-47; the `7xxxxxx` block exists because
 *     `6500001`+ had already been spent by the lessons above)
 *
 * Lessons 850-855 were authored as 900-905, which collided with the Spanish
 * themed units' lessons in `SpanishVocabularyCurriculumData`. They were moved
 * here because 806-899 was the one block free corpus-wide, and the renumber
 * moved every referencing `ChallengeEntity(lessonId = ...)` with it. The
 * challenge id ranges were left alone: they never collided.
 *
 * The six held-out items are 60006, 60014, 60021, 60028, 60036 and 60043, all in
 * units 40-41. Units 42-45 hold nothing out: the N4 pool is fixed at 10 and a held-out
 * item in unit 42 or later would be selected by no checkpoint. They are
 * seeded like everything else and carry `heldOut = true`, so the lesson path skips
 * them and a checkpoint is the only thing that reaches them. No `LISTEN` item is
 * held out: the held-out pool exists to withhold a *taught form*, and a
 * listening round is the one mechanic a checkpoint cannot substitute for.
 */
object JapaneseN4CurriculumData {

    // JAPANESE JLPT N4 (Units 11 - 16)
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
                ChallengeOptionEntity(id = 6300086, challengeId = 62004, text = "見られる", romaji = "mirareru", correct = true, audioSrc = "asset:///audio/ja/g_passive_mirerareru.ogg"),
                ChallengeOptionEntity(id = 6300087, challengeId = 62004, text = "話す", romaji = "hanasu", correct = true),
                ChallengeOptionEntity(id = 6300088, challengeId = 62004, text = "話される", romaji = "hanasareru", correct = true, audioSrc = "asset:///audio/ja/g_passive_hanasareru.ogg"),
                ChallengeOptionEntity(id = 6300089, challengeId = 62004, text = "食べる", romaji = "taberu", correct = true),
                ChallengeOptionEntity(id = 6300090, challengeId = 62004, text = "食べられる", romaji = "taberareru", correct = true, audioSrc = "asset:///audio/ja/g_passive_taberareta.ogg"),
                ChallengeOptionEntity(id = 6300091, challengeId = 62004, text = "書く", romaji = "kaku", correct = true),
                ChallengeOptionEntity(id = 6300092, challengeId = 62004, text = "書かれる", romaji = "kakareru", correct = true, audioSrc = "asset:///audio/ja/g_passive_kakareru.ogg"),

                ChallengeOptionEntity(id = 6300101, challengeId = 62005, text = "母に叱られました", romaji = "haha ni shikararemashita", correct = true, audioSrc = "asset:///audio/ja/g_passive_haha_ni_shikarareta.ogg"),
                ChallengeOptionEntity(id = 6300102, challengeId = 62005, text = "母を叱られました", romaji = "haha o shikararemashita", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6300103, challengeId = 62005, text = "母が叱られました", romaji = "haha ga shikararemashita", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 6300121, challengeId = 62006, text = "見られる", romaji = "mirareru", correct = true, audioSrc = "asset:///audio/ja/g_passive_mirerareru.ogg"),
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
                ChallengeOptionEntity(id = 6300285, challengeId = 62014, text = "見られる", romaji = "mirareru", correct = true),
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
                ChallengeOptionEntity(id = 6300584, challengeId = 62029, text = "待たせる", romaji = "mataseru", correct = true, audioSrc = "asset:///audio/ja/g_causative_matasaseru.ogg"),
                ChallengeOptionEntity(id = 6300585, challengeId = 62029, text = "飲まれる", romaji = "nomareru", correct = true),
                ChallengeOptionEntity(id = 6300586, challengeId = 62029, text = "飲ませる", romaji = "nomaseru", correct = true, audioSrc = "asset:///audio/ja/g_causative_nomasaseru.ogg"),
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
        // =====================================================================
        // Units 15-16: the four areas the roadmap said the Japanese corpus
        // never reached — 条件, 意向形, 敬語, and られる as a system rather
        // than as four confrontations.
        //
        // Id layout for this appended block, disjoint from units 11-14 above
        // and from every other curriculum file:
        //   - units `44`, `45`
        //   - lessons `800`-`802` (unit 44), `803`-`805` (unit 45)
        //   - challenges `64000`-`64026` plus the `LISTEN` round `64100`-`64102`
        //     at the end of lesson 802, and `64103`-`64105` at the end of 805
        //   - options `6400001`-`6400123` plus the `LISTEN` block
        //     `6410001`-`6410018`
        //
        // **A conditional is a choice between four forms, not a rule, and this
        // block does not pretend it is one.** They are not interchangeable:
        //   - 〜と states a general or inevitable outcome — 猫が魚を食べると、
        //     猫が魚を食べる — and that is why it will not carry a 意志 or a
        //     依頼 in the consequent. と is named as the rejected distractor in
        //     64000 and 64002 and is not given a focus of its own: the roadmap's
        //     gap was たら, なら and ば, which appeared zero times.
        //   - 〜たら is the plain past plus たら, and it is the most versatile
        //     and by far the most frequent of the three taught here — it carries
        //     a request, a proposal and a 意志 without trouble. Its た is the
        //     仮定形 and not a past: 明日雨が降ったら is a condition about
        //     tomorrow, and 64000's rule text says so.
        //   - 〜なら is the 辞書形 plus なら, so the clause carries no tense at
        //     all — 行くなら, never 行ったなら — and it supposes a case, which
        //     is why the 依頼 or 提案 after it is what the speaker wants done.
        //   - 〜ば is the written and formal one. う-verbs and る-verbs drop
        //     the る, う-verbs take the 音便, and ば never takes ます, so ばます
        //     is not a shape. 64009 names 買らば, which is a real ending for
        //     来れば and すれば and is wrong for 買う.
        // **And what this block does not claim.** It is not the whole system:
        // 〜と's 一方 and 発見 uses, たら's 一方 and 逆接 uses, と's 過去形 and
        // れば's restriction on a negative consequent are all outside it, and
        // 64005 and 64008 say out loud that たら and なら can say the same
        // thing about the same case — what separates them there is the tense
        // of the clause and the register, not the meaning. Where the textbook
        // rule is a restriction rather than a form, the rule text gives the
        // register and leaves the restriction unstated instead of inventing it.
        //
        // **Volition is 意向形 in the plain form and ましょう in polite.** What
        // a learner builds is the ます-stem plus ましょう, and 64013 keeps
        // でしょう away from it on purpose: でしょう is 推量, the speaker's
        // guess, and it attaches to a plain form, so 彼は来るでしょう and
        // 彼は来ましょう are different sentences and only one is about
        // probability.
        //
        // **Keigo is opened here, not taught.** The corpus had no honorific or
        // humble register anywhere, so this block adds one small set of each
        // plus the ascript お〜になる, which is the part a learner meets first
        // in real Japanese. 64015 states the direction — 尊敬語 lifts the other
        // person's action, 謙譲語 lowers the speaker's own — and 64018, 64019
        // and 64103 are built on the two ways to get that backwards. What is
        // deliberately absent: the split of 謙譲語 into the ます humble and the
        // plain いたす humble, 尊敬 and 謙譲 used together the way business
        // correspondence uses them, and 二重謙譲. 敬語 sits above what most
        // JLPT syllabuses place at N4, so this is a recognition set and the
        // rule texts say as much rather than implying the course teaches
        // business Japanese. It is two lessons, not a system.
        //
        // **られる as a rule, not four confrontations.** Units 13-14 pited two
        // readings against each other in four items; this block states the
        // system those four sit inside. A godan verb never collides — 読める
        // against 読まれる (64021) and 作られる against 作れる (64022). An
        // ichidan verb always does, because られる is built the same way for
        // both, so 見られる and 食べられる are one string with two meanings
        // and only the subject slot and the particles tell them apart (64023,
        // 64105). The third reading, 得る, is a small closed set — 得る, 求める,
        // 採る — and 64024 gives it one item rather than a lesson. And する is
        // the one verb whose potential is not られる at all: できる (64026).
        //
        // **Audio.** 43 new clips, all wired and all referenced: 28 on a
        // challenge and 38 on the option that speaks the same text, and the two
        // counts overlap because a clip can do both. A `MATCH_PAIRS` challenge
        // gets no challenge clip — its prompt is the instruction, not a
        // sentence — so its clips hang off the derived half of each pair. Every
        // `LISTEN` challenge carries a clip and none of its options does, the
        // way `ExerciseScreen` needs, and no `WRONG_*` distractor anywhere in
        // this block carries one.
        //
        // **Nothing here is unanswerable by ear.** Every LISTEN contrast was
        // measured rather than assumed, and each one differs from the correct
        // answer in at least two morae: 64100's 降るなら sits 4 morae from
        // 降ると and 8 from 降りましたら, 64103's お帰りになりました is 8 morae
        // from 帰りました and 17 from お帰りください, and 64105's されています
        // is 3 morae from されます and 6 from していません. The られる pair is
        // the real risk and it is excluded the way unit 13 excluded it: 64105
        // contrasts the passive progressive with a plain form and a negative,
        // never される against させる, and 書かれる and 書ける sit one mora apart,
        // so 64021 puts them in front of the learner as text to be read rather
        // than as audio to be told apart, exactly as unit 13 did with 書かれる.
        // The dakuten, handakuten, long-vowel and geminate marks were read in
        // the IPA the acoustic model is actually handed: ɸɯʔtaɾa for 降った,
        // ɾeba for れば, naɾa for なら, kaeba for 買えば, maɕoː and deɕoː for
        // ましょう and でしょう, keːjakɯɕo for 契約, ɯkaɡai for 伺, moːɕiaɡeɾɯ
        // for 申し上げる, eiɡa for 映画, soːʥi for 掃除, and a bare e for 得 in
        // 得られる.
        //
        // No item in this block is held out: the held-out pool is fixed at
        // A2 4, B1 8, N4 10 and `held-out items are reachable by a checkpoint`
        // scopes the checkpoints to units 10-33 plus 40-43, so a held-out item
        // in unit 44 would be filtered out of every lesson and reachable by
        // nothing.
        UnitPayload(
            unit = UnitEntity(
                id = 44,
                courseId = 2,
                title = "Unit 15: Conditionals & Proposals",
                description = "Say 'if' the way Japanese actually divides it: 〜たら, 〜なら and 〜ば, what 〜と refuses to do, and propose a plan with 〜ましょう",
                orderIndex = 14,
            ),
            lessons = listOf(
                LessonEntity(id = 800, unitId = 44, title = "Lesson 33: If It Happens", orderIndex = 0),
                LessonEntity(id = 801, unitId = 44, title = "Lesson 34: If That's The Case", orderIndex = 1),
                LessonEntity(id = 802, unitId = 44, title = "Lesson 35: Let's", orderIndex = 2),
            ),
            challenges = listOf(
                // --- Lesson 33: 〜たら ------------------------------------------
                ChallengeEntity(
                    id = 64000, lessonId = 800, type = ChallengeType.SELECT,
                    question = "How do you say 'If it rains, please stay at home'?",
                    audioSrc = "asset:///audio/ja/ja_cond_ame_ga_futtara_ie_tekudasai.ogg",
                    orderIndex = 0,
                    grammaticalFocus = "ja.conditional_tara",
                    ruleText = "〜たら is the plain past plus たら — 降る → 降った → 降ったら — and it is the one condition that can carry a request, so 明日、雨が降ったら、家にいてください is a condition about tomorrow even though the た is the shape of a past.\n雨が降ると、家にいてください is the と condition, which states a general outcome and never takes a 依頼. 雨が降るたら is 辞書形 + たら, but たら is built on the た and not on the 辞書形. 雨が降りましたら is a real form, though ましたら is the formal business conditional and is the wrong register here.",
                ),
                ChallengeEntity(
                    id = 64001, lessonId = 800, type = ChallengeType.CONJUGATE,
                    question = "Which たら condition of 飲む means 'if (you) drink it'?",
                    audioSrc = "asset:///audio/ja/ja_cond_nondattara.ogg",
                    orderIndex = 1,
                    grammaticalFocus = "ja.conditional_tara",
                    ruleText = "A godan verb takes its plain past in front of たら: 飲む → 飲んだ → 飲んだら.\n飲むなら is 辞書形 + なら, so it is the なら condition and not たら. 飲めば is 飲む → 飲め + ば, the ば condition, which belongs to the written and formal register. 飲みたら puts a ます-stem in front of たら, and a ます form never carries a conditional ending.",
                ),
                ChallengeEntity(
                    id = 64002, lessonId = 800, type = ChallengeType.FILL_BLANK,
                    question = "雨が___、出かけません。",
                    audioSrc = "asset:///audio/ja/ja_cond_ame_ga_futtara_dekakemasen.ogg",
                    orderIndex = 2,
                    grammaticalFocus = "ja.conditional_tara",
                    acceptedAnswers = "降ったら|ふったら",
                    ruleText = "〜たら is the plain past of the verb, so 降る → 降った → 降ったら, and 出かけません is the result that follows it.\n雨が降ります is the polite present, so the rain is already falling. 雨が降ると is the と condition, which states a general outcome rather than one case. 雨が降るなら is the なら condition, and it is a different condition from the たら this item asks for.",
                ),
                ChallengeEntity(
                    id = 64003, lessonId = 800, type = ChallengeType.WORD_BANK,
                    question = "Assemble: 'If the work is finished, let's go home'",
                    orderIndex = 3,
                    grammaticalFocus = "ja.conditional_tara",
                    ruleText = "〜たら is the plain past of the verb, so 終わる → 終わった → 終わったら, and 帰りましょう is a proposal the finished work makes possible.\n仕事が変わるなら、帰りましょう is the なら condition, which puts the 辞書形 終わる in front of なら. 仕事が終われば、帰りましょう is the ば condition, which is the written and formal register. 仕事が終わって、帰りましょう is the て-form, a connective that links two actions rather than a condition that opens one.",
                ),
                ChallengeEntity(
                    id = 64004, lessonId = 800, type = ChallengeType.MATCH_PAIRS,
                    question = "Match the verb with its たら condition",
                    orderIndex = 4,
                ),

                // --- Lesson 34: 〜なら and 〜ば --------------------------------
                ChallengeEntity(
                    id = 64005, lessonId = 801, type = ChallengeType.SELECT,
                    question = "How do you say 'If you understand it, please tell me'?",
                    audioSrc = "asset:///audio/ja/ja_cond_wakaru_nara_oshietekudasai.ogg",
                    orderIndex = 0,
                    grammaticalFocus = "ja.conditional_nara",
                    ruleText = "〜なら is the 辞書形 plus なら — 分かる → 分かるなら — and it supposes a case rather than predicting one, so the 依頼 after it is what the speaker wants done.\n分かったなら、教えてください is the plain past in front of なら, but なら takes the 辞書形 and never the past. 分かるたびに、教えてください is たびに, which means every time and states a habit rather than a condition. 分かるなら、教えください hangs ください off the noun 教え, and ください takes the て-form, so the noun has to become 教えて.",
                ),
                ChallengeEntity(
                    id = 64006, lessonId = 801, type = ChallengeType.CONJUGATE,
                    question = "Which なら condition of 行く means 'if (you) go'?",
                    audioSrc = "asset:///audio/ja/ja_cond_iku_nara.ogg",
                    orderIndex = 1,
                    grammaticalFocus = "ja.conditional_nara",
                    ruleText = "〜なら takes the verb exactly as a dictionary lists it: 行く → 行くなら, and a なら clause carries no tense of its own.\n行ったら is the たら condition, which is built on the plain past 行った. 行きなら puts the ます-stem in the 辞書形 slot, but 行く ends in く, so its ます-stem is 行きます while its dictionary form is 行く. 行けば is 行く → 行け + ば, the ば condition, and ば is the written and formal register.",
                ),
                ChallengeEntity(
                    id = 64007, lessonId = 801, type = ChallengeType.FILL_BLANK,
                    question = "もし病気が___、学校を休みます。",
                    audioSrc = "asset:///audio/ja/ja_cond_byoki_ga_naorunara_gakkou_o_yasumimasu.ogg",
                    orderIndex = 2,
                    grammaticalFocus = "ja.conditional_nara",
                    acceptedAnswers = "治るなら|なおるなら",
                    ruleText = "〜なら takes the 辞書形, so 治る → 治るなら is the whole condition, and 学校を休みます is what the speaker will do if that turns out to be the case.\n治ったなら、学校を休みます is the plain past in front of なら, but なら takes the 辞書形 and never the past. 治りますなら、学校を休みます is a ます form in the 辞書形 slot, and a なら clause carries no tense of its own. 治るたびに、学校を休みます is たびに, which means every time and states a habit rather than a condition.",
                ),
                ChallengeEntity(
                    id = 64008, lessonId = 801, type = ChallengeType.SELECT,
                    question = "How do you say 'If you are busy, you don't have to come'?",
                    audioSrc = "asset:///audio/ja/ja_cond_isogashikereba_konakuteiidesu.ogg",
                    orderIndex = 3,
                    grammaticalFocus = "ja.conditional_ba",
                    ruleText = "An い-adjective makes ば by dropping the final い and adding れば: 忙しい → 忙しければ, and ば is the written and formal way of saying if.\n忙ししかったら、来なくていいです is たら, which says exactly the same thing and is what people actually use when they speak — the difference is register, not meaning. 忙しけら、来なくていいです has ら where the れば needs れば. 忙しいれば、来なくていいです keeps the い of 忙しい in front of れば, and the ば form is built without that い.",
                ),
                ChallengeEntity(
                    id = 64009, lessonId = 801, type = ChallengeType.CONJUGATE,
                    question = "Which ば condition of 買う means 'if (you) buy'?",
                    audioSrc = "asset:///audio/ja/ja_cond_kau_ba.ogg",
                    orderIndex = 4,
                    grammaticalFocus = "ja.conditional_ba",
                    ruleText = "A う-verb makes ば from its ます-stem plus れば, and that change from う to え is the 音便 the e-row verbs have: 買う → 買えば, 話す → 話せば, 待つ → 待てば.\n買たら keeps the た of たら where the れば belongs. 買らば is the shape every る-verb takes — 食べれば, 来れば, すれば — but 買う is a う-verb, so it takes the 音便 and comes out as 買えば. 買わなければ negates the condition and says if you don't buy, which is the other side of the question.",
                ),

                // --- Lesson 35: 〜ましょう and the LISTEN round ----------------
                ChallengeEntity(
                    id = 64010, lessonId = 802, type = ChallengeType.SELECT,
                    question = "How do you say 'Let's go to the park'?",
                    audioSrc = "asset:///audio/ja/ja_vol_koen_ni_ikimashou.ogg",
                    orderIndex = 0,
                    grammaticalFocus = "ja.volition_polite",
                    ruleText = "The polite volitional is the ます-stem plus ましょう: 行く → 行きます → 行きましょう, and it is a proposal, something the other person is free to refuse.\n公園に行こう is the plain 意向形, which is grammatical but the wrong register beside a group. 公園に行きました is the polite past, so the visit is already over. 公園に行こうましょう is the plain 意向形 行こう with the polite ましょう stuck onto it, and the polite form is the ます-stem 行き plus ましょう.",
                ),
                ChallengeEntity(
                    id = 64011, lessonId = 802, type = ChallengeType.CONJUGATE,
                    question = "Which polite volitional of 勉強する means 'let's study'?",
                    audioSrc = "asset:///audio/ja/ja_vol_benkyou_shimashou.ogg",
                    orderIndex = 1,
                    grammaticalFocus = "ja.volition_polite",
                    ruleText = "する is irregular here: its volitional is しよう, the し of する plus ょう, and the polite shape is the ます-stem of 勉強します plus ましょう, 勉強しましょう.\n勉強しよう is the plain 意向形, so it is the wrong register here. 勉強しますましょう has the whole ます form in front of ましょう, and ましょう already stands on a ます-stem — the stem is 勉強し. 勉強したましょう puts the plain past した in front of ましょう, and ましょう is not built on the past.",
                ),
                ChallengeEntity(
                    id = 64012, lessonId = 802, type = ChallengeType.FILL_BLANK,
                    question = "いっしょに映画を___。",
                    audioSrc = "asset:///audio/ja/ja_vol_eiga_o_mimashou.ogg",
                    orderIndex = 2,
                    grammaticalFocus = "ja.volition_polite",
                    acceptedAnswers = "見ましょう|みましょう",
                    ruleText = "〜ましょう is the ます-stem plus ましょう — 見ます → 見ましょう — and いっしょに marks the people the proposal is made to.\n見ます is the polite present, so it states what you do instead of proposing anything. 見よう is the plain 意向形, which is the wrong level for a suggestion made to another person. 見た is the plain past, so the film is already over.",
                ),
                ChallengeEntity(
                    id = 64013, lessonId = 802, type = ChallengeType.SELECT,
                    question = "How do you say 'He will probably come'?",
                    audioSrc = "asset:///audio/ja/ja_vol_kare_ga_kuru_deshou.ogg",
                    orderIndex = 3,
                    grammaticalFocus = "ja.volition_polite",
                    ruleText = "でしょう is 推量, the speaker's guess, and it attaches to the plain form 来る; it says nothing about wanting or proposing anything.\n彼は来ましょう is ましょう, a proposal the speaker is making, and you do not propose on someone else's behalf. 彼は来ます is a flat statement of what he will do, with no guess in it and no でしょう to hold the guess. 彼は来ませんでした is the polite past negative, so he did not come and that is settled.",
                ),
                ChallengeEntity(
                    id = 64014, lessonId = 802, type = ChallengeType.WORD_BANK,
                    question = "Assemble: 'Let's all take the train'",
                    orderIndex = 4,
                    grammaticalFocus = "ja.volition_polite",
                    ruleText = "〜ましょう is the ます-stem of 乗る plus ましょう — 乗ります → 乗りましょう — and みんなで marks the group the proposal is made to.\nみんなで電車に乗ります states what the group does. みんなで電車に乗りません is the polite negative, so the group is not riding. みんなで電車に乗った is the plain past, so the ride is already over.",
                ),
                ChallengeEntity(
                    id = 64100, lessonId = 802, type = ChallengeType.LISTEN,
                    question = "Tap what you hear",
                    audioSrc = "asset:///audio/ja/ja_cond_ashita_ame_ga_furunara_ryokou_ni_ikimashou.ogg",
                    orderIndex = 5,
                    grammaticalFocus = "ja.conditional_nara",
                    ruleText = "明日は雨が降るなら、旅行に行きましょう is 雨が降るなら + 旅行に行きましょう: a なら condition with a ましょう proposal on the end of it.\n明日は雨が降りましたら、旅行に行きましょう is ましたら, the formal business conditional, and ましたら is built on the polite past 降りました where なら is built on the 辞書形 降る. 明日は雨が降ると、旅行に行きましょう is the と condition, which states a general outcome and will not carry the 意志 that ましょう is.",
                ),
                ChallengeEntity(
                    id = 64101, lessonId = 802, type = ChallengeType.LISTEN,
                    question = "Tap what you hear",
                    audioSrc = "asset:///audio/ja/ja_cond_jikan_ga_areba_oshietekudasai.ogg",
                    orderIndex = 6,
                    grammaticalFocus = "ja.conditional_ba",
                    ruleText = "時間があれば、教えてください is ある → あれば for the condition, and a 依頼 is one of the things a ば condition carries.\n時間がなければ、教えてください is なければ, if there is no time, so the request is made on the other side of the condition. 時間があれば、教えください hangs ください off the noun 教え, and ください takes the て-form, so the noun has to become 教えて.",
                ),
                ChallengeEntity(
                    id = 64102, lessonId = 802, type = ChallengeType.LISTEN,
                    question = "Tap what you hear",
                    audioSrc = "asset:///audio/ja/ja_vol_ashita_wa_isshoni_eiga_o_mimashou.ogg",
                    orderIndex = 7,
                    grammaticalFocus = "ja.volition_polite",
                    ruleText = "あしたは一緒に映画を見ましょう is 一緒に + 見ましょう, the ます-stem 見ます plus ましょう, proposing the film to whoever 一緒に names.\nあしたは一緒に映画を見ません is the polite negative, so the film is not being watched at all. あしたは一緒に映画を見た is the plain past, so the film has already been seen.",
                ),
            ),
            options = listOf(
                ChallengeOptionEntity(id = 6400001, challengeId = 64000, text = "雨が降ったら、家にいてください。", romaji = "ame ga futtara, ie ni ite kudasai", correct = true, audioSrc = "asset:///audio/ja/ja_cond_ame_ga_futtara_ie_tekudasai.ogg"),
                ChallengeOptionEntity(id = 6400002, challengeId = 64000, text = "雨が降ると、家にいてください。", romaji = "ame ga furuto, ie ni ite kudasai", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6400003, challengeId = 64000, text = "雨が降るたら、家にいてください。", romaji = "ame ga furutara, ie ni ite kudasai", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6400004, challengeId = 64000, text = "雨が降りましたら、家にいてください。", romaji = "ame ga furimashitara, ie ni ite kudasai", correct = false, errorTag = "WRONG_REGISTER"),

                ChallengeOptionEntity(id = 6400005, challengeId = 64001, text = "飲んだら", romaji = "nondattara", correct = true, audioSrc = "asset:///audio/ja/ja_cond_nondattara.ogg"),
                ChallengeOptionEntity(id = 6400006, challengeId = 64001, text = "飲むなら", romaji = "nomunara", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6400007, challengeId = 64001, text = "飲めば", romaji = "nomereba", correct = false, errorTag = "WRONG_REGISTER"),
                ChallengeOptionEntity(id = 6400008, challengeId = 64001, text = "飲みたら", romaji = "nomitara", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 6400009, challengeId = 64002, text = "降ったら", romaji = "futtara", correct = true, audioSrc = "asset:///audio/ja/ja_cond_ame_ga_futtara_dekakemasen.ogg"),
                ChallengeOptionEntity(id = 6400010, challengeId = 64002, text = "降ります", romaji = "furimasu", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 6400011, challengeId = 64002, text = "降ると", romaji = "furuto", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6400012, challengeId = 64002, text = "降るなら", romaji = "furunara", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 6400013, challengeId = 64003, text = "仕事が終わったら、帰りましょう。", romaji = "shigoto ga owattara, kaerimashou", correct = true, audioSrc = "asset:///audio/ja/ja_cond_shigato_ga_owattara_kaerimashou.ogg"),
                ChallengeOptionEntity(id = 6400014, challengeId = 64003, text = "仕事が変わるなら、帰りましょう。", romaji = "shigoto ga kawarunara, kaerimashou", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6400015, challengeId = 64003, text = "仕事が終われば、帰りましょう。", romaji = "shigoto ga owareba, kaerimashou", correct = false, errorTag = "WRONG_REGISTER"),
                ChallengeOptionEntity(id = 6400016, challengeId = 64003, text = "仕事が終わって、帰りましょう。", romaji = "shigoto ga owatte, kaerimashou", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 6400017, challengeId = 64004, text = "読む", romaji = "yomu", correct = true),
                ChallengeOptionEntity(id = 6400018, challengeId = 64004, text = "読んだら", romaji = "yondattara", correct = true, audioSrc = "asset:///audio/ja/ja_tara_yondattara.ogg"),
                ChallengeOptionEntity(id = 6400019, challengeId = 64004, text = "行く", romaji = "iku", correct = true),
                ChallengeOptionEntity(id = 6400020, challengeId = 64004, text = "行ったら", romaji = "ittara", correct = true, audioSrc = "asset:///audio/ja/ja_tara_ittara.ogg"),
                ChallengeOptionEntity(id = 6400021, challengeId = 64004, text = "食べる", romaji = "taberu", correct = true),
                ChallengeOptionEntity(id = 6400022, challengeId = 64004, text = "食べたら", romaji = "tabetara", correct = true, audioSrc = "asset:///audio/ja/ja_tara_tabetara.ogg"),
                ChallengeOptionEntity(id = 6400023, challengeId = 64004, text = "飲む", romaji = "nomu", correct = true),
                ChallengeOptionEntity(id = 6400024, challengeId = 64004, text = "飲んだら", romaji = "nondattara", correct = true, audioSrc = "asset:///audio/ja/ja_cond_nondattara.ogg"),

                ChallengeOptionEntity(id = 6400025, challengeId = 64005, text = "分かるなら、教えてください。", romaji = "wakaru nara, oshiete kudasai", correct = true, audioSrc = "asset:///audio/ja/ja_cond_wakaru_nara_oshietekudasai.ogg"),
                ChallengeOptionEntity(id = 6400026, challengeId = 64005, text = "分かったなら、教えてください。", romaji = "wakatta nara, oshiete kudasai", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6400027, challengeId = 64005, text = "分かるたびに、教えてください。", romaji = "wakaru tabi ni, oshiete kudasai", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6400028, challengeId = 64005, text = "分かるなら、教えください。", romaji = "wakaru nara, oshie kudasai", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 6400029, challengeId = 64006, text = "行くなら", romaji = "iku nara", correct = true, audioSrc = "asset:///audio/ja/ja_cond_iku_nara.ogg"),
                ChallengeOptionEntity(id = 6400030, challengeId = 64006, text = "行ったら", romaji = "ittara", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6400031, challengeId = 64006, text = "行きなら", romaji = "iki nara", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6400032, challengeId = 64006, text = "行けば", romaji = "ikeba", correct = false, errorTag = "WRONG_REGISTER"),

                ChallengeOptionEntity(id = 6400033, challengeId = 64007, text = "治るなら", romaji = "naoru nara", correct = true, audioSrc = "asset:///audio/ja/ja_cond_byoki_ga_naorunara_gakkou_o_yasumimasu.ogg"),
                ChallengeOptionEntity(id = 6400034, challengeId = 64007, text = "治ったなら", romaji = "naotta nara", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6400035, challengeId = 64007, text = "治りますなら", romaji = "naorimasu nara", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 6400036, challengeId = 64007, text = "治るたびに", romaji = "naoru tabi ni", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 6400037, challengeId = 64008, text = "忙しければ、来なくていいです。", romaji = "isogashikereba, konakuteiidesu", correct = true, audioSrc = "asset:///audio/ja/ja_cond_isogashikereba_konakuteiidesu.ogg"),
                ChallengeOptionEntity(id = 6400038, challengeId = 64008, text = "忙ししかったら、来なくていいです。", romaji = "isogashikattara, konakuteiidesu", correct = false, errorTag = "WRONG_REGISTER"),
                ChallengeOptionEntity(id = 6400039, challengeId = 64008, text = "忙しけら、来なくていいです。", romaji = "isogashikera, konakuteiidesu", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6400040, challengeId = 64008, text = "忙しいれば、来なくていいです。", romaji = "isogashiireba, konakuteiidesu", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 6400041, challengeId = 64009, text = "買えば", romaji = "kaeba", correct = true, audioSrc = "asset:///audio/ja/ja_cond_kau_ba.ogg"),
                ChallengeOptionEntity(id = 6400042, challengeId = 64009, text = "買たら", romaji = "kaitara", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6400043, challengeId = 64009, text = "買らば", romaji = "kairaba", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6400044, challengeId = 64009, text = "買わなければ", romaji = "kawanakereba", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 6400045, challengeId = 64010, text = "公園に行きましょう。", romaji = "koen ni ikimashou", correct = true, audioSrc = "asset:///audio/ja/ja_vol_koen_ni_ikimashou.ogg"),
                ChallengeOptionEntity(id = 6400046, challengeId = 64010, text = "公園に行こう。", romaji = "koen ni ikou", correct = false, errorTag = "WRONG_REGISTER"),
                ChallengeOptionEntity(id = 6400047, challengeId = 64010, text = "公園に行きました。", romaji = "koen ni ikimashita", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 6400048, challengeId = 64010, text = "公園に行こうましょう。", romaji = "koen ni ikou mashou", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 6400049, challengeId = 64011, text = "勉強しましょう。", romaji = "benkyou shimashou", correct = true, audioSrc = "asset:///audio/ja/ja_vol_benkyou_shimashou.ogg"),
                ChallengeOptionEntity(id = 6400050, challengeId = 64011, text = "勉強しよう。", romaji = "benkyou shiyou", correct = false, errorTag = "WRONG_REGISTER"),
                ChallengeOptionEntity(id = 6400051, challengeId = 64011, text = "勉強しますましょう。", romaji = "benkyou shimasu mashou", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6400052, challengeId = 64011, text = "勉強したましょう。", romaji = "benkyou shita mashou", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 6400053, challengeId = 64012, text = "見ましょう", romaji = "mimashou", correct = true, audioSrc = "asset:///audio/ja/ja_vol_eiga_o_mimashou.ogg"),
                ChallengeOptionEntity(id = 6400054, challengeId = 64012, text = "見ます", romaji = "mimasu", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6400055, challengeId = 64012, text = "見よう", romaji = "miyou", correct = false, errorTag = "WRONG_REGISTER"),
                ChallengeOptionEntity(id = 6400056, challengeId = 64012, text = "見た", romaji = "mita", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 6400057, challengeId = 64013, text = "彼は来るでしょう。", romaji = "kare wa kuru deshou", correct = true, audioSrc = "asset:///audio/ja/ja_vol_kare_ga_kuru_deshou.ogg"),
                ChallengeOptionEntity(id = 6400058, challengeId = 64013, text = "彼は来ましょう。", romaji = "kare wa kimashou", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6400059, challengeId = 64013, text = "彼は来ます。", romaji = "kare wa kimasu", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6400060, challengeId = 64013, text = "彼は来ませんでした。", romaji = "kare wa kimasen deshita", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 6400061, challengeId = 64014, text = "みんなで電車に乗りましょう。", romaji = "minna de densha ni norimashou", correct = true, audioSrc = "asset:///audio/ja/ja_vol_minna_de_densha_ni_norimashou.ogg"),
                ChallengeOptionEntity(id = 6400062, challengeId = 64014, text = "みんなで電車に乗ります。", romaji = "minna de densha ni norimasu", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6400063, challengeId = 64014, text = "みんなで電車に乗りません。", romaji = "minna de densha ni norimasen", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 6400064, challengeId = 64014, text = "みんなで電車に乗った。", romaji = "minna de densha ni notta", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 6410001, challengeId = 64100, text = "明日は雨が降るなら、旅行に行きましょう。", romaji = "ashita wa ame ga furunara, ryokou ni ikimashou", correct = true),
                ChallengeOptionEntity(id = 6410002, challengeId = 64100, text = "明日は雨が降りましたら、旅行に行きましょう。", romaji = "ashita wa ame ga furimashitara, ryokou ni ikimashou", correct = false, errorTag = "WRONG_REGISTER"),
                ChallengeOptionEntity(id = 6410003, challengeId = 64100, text = "明日は雨が降ると、旅行に行きましょう。", romaji = "ashita wa ame ga furuto, ryokou ni ikimashou", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 6410004, challengeId = 64101, text = "時間があれば、教えてください。", romaji = "jikan ga areba, oshiete kudasai", correct = true),
                ChallengeOptionEntity(id = 6410005, challengeId = 64101, text = "時間がなければ、教えてください。", romaji = "jikan ga nakereba, oshiete kudasai", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6410006, challengeId = 64101, text = "時間があれば、教えください。", romaji = "jikan ga areba, oshie kudasai", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 6410007, challengeId = 64102, text = "あしたは一緒に映画を見ましょう。", romaji = "ashita wa isshoni eiga o mimashou", correct = true),
                ChallengeOptionEntity(id = 6410008, challengeId = 64102, text = "あしたは一緒に映画を見ません。", romaji = "ashita wa isshoni eiga o mimasen", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 6410009, challengeId = 64102, text = "あしたは一緒に映画を見た。", romaji = "ashita wa isshoni eiga o mita", correct = false, errorTag = "WRONG_TENSE"),
            ),
        ),
        // =====================================================================
        UnitPayload(
            unit = UnitEntity(
                id = 45,
                courseId = 2,
                title = "Unit 16: Keigo & Three られる",
                description = "A first, small set of 尊敬語 and 謙譲語 with the ascript お〜になる, and the rule that tells the passive, the potential and 得る apart",
                orderIndex = 15,
            ),
            lessons = listOf(
                LessonEntity(id = 803, unitId = 45, title = "Lesson 36: Lifting the Other Person", orderIndex = 0),
                LessonEntity(id = 804, unitId = 45, title = "Lesson 37: Lowering Yourself", orderIndex = 1),
                LessonEntity(id = 805, unitId = 45, title = "Lesson 38: られる, Three Times Over", orderIndex = 2),
            ),
            challenges = listOf(
                // --- Lesson 36: 尊敬語 ------------------------------------------
                ChallengeEntity(
                    id = 64015, lessonId = 803, type = ChallengeType.SELECT,
                    question = "The president is having lunch. Which one uses 尊敬語?",
                    audioSrc = "asset:///audio/ja/ja_keigo_shachou_ga_hirougohan_o_meshimasu.ogg",
                    orderIndex = 0,
                    grammaticalFocus = "ja.keigo_honorific",
                    ruleText = "尊敬語 lifts the other person's action, and 食べる → 召し上がる is one of the everyday substitutions, so 社長がお昼を召し上がります is a sentence about the president.\nお昼をいただきます is 謙譲語: it lowers the speaker's own action, so used here it makes the speaker the one eating and the president's lunch an eavesdropped one. 社長がお昼を食べます is 丁寧語, which is neutral — it neither lifts the president nor lowers the speaker, and neutral is the level this course already speaks at. 社長がお昼を食べなさいます is なさる, which is the honorific of する and never of 食べる.",
                ),
                ChallengeEntity(
                    id = 64016, lessonId = 803, type = ChallengeType.SELECT,
                    question = "The manager is reading the contract. How do you say it with 尊敬語?",
                    audioSrc = "asset:///audio/ja/ja_keigo_bucho_ga_keiyakusho_o_okomi_ni_narimasu.ogg",
                    orderIndex = 1,
                    grammaticalFocus = "ja.keigo_honorific",
                    ruleText = "お〜になる is the ascript honorific: お + the verb's ます-stem + になる, so 読みます → お読みになります.\n部長が契約書を読みます is 丁寧語, which leaves the manager at the neutral level where this sentence needs him lifted. 部長が契約書をお読みます glues the お onto a ます form, and the honorific is お + ます-stem + になる, so お読みます is not a shape Japanese has. 部長が契約書をお書きになります is the honorific of 書く and not of 読む, so it puts a different verb in the slot.",
                ),
                ChallengeEntity(
                    id = 64017, lessonId = 803, type = ChallengeType.MATCH_PAIRS,
                    question = "Match the plain verb to its 尊敬語",
                    orderIndex = 2,
                ),

                // --- Lesson 37: 謙譲語 ------------------------------------------
                ChallengeEntity(
                    id = 64018, lessonId = 804, type = ChallengeType.SELECT,
                    question = "You are visiting a client. How do you say 'I will come to see you tomorrow'?",
                    audioSrc = "asset:///audio/ja/ja_keigo_ashita_wa_ukagaimasu.ogg",
                    orderIndex = 0,
                    grammaticalFocus = "ja.keigo_humble",
                    ruleText = "謙譲語 lowers the speaker's own action toward the other person, and 行く → 伺う is the substitution that carries a visit, so 明日はうかがいます is I will come to you said modestly.\n明日は行きます is 丁寧語: polite, but neutral, and neutral is what you say to a friend rather than to someone whose guest you are about to be. 明日は行なさいます is なさる, which is 尊敬語 — it would lift your own coming to the rank the humble form exists to avoid. 明日はいただきます is いただく, which humbles 食べる and 飲む and not 来る.",
                ),
                ChallengeEntity(
                    id = 64019, lessonId = 804, type = ChallengeType.CONJUGATE,
                    question = "Which humble form of する means 'I will do it'?",
                    audioSrc = "asset:///audio/ja/ja_keigo_itashimasu.ogg",
                    orderIndex = 1,
                    grammaticalFocus = "ja.keigo_humble",
                    ruleText = "いたす is the 謙譲語 of する, and the ます form in its own right is いたします — いたす + ます — so the humble of する never loses the ます.\nなさいます is the 尊敬語 of する: it lifts whoever is doing it, and said about your own action it means the opposite of what the humble form is for. うかがいます is the humble of 行く, a different verb. したします puts the plain past した in front of します, and a する-verb does not take た that way outside the plain past しました.",
                ),
                ChallengeEntity(
                    id = 64020, lessonId = 804, type = ChallengeType.MATCH_PAIRS,
                    question = "Match the plain verb to its 謙譲語",
                    orderIndex = 2,
                ),

                // --- Lesson 38: られる and the LISTEN round --------------------
                ChallengeEntity(
                    id = 64021, lessonId = 805, type = ChallengeType.SELECT,
                    question = "書く makes two different forms here. Which one is the potential, 'can write'?",
                    audioSrc = "asset:///audio/ja/ja_rareru_kakeru.ogg",
                    orderIndex = 0,
                    grammaticalFocus = "ja.rareru_readings",
                    ruleText = "A godan verb never collides: 書く makes 書ける for the potential and 書かれる for the passive, and ける against られる is the whole difference.\n書かれる is the 受身 of unit 13 — the letter is the one being written, not the writer. 書きます is the polite present of the plain verb, which says nothing about ability. 書きました is the polite past, so the writing is over rather than possible.",
                ),
                ChallengeEntity(
                    id = 64022, lessonId = 805, type = ChallengeType.CONJUGATE,
                    question = "Which plain passive form of 作る means 'is made'?",
                    audioSrc = "asset:///audio/ja/ja_rareru_tsukurareru.ogg",
                    orderIndex = 1,
                    grammaticalFocus = "ja.rareru_readings",
                    ruleText = "作る is a godan verb, so the passive is う → れる and the potential is う → える, which means the two never look alike: 作られる against 作れる.\n作れる is the potential, can make, and its subject is the one doing the making. 作られます is the polite passive present, and this item asks for the plain form. 作られた is the plain passive past, so the thing has already been made.",
                ),
                ChallengeEntity(
                    id = 64023, lessonId = 805, type = ChallengeType.SELECT,
                    question = "見る makes 見られる, which reads as can see and as is seen at the same time. Which sentence is the potential?",
                    audioSrc = "asset:///audio/ja/ja_rareru_watashi_wa_sono_eiga_o_mireraremasu.ogg",
                    orderIndex = 2,
                    grammaticalFocus = "ja.rareru_readings",
                    ruleText = "An ichidan verb builds られる the same way for the potential and for the passive — 見る can 見られる either way — so the ending alone never says which one you are reading. What settles it is who stands in the subject slot: 私は…を見られます puts the doer there, which is the potential, while その映画は私に見られます puts the film in the topic slot and me under に, which is the 受身.\n私はその映画を見ませんでした is the polite past negative, so the seeing is over and did not happen. 私はその映画を見させられる carries the causative せ inside, so it is I am made to watch the film — and the same string said by an agent is I make someone watch it.",
                ),
                ChallengeEntity(
                    id = 64024, lessonId = 805, type = ChallengeType.SELECT,
                    question = "Which sentence uses られる as 得る, 'can get / can obtain'?",
                    audioSrc = "asset:///audio/ja/ja_rareru_ii_kekka_ga_eraremasu.ogg",
                    orderIndex = 3,
                    grammaticalFocus = "ja.rareru_readings",
                    ruleText = "One reading of られる is 得る, can get, and it is a small closed set — 得る, 求める, 採る and a few more — so いい結果が得られます says that the results can be obtained. In 得られる and 求められる the 得る reading and the plain potential of 得る and 求める come to nearly the same thing, which is why dictionaries list both.\nいい結果が食べられます is 食べられる, the potential and the passive in one string, can eat or is eaten, and neither of those is 得る. いい結果が見られます is 見られる, the same collision for 見る. いい結果が読まれます is the plain 受身 of 読む, so somebody is reading the results.",
                ),
                ChallengeEntity(
                    id = 64025, lessonId = 805, type = ChallengeType.FILL_BLANK,
                    question = "この本は、ともだちに___ました。",
                    audioSrc = "asset:///audio/ja/ja_rareru_kono_hon_wa_tomodachi_ni_yomaremasu.ogg",
                    orderIndex = 4,
                    grammaticalFocus = "ja.rareru_readings",
                    acceptedAnswers = "読まれました|よまれました",
                    ruleText = "ともだちに puts に in front of the one who did it, and that is the slot 受身 uses, so 読む takes the passive: 読まれました.\nこの本は、ともだちに読みました is the active polite past, and its に is the 受身's agent slot with an active verb standing in it, which is not a sentence Japanese builds. 読ませました is the causative, so the friend made somebody read it. 読まれませんでした is the polite past negative, so the book was not read at all.",
                ),
                ChallengeEntity(
                    id = 64026, lessonId = 805, type = ChallengeType.SELECT,
                    question = "する is the one verb whose potential is not られる. Which form means 'can do'?",
                    audioSrc = "asset:///audio/ja/ja_rareru_dekiru.ogg",
                    orderIndex = 5,
                    grammaticalFocus = "ja.rareru_readings",
                    ruleText = "する's potential is irregular: する → できる, with no られる in it at all, and できる keeps its own れば and なければ — できれば, できなければ.\nされる is the passive of する, is done, with the one who does it under に. すられる hangs られる straight onto the plain する, and neither the potential nor the passive is built that way. できた is the plain past of できる, so the doing is already over.",
                ),
                ChallengeEntity(
                    id = 64103, lessonId = 805, type = ChallengeType.LISTEN,
                    question = "Tap what you hear",
                    audioSrc = "asset:///audio/ja/ja_keigo_shachou_wa_mou_okaeri_ni_narimashita.ogg",
                    orderIndex = 6,
                    grammaticalFocus = "ja.keigo_honorific",
                    ruleText = "社長はもうお帰りになりました is お + 帰ります + になる + ました, the ascript honorific in the polite past.\n社長はもう帰りました is the 丁寧 past, which leaves the president at the neutral level this sentence is supposed to lift him above. 社長はもうお帰りください hangs ください off the noun お帰り, and ください attaches to the て-form rather than to a noun.",
                ),
                ChallengeEntity(
                    id = 64104, lessonId = 805, type = ChallengeType.LISTEN,
                    question = "Tap what you hear",
                    audioSrc = "asset:///audio/ja/ja_keigo_shiryo_o_haiken_itashimasu.ogg",
                    orderIndex = 7,
                    grammaticalFocus = "ja.keigo_humble",
                    ruleText = "この資料を拝見いたします is 拝見する, the 謙譲語 of 見る, with the ます form of いたす on the end.\nこの資料を見ます is the 丁寧 form of the same verb, which neither lifts the other person nor lowers the speaker. この資料を拝見しません is the polite negative, so the material is not being looked at at all.",
                ),
                ChallengeEntity(
                    id = 64105, lessonId = 805, type = ChallengeType.LISTEN,
                    question = "Tap what you hear",
                    audioSrc = "asset:///audio/ja/ja_rareru_kono_heya_wa_mainichi_souji_sareteimasu.ogg",
                    orderIndex = 8,
                    grammaticalFocus = "ja.rareru_readings",
                    ruleText = "この部屋は毎日掃除されています is 掃除される + ています, and は makes the room the thing acted on, which is what settles the reading as 受身.\nこの部屋は毎日掃除されます is the plain 受身 with no ている on it, so it names the state rather than saying it is going on right now. この部屋は毎日掃除していません is the polite negative, so the room is not being swept.",
                ),
            ),
            options = listOf(
                ChallengeOptionEntity(id = 6400065, challengeId = 64015, text = "社長がお昼を召し上がります。", romaji = "shachou ga ohiru o meshimasu", correct = true, audioSrc = "asset:///audio/ja/ja_keigo_shachou_ga_hirougohan_o_meshimasu.ogg"),
                ChallengeOptionEntity(id = 6400066, challengeId = 64015, text = "お昼をいただきます。", romaji = "ohiru o itadakimasu", correct = false, errorTag = "WRONG_REGISTER"),
                ChallengeOptionEntity(id = 6400067, challengeId = 64015, text = "社長がお昼を食べます。", romaji = "shachou ga ohiru o tabemasu", correct = false, errorTag = "WRONG_REGISTER"),
                ChallengeOptionEntity(id = 6400068, challengeId = 64015, text = "社長がお昼を食べなさいます。", romaji = "shachou ga ohiru o tabenasaimasu", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 6400069, challengeId = 64016, text = "部長が契約書を お読みになります。", romaji = "bucho ga keiyakusho o oyomi ni narimasu", correct = true, audioSrc = "asset:///audio/ja/ja_keigo_bucho_ga_keiyakusho_o_okomi_ni_narimasu.ogg"),
                ChallengeOptionEntity(id = 6400070, challengeId = 64016, text = "部長が契約書を読みます。", romaji = "bucho ga keiyakusho o yomimasu", correct = false, errorTag = "WRONG_REGISTER"),
                ChallengeOptionEntity(id = 6400071, challengeId = 64016, text = "部長が契約書をお読みます。", romaji = "bucho ga keiyakusho o oyomimasu", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6400072, challengeId = 64016, text = "部長が契約書をお書きになります。", romaji = "bucho ga keiyakusho o okaki ni narimasu", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 6400073, challengeId = 64017, text = "食べる", romaji = "taberu", correct = true),
                ChallengeOptionEntity(id = 6400074, challengeId = 64017, text = "召し上がる", romaji = "meshiagaru", correct = true, audioSrc = "asset:///audio/ja/ja_keigo_meshiagaru.ogg"),
                ChallengeOptionEntity(id = 6400075, challengeId = 64017, text = "見る", romaji = "miru", correct = true),
                ChallengeOptionEntity(id = 6400076, challengeId = 64017, text = "ご覧になる", romaji = "goran ni naru", correct = true, audioSrc = "asset:///audio/ja/ja_keigo_gomiruninaru.ogg"),
                ChallengeOptionEntity(id = 6400077, challengeId = 64017, text = "する", romaji = "suru", correct = true),
                ChallengeOptionEntity(id = 6400078, challengeId = 64017, text = "なさる", romaji = "nasaru", correct = true, audioSrc = "asset:///audio/ja/ja_keigo_nasaru.ogg"),
                ChallengeOptionEntity(id = 6400079, challengeId = 64017, text = "言う", romaji = "iu", correct = true),
                ChallengeOptionEntity(id = 6400080, challengeId = 64017, text = "おっしゃる", romaji = "ossyaru", correct = true, audioSrc = "asset:///audio/ja/ja_keigo_ossyaru.ogg"),
                ChallengeOptionEntity(id = 6400081, challengeId = 64017, text = "いる", romaji = "iru", correct = true),
                ChallengeOptionEntity(id = 6400082, challengeId = 64017, text = "いらっしゃる", romaji = "irassharu", correct = true, audioSrc = "asset:///audio/ja/ja_keigo_irassharu.ogg"),

                ChallengeOptionEntity(id = 6400083, challengeId = 64018, text = "明日はうかがいます。", romaji = "ashita wa ukagaimasu", correct = true, audioSrc = "asset:///audio/ja/ja_keigo_ashita_wa_ukagaimasu.ogg"),
                ChallengeOptionEntity(id = 6400084, challengeId = 64018, text = "明日は行きます。", romaji = "ashita wa ikimasu", correct = false, errorTag = "WRONG_REGISTER"),
                ChallengeOptionEntity(id = 6400085, challengeId = 64018, text = "明日は行なさいます。", romaji = "ashita wa ikinasaimasu", correct = false, errorTag = "WRONG_REGISTER"),
                ChallengeOptionEntity(id = 6400086, challengeId = 64018, text = "明日はいただきます。", romaji = "ashita wa itadakimasu", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 6400087, challengeId = 64019, text = "いたします", romaji = "itashimasu", correct = true, audioSrc = "asset:///audio/ja/ja_keigo_itashimasu.ogg"),
                ChallengeOptionEntity(id = 6400088, challengeId = 64019, text = "なさいます", romaji = "nasaimasu", correct = false, errorTag = "WRONG_REGISTER"),
                ChallengeOptionEntity(id = 6400089, challengeId = 64019, text = "うかがいます", romaji = "ukagaimasu", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6400090, challengeId = 64019, text = "したします", romaji = "shitashimasu", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 6400091, challengeId = 64020, text = "食べる", romaji = "taberu", correct = true),
                ChallengeOptionEntity(id = 6400092, challengeId = 64020, text = "いただく", romaji = "itadaku", correct = true, audioSrc = "asset:///audio/ja/ja_keigo_itadaku.ogg"),
                ChallengeOptionEntity(id = 6400093, challengeId = 64020, text = "見る", romaji = "miru", correct = true),
                ChallengeOptionEntity(id = 6400094, challengeId = 64020, text = "拝見する", romaji = "haiken suru", correct = true, audioSrc = "asset:///audio/ja/ja_keigo_haiken_suru.ogg"),
                ChallengeOptionEntity(id = 6400095, challengeId = 64020, text = "言う", romaji = "iu", correct = true),
                ChallengeOptionEntity(id = 6400096, challengeId = 64020, text = "申し上げる", romaji = "moshiageru", correct = true, audioSrc = "asset:///audio/ja/ja_keigo_moshiageru.ogg"),
                ChallengeOptionEntity(id = 6400097, challengeId = 64020, text = "行く", romaji = "iku", correct = true),
                ChallengeOptionEntity(id = 6400098, challengeId = 64020, text = "伺う", romaji = "ukagau", correct = true, audioSrc = "asset:///audio/ja/ja_keigo_ukagau.ogg"),
                ChallengeOptionEntity(id = 6400099, challengeId = 64020, text = "する", romaji = "suru", correct = true),
                ChallengeOptionEntity(id = 6400100, challengeId = 64020, text = "いたす", romaji = "itasu", correct = true, audioSrc = "asset:///audio/ja/ja_keigo_itasu.ogg"),

                ChallengeOptionEntity(id = 6400101, challengeId = 64021, text = "書ける", romaji = "kakeru", correct = true, audioSrc = "asset:///audio/ja/ja_rareru_kakeru.ogg"),
                ChallengeOptionEntity(id = 6400102, challengeId = 64021, text = "書かれる", romaji = "kakareru", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6400103, challengeId = 64021, text = "書きます", romaji = "kakimasu", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6400104, challengeId = 64021, text = "書きました", romaji = "kakimashita", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 6400105, challengeId = 64022, text = "作られる", romaji = "tsukurareru", correct = true, audioSrc = "asset:///audio/ja/ja_rareru_tsukurareru.ogg"),
                ChallengeOptionEntity(id = 6400106, challengeId = 64022, text = "作れる", romaji = "tsukureru", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6400107, challengeId = 64022, text = "作られます", romaji = "tsukurarerumasu", correct = false, errorTag = "WRONG_REGISTER"),
                ChallengeOptionEntity(id = 6400108, challengeId = 64022, text = "作られた", romaji = "tsukurarerutta", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 6400109, challengeId = 64023, text = "私はその映画を見られます。", romaji = "watashi wa sono eiga o miraremasu", correct = true, audioSrc = "asset:///audio/ja/ja_rareru_watashi_wa_sono_eiga_o_mireraremasu.ogg"),
                ChallengeOptionEntity(id = 6400110, challengeId = 64023, text = "私はその映画を見ませんでした。", romaji = "watashi wa sono eiga o mirimasen deshita", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 6400111, challengeId = 64023, text = "私はその映画を見させられる。", romaji = "watashi wa sono eiga o miraserareru", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 6400112, challengeId = 64024, text = "いい結果が得られます。", romaji = "ii kekka ga eraremasu", correct = true, audioSrc = "asset:///audio/ja/ja_rareru_ii_kekka_ga_eraremasu.ogg"),
                ChallengeOptionEntity(id = 6400113, challengeId = 64024, text = "いい結果が食べられます。", romaji = "ii kekka ga taberaremasu", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6400114, challengeId = 64024, text = "いい結果が見られます。", romaji = "ii kekka ga mireraremasu", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6400115, challengeId = 64024, text = "いい結果が読まれます。", romaji = "ii kekka ga yomaremasu", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 6400116, challengeId = 64025, text = "読まれました", romaji = "yomaremashita", correct = true, audioSrc = "asset:///audio/ja/ja_rareru_kono_hon_wa_tomodachi_ni_yomaremasu.ogg"),
                ChallengeOptionEntity(id = 6400117, challengeId = 64025, text = "読みました", romaji = "yomimashita", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6400118, challengeId = 64025, text = "読ませました", romaji = "yomasesemashita", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6400119, challengeId = 64025, text = "読まれませんでした", romaji = "yomaremasen deshita", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 6400120, challengeId = 64026, text = "できる", romaji = "dekiru", correct = true, audioSrc = "asset:///audio/ja/ja_rareru_dekiru.ogg"),
                ChallengeOptionEntity(id = 6400121, challengeId = 64026, text = "される", romaji = "sareru", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6400122, challengeId = 64026, text = "すられる", romaji = "sureraru", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6400123, challengeId = 64026, text = "できた", romaji = "dekita", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 6410010, challengeId = 64103, text = "社長はもうお帰りになりました。", romaji = "shachou wa mou okaeri ni narimashita", correct = true),
                ChallengeOptionEntity(id = 6410011, challengeId = 64103, text = "社長はもう帰りました。", romaji = "shachou wa mou kaerimashita", correct = false, errorTag = "WRONG_REGISTER"),
                ChallengeOptionEntity(id = 6410012, challengeId = 64103, text = "社長はもうお帰りください。", romaji = "shachou wa mou okaeri kudasai", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 6410013, challengeId = 64104, text = "この資料を拝見いたします。", romaji = "kono shiryo o haiken itashimasu", correct = true),
                ChallengeOptionEntity(id = 6410014, challengeId = 64104, text = "この資料を見ます。", romaji = "kono shiryo o mimasu", correct = false, errorTag = "WRONG_REGISTER"),
                ChallengeOptionEntity(id = 6410015, challengeId = 64104, text = "この資料を拝見しません。", romaji = "kono shiryo o haikenimasen", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 6410016, challengeId = 64105, text = "この部屋は毎日掃除されています。", romaji = "kono heya wa mainichi souji sareteimasu", correct = true),
                ChallengeOptionEntity(id = 6410017, challengeId = 64105, text = "この部屋は毎日掃除されます。", romaji = "kono heya wa mainichi souji saremasu", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6410018, challengeId = 64105, text = "この部屋は毎日掃除していません。", romaji = "kono heya wa mainichi souji shiteimasen", correct = false, errorTag = "WRONG_TENSE"),
            ),
        ),
        // =====================================================================
        // =====================================================================
        UnitPayload(
            unit = UnitEntity(
                id = 46,
                courseId = 2,
                title = "Unit 17: Shopping, Money & the Home",
                description = "The everyday N4 nouns of the two places a learner spends money and time: the shop, the till, and the flat",
                orderIndex = 16,
            ),
            lessons = listOf(
                LessonEntity(id = 850, unitId = 46, title = "Lesson 39: At the Shop", orderIndex = 0),
                LessonEntity(id = 851, unitId = 46, title = "Lesson 40: Paying and Prices", orderIndex = 1),
                LessonEntity(id = 852, unitId = 46, title = "Lesson 41: The Home", orderIndex = 2),
            ),
            challenges = listOf(
                // --- Lesson 39: At the Shop ---
                ChallengeEntity(
                    id = 65000, lessonId = 850, type = ChallengeType.MATCH_PAIRS,
                    question = "Match the shop words",
                    orderIndex = 0,
                ),
                ChallengeEntity(
                    id = 65001, lessonId = 850, type = ChallengeType.MATCH_PAIRS,
                    question = "Match the buying words",
                    orderIndex = 1,
                ),
                ChallengeEntity(
                    id = 65002, lessonId = 850, type = ChallengeType.MATCH_PAIRS,
                    question = "Match the shop signs and places",
                    orderIndex = 2,
                ),
                ChallengeEntity(
                    id = 65003, lessonId = 850, type = ChallengeType.MATCH_PAIRS,
                    question = "Match the words for size and price",
                    orderIndex = 3,
                ),
                ChallengeEntity(
                    id = 65004, lessonId = 850, type = ChallengeType.SELECT,
                    question = "Which one means 'the list price'?",
                    audioSrc = "asset:///audio/ja/teika.ogg",
                    orderIndex = 4,
                    ruleText = "定価 is 定 (a set, as in 設定) + 価 (price): the price the shop set and printed, before anything comes off it. The 価 is read ka, so teika, and it is the same 価 as in 物価 and 評価.\n値段 is the price you are quoted, 値札 is the card the price is written on, and 割引 is money taken off. All three are real shop words; only 定価 is the price as printed.",
                ),
                ChallengeEntity(
                    id = 65005, lessonId = 850, type = ChallengeType.SELECT,
                    question = "Which one means 'sold out'?",
                    audioSrc = "asset:///audio/ja/urikire.ogg",
                    orderIndex = 5,
                    ruleText = "売り切れ is 売り (selling) + 切れ (cut off): there is nothing left on the shelf. The 切 carries a small つ before れ, so uriKIRE, and the れ is the plain れ of れる.\n営業中 is the sign that says the shop is trading, 閉店 is the hour the shutters come down, and 値札 is the price card. 営業中 is the opposite state: the shop is open and there is stock to sell.",
                ),
                ChallengeEntity(
                    id = 65006, lessonId = 850, type = ChallengeType.SELECT,
                    question = "Which one means 'vegetables'?",
                    audioSrc = "asset:///audio/ja/yasai.ogg",
                    orderIndex = 6,
                    ruleText = "野菜 is やさい, said yasai: the や is short and the さい carries no long vowel. It is the same 菜 as in お菜.\n果物は katsumono, the fruit; 肉 is meat and 魚 is fish. Those three are all food words and all of them are things you buy at the same counter, which is what makes them good distractors.",
                ),
                ChallengeEntity(
                    id = 65007, lessonId = 850, type = ChallengeType.SELECT,
                    question = "Which one means 'the day a shop is always shut'?",
                    audioSrc = "asset:///audio/ja/teikyubi.ogg",
                    orderIndex = 7,
                    ruleText = "定休日 is 定 (fixed) + 休 (rest) + 日 (day): the weekday the shop is always shut. The 休 is read kyuu here, with the small ゆ, and then 日 is bi, so teikyuubi — the long ゅ carries the extra mora and the 日 gives the bi.\n閉店 is the hour the shutters come down tonight, 営業中 is the open sign, and 半額 is a price. 定休日 is a day on a calendar, not an hour on a clock.",
                ),
                ChallengeEntity(
                    id = 65008, lessonId = 850, type = ChallengeType.WORD_BANK,
                    question = "Assemble: 'Yesterday I bought a hat at that shop'",
                    orderIndex = 8,
                    grammaticalFocus = "ja.past_polite",
                    ruleText = "The polite past is the ます form plus ました: 買う → 買います → 買いました.\nあの店で is あの (that) + 店 (shop) + で, and で marks the place an action happens at. 買った is the plain 断定形, and this item asks for the 丁寧形. あの店と puts と (and / with) where the place marker belongs, and 買う on its own is the plain present.",
                ),
                ChallengeEntity(
                    id = 65009, lessonId = 850, type = ChallengeType.CONJUGATE,
                    question = "Which one means 'I have to pay in cash'?",
                    audioSrc = "asset:///audio/ja/genkindeharwanakerebanarimasen.ogg",
                    orderIndex = 9,
                    grammaticalFocus = "ja.necessity",
                    ruleText = "Necessity is 〜なければなりません, built in two halves: the ない-form minus い plus なければ, then なりません. 払う → 払わない → 払わなければ + なりません. で marks the way the paying is done, and nothing may follow なければ: なければなりません is the whole ending.\n現金で払う is the bare plain verb. It says what I do, not that I have to. 現金で払わなくてもいいです is 〜てもいいです, which is permission, the opposite of an obligation. 現金で払わなかった is the plain past negative, which says the paying did not happen rather than that it had to.",
                ),
                ChallengeEntity(
                    id = 65010, lessonId = 850, type = ChallengeType.FILL_BLANK,
                    question = "この靴は___。",
                    audioSrc = "asset:///audio/ja/ookii.ogg",
                    orderIndex = 10,
                    grammaticalFocus = "ja.i_adjective",
                    acceptedAnswers = "大きい|おおきい",
                    ruleText = "この靴は大きい is a plain い-adjective in front of は: 大 + き + い, with the final い intact.\n大きく drops that い and leaves only the stem, and 大きく is what ない and ありません are built on, so on its own it is not an adjective. 大きいです adds the polite copula, and this blank is the plain 断定形. 大きかった is the plain past, so the shoes are not big any more.",
                ),
                ChallengeEntity(
                    id = 65011, lessonId = 850, type = ChallengeType.SELECT,
                    question = "You want to suggest a film to a friend. Which one is the polite suggestion?",
                    orderIndex = 11,
                    grammaticalFocus = "ja.volition_polite",
                    ruleText = "The polite volitional is the ます-stem plus ましょう: 見る → 見ます + ましょう, so 見ましょう, and it is a proposal the other person is free to refuse.\nいっしょに映画を見ましょうか turns the proposal into a question that expects an answer, and this item states the proposal. いっしょに映画を見よう is the plain 意向形, which is right to a close friend but drops the politeness the register asks for. いっしょに映画を見ました is the polite past, so the film is already over.",
                ),
                ChallengeEntity(
                    id = 65100, lessonId = 850, type = ChallengeType.LISTEN,
                    question = "Tap what you hear",
                    audioSrc = "asset:///audio/ja/konomisenokutsuwayasuidesu.ogg",
                    orderIndex = 12,
                    grammaticalFocus = "ja.i_adjective",
                    ruleText = "安いです is the polite present of the い-adjective 安い. The stem やす never surfaces here: the polite form is the bare stem plus です, so the ear hears yasui and not yasuku.\nこの店の靴は安いくです glues く onto です, and く is the stem the negative is built on, never a polite ending. yasui against yasuku is a whole extra syllable. この店の靴は安いでした hangs です past tense onto an い-adjective, which takes かった and never でした.",
                ),
                ChallengeEntity(
                    id = 65101, lessonId = 850, type = ChallengeType.LISTEN,
                    question = "Tap what you hear",
                    audioSrc = "asset:///audio/ja/kinouanomisedekutsuokaimashita.ogg",
                    orderIndex = 13,
                    grammaticalFocus = "ja.past_polite",
                    ruleText = "買いました is 買います plus ました. The small つ of the ます-stem sits in the middle of it, and the shop word 靴 carries its own small つ in kutsu, so this sentence has two geminate marks in it.\nきのう、あの店で靴を買います is the polite present, so nothing in it says the buying is over. きのう、あの店で靴を買った is the plain 断定形, which drops ます altogether, and the plain past sounds almost exactly like the polite one, which is what this round is for.",
                ),
                ChallengeEntity(
                    id = 65102, lessonId = 850, type = ChallengeType.LISTEN,
                    question = "Tap what you hear",
                    audioSrc = "asset:///audio/ja/ekidekippuokawanakerebanarimasen.ogg",
                    orderIndex = 14,
                    grammaticalFocus = "ja.necessity",
                    ruleText = "買わなければなりません is 買わない minus い, plus なければ, plus なりません. The small つ inside 切符 has to survive too: kippu, not kipu.\n駅で切符を買わなくてもいいです is 〜てもいいです, which is permission and says the opposite thing. 駅で切符を買いました is the polite past, so the buying is over rather than obligatory.",
                ),
                ChallengeEntity(
                    id = 65103, lessonId = 850, type = ChallengeType.STORY,
                    question = "📖 買い物\n\n\n母: 「あした、靴を買いに行こう。」\n私: 「私も新しい帽子がほしい。」\n母: 「じゃあ、商店街へ行きましょう。試着室があればいいね。」\n私: 「うん、ついでお菓子も買おう。」\n\n❓ Where will the mother and child go?",
                    orderIndex = 15,
                    ruleText = "商店街 is the street lined with small shops. It is shoTENgai, and the 街 carries a gy-: the ear hears ぎゃ on it, not a bare g.\n百貨店 is a department store, a single big building. 屋台 is a street food cart. 試着室 is a fitting room. All three are real words, and none of them is a street of shops.",
                ),
                // --- Lesson 40: Paying and Prices ---
                ChallengeEntity(
                    id = 65028, lessonId = 851, type = ChallengeType.MATCH_PAIRS,
                    question = "Match the money words",
                    orderIndex = 0,
                ),
                ChallengeEntity(
                    id = 65029, lessonId = 851, type = ChallengeType.MATCH_PAIRS,
                    question = "Match the paying words",
                    orderIndex = 1,
                ),
                ChallengeEntity(
                    id = 65030, lessonId = 851, type = ChallengeType.MATCH_PAIRS,
                    question = "Match the shop types",
                    orderIndex = 2,
                ),
                ChallengeEntity(
                    id = 65031, lessonId = 851, type = ChallengeType.MATCH_PAIRS,
                    question = "Match the words for price and money",
                    orderIndex = 3,
                ),
                ChallengeEntity(
                    id = 65014, lessonId = 851, type = ChallengeType.SELECT,
                    question = "Which one means 'a convenience store'?",
                    audioSrc = "asset:///audio/ja/konbini.ogg",
                    orderIndex = 4,
                    ruleText = "コンビニ is a katakana loanword: koNBI ni. The ビ carries the dakuten of bi, so the ear hears konbini and never konbinni.\nスーパー is the supermarket, 薬屋 the pharmacy and 書店 the bookshop. All four are places that sell things, which is exactly why they are the distractors: only コンビニ is the corner shop that is open late.",
                ),
                ChallengeEntity(
                    id = 65015, lessonId = 851, type = ChallengeType.SELECT,
                    question = "Which one means 'tax included'?",
                    audioSrc = "asset:///audio/ja/zeikomi.ogg",
                    orderIndex = 5,
                    ruleText = "税込 is 税 (tax) + 込 (mi, as in 込む, to put in): the price already has the tax put in. It is zeikomi, and 税 is read zei here.\n割引 is money taken off and 値札 is the price card. 予算 is the amount of money you decided to spend. 税込 is the label that says the tax is already inside the number.",
                ),
                ChallengeEntity(
                    id = 65016, lessonId = 851, type = ChallengeType.SELECT,
                    question = "Which one means 'a receipt'?",
                    audioSrc = "asset:///audio/ja/ryoushosho.ogg",
                    orderIndex = 6,
                    ruleText = "領収書 is 領収 (receiving) + 書 (a document). The 書 is read sho, so ryouSHOshO, with a small ょ in each of the two syllables.\n割引券 is the coupon that takes money off before you pay, 会計 is the bill itself, and 値札 is the price tag on the shelf. The receipt is the one piece of paper you keep: 領収書.",
                ),
                ChallengeEntity(
                    id = 65017, lessonId = 851, type = ChallengeType.SELECT,
                    question = "Which one means 'a fish shop'?",
                    audioSrc = "asset:///audio/ja/sakanaya.ogg",
                    orderIndex = 7,
                    ruleText = "魚屋 is 魚 (fish) + 屋 (shop), said sakana-ya. The 魚 is the same one as in 魚 and 釣り, and the 屋 is the same 屋 as in 屋台 and 本屋.\n薬屋 sells medicine, 八百屋 sells vegetables, and 書店 sells books. They are all 屋 words, which is what makes them the distractors: the one that sells fish is 魚屋.",
                ),
                ChallengeEntity(
                    id = 65018, lessonId = 851, type = ChallengeType.CONJUGATE,
                    question = "Which one means 'How much is it altogether'?",
                    audioSrc = "asset:///audio/ja/goukeiwaikuradesuka.ogg",
                    orderIndex = 8,
                    grammaticalFocus = "ja.na_adjective",
                    ruleText = "合計 is a な-adjective, and a な-adjective never changes its stem: 合計 + です attaches です straight on, which is the polite copula doing the work, not a conjugation of 合計.\n合計はいくらだった is the plain 断定形, so it drops the polite ending the question asks for. 合計である is the plain written copula である, which is the register wrong. 合計がいくらですか puts が where the topic marker belongs: は names what is being asked about, and this question asks about 合計.",
                ),
                ChallengeEntity(
                    id = 65019, lessonId = 851, type = ChallengeType.CONJUGATE,
                    question = "Which one means 'I can't buy it by card'?",
                    audioSrc = "asset:///audio/ja/kaadodewakaemasen.ogg",
                    orderIndex = 9,
                    grammaticalFocus = "ja.ability_polite",
                    ruleText = "買えません is the polite potential negative: 買う → 買えます → 買えません. The う-stem changes to え, and then ません makes the negative, so kaemasen and not kaimasen. で は carries the は of contrast, so de wa says 'by card rather than some other way', and the は is read wa after で.\nカードでは買います is the plain ability to buy, with nothing said about being unable to. カードでは買わなくてもいい is 〜てもいい, which is permission, and it drops the polite です. カードでは買わなくていい puts なくて where えません belongs, and ない is the plain negative where this slot wants the polite potential.",
                ),
                ChallengeEntity(
                    id = 65105, lessonId = 851, type = ChallengeType.LISTEN,
                    question = "Tap what you hear",
                    audioSrc = "asset:///audio/ja/konomisenoyasaiwayasuidesu.ogg",
                    orderIndex = 10,
                    grammaticalFocus = "ja.i_adjective",
                    ruleText = "安いです is the polite present of the い-adjective 安い: the bare stem plus です, so yasui and not yasuku.\nこの店の野菜は安いくです glues the negative stem く onto です, which is never a polite ending. この店の野菜は安いでした hangs です past tense onto an い-adjective, which takes かった and never でした.",
                ),
                ChallengeEntity(
                    id = 65104, lessonId = 851, type = ChallengeType.STORY,
                    question = "📖 お会計\n\n\n店員: 「お会計をお願いします。」\n私: 「これ、現金でお願いします。」\n店員: 「かしこまりました。領収書をお渡しします。」\n私: 「ありがとうございます。」\n\n❓ What will the customer get from the shop assistant?",
                    orderIndex = 11,
                    ruleText = "領収書 is what お渡しします, what the assistant hands over with the change: ryouSHOshO, with a small ょ in each of the two syllables.\n割引券 is the coupon you hand in before paying, 値札 is the tag on the shelf, and 小銭 is the coins. None of them is a document the shop gives you at the till.",
                ),
                // --- Lesson 41: The Home ---
                ChallengeEntity(
                    id = 65020, lessonId = 852, type = ChallengeType.MATCH_PAIRS,
                    question = "Match the rooms",
                    orderIndex = 0,
                ),
                ChallengeEntity(
                    id = 65021, lessonId = 852, type = ChallengeType.MATCH_PAIRS,
                    question = "Match the furniture and machines",
                    orderIndex = 1,
                ),
                ChallengeEntity(
                    id = 65022, lessonId = 852, type = ChallengeType.MATCH_PAIRS,
                    question = "Match the things in the kitchen",
                    orderIndex = 2,
                ),
                ChallengeEntity(
                    id = 65023, lessonId = 852, type = ChallengeType.MATCH_PAIRS,
                    question = "Match the describing words",
                    orderIndex = 3,
                ),
                ChallengeEntity(
                    id = 65024, lessonId = 852, type = ChallengeType.SELECT,
                    question = "Which one means the kitchen?",
                    audioSrc = "asset:///audio/ja/daidokoro.ogg",
                    orderIndex = 4,
                    ruleText = "台所 is the place where you cook: the older spelling is 厨, and 所 is a place. It is daiDOKoro — the second syllable is the low do, not a high one.\n洗面所 is the washroom, 寝室 the bedroom, and 冷蔵庫 the fridge. All three are rooms or objects in a flat, and the one where you cook is 台所.",
                ),
                ChallengeEntity(
                    id = 65025, lessonId = 852, type = ChallengeType.SELECT,
                    question = "Which one means 'a washing machine'?",
                    audioSrc = "asset:///audio/ja/sentakuki.ogg",
                    orderIndex = 5,
                    ruleText = "洗濯機 is 洗濯 (laundry) + 機 (a machine), said sentaKUKI: the き carries a small つ in its second mora.\n冷蔵庫 is the fridge, 掃除機 the vacuum cleaner, and 茶碗 a rice bowl. Two machines and a piece of crockery, and only one of them washes your clothes.",
                ),
                ChallengeEntity(
                    id = 65106, lessonId = 852, type = ChallengeType.LISTEN,
                    question = "Tap what you hear",
                    audioSrc = "asset:///audio/ja/konoheyawahiroidesu.ogg",
                    orderIndex = 6,
                    grammaticalFocus = "ja.i_adjective",
                    ruleText = "広いです is the polite present of the い-adjective 広い: the bare stem plus です, so hiroi and not hiroku.\nこの部屋は広くなかった is the plain past negative, so the room is no longer spacious. この部屋は広いでした hangs です past tense onto an い-adjective, which takes かった and never でした.",
                ),
                ChallengeEntity(
                    id = 65026, lessonId = 852, type = ChallengeType.FILL_BLANK,
                    question = "去年、日本へ___。",
                    audioSrc = "asset:///audio/ja/ikimashita.ogg",
                    orderIndex = 7,
                    grammaticalFocus = "ja.past_polite",
                    acceptedAnswers = "行きました|いきました",
                    ruleText = "去年、日本へ行きました is the polite past: 行く → 行きます → 行きました, and 去年 is last year so the journey is over. で is not used with 行く to a country: 日本へ takes the directional へ.\n去年、日本へ行きます is the polite present, so nothing in it says the journey is finished. 去年、日本へ行きましたか is the polite past turned into a question, and this item states a fact. 去年、日本へ行ってください is a request addressed to someone else, and the blank here is the speaker's own report of what they did.",
                ),
                ChallengeEntity(
                    id = 65027, lessonId = 852, type = ChallengeType.SELECT,
                    question = "Which one says 'I have lived here for three years'?",
                    orderIndex = 8,
                    grammaticalFocus = "ja.te_form",
                    ruleText = "住んでいます is 住む + ています, the polite present progressive: I still live here. Here is に, because 住む takes に for the place someone lives in.\nここに三年住みました is the polite past, so the living is over. ここに三年住んでいますか is the same sentence turned into a question, which is the shape of an ask rather than a statement. ここに三年住んでいません is the progressive negative, so the living has stopped.",
                ),
                ChallengeEntity(
                    id = 65107, lessonId = 852, type = ChallengeType.LISTEN,
                    question = "Tap what you hear",
                    audioSrc = "asset:///audio/ja/mainichikonoheyaosoujishimasu.ogg",
                    orderIndex = 9,
                    grammaticalFocus = "ja.polite_verb",
                    ruleText = "掃除します is the polite present of the ichidan verb 掃除する: drop る, and す becomes します.\n毎日この部屋を掃除しません is the polite negative present, so the sweeping does not happen. 毎日この部屋を掃除でした is a noun given a copula, and 掃除する is a する-verb — it takes します, not です.",
                ),
                ChallengeEntity(
                    id = 65108, lessonId = 852, type = ChallengeType.STORY,
                    question = "📖 買い物\n\n\n母: 「冷蔵庫に何もないね。買い物に行こう。」\n私: 「うん。野菜と牛乳を買おう。」\n母: 「いいわ。あと、おさとうも。」\n私: 「冷蔵庫に入れておくね。」\n\n❓ What do the mother and child decide to do?",
                    orderIndex = 10,
                    ruleText = "買い物 is 買物 in the modern spelling: 買 (to buy) + 物 (a thing), said kaimono. The 物 is mono, so the second syllable is long — the opposite of 食べ物, where the second syllable is short.\n台所 is the kitchen, 洗面所 the washroom, and 寝室 the bedroom. All three are rooms in the flat, and the one that means a trip to the shops is 買い物.",
                ),
            ),
            options = listOf(

                ChallengeOptionEntity(id = 6500001, challengeId = 65000, text = "店", romaji = "mise", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500002, challengeId = 65000, text = "shop", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500003, challengeId = 65000, text = "靴", romaji = "kutsu", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500004, challengeId = 65000, text = "shoes", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500005, challengeId = 65000, text = "帽子", romaji = "boshi", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500006, challengeId = 65000, text = "hat", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500007, challengeId = 65000, text = "かばん", romaji = "kaban", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500008, challengeId = 65000, text = "bag", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500009, challengeId = 65000, text = "服", romaji = "fuku", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500010, challengeId = 65000, text = "clothes", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500011, challengeId = 65000, text = "靴下", romaji = "kutsushita", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500012, challengeId = 65000, text = "socks", romaji = null, correct = true, errorTag = null),

                ChallengeOptionEntity(id = 6500013, challengeId = 65001, text = "買う", romaji = "kau", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500014, challengeId = 65001, text = "to buy", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500015, challengeId = 65001, text = "値段", romaji = "nedan", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500016, challengeId = 65001, text = "price", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500017, challengeId = 65001, text = "安い", romaji = "yasui", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500018, challengeId = 65001, text = "cheap", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500019, challengeId = 65001, text = "選ぶ", romaji = "erabu", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500020, challengeId = 65001, text = "to choose", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500021, challengeId = 65001, text = "割引", romaji = "waribiki", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500022, challengeId = 65001, text = "discount", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500023, challengeId = 65001, text = "値札", romaji = "nefuda", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500024, challengeId = 65001, text = "price tag", romaji = null, correct = true, errorTag = null),

                ChallengeOptionEntity(id = 6500025, challengeId = 65002, text = "試着室", romaji = "shichakushitsu", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500026, challengeId = 65002, text = "fitting room", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500027, challengeId = 65002, text = "売り場", romaji = "uriba", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500028, challengeId = 65002, text = "sales area", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500029, challengeId = 65002, text = "営業中", romaji = "eigyouchuu", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500030, challengeId = 65002, text = "open for business", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500031, challengeId = 65002, text = "閉店", romaji = "heiten", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500032, challengeId = 65002, text = "closing time", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500033, challengeId = 65002, text = "屋台", romaji = "yatai", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500034, challengeId = 65002, text = "street food stall", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500035, challengeId = 65002, text = "百貨店", romaji = "hyakkaten", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500036, challengeId = 65002, text = "department store", romaji = null, correct = true, errorTag = null),

                ChallengeOptionEntity(id = 6500037, challengeId = 65003, text = "古い", romaji = "furui", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500038, challengeId = 65003, text = "old", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500039, challengeId = 65003, text = "サイズ", romaji = "saizu", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500040, challengeId = 65003, text = "size", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500041, challengeId = 65003, text = "大きい", romaji = "ookii", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500042, challengeId = 65003, text = "big", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500043, challengeId = 65003, text = "小さい", romaji = "chiisai", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500044, challengeId = 65003, text = "small", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500045, challengeId = 65003, text = "新しい", romaji = "atarashii", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500046, challengeId = 65003, text = "new", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500047, challengeId = 65003, text = "半額", romaji = "hangaku", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500048, challengeId = 65003, text = "half price", romaji = null, correct = true, errorTag = null),

                ChallengeOptionEntity(id = 6500049, challengeId = 65004, text = "定価", romaji = "teika", correct = true, errorTag = null, audioSrc = "asset:///audio/ja/teika.ogg"),
                ChallengeOptionEntity(id = 6500050, challengeId = 65004, text = "値段", romaji = "nedan", correct = false, errorTag = null),
                ChallengeOptionEntity(id = 6500051, challengeId = 65004, text = "値札", romaji = "nefuda", correct = false, errorTag = null),
                ChallengeOptionEntity(id = 6500052, challengeId = 65004, text = "割引", romaji = "waribiki", correct = false, errorTag = null),

                ChallengeOptionEntity(id = 6500053, challengeId = 65005, text = "売り切れ", romaji = "urikire", correct = true, errorTag = null, audioSrc = "asset:///audio/ja/urikire.ogg"),
                ChallengeOptionEntity(id = 6500054, challengeId = 65005, text = "営業中", romaji = "eigyouchuu", correct = false, errorTag = null),
                ChallengeOptionEntity(id = 6500055, challengeId = 65005, text = "閉店", romaji = "heiten", correct = false, errorTag = null),
                ChallengeOptionEntity(id = 6500056, challengeId = 65005, text = "値札", romaji = "nefuda", correct = false, errorTag = null),

                ChallengeOptionEntity(id = 6500057, challengeId = 65006, text = "野菜", romaji = "yasai", correct = true, errorTag = null, audioSrc = "asset:///audio/ja/yasai.ogg"),
                ChallengeOptionEntity(id = 6500058, challengeId = 65006, text = "果物", romaji = "katsumono", correct = false, errorTag = null),
                ChallengeOptionEntity(id = 6500059, challengeId = 65006, text = "肉", romaji = "niku", correct = false, errorTag = null),
                ChallengeOptionEntity(id = 6500060, challengeId = 65006, text = "魚", romaji = "sakana", correct = false, errorTag = null),

                ChallengeOptionEntity(id = 6500061, challengeId = 65007, text = "定休日", romaji = "teikyuubi", correct = true, errorTag = null, audioSrc = "asset:///audio/ja/teikyubi.ogg"),
                ChallengeOptionEntity(id = 6500062, challengeId = 65007, text = "閉店", romaji = "heiten", correct = false, errorTag = null),
                ChallengeOptionEntity(id = 6500063, challengeId = 65007, text = "営業中", romaji = "eigyouchuu", correct = false, errorTag = null),
                ChallengeOptionEntity(id = 6500064, challengeId = 65007, text = "半額", romaji = "hangaku", correct = false, errorTag = null),

                ChallengeOptionEntity(id = 6500065, challengeId = 65008, text = "きのう、", romaji = "kinou", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500066, challengeId = 65008, text = "あの店で", romaji = "ano mise de", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500067, challengeId = 65008, text = "帽子を", romaji = "boshi o", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500068, challengeId = 65008, text = "買いました", romaji = "kaimashita", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500069, challengeId = 65008, text = "買った", romaji = "katta", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 6500070, challengeId = 65008, text = "あの店と", romaji = "ano mise to", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6500071, challengeId = 65008, text = "買う", romaji = "kau", correct = false, errorTag = "WRONG_REGISTER"),

                ChallengeOptionEntity(id = 6500072, challengeId = 65009, text = "現金で払わなければなりません。", romaji = "genkin de harawanakereba narimasen", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500073, challengeId = 65009, text = "現金で払う。", romaji = "genkin de harau", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6500074, challengeId = 65009, text = "現金で払わなくてもいいです。", romaji = "genkin de harawanakutemo ii desu", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6500075, challengeId = 65009, text = "現金で払わなかった。", romaji = "genkin de harawanakatta", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 6500076, challengeId = 65010, text = "大きい", romaji = "ookii", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500077, challengeId = 65010, text = "大きく", romaji = "ookiku", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6500078, challengeId = 65010, text = "大きいです", romaji = "ookii desu", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6500079, challengeId = 65010, text = "大きかった", romaji = "ookikatta", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 6500080, challengeId = 65011, text = "いっしょに映画を見ましょう。", romaji = "isshoni eiga o mimashou", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500081, challengeId = 65011, text = "いっしょに映画を見ましょうか。", romaji = "isshoni eiga o mimashou ka", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6500082, challengeId = 65011, text = "いっしょに映画を見よう。", romaji = "isshoni eiga o miyou", correct = false, errorTag = "WRONG_REGISTER"),
                ChallengeOptionEntity(id = 6500083, challengeId = 65011, text = "いっしょに映画を見ました。", romaji = "isshoni eiga o mimashita", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 6500084, challengeId = 65100, text = "この店の靴は安いです。", romaji = "kono mise no kutsu wa yasui desu", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500085, challengeId = 65100, text = "この店の靴は安いくです。", romaji = "kono mise no kutsu wa yasuku desu", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6500086, challengeId = 65100, text = "この店の靴は安いでした。", romaji = "kono mise no kutsu wa yasui deshita", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 6500087, challengeId = 65101, text = "きのう、あの店で靴を買いました。", romaji = "kinou ano mise de kutsu o kaimashita", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500088, challengeId = 65101, text = "きのう、あの店で靴を買います。", romaji = "kinou ano mise de kutsu o kaimasu", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 6500089, challengeId = 65101, text = "きのう、あの店で靴を買った。", romaji = "kinou ano mise de kutsu o katta", correct = false, errorTag = "WRONG_REGISTER"),

                ChallengeOptionEntity(id = 6500090, challengeId = 65102, text = "駅で切符を買わなければなりません。", romaji = "eki de kippu o kawanakereba narimasen", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500091, challengeId = 65102, text = "駅で切符を買わなくてもいいです。", romaji = "eki de kippu o kawanakutemo ii desu", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6500092, challengeId = 65102, text = "駅で切符を買いました。", romaji = "eki de kippu o kaimashita", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 6500093, challengeId = 65103, text = "商店街", romaji = "shotengai", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500094, challengeId = 65103, text = "百貨店", romaji = "hyakkaten", correct = false, errorTag = "UNRELATED"),
                ChallengeOptionEntity(id = 6500095, challengeId = 65103, text = "屋台", romaji = "yatai", correct = false, errorTag = "UNRELATED"),
                ChallengeOptionEntity(id = 6500096, challengeId = 65103, text = "試着室", romaji = "shichakushitsu", correct = false, errorTag = "UNRELATED"),

                ChallengeOptionEntity(id = 6500097, challengeId = 65028, text = "お金", romaji = "okane", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500098, challengeId = 65028, text = "money", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500099, challengeId = 65028, text = "現金", romaji = "genkin", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500100, challengeId = 65028, text = "cash", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500101, challengeId = 65028, text = "おつり", romaji = "otsuri", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500102, challengeId = 65028, text = "change", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500103, challengeId = 65028, text = "札", romaji = "satsu", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500104, challengeId = 65028, text = "banknote", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500105, challengeId = 65028, text = "小銭", romaji = "kozeni", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500106, challengeId = 65028, text = "coins", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500107, challengeId = 65028, text = "財布", romaji = "saifu", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500108, challengeId = 65028, text = "wallet", romaji = null, correct = true, errorTag = null),

                ChallengeOptionEntity(id = 6500109, challengeId = 65029, text = "払う", romaji = "harau", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500110, challengeId = 65029, text = "to pay", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500111, challengeId = 65029, text = "会計", romaji = "kaikei", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500112, challengeId = 65029, text = "the bill", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500113, challengeId = 65029, text = "領収書", romaji = "ryoushuusho", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500114, challengeId = 65029, text = "receipt", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500115, challengeId = 65029, text = "両替", romaji = "riougae", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500116, challengeId = 65029, text = "changing money", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500117, challengeId = 65029, text = "カウンター", romaji = "kauntaa", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500118, challengeId = 65029, text = "the counter", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500119, challengeId = 65029, text = "値札", romaji = "nefuda", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500120, challengeId = 65029, text = "price tag", romaji = null, correct = true, errorTag = null),

                ChallengeOptionEntity(id = 6500121, challengeId = 65030, text = "スーパー", romaji = "suupaa", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500122, challengeId = 65030, text = "supermarket", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500123, challengeId = 65030, text = "コンビニ", romaji = "konbini", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500124, challengeId = 65030, text = "convenience store", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500125, challengeId = 65030, text = "書店", romaji = "shoten", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500126, challengeId = 65030, text = "bookshop", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500127, challengeId = 65030, text = "薬屋", romaji = "kusuriya", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500128, challengeId = 65030, text = "pharmacy", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500129, challengeId = 65030, text = "魚屋", romaji = "sakana ya", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500130, challengeId = 65030, text = "fish shop", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500131, challengeId = 65030, text = "八百屋", romaji = "yaoya", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500132, challengeId = 65030, text = "greengrocer", romaji = null, correct = true, errorTag = null),

                ChallengeOptionEntity(id = 6500133, challengeId = 65031, text = "予算", romaji = "yosan", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500134, challengeId = 65031, text = "budget", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500135, challengeId = 65031, text = "税込", romaji = "zeikomi", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500136, challengeId = 65031, text = "tax included", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500137, challengeId = 65031, text = "割引券", romaji = "waribikiken", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500138, challengeId = 65031, text = "discount coupon", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500139, challengeId = 65031, text = "品物", romaji = "shinamono", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500140, challengeId = 65031, text = "goods", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500141, challengeId = 65031, text = "封筒", romaji = "fuutoo", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500142, challengeId = 65031, text = "an envelope", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500143, challengeId = 65031, text = "切手", romaji = "kitte", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500144, challengeId = 65031, text = "a stamp", romaji = null, correct = true, errorTag = null),

                ChallengeOptionEntity(id = 6500145, challengeId = 65014, text = "コンビニ", romaji = "konbini", correct = true, errorTag = null, audioSrc = "asset:///audio/ja/konbini.ogg"),
                ChallengeOptionEntity(id = 6500146, challengeId = 65014, text = "スーパー", romaji = "suupaa", correct = false, errorTag = null),
                ChallengeOptionEntity(id = 6500147, challengeId = 65014, text = "薬屋", romaji = "kusuriya", correct = false, errorTag = null),
                ChallengeOptionEntity(id = 6500148, challengeId = 65014, text = "書店", romaji = "shoten", correct = false, errorTag = null),

                ChallengeOptionEntity(id = 6500149, challengeId = 65015, text = "税込", romaji = "zeikomi", correct = true, errorTag = null, audioSrc = "asset:///audio/ja/zeikomi.ogg"),
                ChallengeOptionEntity(id = 6500150, challengeId = 65015, text = "割引", romaji = "waribiki", correct = false, errorTag = null),
                ChallengeOptionEntity(id = 6500151, challengeId = 65015, text = "値札", romaji = "nefuda", correct = false, errorTag = null),
                ChallengeOptionEntity(id = 6500152, challengeId = 65015, text = "予算", romaji = "yosan", correct = false, errorTag = null),

                ChallengeOptionEntity(id = 6500153, challengeId = 65016, text = "領収書", romaji = "ryoushuusho", correct = true, errorTag = null, audioSrc = "asset:///audio/ja/ryoushosho.ogg"),
                ChallengeOptionEntity(id = 6500154, challengeId = 65016, text = "割引券", romaji = "waribikiken", correct = false, errorTag = null),
                ChallengeOptionEntity(id = 6500155, challengeId = 65016, text = "会計", romaji = "kaikei", correct = false, errorTag = null),
                ChallengeOptionEntity(id = 6500156, challengeId = 65016, text = "値札", romaji = "nefuda", correct = false, errorTag = null),

                ChallengeOptionEntity(id = 6500157, challengeId = 65017, text = "魚屋", romaji = "sakana ya", correct = true, errorTag = null, audioSrc = "asset:///audio/ja/sakanaya.ogg"),
                ChallengeOptionEntity(id = 6500158, challengeId = 65017, text = "薬屋", romaji = "kusuriya", correct = false, errorTag = null),
                ChallengeOptionEntity(id = 6500159, challengeId = 65017, text = "八百屋", romaji = "yaoya", correct = false, errorTag = null),
                ChallengeOptionEntity(id = 6500160, challengeId = 65017, text = "書店", romaji = "shoten", correct = false, errorTag = null),

                ChallengeOptionEntity(id = 6500161, challengeId = 65018, text = "合計はいくらですか。", romaji = "goukei wa ikura desu ka", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500162, challengeId = 65018, text = "合計はいくらだった。", romaji = "goukei wa ikura datta", correct = false, errorTag = "WRONG_REGISTER"),
                ChallengeOptionEntity(id = 6500163, challengeId = 65018, text = "合計である。", romaji = "goukei de aru", correct = false, errorTag = "WRONG_REGISTER"),
                ChallengeOptionEntity(id = 6500164, challengeId = 65018, text = "合計がいくらですか。", romaji = "goukei ga ikura desu ka", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 6500165, challengeId = 65019, text = "カードでは買えません。", romaji = "kaado de wa kaemasen", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500166, challengeId = 65019, text = "カードでは買います。", romaji = "kaado de wa kaimasu", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 6500167, challengeId = 65019, text = "カードでは買わなくてもいい。", romaji = "kaado de wa kawanakutemo ii", correct = false, errorTag = "WRONG_REGISTER"),
                ChallengeOptionEntity(id = 6500168, challengeId = 65019, text = "カードでは買わなくていい。", romaji = "kaado de wa kawanakute ii", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 6500169, challengeId = 65105, text = "この店の野菜は安いです。", romaji = "kono mise no yasai wa yasui desu", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500170, challengeId = 65105, text = "この店の野菜は安いくです。", romaji = "kono mise no yasai wa yasuku desu", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6500171, challengeId = 65105, text = "この店の野菜は安いでした。", romaji = "kono mise no yasai wa yasui deshita", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 6500172, challengeId = 65104, text = "領収書", romaji = "ryoushuusho", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500173, challengeId = 65104, text = "割引券", romaji = "waribikiken", correct = false, errorTag = "UNRELATED"),
                ChallengeOptionEntity(id = 6500174, challengeId = 65104, text = "値札", romaji = "nefuda", correct = false, errorTag = "UNRELATED"),
                ChallengeOptionEntity(id = 6500175, challengeId = 65104, text = "小銭", romaji = "kozeni", correct = false, errorTag = "UNRELATED"),

                ChallengeOptionEntity(id = 6500176, challengeId = 65020, text = "台所", romaji = "daidokoro", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500177, challengeId = 65020, text = "kitchen", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500178, challengeId = 65020, text = "寝室", romaji = "shinshitsu", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500179, challengeId = 65020, text = "bedroom", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500180, challengeId = 65020, text = "居間", romaji = "ima", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500181, challengeId = 65020, text = "living room", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500182, challengeId = 65020, text = "洗面所", romaji = "senmenjo", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500183, challengeId = 65020, text = "washroom", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500184, challengeId = 65020, text = "冷蔵庫", romaji = "reezooko", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500185, challengeId = 65020, text = "fridge", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500186, challengeId = 65020, text = "窓", romaji = "mado", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500187, challengeId = 65020, text = "window", romaji = null, correct = true, errorTag = null),

                ChallengeOptionEntity(id = 6500188, challengeId = 65021, text = "テーブル", romaji = "teeburu", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500189, challengeId = 65021, text = "table", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500190, challengeId = 65021, text = "椅子", romaji = "isu", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500191, challengeId = 65021, text = "chair", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500192, challengeId = 65021, text = "洗濯機", romaji = "sentakuki", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500193, challengeId = 65021, text = "washing machine", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500194, challengeId = 65021, text = "掃除機", romaji = "soojiki", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500195, challengeId = 65021, text = "vacuum cleaner", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500196, challengeId = 65021, text = "ドア", romaji = "doa", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500197, challengeId = 65021, text = "door", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500198, challengeId = 65021, text = "電気", romaji = "denki", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500199, challengeId = 65021, text = "electricity", romaji = null, correct = true, errorTag = null),

                ChallengeOptionEntity(id = 6500200, challengeId = 65022, text = "包丁", romaji = "houchou", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500201, challengeId = 65022, text = "kitchen knife", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500202, challengeId = 65022, text = "皿", romaji = "sara", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500203, challengeId = 65022, text = "plate", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500204, challengeId = 65022, text = "箸", romaji = "hashi", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500205, challengeId = 65022, text = "chopsticks", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500206, challengeId = 65022, text = "茶碗", romaji = "chawan", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500207, challengeId = 65022, text = "rice bowl", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500208, challengeId = 65022, text = "鍋", romaji = "nabe", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500209, challengeId = 65022, text = "cooking pot", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500210, challengeId = 65022, text = "飲み物", romaji = "nomimono", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500211, challengeId = 65022, text = "a drink", romaji = null, correct = true, errorTag = null),

                ChallengeOptionEntity(id = 6500212, challengeId = 65023, text = "広い", romaji = "hiroi", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500213, challengeId = 65023, text = "spacious", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500214, challengeId = 65023, text = "狭い", romaji = "semai", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500215, challengeId = 65023, text = "narrow", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500216, challengeId = 65023, text = "新しい", romaji = "atarashii", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500217, challengeId = 65023, text = "new", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500218, challengeId = 65023, text = "古い", romaji = "furui", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500219, challengeId = 65023, text = "old", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500220, challengeId = 65023, text = "明るい", romaji = "akarui", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500221, challengeId = 65023, text = "bright", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500222, challengeId = 65023, text = "暗い", romaji = "kurai", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500223, challengeId = 65023, text = "dark", romaji = null, correct = true, errorTag = null),

                ChallengeOptionEntity(id = 6500224, challengeId = 65024, text = "台所", romaji = "daidokoro", correct = true, errorTag = null, audioSrc = "asset:///audio/ja/daidokoro.ogg"),
                ChallengeOptionEntity(id = 6500225, challengeId = 65024, text = "洗面所", romaji = "senmenjo", correct = false, errorTag = null),
                ChallengeOptionEntity(id = 6500226, challengeId = 65024, text = "寝室", romaji = "shinshitsu", correct = false, errorTag = null),
                ChallengeOptionEntity(id = 6500227, challengeId = 65024, text = "冷蔵庫", romaji = "reezooko", correct = false, errorTag = null),

                ChallengeOptionEntity(id = 6500228, challengeId = 65025, text = "洗濯機", romaji = "sentakuki", correct = true, errorTag = null, audioSrc = "asset:///audio/ja/sentakuki.ogg"),
                ChallengeOptionEntity(id = 6500229, challengeId = 65025, text = "冷蔵庫", romaji = "reezooko", correct = false, errorTag = null),
                ChallengeOptionEntity(id = 6500230, challengeId = 65025, text = "掃除機", romaji = "soojiki", correct = false, errorTag = null),
                ChallengeOptionEntity(id = 6500231, challengeId = 65025, text = "茶碗", romaji = "chawan", correct = false, errorTag = null),

                ChallengeOptionEntity(id = 6500232, challengeId = 65106, text = "この部屋は広いです。", romaji = "kono heya wa hiroi desu", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500233, challengeId = 65106, text = "この部屋は広くなかった。", romaji = "kono heya wa hiroku nakatta", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 6500234, challengeId = 65106, text = "この部屋は広いでした。", romaji = "kono heya wa hiroi deshita", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 6500235, challengeId = 65026, text = "行きました", romaji = "ikimashita", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500236, challengeId = 65026, text = "行きます。", romaji = "ikimasu", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 6500237, challengeId = 65026, text = "行きましたか。", romaji = "ikimashita ka", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6500238, challengeId = 65026, text = "去年、日本へ行ってください。", romaji = "kyonen nippon e itte kudasai", correct = false, errorTag = "WRONG_REGISTER"),

                ChallengeOptionEntity(id = 6500239, challengeId = 65027, text = "ここに三年住んでいます。", romaji = "koko ni sannen sunde imasu", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500240, challengeId = 65027, text = "ここに三年住みました。", romaji = "koko ni sannen sumimashita", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 6500241, challengeId = 65027, text = "ここに三年住んでいますか。", romaji = "koko ni sannen sunde imasu ka", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 6500242, challengeId = 65027, text = "ここに三年住んでいません。", romaji = "koko ni sannen sunde imasen", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 6500243, challengeId = 65107, text = "毎日この部屋を掃除します。", romaji = "mainichi kono heya o souji shimasu", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500244, challengeId = 65107, text = "毎日この部屋を掃除しません。", romaji = "mainichi kono heya o souji shimasen", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 6500245, challengeId = 65107, text = "毎日この部屋を掃除でした。", romaji = "mainichi kono heya o souji deshita", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 6500246, challengeId = 65108, text = "買い物", romaji = "kaimono", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 6500247, challengeId = 65108, text = "台所", romaji = "daidokoro", correct = false, errorTag = "UNRELATED"),
                ChallengeOptionEntity(id = 6500248, challengeId = 65108, text = "洗面所", romaji = "senmenjo", correct = false, errorTag = "UNRELATED"),
                ChallengeOptionEntity(id = 6500249, challengeId = 65108, text = "寝室", romaji = "shinshitsu", correct = false, errorTag = "UNRELATED"),
            ),
        ),
        // =====================================================================
        UnitPayload(
            unit = UnitEntity(
                id = 47,
                courseId = 2,
                title = "Unit 18: Getting Around & Going to Work",
                description = "The N4 nouns of a commute: the ticket, the platform, the office, and the people in it",
                orderIndex = 17,
            ),
            lessons = listOf(
                LessonEntity(id = 853, unitId = 47, title = "Lesson 42: The Train and the Ticket", orderIndex = 0),
                LessonEntity(id = 854, unitId = 47, title = "Lesson 43: At Work", orderIndex = 1),
                LessonEntity(id = 855, unitId = 47, title = "Lesson 44: The Week and the Weather", orderIndex = 2),
            ),
            challenges = listOf(
                // --- Lesson 42: The Train and the Ticket ---
                ChallengeEntity(
                    id = 65200, lessonId = 853, type = ChallengeType.MATCH_PAIRS,
                    question = "Match the ticket words",
                    orderIndex = 0,
                ),
                ChallengeEntity(
                    id = 65201, lessonId = 853, type = ChallengeType.MATCH_PAIRS,
                    question = "Match the station words",
                    orderIndex = 1,
                ),
                ChallengeEntity(
                    id = 65202, lessonId = 853, type = ChallengeType.MATCH_PAIRS,
                    question = "Match the people on the train",
                    orderIndex = 2,
                ),
                ChallengeEntity(
                    id = 65203, lessonId = 853, type = ChallengeType.MATCH_PAIRS,
                    question = "Match the words for the journey",
                    orderIndex = 3,
                ),
                ChallengeEntity(
                    id = 65204, lessonId = 853, type = ChallengeType.SELECT,
                    question = "Which one means 'a season ticket'?",
                    audioSrc = "asset:///audio/ja/teekiken.ogg",
                    orderIndex = 4,
                    ruleText = "定期券 is 定期 (a fixed term) + 券 (a voucher): the ticket that is valid for a month or a year. The 期 is read ki with a small い before the ん, so teiki.\n切符 is a single ticket, 往復券 the return ticket and 片道券 the one-way ticket. All four are things you hand over at the gate, and the one you buy once and use all month is 定期券.",
                ),
                ChallengeEntity(
                    id = 65205, lessonId = 853, type = ChallengeType.SELECT,
                    question = "Which one means 'the bus stop'?",
                    audioSrc = "asset:///audio/ja/teeryuujo.ogg",
                    orderIndex = 5,
                    ruleText = "停留所 is 停留 (a stop, as in 停留所) written with a kanji form you do not need: said teeRYUUjo. The り carries a long vowel, so ryuu, and the 所 is jo.\n待合室 is the indoor waiting room, 切符売り場 the ticket office, and 入口 the way in. Three places inside a station, and the one the bus stops at is 停留所.",
                ),
                ChallengeEntity(
                    id = 65206, lessonId = 853, type = ChallengeType.SELECT,
                    question = "Which one says 'the train leaves at nine'?",
                    orderIndex = 6,
                    grammaticalFocus = "ja.polite_verb",
                    ruleText = "出発します is 出発する, a する-verb in the polite present: 出発 + します. 九時に puts に on the hour, because a time takes に and not は.\n電車は九時に到着します is the arrival, not the departure, and the two nouns are not interchangeable. 電車は九時に出発しません is the polite negative. 電車は九時が出発します puts が on the hour, and が marks the subject of a transitive verb — 出発する has no object, so the hour goes under に.",
                ),
                ChallengeEntity(
                    id = 65207, lessonId = 853, type = ChallengeType.CONJUGATE,
                    question = "Which one means 'I change trains at the next station'?",
                    audioSrc = "asset:///audio/ja/tsuginoekidenorikaemasu.ogg",
                    orderIndex = 7,
                    grammaticalFocus = "ja.polite_verb",
                    ruleText = "乗り換えます is the polite present: 乗り換える → 乗り換えます. で marks the station where the change happens, and 換 is the 扌 plus 奐.\n次の駅で乗り換えますか is the same sentence as a question. 次の駅で乗り換えました is the polite past, so the change is over. 次の駅で乗り換えています is the progressive, built on the te-form 乗り換えて — and that is not the plain form the polite present is made from.",
                ),
                ChallengeEntity(
                    id = 65300, lessonId = 853, type = ChallengeType.LISTEN,
                    question = "Tap what you hear",
                    audioSrc = "asset:///audio/ja/denshagaokureteimasu.ogg",
                    orderIndex = 8,
                    grammaticalFocus = "ja.te_form",
                    ruleText = "遅れています is 遅れる + ています, the polite present progressive, so the train is late right now. が marks the train as the thing that is late.\n電車が遅れました is the polite past, so the train was late and is not any more. 電車は遅れています swaps が for は, and は marks the topic rather than the subject of an intransitive verb — the difference is audible as a pause.",
                ),
                ChallengeEntity(
                    id = 65301, lessonId = 853, type = ChallengeType.LISTEN,
                    question = "Tap what you hear",
                    audioSrc = "asset:///audio/ja/kippuoichimaikattekudasai.ogg",
                    orderIndex = 9,
                    grammaticalFocus = "ja.request_polite",
                    ruleText = "買ってください is the polite request: the て-form of 買う plus ください, so katte kudasai. 一枚 is ichiMAI — the counter for thin flat things, and the い carries no long vowel.\n切符を一枚買ってくださいです doubles the polite ending: ください is already the polite request, and です may not follow it. 切符を一枚買うてください separates 買う and てください, and ください only ever follows the て-form.",
                ),
                ChallengeEntity(
                    id = 65302, lessonId = 853, type = ChallengeType.STORY,
                    question = "📖 駅\n\n\n私: 「切符を一枚お願いします。」\n駅員: 「片道ですか、往復ですか。」\n私: 「往復で。定期券は持っています。」\n駅員: 「では、切符売り場へどうぞ。」\n\n❓ Where does the station staff send her?",
                    orderIndex = 10,
                    ruleText = "切符売り場 is where you buy the ticket: 切符 + 売り (selling) + 場 (a place), said kippuURIBA. The 売 carries the り, so the middle syllable is longer than the first.\n待合室 is where you sit and wait, 停留所 is where the bus stops, and 入口 is the way in. The 場 is the same 場 as in 駐車場 and 工場 — the counter for a place, and 売り場 is the part of a shop where a thing is sold.",
                ),
                // --- Lesson 43: At Work ---
                ChallengeEntity(
                    id = 65210, lessonId = 854, type = ChallengeType.MATCH_PAIRS,
                    question = "Match the job titles",
                    orderIndex = 0,
                ),
                ChallengeEntity(
                    id = 65211, lessonId = 854, type = ChallengeType.MATCH_PAIRS,
                    question = "Match the workplace words",
                    orderIndex = 1,
                ),
                ChallengeEntity(
                    id = 65212, lessonId = 854, type = ChallengeType.MATCH_PAIRS,
                    question = "Match the people at work",
                    orderIndex = 2,
                ),
                ChallengeEntity(
                    id = 65213, lessonId = 854, type = ChallengeType.MATCH_PAIRS,
                    question = "Match the place-of-work words",
                    orderIndex = 3,
                ),
                ChallengeEntity(
                    id = 65214, lessonId = 854, type = ChallengeType.SELECT,
                    question = "Which one means 'overtime'?",
                    audioSrc = "asset:///audio/ja/zangyoo.ogg",
                    orderIndex = 4,
                    ruleText = "残業 is 残 (to remain) + 業 (work): work you stay on past the hours. The 業 is read gyoo, so zangyoo, and the よ is long.\n休憩 is the break you get during the day, 出張 is a trip for work, and 給料 is the money that comes at the end of the month. The one that means working extra hours is 残業.",
                ),
                ChallengeEntity(
                    id = 65215, lessonId = 854, type = ChallengeType.SELECT,
                    question = "Which one means 'a CV'?",
                    audioSrc = "asset:///audio/ja/rirekisho.ogg",
                    orderIndex = 5,
                    ruleText = "履歴書 is 履歴 (a record) + 書 (a document): the document listing what you have done. The 書 is read sho.\n名刺 is the business card you hand over, 貯金 is the money you save, and 面接 is the meeting itself. The paper you write your history on is 履歴書.",
                ),
                ChallengeEntity(
                    id = 65216, lessonId = 854, type = ChallengeType.SELECT,
                    question = "Which one says 'I work for a company from nine to six'?",
                    orderIndex = 6,
                    grammaticalFocus = "ja.polite_verb",
                    ruleText = "働きます is the polite present of the godan verb 働く: the く becomes き and ます is added, so hatarakimasu. から marks the start of a span of time and まで marks the end of it — まで cannot stand alone here.\n私は九時から六時まで働きました is the polite past, so the working is over. 私は九時から六時に働きます drops まで and leaves the end of the span unsaid. 私は九時から六時まで働いています is the progressive, built on the te-form 働いて, and this item asks for the plain polite form.",
                ),
                ChallengeEntity(
                    id = 65217, lessonId = 854, type = ChallengeType.SELECT,
                    question = "Which one asks the boss for permission to go home early?",
                    orderIndex = 7,
                    grammaticalFocus = "ja.permission",
                    ruleText = "てもいいですか asks permission and expects an answer, so it carries both the 〜てもいい ending and the question ですか. Here 帰る is an ichidan verb, so the て-form is 帰って and nothing more may follow it before も.\n早めに帰ってください is a command, not an ask, and ください cannot follow ても. 早めに帰らなければ is the necessity ending, which states an obligation instead of requesting one. 早めに帰ってもいいです ends in です where the question needs ですか, and a statement gets no answer to come back.",
                ),
                ChallengeEntity(
                    id = 65303, lessonId = 854, type = ChallengeType.LISTEN,
                    question = "Tap what you hear",
                    audioSrc = "asset:///audio/ja/kaigiwasanjikarahajimarimasu.ogg",
                    orderIndex = 8,
                    grammaticalFocus = "ja.polite_verb",
                    ruleText = "始まります is the polite present of the godan verb 始まる: the る becomes り and ます is added, so hajimarimasu. から marks the point the meeting starts from.\n会議は三時に始まります drops から and leaves only the hour. 会議は三時から始まりました is the polite past, so the meeting has already begun — the two differ by one mora and a whole tense.",
                ),
                ChallengeEntity(
                    id = 65304, lessonId = 854, type = ChallengeType.STORY,
                    question = "📖 出勤\n\n\n部長: 「今日、九時からです。」\n私: 「はい、会議室に行きます。」\n部長: 「資料をコピーしてください。」\n私: 「コピーしてきます。」\n\n❓ What is the boss telling the worker to do?",
                    orderIndex = 9,
                    ruleText = "資料 is the material: 資 (goods) + 料 (material), and コピー is the katakana loanword for a photocopy, said koPII. ください is the polite request.\n給料をコピーしてください is the pay, 会議をコピーしてください the meeting, and 休憩をコピーしてください the break. All three take を the same way 資料 does, and only one of them is a thing you would photocopy.",
                ),
                // --- Lesson 44: The Week and the Weather ---
                ChallengeEntity(
                    id = 65220, lessonId = 855, type = ChallengeType.MATCH_PAIRS,
                    question = "Match the weather words",
                    orderIndex = 0,
                ),
                ChallengeEntity(
                    id = 65221, lessonId = 855, type = ChallengeType.MATCH_PAIRS,
                    question = "Match the more weather words",
                    orderIndex = 1,
                ),
                ChallengeEntity(
                    id = 65222, lessonId = 855, type = ChallengeType.MATCH_PAIRS,
                    question = "Match the seasons",
                    orderIndex = 2,
                ),
                ChallengeEntity(
                    id = 65223, lessonId = 855, type = ChallengeType.MATCH_PAIRS,
                    question = "Match the time-of-day words",
                    orderIndex = 3,
                ),
                ChallengeEntity(
                    id = 65224, lessonId = 855, type = ChallengeType.MATCH_PAIRS,
                    question = "Match the frequency words",
                    orderIndex = 4,
                ),
                ChallengeEntity(
                    id = 65225, lessonId = 855, type = ChallengeType.SELECT,
                    question = "Which one means 'the rainy season'?",
                    audioSrc = "asset:///audio/ja/tsuyu.ogg",
                    orderIndex = 5,
                    ruleText = "梅雨 is 梅 (the plum tree) + 雨 (rain), said tsuYU. Both kanji keep their own reading: 梅 is tsu and 雨 is yu, and neither is the reading you would guess from the meaning.\n季節 is any season, 春雨 is the light rain of early spring, and 曇り is the cloudiness itself. The one that names the wet weeks of early summer is 梅雨.",
                ),
                ChallengeEntity(
                    id = 65226, lessonId = 855, type = ChallengeType.SELECT,
                    question = "Which one means 'rarely'?",
                    audioSrc = "asset:///audio/ja/mettani.ogg",
                    orderIndex = 6,
                    ruleText = "めったに is written in kana and takes a negative: めったに + ない. The つ is small, so mettaNI.\nよく is often, 時々 is sometimes, and いつも is always. All four are frequency adverbs and three of them are positive; the one that needs ない in front of a verb is めったに.",
                ),
                ChallengeEntity(
                    id = 65227, lessonId = 855, type = ChallengeType.CONJUGATE,
                    question = "Which one means 'I have been to Japan before'?",
                    audioSrc = "asset:///audio/ja/nipponniittakotogaarimasu.ogg",
                    orderIndex = 7,
                    grammaticalFocus = "ja.experience",
                    ruleText = "Experience is 〜たことがあります: the plain past た, then こと (the する nominaliser) plus が, then あります. 行った + ことがあります, so itta koto ga arimasu. The が is what makes it a clause rather than a phrase.\n日本に行きます is the plain present, so nothing in it says you were ever there. 日本に行ったります is not a form: あります takes あ, and ります is the negative of あります — ありません — so nothing may stand between こと and あります. 日本に行ったことあります drops the が that binds the clause together, and the subject of あります has to be named.",
                ),
                ChallengeEntity(
                    id = 65228, lessonId = 855, type = ChallengeType.SELECT,
                    question = "Which one says 'It will snow tomorrow'?",
                    orderIndex = 8,
                    grammaticalFocus = "ja.polite_verb",
                    ruleText = "雪になります is 雪 + になる: the noun 雪, に, and the godan verb なる. It is the polite present, so narimasu.\n明日は雪です treats 雪 as a plain noun with a copula, and a weather noun like this takes になる, not です. 明日は雪がなります puts が where に belongs, and になる is a directional change: something turns into snow rather than being snow. 明日は雪でした is the polite past, so the snow is already over.",
                ),
                ChallengeEntity(
                    id = 65305, lessonId = 855, type = ChallengeType.LISTEN,
                    question = "Tap what you hear",
                    audioSrc = "asset:///audio/ja/kyoowakazegatsuyoidesu.ogg",
                    orderIndex = 9,
                    grammaticalFocus = "ja.i_adjective",
                    ruleText = "強いです is the polite present of the い-adjective 強い: the bare stem plus です, so tsuyoi and not tsuyoku.\n今日は風が強くです glues the negative stem く onto です, which is never a polite ending. 今日は風が強いでした hangs です past tense onto an い-adjective, which takes かった and never でした.",
                ),
                ChallengeEntity(
                    id = 65306, lessonId = 855, type = ChallengeType.STORY,
                    question = "📖 天気\n\n\n同僚: 「今朝は雨でしたよ。」\n私: 「ええ、突然の雨だった。」\n同僚: 「今日は空に雲が多いでしょう。」\n私: 「でも、夕方からは晴れるかもしれません。」\n\n❓ What does the colleague predict about the sky today?",
                    orderIndex = 10,
                    ruleText = "雲 is the cloud itself: the older spelling is 雲, read kumo, and the 雲 in 曇り is the same character.\n雪 is snow, 風 is wind and 霜 is frost. All four are things that appear in the sky, and the one the colleague says there is a lot of is 雲.",
                ),
            ),
            options = listOf(

                ChallengeOptionEntity(id = 7500001, challengeId = 65200, text = "切符", romaji = "kippu", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500002, challengeId = 65200, text = "ticket", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500003, challengeId = 65200, text = "定期券", romaji = "teekiken", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500004, challengeId = 65200, text = "season ticket", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500005, challengeId = 65200, text = "往復", romaji = "oofuku", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500006, challengeId = 65200, text = "return", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500007, challengeId = 65200, text = "片道", romaji = "katamichi", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500008, challengeId = 65200, text = "one way", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500009, challengeId = 65200, text = "券", romaji = "ken", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500010, challengeId = 65200, text = "a voucher", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500011, challengeId = 65200, text = "切り取り線", romaji = "kiritorisen", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500012, challengeId = 65200, text = "the perforation", romaji = null, correct = true, errorTag = null),

                ChallengeOptionEntity(id = 7500013, challengeId = 65201, text = "停留所", romaji = "teiryuujyo", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500014, challengeId = 65201, text = "bus stop", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500015, challengeId = 65201, text = "乗り換え", romaji = "norikae", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500016, challengeId = 65201, text = "a change", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500017, challengeId = 65201, text = "出口", romaji = "deguchi", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500018, challengeId = 65201, text = "exit", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500019, challengeId = 65201, text = "入口", romaji = "iriguchi", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500020, challengeId = 65201, text = "entrance", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500021, challengeId = 65201, text = "切符売り場", romaji = "kippuuriba", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500022, challengeId = 65201, text = "ticket office", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500023, challengeId = 65201, text = "待合室", romaji = "machiaishitsu", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500024, challengeId = 65201, text = "waiting room", romaji = null, correct = true, errorTag = null),

                ChallengeOptionEntity(id = 7500025, challengeId = 65202, text = "運転手", romaji = "untenshu", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500026, challengeId = 65202, text = "driver", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500027, challengeId = 65202, text = "車掌", romaji = "shashoo", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500028, challengeId = 65202, text = "conductor", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500029, challengeId = 65202, text = "乗客", romaji = "joukyaku", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500030, challengeId = 65202, text = "passenger", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500031, challengeId = 65202, text = "駅員", romaji = "ekiin", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500032, challengeId = 65202, text = "station staff", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500033, challengeId = 65202, text = "車内", romaji = "shanai", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500034, challengeId = 65202, text = "inside the train", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500035, challengeId = 65202, text = "途中", romaji = "tochuu", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500036, challengeId = 65202, text = "on the way", romaji = null, correct = true, errorTag = null),

                ChallengeOptionEntity(id = 7500037, challengeId = 65203, text = "出発", romaji = "shuppatsu", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500038, challengeId = 65203, text = "departure", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500039, challengeId = 65203, text = "到着", romaji = "touchaku", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500040, challengeId = 65203, text = "arrival", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500041, challengeId = 65203, text = "遅れ", romaji = "okure", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500042, challengeId = 65203, text = "a delay", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500043, challengeId = 65203, text = "満員", romaji = "manin", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500044, challengeId = 65203, text = "full", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500045, challengeId = 65203, text = "空席", romaji = "kuuseki", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500046, challengeId = 65203, text = "a free seat", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500047, challengeId = 65203, text = "折り返し", romaji = "orikaeshi", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500048, challengeId = 65203, text = "the return service", romaji = null, correct = true, errorTag = null),

                ChallengeOptionEntity(id = 7500049, challengeId = 65204, text = "定期券", romaji = "teekiken", correct = true, errorTag = null, audioSrc = "asset:///audio/ja/teekiken.ogg"),
                ChallengeOptionEntity(id = 7500050, challengeId = 65204, text = "切符", romaji = "kippu", correct = false, errorTag = null),
                ChallengeOptionEntity(id = 7500051, challengeId = 65204, text = "往復券", romaji = "oofukuken", correct = false, errorTag = null),
                ChallengeOptionEntity(id = 7500052, challengeId = 65204, text = "片道券", romaji = "katamichiken", correct = false, errorTag = null),

                ChallengeOptionEntity(id = 7500053, challengeId = 65205, text = "停留所", romaji = "teiryuujyo", correct = true, errorTag = null, audioSrc = "asset:///audio/ja/teeryuujo.ogg"),
                ChallengeOptionEntity(id = 7500054, challengeId = 65205, text = "待合室", romaji = "machiaishitsu", correct = false, errorTag = null),
                ChallengeOptionEntity(id = 7500055, challengeId = 65205, text = "切符売り場", romaji = "kippuuriba", correct = false, errorTag = null),
                ChallengeOptionEntity(id = 7500056, challengeId = 65205, text = "入口", romaji = "iriguchi", correct = false, errorTag = null),

                ChallengeOptionEntity(id = 7500057, challengeId = 65206, text = "電車は九時に出発します。", romaji = "densha wa kuji ni shuppatsu shimasu", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500058, challengeId = 65206, text = "電車は九時に到着します。", romaji = "densha wa kuji ni touchaku shimasu", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 7500059, challengeId = 65206, text = "電車は九時に出発しません。", romaji = "densha wa kuji ni shuppatsu shimasen", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 7500060, challengeId = 65206, text = "電車は九時が出発します。", romaji = "densha wa kuji ga shuppatsu shimasu", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 7500061, challengeId = 65207, text = "次の駅で乗り換えます。", romaji = "tsugi no eki de norikaemasu", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500062, challengeId = 65207, text = "次の駅で乗り換えますか。", romaji = "tsugi no eki de norikaemasu ka", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 7500063, challengeId = 65207, text = "次の駅で乗り換えました。", romaji = "tsugi no eki de norikaemashita", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 7500064, challengeId = 65207, text = "次の駅で乗り換えています。", romaji = "tsugi no eki de norikaete imasu", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 7500065, challengeId = 65300, text = "電車が遅れています。", romaji = "densha ga okurete imasu", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500066, challengeId = 65300, text = "電車が遅れました。", romaji = "densha ga okuremashita", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 7500067, challengeId = 65300, text = "電車は遅れています。", romaji = "densha wa okurete imasu", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 7500068, challengeId = 65301, text = "切符を一枚買ってください。", romaji = "kippu o ichimai katte kudasai", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500069, challengeId = 65301, text = "切符を一枚買ってくださいです。", romaji = "kippu o ichimai katte kudasai desu", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 7500070, challengeId = 65301, text = "切符を一枚買うてください。", romaji = "kippu o ichimai kau te kudasai", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 7500071, challengeId = 65302, text = "切符売り場", romaji = "kippuuriba", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500072, challengeId = 65302, text = "待合室", romaji = "machiaishitsu", correct = false, errorTag = "UNRELATED"),
                ChallengeOptionEntity(id = 7500073, challengeId = 65302, text = "停留所", romaji = "teiryuujyo", correct = false, errorTag = "UNRELATED"),
                ChallengeOptionEntity(id = 7500074, challengeId = 65302, text = "入口", romaji = "iriguchi", correct = false, errorTag = "UNRELATED"),

                ChallengeOptionEntity(id = 7500075, challengeId = 65210, text = "会社員", romaji = "kaishain", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500076, challengeId = 65210, text = "office worker", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500077, challengeId = 65210, text = "社長", romaji = "shachoo", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500078, challengeId = 65210, text = "company president", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500079, challengeId = 65210, text = "部長", romaji = "buchoo", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500080, challengeId = 65210, text = "department head", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500081, challengeId = 65210, text = "事務", romaji = "jimu", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500082, challengeId = 65210, text = "clerical work", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500083, challengeId = 65210, text = "運転手", romaji = "untenshu", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500084, challengeId = 65210, text = "driver", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500085, challengeId = 65210, text = "面接", romaji = "mensetsu", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500086, challengeId = 65210, text = "an interview", romaji = null, correct = true, errorTag = null),

                ChallengeOptionEntity(id = 7500087, challengeId = 65211, text = "会議", romaji = "kaigi", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500088, challengeId = 65211, text = "a meeting", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500089, challengeId = 65211, text = "休憩", romaji = "kyuukee", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500090, challengeId = 65211, text = "a break", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500091, challengeId = 65211, text = "出張", romaji = "shucchoo", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500092, challengeId = 65211, text = "a business trip", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500093, challengeId = 65211, text = "残業", romaji = "zangyoo", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500094, challengeId = 65211, text = "overtime", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500095, challengeId = 65211, text = "給料", romaji = "kyuuryoo", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500096, challengeId = 65211, text = "salary", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500097, challengeId = 65211, text = "履歴書", romaji = "rirekisho", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500098, challengeId = 65211, text = "CV", romaji = null, correct = true, errorTag = null),

                ChallengeOptionEntity(id = 7500099, challengeId = 65212, text = "部下", romaji = "buka", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500100, challengeId = 65212, text = "a subordinate", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500101, challengeId = 65212, text = "同僚", romaji = "dooryoo", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500102, challengeId = 65212, text = "a colleague", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500103, challengeId = 65212, text = "社員", romaji = "shain", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500104, challengeId = 65212, text = "a staff member", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500105, challengeId = 65212, text = "アルバイト", romaji = "arubaito", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500106, challengeId = 65212, text = "a part-timer", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500107, challengeId = 65212, text = "退職", romaji = "taishoku", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500108, challengeId = 65212, text = "retirement", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500109, challengeId = 65212, text = "社員", romaji = "shain", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500110, challengeId = 65212, text = "a staff member", romaji = null, correct = true, errorTag = null),

                ChallengeOptionEntity(id = 7500111, challengeId = 65213, text = "事務所", romaji = "jimusho", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500112, challengeId = 65213, text = "an office", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500113, challengeId = 65213, text = "工場", romaji = "kojou", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500114, challengeId = 65213, text = "the factory", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500115, challengeId = 65213, text = "会議室", romaji = "kaigishitsu", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500116, challengeId = 65213, text = "the meeting room", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500117, challengeId = 65213, text = "応接室", romaji = "ousetsushitsu", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500118, challengeId = 65213, text = "the reception room", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500119, challengeId = 65213, text = "更衣室", romaji = "kouishitsu", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500120, challengeId = 65213, text = "the changing room", romaji = null, correct = true, errorTag = null),

                ChallengeOptionEntity(id = 7500121, challengeId = 65214, text = "残業", romaji = "zangyoo", correct = true, errorTag = null, audioSrc = "asset:///audio/ja/zangyoo.ogg"),
                ChallengeOptionEntity(id = 7500122, challengeId = 65214, text = "休憩", romaji = "kyuukee", correct = false, errorTag = null),
                ChallengeOptionEntity(id = 7500123, challengeId = 65214, text = "出張", romaji = "shucchoo", correct = false, errorTag = null),
                ChallengeOptionEntity(id = 7500124, challengeId = 65214, text = "給料", romaji = "kyuuryoo", correct = false, errorTag = null),

                ChallengeOptionEntity(id = 7500125, challengeId = 65215, text = "履歴書", romaji = "rirekisho", correct = true, errorTag = null, audioSrc = "asset:///audio/ja/rirekisho.ogg"),
                ChallengeOptionEntity(id = 7500126, challengeId = 65215, text = "名刺", romaji = "meishi", correct = false, errorTag = null),
                ChallengeOptionEntity(id = 7500127, challengeId = 65215, text = "貯金", romaji = "chokin", correct = false, errorTag = null),
                ChallengeOptionEntity(id = 7500128, challengeId = 65215, text = "面接", romaji = "mensetsu", correct = false, errorTag = null),

                ChallengeOptionEntity(id = 7500129, challengeId = 65216, text = "私は九時から六時まで働きます。", romaji = "watashi wa kuji kara roku made hatarakimasu", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500130, challengeId = 65216, text = "私は九時から六時まで働きました。", romaji = "watashi wa kuji kara roku made hatarakimashita", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 7500131, challengeId = 65216, text = "私は九時から六時に働きます。", romaji = "watashi wa kuji kara roku ni hatarakimasu", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 7500132, challengeId = 65216, text = "私は九時から六時まで働いています。", romaji = "watashi wa kuji kara roku made hataraite imasu", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 7500133, challengeId = 65217, text = "早めに帰ってもいいですか。", romaji = "hayame ni kaette mo ii desu ka", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500134, challengeId = 65217, text = "早めに帰ってください。", romaji = "hayame ni kaette kudasai", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 7500135, challengeId = 65217, text = "早めに帰らなければ。", romaji = "hayame ni kaeranakereba", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 7500136, challengeId = 65217, text = "早めに帰ってもいいです。", romaji = "hayame ni kaette mo ii desu", correct = false, errorTag = "WRONG_REGISTER"),

                ChallengeOptionEntity(id = 7500137, challengeId = 65303, text = "会議は三時から始まります。", romaji = "kaigi wa sanji kara hajimarimasu", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500138, challengeId = 65303, text = "会議は三時に始まります。", romaji = "kaigi wa sanji ni hajimarimasu", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 7500139, challengeId = 65303, text = "会議は三時から始まりました。", romaji = "kaigi wa sanji kara hajimarimashita", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 7500140, challengeId = 65304, text = "資料をコピーしてください", romaji = "shiryo o kopii shite kudasai", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500141, challengeId = 65304, text = "給料をコピーしてください", romaji = "kyuuryo o kopii shite kudasai", correct = false, errorTag = "UNRELATED"),
                ChallengeOptionEntity(id = 7500142, challengeId = 65304, text = "会議をコピーしてください", romaji = "kaigi o kopii shite kudasai", correct = false, errorTag = "UNRELATED"),
                ChallengeOptionEntity(id = 7500143, challengeId = 65304, text = "休憩をコピーしてください", romaji = "kyuukee o kopii shite kudasai", correct = false, errorTag = "UNRELATED"),

                ChallengeOptionEntity(id = 7500144, challengeId = 65220, text = "天気", romaji = "tenki", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500145, challengeId = 65220, text = "weather", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500146, challengeId = 65220, text = "晴れ", romaji = "hare", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500147, challengeId = 65220, text = "a clear sky", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500148, challengeId = 65220, text = "曇り", romaji = "kumori", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500149, challengeId = 65220, text = "cloudiness", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500150, challengeId = 65220, text = "雪", romaji = "yuki", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500151, challengeId = 65220, text = "snow", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500152, challengeId = 65220, text = "風", romaji = "kaze", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500153, challengeId = 65220, text = "wind", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500154, challengeId = 65220, text = "雲", romaji = "kumo", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500155, challengeId = 65220, text = "cloud", romaji = null, correct = true, errorTag = null),

                ChallengeOptionEntity(id = 7500156, challengeId = 65221, text = "雷", romaji = "kaminari", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500157, challengeId = 65221, text = "thunder", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500158, challengeId = 65221, text = "虹", romaji = "niji", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500159, challengeId = 65221, text = "a rainbow", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500160, challengeId = 65221, text = "傘", romaji = "kasa", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500161, challengeId = 65221, text = "an umbrella", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500162, challengeId = 65221, text = "霜", romaji = "shimo", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500163, challengeId = 65221, text = "frost", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500164, challengeId = 65221, text = "霧", romaji = "kiri", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500165, challengeId = 65221, text = "fog", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500166, challengeId = 65221, text = "嵐", romaji = "arashi", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500167, challengeId = 65221, text = "a storm", romaji = null, correct = true, errorTag = null),

                ChallengeOptionEntity(id = 7500168, challengeId = 65222, text = "春", romaji = "haru", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500169, challengeId = 65222, text = "spring", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500170, challengeId = 65222, text = "夏", romaji = "natsu", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500171, challengeId = 65222, text = "summer", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500172, challengeId = 65222, text = "秋", romaji = "aki", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500173, challengeId = 65222, text = "autumn", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500174, challengeId = 65222, text = "冬", romaji = "fuyu", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500175, challengeId = 65222, text = "winter", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500176, challengeId = 65222, text = "梅雨", romaji = "tsuyu", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500177, challengeId = 65222, text = "the rainy season", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500178, challengeId = 65222, text = "季節", romaji = "kisetsu", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500179, challengeId = 65222, text = "season", romaji = null, correct = true, errorTag = null),

                ChallengeOptionEntity(id = 7500180, challengeId = 65223, text = "朝", romaji = "asa", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500181, challengeId = 65223, text = "morning", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500182, challengeId = 65223, text = "昼", romaji = "hiru", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500183, challengeId = 65223, text = "daytime", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500184, challengeId = 65223, text = "夕方", romaji = "yuugata", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500185, challengeId = 65223, text = "evening", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500186, challengeId = 65223, text = "夜", romaji = "yoru", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500187, challengeId = 65223, text = "night", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500188, challengeId = 65223, text = "今朝", romaji = "kesa", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500189, challengeId = 65223, text = "this morning", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500190, challengeId = 65223, text = "昨夜", romaji = "sakuya", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500191, challengeId = 65223, text = "last night", romaji = null, correct = true, errorTag = null),

                ChallengeOptionEntity(id = 7500192, challengeId = 65224, text = "毎日", romaji = "mainichi", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500193, challengeId = 65224, text = "every day", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500194, challengeId = 65224, text = "毎週", romaji = "maishuu", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500195, challengeId = 65224, text = "every week", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500196, challengeId = 65224, text = "よく", romaji = "yoku", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500197, challengeId = 65224, text = "often", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500198, challengeId = 65224, text = "時々", romaji = "tokidoki", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500199, challengeId = 65224, text = "sometimes", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500200, challengeId = 65224, text = "あまり", romaji = "amari", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500201, challengeId = 65224, text = "not very", romaji = null, correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500202, challengeId = 65224, text = "めったに", romaji = "mettani", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500203, challengeId = 65224, text = "rarely", romaji = null, correct = true, errorTag = null),

                ChallengeOptionEntity(id = 7500204, challengeId = 65225, text = "梅雨", romaji = "tsuyu", correct = true, errorTag = null, audioSrc = "asset:///audio/ja/tsuyu.ogg"),
                ChallengeOptionEntity(id = 7500205, challengeId = 65225, text = "季節", romaji = "kisetsu", correct = false, errorTag = null),
                ChallengeOptionEntity(id = 7500206, challengeId = 65225, text = "春雨", romaji = "harusame", correct = false, errorTag = null),
                ChallengeOptionEntity(id = 7500207, challengeId = 65225, text = "曇り", romaji = "kumori", correct = false, errorTag = null),

                ChallengeOptionEntity(id = 7500208, challengeId = 65226, text = "めったに", romaji = "mettani", correct = true, errorTag = null, audioSrc = "asset:///audio/ja/mettani.ogg"),
                ChallengeOptionEntity(id = 7500209, challengeId = 65226, text = "よく", romaji = "yoku", correct = false, errorTag = null),
                ChallengeOptionEntity(id = 7500210, challengeId = 65226, text = "時々", romaji = "tokidoki", correct = false, errorTag = null),
                ChallengeOptionEntity(id = 7500211, challengeId = 65226, text = "いつも", romaji = "itsumo", correct = false, errorTag = null),

                ChallengeOptionEntity(id = 7500212, challengeId = 65227, text = "日本に行ったことがあります。", romaji = "nippon ni itta koto ga arimasu", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500213, challengeId = 65227, text = "日本に行きます。", romaji = "nippon ni ikimasu", correct = false, errorTag = "WRONG_TENSE"),
                ChallengeOptionEntity(id = 7500214, challengeId = 65227, text = "日本に行ったります。", romaji = "nippon ni itta arimasu", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 7500215, challengeId = 65227, text = "日本に行ったことあります。", romaji = "nippon ni itta koto arimasu", correct = false, errorTag = "WRONG_FORM"),

                ChallengeOptionEntity(id = 7500216, challengeId = 65228, text = "明日は雪になります。", romaji = "ashita wa yuki ni narimasu", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500217, challengeId = 65228, text = "明日は雪です。", romaji = "ashita wa yuki desu", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 7500218, challengeId = 65228, text = "明日は雪がなります。", romaji = "ashita wa yuki ga narimasu", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 7500219, challengeId = 65228, text = "明日は雪でした。", romaji = "ashita wa yuki deshita", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 7500220, challengeId = 65305, text = "今日は風が強いです。", romaji = "kyou wa kaze ga tsuyoi desu", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500221, challengeId = 65305, text = "今日は風が強くです。", romaji = "kyou wa kaze ga tsuyoku desu", correct = false, errorTag = "WRONG_FORM"),
                ChallengeOptionEntity(id = 7500222, challengeId = 65305, text = "今日は風が強いでした。", romaji = "kyou wa kaze ga tsuyoi deshita", correct = false, errorTag = "WRONG_TENSE"),

                ChallengeOptionEntity(id = 7500223, challengeId = 65306, text = "雲", romaji = "kumo", correct = true, errorTag = null),
                ChallengeOptionEntity(id = 7500224, challengeId = 65306, text = "雪", romaji = "yuki", correct = false, errorTag = "UNRELATED"),
                ChallengeOptionEntity(id = 7500225, challengeId = 65306, text = "風", romaji = "kaze", correct = false, errorTag = "UNRELATED"),
                ChallengeOptionEntity(id = 7500226, challengeId = 65306, text = "霜", romaji = "shimo", correct = false, errorTag = "UNRELATED"),
            ),
        ),
    )
}

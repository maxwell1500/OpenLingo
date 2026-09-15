package com.duo.app.data.local.character

data class StrokePoint(
    val x: Float, // Normalized 0.0f .. 1.0f
    val y: Float, // Normalized 0.0f .. 1.0f
)

data class CharacterStroke(
    val strokeNumber: Int,
    val points: List<StrokePoint>,
)

enum class ScriptType {
    HIRAGANA,
    KATAKANA
}

data class JapaneseCharacter(
    val character: String,
    val romaji: String,
    val group: String, // "Vowels", "K-Row", "S-Row", "T-Row", "N-Row", "H-Row", "M-Row", "Y-Row", "R-Row", "W/N-Row"
    val scriptType: ScriptType = ScriptType.HIRAGANA,
    val strokes: List<CharacterStroke>,
)

object KanaRepository {

    // =========================================================================
    // HIRAGANA (Complete Syllabary)
    // =========================================================================
    val hiraganaList: List<JapaneseCharacter> = listOf(
        // VOWELS (あ, い, う, え, お)
        JapaneseCharacter("あ", "a", "Vowels", ScriptType.HIRAGANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.25f, 0.30f), StrokePoint(0.50f, 0.30f), StrokePoint(0.75f, 0.30f))),
            CharacterStroke(2, listOf(StrokePoint(0.48f, 0.18f), StrokePoint(0.47f, 0.50f), StrokePoint(0.44f, 0.82f))),
            CharacterStroke(3, listOf(StrokePoint(0.60f, 0.42f), StrokePoint(0.35f, 0.58f), StrokePoint(0.28f, 0.72f), StrokePoint(0.42f, 0.86f), StrokePoint(0.68f, 0.80f), StrokePoint(0.78f, 0.62f), StrokePoint(0.66f, 0.52f)))
        )),
        JapaneseCharacter("い", "i", "Vowels", ScriptType.HIRAGANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.35f, 0.25f), StrokePoint(0.32f, 0.50f), StrokePoint(0.34f, 0.75f), StrokePoint(0.40f, 0.68f))),
            CharacterStroke(2, listOf(StrokePoint(0.65f, 0.35f), StrokePoint(0.68f, 0.55f), StrokePoint(0.65f, 0.68f)))
        )),
        JapaneseCharacter("う", "u", "Vowels", ScriptType.HIRAGANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.45f, 0.22f), StrokePoint(0.55f, 0.26f))),
            CharacterStroke(2, listOf(StrokePoint(0.38f, 0.42f), StrokePoint(0.58f, 0.40f), StrokePoint(0.68f, 0.55f), StrokePoint(0.58f, 0.75f), StrokePoint(0.38f, 0.82f)))
        )),
        JapaneseCharacter("え", "e", "Vowels", ScriptType.HIRAGANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.46f, 0.20f), StrokePoint(0.54f, 0.24f))),
            CharacterStroke(2, listOf(StrokePoint(0.32f, 0.42f), StrokePoint(0.62f, 0.38f), StrokePoint(0.36f, 0.65f), StrokePoint(0.55f, 0.62f), StrokePoint(0.72f, 0.75f)))
        )),
        JapaneseCharacter("お", "o", "Vowels", ScriptType.HIRAGANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.28f, 0.32f), StrokePoint(0.50f, 0.32f), StrokePoint(0.62f, 0.32f))),
            CharacterStroke(2, listOf(StrokePoint(0.46f, 0.22f), StrokePoint(0.44f, 0.58f), StrokePoint(0.36f, 0.68f), StrokePoint(0.46f, 0.78f), StrokePoint(0.68f, 0.72f), StrokePoint(0.68f, 0.55f))),
            CharacterStroke(3, listOf(StrokePoint(0.70f, 0.32f), StrokePoint(0.76f, 0.38f)))
        )),

        // K-ROW (か, き, く, け, こ)
        JapaneseCharacter("か", "ka", "K-Row", ScriptType.HIRAGANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.28f, 0.34f), StrokePoint(0.52f, 0.32f), StrokePoint(0.50f, 0.70f), StrokePoint(0.42f, 0.72f))),
            CharacterStroke(2, listOf(StrokePoint(0.36f, 0.22f), StrokePoint(0.28f, 0.76f))),
            CharacterStroke(3, listOf(StrokePoint(0.68f, 0.30f), StrokePoint(0.74f, 0.38f)))
        )),
        JapaneseCharacter("き", "ki", "K-Row", ScriptType.HIRAGANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.32f, 0.36f), StrokePoint(0.68f, 0.32f))),
            CharacterStroke(2, listOf(StrokePoint(0.28f, 0.50f), StrokePoint(0.72f, 0.46f))),
            CharacterStroke(3, listOf(StrokePoint(0.54f, 0.20f), StrokePoint(0.46f, 0.66f))),
            CharacterStroke(4, listOf(StrokePoint(0.36f, 0.74f), StrokePoint(0.58f, 0.80f), StrokePoint(0.64f, 0.72f)))
        )),
        JapaneseCharacter("く", "ku", "K-Row", ScriptType.HIRAGANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.64f, 0.26f), StrokePoint(0.32f, 0.52f), StrokePoint(0.66f, 0.76f)))
        )),
        JapaneseCharacter("け", "ke", "K-Row", ScriptType.HIRAGANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.32f, 0.24f), StrokePoint(0.30f, 0.55f), StrokePoint(0.32f, 0.78f))),
            CharacterStroke(2, listOf(StrokePoint(0.48f, 0.36f), StrokePoint(0.75f, 0.34f))),
            CharacterStroke(3, listOf(StrokePoint(0.64f, 0.22f), StrokePoint(0.62f, 0.78f)))
        )),
        JapaneseCharacter("こ", "ko", "K-Row", ScriptType.HIRAGANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.30f, 0.35f), StrokePoint(0.68f, 0.34f))),
            CharacterStroke(2, listOf(StrokePoint(0.30f, 0.68f), StrokePoint(0.55f, 0.72f), StrokePoint(0.70f, 0.66f)))
        )),

        // S-ROW (さ, し, す, せ, そ)
        JapaneseCharacter("さ", "sa", "S-Row", ScriptType.HIRAGANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.32f, 0.38f), StrokePoint(0.68f, 0.34f))),
            CharacterStroke(2, listOf(StrokePoint(0.55f, 0.22f), StrokePoint(0.44f, 0.65f))),
            CharacterStroke(3, listOf(StrokePoint(0.34f, 0.72f), StrokePoint(0.56f, 0.78f), StrokePoint(0.65f, 0.70f)))
        )),
        JapaneseCharacter("し", "shi", "S-Row", ScriptType.HIRAGANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.38f, 0.22f), StrokePoint(0.38f, 0.68f), StrokePoint(0.55f, 0.80f), StrokePoint(0.72f, 0.68f)))
        )),
        JapaneseCharacter("す", "su", "S-Row", ScriptType.HIRAGANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.24f, 0.35f), StrokePoint(0.76f, 0.33f))),
            CharacterStroke(2, listOf(StrokePoint(0.55f, 0.18f), StrokePoint(0.55f, 0.48f), StrokePoint(0.40f, 0.54f), StrokePoint(0.45f, 0.66f), StrokePoint(0.55f, 0.60f), StrokePoint(0.50f, 0.86f)))
        )),
        JapaneseCharacter("せ", "se", "S-Row", ScriptType.HIRAGANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.22f, 0.45f), StrokePoint(0.76f, 0.40f))),
            CharacterStroke(2, listOf(StrokePoint(0.64f, 0.26f), StrokePoint(0.64f, 0.55f), StrokePoint(0.72f, 0.54f))),
            CharacterStroke(3, listOf(StrokePoint(0.40f, 0.22f), StrokePoint(0.38f, 0.72f), StrokePoint(0.55f, 0.75f)))
        )),
        JapaneseCharacter("そ", "so", "S-Row", ScriptType.HIRAGANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.36f, 0.24f), StrokePoint(0.64f, 0.24f), StrokePoint(0.36f, 0.52f), StrokePoint(0.66f, 0.50f), StrokePoint(0.34f, 0.78f), StrokePoint(0.66f, 0.75f)))
        )),

        // T-ROW (た, ち, つ, て, と)
        JapaneseCharacter("た", "ta", "T-Row", ScriptType.HIRAGANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.22f, 0.36f), StrokePoint(0.54f, 0.34f))),
            CharacterStroke(2, listOf(StrokePoint(0.40f, 0.20f), StrokePoint(0.32f, 0.74f))),
            CharacterStroke(3, listOf(StrokePoint(0.52f, 0.48f), StrokePoint(0.74f, 0.46f))),
            CharacterStroke(4, listOf(StrokePoint(0.54f, 0.68f), StrokePoint(0.72f, 0.66f)))
        )),
        JapaneseCharacter("ち", "chi", "T-Row", ScriptType.HIRAGANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.28f, 0.34f), StrokePoint(0.68f, 0.30f))),
            CharacterStroke(2, listOf(StrokePoint(0.48f, 0.20f), StrokePoint(0.44f, 0.48f), StrokePoint(0.68f, 0.56f), StrokePoint(0.64f, 0.76f), StrokePoint(0.36f, 0.78f)))
        )),
        JapaneseCharacter("つ", "tsu", "T-Row", ScriptType.HIRAGANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.28f, 0.38f), StrokePoint(0.68f, 0.34f), StrokePoint(0.74f, 0.58f), StrokePoint(0.44f, 0.80f), StrokePoint(0.26f, 0.78f)))
        )),
        JapaneseCharacter("て", "te", "T-Row", ScriptType.HIRAGANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.25f, 0.32f), StrokePoint(0.72f, 0.30f), StrokePoint(0.38f, 0.52f), StrokePoint(0.64f, 0.74f)))
        )),
        JapaneseCharacter("と", "to", "T-Row", ScriptType.HIRAGANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.38f, 0.22f), StrokePoint(0.45f, 0.52f))),
            CharacterStroke(2, listOf(StrokePoint(0.65f, 0.38f), StrokePoint(0.36f, 0.64f), StrokePoint(0.66f, 0.78f)))
        )),

        // N-ROW (な, に, ぬ, ね, の)
        JapaneseCharacter("な", "na", "N-Row", ScriptType.HIRAGANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.24f, 0.38f), StrokePoint(0.48f, 0.34f))),
            CharacterStroke(2, listOf(StrokePoint(0.38f, 0.22f), StrokePoint(0.32f, 0.72f))),
            CharacterStroke(3, listOf(StrokePoint(0.58f, 0.32f), StrokePoint(0.66f, 0.40f))),
            CharacterStroke(4, listOf(StrokePoint(0.60f, 0.55f), StrokePoint(0.55f, 0.78f)))
        )),
        JapaneseCharacter("に", "ni", "N-Row", ScriptType.HIRAGANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.30f, 0.22f), StrokePoint(0.30f, 0.78f))),
            CharacterStroke(2, listOf(StrokePoint(0.48f, 0.38f), StrokePoint(0.74f, 0.36f))),
            CharacterStroke(3, listOf(StrokePoint(0.50f, 0.66f), StrokePoint(0.76f, 0.64f)))
        )),
        JapaneseCharacter("ぬ", "nu", "N-Row", ScriptType.HIRAGANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.36f, 0.24f), StrokePoint(0.32f, 0.74f))),
            CharacterStroke(2, listOf(StrokePoint(0.28f, 0.44f), StrokePoint(0.66f, 0.34f), StrokePoint(0.74f, 0.68f), StrokePoint(0.50f, 0.82f), StrokePoint(0.66f, 0.84f)))
        )),
        JapaneseCharacter("ね", "ne", "N-Row", ScriptType.HIRAGANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.34f, 0.20f), StrokePoint(0.34f, 0.78f))),
            CharacterStroke(2, listOf(StrokePoint(0.24f, 0.44f), StrokePoint(0.64f, 0.38f), StrokePoint(0.38f, 0.72f), StrokePoint(0.66f, 0.65f), StrokePoint(0.68f, 0.80f)))
        )),
        JapaneseCharacter("の", "no", "N-Row", ScriptType.HIRAGANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.52f, 0.28f), StrokePoint(0.36f, 0.55f), StrokePoint(0.48f, 0.78f), StrokePoint(0.74f, 0.62f), StrokePoint(0.66f, 0.38f), StrokePoint(0.36f, 0.48f)))
        )),

        // H-ROW (は, ひ, ふ, へ, ほ)
        JapaneseCharacter("は", "ha", "H-Row", ScriptType.HIRAGANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.30f, 0.20f), StrokePoint(0.30f, 0.78f))),
            CharacterStroke(2, listOf(StrokePoint(0.44f, 0.38f), StrokePoint(0.74f, 0.36f))),
            CharacterStroke(3, listOf(StrokePoint(0.62f, 0.25f), StrokePoint(0.62f, 0.62f), StrokePoint(0.50f, 0.74f), StrokePoint(0.66f, 0.74f)))
        )),
        JapaneseCharacter("ひ", "hi", "H-Row", ScriptType.HIRAGANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.26f, 0.38f), StrokePoint(0.48f, 0.32f), StrokePoint(0.32f, 0.68f), StrokePoint(0.52f, 0.78f), StrokePoint(0.68f, 0.62f), StrokePoint(0.74f, 0.42f)))
        )),
        JapaneseCharacter("ふ", "fu", "H-Row", ScriptType.HIRAGANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.50f, 0.20f), StrokePoint(0.50f, 0.32f))),
            CharacterStroke(2, listOf(StrokePoint(0.48f, 0.44f), StrokePoint(0.44f, 0.78f))),
            CharacterStroke(3, listOf(StrokePoint(0.30f, 0.52f), StrokePoint(0.24f, 0.62f))),
            CharacterStroke(4, listOf(StrokePoint(0.70f, 0.50f), StrokePoint(0.76f, 0.60f)))
        )),
        JapaneseCharacter("へ", "he", "H-Row", ScriptType.HIRAGANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.24f, 0.62f), StrokePoint(0.48f, 0.32f), StrokePoint(0.76f, 0.62f)))
        )),
        JapaneseCharacter("ほ", "ho", "H-Row", ScriptType.HIRAGANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.28f, 0.20f), StrokePoint(0.28f, 0.78f))),
            CharacterStroke(2, listOf(StrokePoint(0.42f, 0.32f), StrokePoint(0.74f, 0.30f))),
            CharacterStroke(3, listOf(StrokePoint(0.42f, 0.48f), StrokePoint(0.74f, 0.46f))),
            CharacterStroke(4, listOf(StrokePoint(0.60f, 0.22f), StrokePoint(0.60f, 0.65f), StrokePoint(0.48f, 0.75f), StrokePoint(0.64f, 0.74f)))
        )),

        // M-ROW (ま, み, む, め, も)
        JapaneseCharacter("ま", "ma", "M-Row", ScriptType.HIRAGANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.32f, 0.35f), StrokePoint(0.70f, 0.33f))),
            CharacterStroke(2, listOf(StrokePoint(0.34f, 0.50f), StrokePoint(0.68f, 0.48f))),
            CharacterStroke(3, listOf(StrokePoint(0.52f, 0.20f), StrokePoint(0.52f, 0.65f), StrokePoint(0.42f, 0.76f), StrokePoint(0.58f, 0.75f)))
        )),
        JapaneseCharacter("み", "mi", "M-Row", ScriptType.HIRAGANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.26f, 0.36f), StrokePoint(0.62f, 0.32f), StrokePoint(0.40f, 0.62f), StrokePoint(0.55f, 0.64f))),
            CharacterStroke(2, listOf(StrokePoint(0.66f, 0.44f), StrokePoint(0.45f, 0.78f)))
        )),
        JapaneseCharacter("む", "mu", "M-Row", ScriptType.HIRAGANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.25f, 0.38f), StrokePoint(0.50f, 0.36f))),
            CharacterStroke(2, listOf(StrokePoint(0.42f, 0.22f), StrokePoint(0.42f, 0.65f), StrokePoint(0.58f, 0.62f), StrokePoint(0.68f, 0.50f), StrokePoint(0.68f, 0.78f))),
            CharacterStroke(3, listOf(StrokePoint(0.74f, 0.30f), StrokePoint(0.80f, 0.38f)))
        )),
        JapaneseCharacter("め", "me", "M-Row", ScriptType.HIRAGANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.42f, 0.25f), StrokePoint(0.32f, 0.72f))),
            CharacterStroke(2, listOf(StrokePoint(0.30f, 0.44f), StrokePoint(0.65f, 0.34f), StrokePoint(0.74f, 0.68f), StrokePoint(0.42f, 0.78f)))
        )),
        JapaneseCharacter("も", "mo", "M-Row", ScriptType.HIRAGANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.48f, 0.20f), StrokePoint(0.48f, 0.72f), StrokePoint(0.68f, 0.66f))),
            CharacterStroke(2, listOf(StrokePoint(0.30f, 0.38f), StrokePoint(0.68f, 0.36f))),
            CharacterStroke(3, listOf(StrokePoint(0.30f, 0.52f), StrokePoint(0.68f, 0.50f)))
        )),

        // Y-ROW (や, ゆ, よ)
        JapaneseCharacter("や", "ya", "Y-Row", ScriptType.HIRAGANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.28f, 0.45f), StrokePoint(0.65f, 0.38f), StrokePoint(0.55f, 0.74f))),
            CharacterStroke(2, listOf(StrokePoint(0.62f, 0.25f), StrokePoint(0.68f, 0.34f))),
            CharacterStroke(3, listOf(StrokePoint(0.42f, 0.22f), StrokePoint(0.35f, 0.76f)))
        )),
        JapaneseCharacter("ゆ", "yu", "Y-Row", ScriptType.HIRAGANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.38f, 0.24f), StrokePoint(0.38f, 0.66f), StrokePoint(0.64f, 0.64f), StrokePoint(0.64f, 0.34f))),
            CharacterStroke(2, listOf(StrokePoint(0.55f, 0.20f), StrokePoint(0.52f, 0.80f)))
        )),
        JapaneseCharacter("よ", "yo", "Y-Row", ScriptType.HIRAGANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.30f, 0.36f), StrokePoint(0.65f, 0.34f))),
            CharacterStroke(2, listOf(StrokePoint(0.55f, 0.20f), StrokePoint(0.55f, 0.64f), StrokePoint(0.40f, 0.76f), StrokePoint(0.58f, 0.74f)))
        )),

        // R-ROW (ら, り, る, れ, ろ)
        JapaneseCharacter("ら", "ra", "R-Row", ScriptType.HIRAGANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.44f, 0.22f), StrokePoint(0.54f, 0.26f))),
            CharacterStroke(2, listOf(StrokePoint(0.42f, 0.40f), StrokePoint(0.64f, 0.42f), StrokePoint(0.60f, 0.72f), StrokePoint(0.38f, 0.76f)))
        )),
        JapaneseCharacter("り", "ri", "R-Row", ScriptType.HIRAGANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.35f, 0.28f), StrokePoint(0.35f, 0.55f))),
            CharacterStroke(2, listOf(StrokePoint(0.64f, 0.22f), StrokePoint(0.64f, 0.68f), StrokePoint(0.52f, 0.80f)))
        )),
        JapaneseCharacter("る", "ru", "R-Row", ScriptType.HIRAGANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.34f, 0.28f), StrokePoint(0.64f, 0.26f), StrokePoint(0.38f, 0.54f), StrokePoint(0.68f, 0.54f), StrokePoint(0.64f, 0.76f), StrokePoint(0.48f, 0.76f), StrokePoint(0.52f, 0.66f)))
        )),
        JapaneseCharacter("れ", "re", "R-Row", ScriptType.HIRAGANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.34f, 0.22f), StrokePoint(0.34f, 0.78f))),
            CharacterStroke(2, listOf(StrokePoint(0.24f, 0.46f), StrokePoint(0.62f, 0.40f), StrokePoint(0.40f, 0.68f), StrokePoint(0.72f, 0.64f)))
        )),
        JapaneseCharacter("ろ", "ro", "R-Row", ScriptType.HIRAGANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.34f, 0.28f), StrokePoint(0.64f, 0.26f), StrokePoint(0.38f, 0.54f), StrokePoint(0.68f, 0.54f), StrokePoint(0.55f, 0.78f), StrokePoint(0.38f, 0.76f)))
        )),

        // W/N-ROW (わ, を, ん)
        JapaneseCharacter("わ", "wa", "W/N-Row", ScriptType.HIRAGANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.34f, 0.22f), StrokePoint(0.34f, 0.78f))),
            CharacterStroke(2, listOf(StrokePoint(0.24f, 0.46f), StrokePoint(0.62f, 0.40f), StrokePoint(0.40f, 0.68f), StrokePoint(0.68f, 0.68f), StrokePoint(0.60f, 0.82f), StrokePoint(0.42f, 0.80f)))
        )),
        JapaneseCharacter("を", "wo", "W/N-Row", ScriptType.HIRAGANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.28f, 0.32f), StrokePoint(0.68f, 0.30f))),
            CharacterStroke(2, listOf(StrokePoint(0.48f, 0.20f), StrokePoint(0.34f, 0.55f), StrokePoint(0.62f, 0.54f))),
            CharacterStroke(3, listOf(StrokePoint(0.44f, 0.65f), StrokePoint(0.65f, 0.75f)))
        )),
        JapaneseCharacter("ん", "n", "W/N-Row", ScriptType.HIRAGANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.38f, 0.24f), StrokePoint(0.30f, 0.76f), StrokePoint(0.58f, 0.50f), StrokePoint(0.74f, 0.72f)))
        ))
    )

    // =========================================================================
    // KATAKANA (Core Foundational Syllabary)
    // =========================================================================
    val katakanaList: List<JapaneseCharacter> = listOf(
        JapaneseCharacter("ア", "a", "Vowels", ScriptType.KATAKANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.28f, 0.30f), StrokePoint(0.68f, 0.28f), StrokePoint(0.56f, 0.50f))),
            CharacterStroke(2, listOf(StrokePoint(0.42f, 0.48f), StrokePoint(0.30f, 0.78f)))
        )),
        JapaneseCharacter("イ", "i", "Vowels", ScriptType.KATAKANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.55f, 0.22f), StrokePoint(0.32f, 0.55f))),
            CharacterStroke(2, listOf(StrokePoint(0.44f, 0.42f), StrokePoint(0.44f, 0.80f)))
        )),
        JapaneseCharacter("ウ", "u", "Vowels", ScriptType.KATAKANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.48f, 0.20f), StrokePoint(0.48f, 0.32f))),
            CharacterStroke(2, listOf(StrokePoint(0.30f, 0.38f), StrokePoint(0.30f, 0.52f))),
            CharacterStroke(3, listOf(StrokePoint(0.30f, 0.40f), StrokePoint(0.70f, 0.38f), StrokePoint(0.50f, 0.78f)))
        )),
        JapaneseCharacter("エ", "e", "Vowels", ScriptType.KATAKANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.32f, 0.28f), StrokePoint(0.68f, 0.28f))),
            CharacterStroke(2, listOf(StrokePoint(0.50f, 0.28f), StrokePoint(0.50f, 0.72f))),
            CharacterStroke(3, listOf(StrokePoint(0.24f, 0.72f), StrokePoint(0.76f, 0.72f)))
        )),
        JapaneseCharacter("オ", "o", "Vowels", ScriptType.KATAKANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.25f, 0.32f), StrokePoint(0.75f, 0.32f))),
            CharacterStroke(2, listOf(StrokePoint(0.50f, 0.32f), StrokePoint(0.50f, 0.80f), StrokePoint(0.42f, 0.74f))),
            CharacterStroke(3, listOf(StrokePoint(0.48f, 0.44f), StrokePoint(0.28f, 0.70f)))
        )),
        JapaneseCharacter("カ", "ka", "K-Row", ScriptType.KATAKANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.28f, 0.36f), StrokePoint(0.68f, 0.34f), StrokePoint(0.58f, 0.75f), StrokePoint(0.50f, 0.70f))),
            CharacterStroke(2, listOf(StrokePoint(0.48f, 0.22f), StrokePoint(0.30f, 0.78f)))
        )),
        JapaneseCharacter("キ", "ki", "K-Row", ScriptType.KATAKANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.32f, 0.34f), StrokePoint(0.68f, 0.32f))),
            CharacterStroke(2, listOf(StrokePoint(0.28f, 0.50f), StrokePoint(0.72f, 0.48f))),
            CharacterStroke(3, listOf(StrokePoint(0.58f, 0.20f), StrokePoint(0.34f, 0.78f)))
        )),
        JapaneseCharacter("ク", "ku", "K-Row", ScriptType.KATAKANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.48f, 0.24f), StrokePoint(0.34f, 0.48f))),
            CharacterStroke(2, listOf(StrokePoint(0.36f, 0.40f), StrokePoint(0.72f, 0.38f), StrokePoint(0.44f, 0.78f)))
        )),
        JapaneseCharacter("ケ", "ke", "K-Row", ScriptType.KATAKANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.48f, 0.22f), StrokePoint(0.32f, 0.48f))),
            CharacterStroke(2, listOf(StrokePoint(0.34f, 0.44f), StrokePoint(0.74f, 0.42f))),
            CharacterStroke(3, listOf(StrokePoint(0.54f, 0.44f), StrokePoint(0.36f, 0.78f)))
        )),
        JapaneseCharacter("コ", "ko", "K-Row", ScriptType.KATAKANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.32f, 0.34f), StrokePoint(0.68f, 0.34f), StrokePoint(0.68f, 0.72f))),
            CharacterStroke(2, listOf(StrokePoint(0.32f, 0.72f), StrokePoint(0.68f, 0.72f)))
        )),
        JapaneseCharacter("サ", "sa", "S-Row", ScriptType.KATAKANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.26f, 0.36f), StrokePoint(0.74f, 0.34f))),
            CharacterStroke(2, listOf(StrokePoint(0.40f, 0.24f), StrokePoint(0.38f, 0.48f))),
            CharacterStroke(3, listOf(StrokePoint(0.60f, 0.24f), StrokePoint(0.52f, 0.78f)))
        )),
        JapaneseCharacter("シ", "shi", "S-Row", ScriptType.KATAKANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.34f, 0.32f), StrokePoint(0.44f, 0.38f))),
            CharacterStroke(2, listOf(StrokePoint(0.30f, 0.52f), StrokePoint(0.40f, 0.58f))),
            CharacterStroke(3, listOf(StrokePoint(0.34f, 0.76f), StrokePoint(0.70f, 0.40f)))
        )),
        JapaneseCharacter("ス", "su", "S-Row", ScriptType.KATAKANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.30f, 0.34f), StrokePoint(0.68f, 0.34f), StrokePoint(0.44f, 0.78f))),
            CharacterStroke(2, listOf(StrokePoint(0.50f, 0.52f), StrokePoint(0.68f, 0.76f)))
        )),
        JapaneseCharacter("セ", "se", "S-Row", ScriptType.KATAKANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.30f, 0.40f), StrokePoint(0.68f, 0.36f), StrokePoint(0.68f, 0.60f), StrokePoint(0.58f, 0.58f))),
            CharacterStroke(2, listOf(StrokePoint(0.48f, 0.24f), StrokePoint(0.48f, 0.76f), StrokePoint(0.66f, 0.76f)))
        )),
        JapaneseCharacter("ソ", "so", "S-Row", ScriptType.KATAKANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.38f, 0.32f), StrokePoint(0.48f, 0.44f))),
            CharacterStroke(2, listOf(StrokePoint(0.66f, 0.28f), StrokePoint(0.36f, 0.76f)))
        )),
        JapaneseCharacter("タ", "ta", "T-Row", ScriptType.KATAKANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.46f, 0.22f), StrokePoint(0.34f, 0.44f))),
            CharacterStroke(2, listOf(StrokePoint(0.34f, 0.38f), StrokePoint(0.72f, 0.36f), StrokePoint(0.46f, 0.78f))),
            CharacterStroke(3, listOf(StrokePoint(0.40f, 0.52f), StrokePoint(0.62f, 0.68f)))
        )),
        JapaneseCharacter("チ", "chi", "T-Row", ScriptType.KATAKANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.62f, 0.24f), StrokePoint(0.34f, 0.34f))),
            CharacterStroke(2, listOf(StrokePoint(0.26f, 0.52f), StrokePoint(0.74f, 0.48f))),
            CharacterStroke(3, listOf(StrokePoint(0.52f, 0.36f), StrokePoint(0.42f, 0.80f)))
        )),
        JapaneseCharacter("ツ", "tsu", "T-Row", ScriptType.KATAKANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.34f, 0.30f), StrokePoint(0.40f, 0.40f))),
            CharacterStroke(2, listOf(StrokePoint(0.52f, 0.34f), StrokePoint(0.58f, 0.46f))),
            CharacterStroke(3, listOf(StrokePoint(0.68f, 0.28f), StrokePoint(0.36f, 0.76f)))
        )),
        JapaneseCharacter("テ", "te", "T-Row", ScriptType.KATAKANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.36f, 0.32f), StrokePoint(0.64f, 0.30f))),
            CharacterStroke(2, listOf(StrokePoint(0.26f, 0.50f), StrokePoint(0.74f, 0.48f))),
            CharacterStroke(3, listOf(StrokePoint(0.52f, 0.32f), StrokePoint(0.38f, 0.78f)))
        )),
        JapaneseCharacter("ト", "to", "T-Row", ScriptType.KATAKANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.48f, 0.22f), StrokePoint(0.48f, 0.80f))),
            CharacterStroke(2, listOf(StrokePoint(0.50f, 0.44f), StrokePoint(0.74f, 0.58f)))
        )),
        JapaneseCharacter("ナ", "na", "N-Row", ScriptType.KATAKANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.26f, 0.40f), StrokePoint(0.74f, 0.38f))),
            CharacterStroke(2, listOf(StrokePoint(0.50f, 0.22f), StrokePoint(0.34f, 0.78f)))
        )),
        JapaneseCharacter("ニ", "ni", "N-Row", ScriptType.KATAKANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.34f, 0.40f), StrokePoint(0.66f, 0.40f))),
            CharacterStroke(2, listOf(StrokePoint(0.24f, 0.68f), StrokePoint(0.76f, 0.68f)))
        )),
        JapaneseCharacter("ヌ", "nu", "N-Row", ScriptType.KATAKANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.30f, 0.36f), StrokePoint(0.68f, 0.34f), StrokePoint(0.42f, 0.78f))),
            CharacterStroke(2, listOf(StrokePoint(0.42f, 0.50f), StrokePoint(0.68f, 0.74f)))
        )),
        JapaneseCharacter("ネ", "ne", "N-Row", ScriptType.KATAKANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.40f, 0.22f), StrokePoint(0.40f, 0.34f))),
            CharacterStroke(2, listOf(StrokePoint(0.30f, 0.38f), StrokePoint(0.30f, 0.78f))),
            CharacterStroke(3, listOf(StrokePoint(0.30f, 0.44f), StrokePoint(0.68f, 0.42f))),
            CharacterStroke(4, listOf(StrokePoint(0.54f, 0.44f), StrokePoint(0.54f, 0.78f)))
        )),
        JapaneseCharacter("ノ", "no", "N-Row", ScriptType.KATAKANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.65f, 0.24f), StrokePoint(0.34f, 0.78f)))
        )),
        JapaneseCharacter("ハ", "ha", "H-Row", ScriptType.KATAKANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.44f, 0.30f), StrokePoint(0.30f, 0.74f))),
            CharacterStroke(2, listOf(StrokePoint(0.58f, 0.32f), StrokePoint(0.72f, 0.74f)))
        )),
        JapaneseCharacter("ヒ", "hi", "H-Row", ScriptType.KATAKANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.34f, 0.34f), StrokePoint(0.62f, 0.32f))),
            CharacterStroke(2, listOf(StrokePoint(0.34f, 0.34f), StrokePoint(0.34f, 0.70f), StrokePoint(0.68f, 0.70f)))
        )),
        JapaneseCharacter("フ", "fu", "H-Row", ScriptType.KATAKANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.32f, 0.34f), StrokePoint(0.70f, 0.32f), StrokePoint(0.46f, 0.76f)))
        )),
        JapaneseCharacter("ヘ", "he", "H-Row", ScriptType.KATAKANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.26f, 0.60f), StrokePoint(0.48f, 0.34f), StrokePoint(0.74f, 0.60f)))
        )),
        JapaneseCharacter("ホ", "ho", "H-Row", ScriptType.KATAKANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.28f, 0.34f), StrokePoint(0.72f, 0.34f))),
            CharacterStroke(2, listOf(StrokePoint(0.50f, 0.22f), StrokePoint(0.50f, 0.80f), StrokePoint(0.42f, 0.74f))),
            CharacterStroke(3, listOf(StrokePoint(0.44f, 0.48f), StrokePoint(0.30f, 0.68f))),
            CharacterStroke(4, listOf(StrokePoint(0.56f, 0.48f), StrokePoint(0.70f, 0.68f)))
        )),
        JapaneseCharacter("マ", "ma", "M-Row", ScriptType.KATAKANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.32f, 0.36f), StrokePoint(0.68f, 0.34f), StrokePoint(0.44f, 0.62f))),
            CharacterStroke(2, listOf(StrokePoint(0.48f, 0.54f), StrokePoint(0.68f, 0.74f)))
        )),
        JapaneseCharacter("ミ", "mi", "M-Row", ScriptType.KATAKANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.34f, 0.34f), StrokePoint(0.56f, 0.42f))),
            CharacterStroke(2, listOf(StrokePoint(0.38f, 0.50f), StrokePoint(0.62f, 0.58f))),
            CharacterStroke(3, listOf(StrokePoint(0.42f, 0.66f), StrokePoint(0.70f, 0.74f)))
        )),
        JapaneseCharacter("ム", "mu", "M-Row", ScriptType.KATAKANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.48f, 0.26f), StrokePoint(0.32f, 0.62f), StrokePoint(0.68f, 0.62f))),
            CharacterStroke(2, listOf(StrokePoint(0.58f, 0.50f), StrokePoint(0.66f, 0.68f)))
        )),
        JapaneseCharacter("メ", "me", "M-Row", ScriptType.KATAKANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.62f, 0.26f), StrokePoint(0.34f, 0.74f))),
            CharacterStroke(2, listOf(StrokePoint(0.36f, 0.38f), StrokePoint(0.68f, 0.68f)))
        )),
        JapaneseCharacter("モ", "mo", "M-Row", ScriptType.KATAKANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.32f, 0.38f), StrokePoint(0.68f, 0.38f))),
            CharacterStroke(2, listOf(StrokePoint(0.28f, 0.54f), StrokePoint(0.72f, 0.54f))),
            CharacterStroke(3, listOf(StrokePoint(0.48f, 0.24f), StrokePoint(0.48f, 0.74f), StrokePoint(0.68f, 0.74f)))
        )),
        JapaneseCharacter("ヤ", "ya", "Y-Row", ScriptType.KATAKANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.30f, 0.40f), StrokePoint(0.68f, 0.36f), StrokePoint(0.60f, 0.64f))),
            CharacterStroke(2, listOf(StrokePoint(0.48f, 0.24f), StrokePoint(0.36f, 0.78f)))
        )),
        JapaneseCharacter("ユ", "yu", "Y-Row", ScriptType.KATAKANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.34f, 0.36f), StrokePoint(0.66f, 0.36f), StrokePoint(0.66f, 0.62f), StrokePoint(0.34f, 0.62f))),
            CharacterStroke(2, listOf(StrokePoint(0.34f, 0.76f), StrokePoint(0.74f, 0.76f)))
        )),
        JapaneseCharacter("ヨ", "yo", "Y-Row", ScriptType.KATAKANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.34f, 0.32f), StrokePoint(0.66f, 0.32f), StrokePoint(0.66f, 0.74f), StrokePoint(0.34f, 0.74f))),
            CharacterStroke(2, listOf(StrokePoint(0.34f, 0.53f), StrokePoint(0.66f, 0.53f))),
            CharacterStroke(3, listOf(StrokePoint(0.34f, 0.24f), StrokePoint(0.34f, 0.80f)))
        )),
        JapaneseCharacter("ラ", "ra", "R-Row", ScriptType.KATAKANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.36f, 0.30f), StrokePoint(0.64f, 0.30f))),
            CharacterStroke(2, listOf(StrokePoint(0.38f, 0.44f), StrokePoint(0.66f, 0.44f), StrokePoint(0.50f, 0.78f)))
        )),
        JapaneseCharacter("リ", "ri", "R-Row", ScriptType.KATAKANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.38f, 0.30f), StrokePoint(0.38f, 0.54f))),
            CharacterStroke(2, listOf(StrokePoint(0.62f, 0.24f), StrokePoint(0.62f, 0.68f), StrokePoint(0.50f, 0.78f)))
        )),
        JapaneseCharacter("ル", "ru", "R-Row", ScriptType.KATAKANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.42f, 0.28f), StrokePoint(0.34f, 0.74f))),
            CharacterStroke(2, listOf(StrokePoint(0.58f, 0.28f), StrokePoint(0.58f, 0.70f), StrokePoint(0.70f, 0.74f)))
        )),
        JapaneseCharacter("レ", "re", "R-Row", ScriptType.KATAKANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.38f, 0.24f), StrokePoint(0.38f, 0.74f), StrokePoint(0.68f, 0.70f)))
        )),
        JapaneseCharacter("ロ", "ro", "R-Row", ScriptType.KATAKANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.32f, 0.32f), StrokePoint(0.32f, 0.74f))),
            CharacterStroke(2, listOf(StrokePoint(0.32f, 0.34f), StrokePoint(0.68f, 0.34f), StrokePoint(0.68f, 0.74f))),
            CharacterStroke(3, listOf(StrokePoint(0.32f, 0.74f), StrokePoint(0.68f, 0.74f)))
        )),
        JapaneseCharacter("ワ", "wa", "W/N-Row", ScriptType.KATAKANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.34f, 0.36f), StrokePoint(0.34f, 0.60f))),
            CharacterStroke(2, listOf(StrokePoint(0.34f, 0.38f), StrokePoint(0.68f, 0.36f), StrokePoint(0.48f, 0.76f)))
        )),
        JapaneseCharacter("ヲ", "wo", "W/N-Row", ScriptType.KATAKANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.32f, 0.34f), StrokePoint(0.68f, 0.34f))),
            CharacterStroke(2, listOf(StrokePoint(0.28f, 0.52f), StrokePoint(0.70f, 0.52f))),
            CharacterStroke(3, listOf(StrokePoint(0.54f, 0.38f), StrokePoint(0.36f, 0.78f)))
        )),
        JapaneseCharacter("ン", "n", "W/N-Row", ScriptType.KATAKANA, listOf(
            CharacterStroke(1, listOf(StrokePoint(0.36f, 0.34f), StrokePoint(0.46f, 0.44f))),
            CharacterStroke(2, listOf(StrokePoint(0.34f, 0.74f), StrokePoint(0.68f, 0.38f)))
        ))
    )
}

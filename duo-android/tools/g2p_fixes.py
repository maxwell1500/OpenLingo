#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""ONE shared G2P patch module for the whole project.  Read this before
writing any new generator.

Why this file exists
--------------------
The `dijimos` fix in `.scratch/generate_b1_audio.py` failed to protect the
shipped audio for two reasons: it was a per-script monkeypatch, and its guard
was an exact whole-string match on the literal string "dijimos." *including the
full stop*.  The shipped `dijimos.ogg` has the text "dijimos" with no full
stop, so the patch would not even have fired for the one word it was written
for, let alone for `dijo`, `cogio` and the rest.

So: every generator in this project must phonemise through `jpn_g2p` /
`spa_g2p` / `make_pipeline` from THIS module, and never through a raw
`pyopenjtalk.g2p()` or a bare `KPipeline`.  The patches are token-level and
context-free, so they hold in any sentence, not just in one exact string.

Location
--------
`duo-android/tools/g2p_fixes.py` -- inside the repository, deliberately.  It
used to live in the working tree's `.scratch/` directory, which is OUTSIDE the
git repository, so it was untracked and unreviewable: a clean clone could not
reproduce the audio it produces.  It is now committed here.  Development-only
tooling; nothing in `duo-android/tools/` is part of any Android `sourceSet`
and cannot be packaged into the APK.

`.scratch/g2p_fixes.py` is a thin shim that loads THIS file and installs it as
the `g2p_fixes` module, so every pre-existing generator keeps working with no
flag day.

The defects patched here
------------------------
1. WA_TO_BETA.  `misaki/cutlet.py` HEPBURN maps chr(12431) わ -> 'βa',
   chr(12430) ゎ -> 'βa' and the う-series うぃ/うぇ/うぉ ->
   'βi'/'βe'/'βo'.  β is U+03B2, the Spanish VOICED bilabial
   fricative.  Japanese わ is a VOICELESS labiodental.  The same table
   already uses the correct symbol ɸ (U+0278) for ふ and
   ふぁ/ふぃ/ふぇ/ふぉ, so it is internally inconsistent.  This is an
   upstream misaki bug (verified present in hexgrad/misaki on main and
   inherited from polm/cutlet).  It is patched here at runtime on the
   instance table; the installed package is NOT edited.

2. GEMINATE_TO_GLOTTAL.  `misaki/cutlet.py::_get_single_mapping` has the
   correct geminate branch COMMENTED OUT and replaced by `return 'ʔ'`:
       if kk == 'っ':
           # tnk = self.table.get(nk)
           # if tnk and tnk[0] in 'bdɸɡhçijkmnɲopɾstʯvβz':
           #     return tnk[0]
           return 'ʔ'
   Japanese has no glottal stops, so a lesson that drills 学校 as beginning
   with a held /k/ was shipping a glottal stop.  pyopenjtalk produces the
   correct kana (ガッコー); the corruption is downstream, in the kana->IPA
   step.  This patch restores the real geminate and extends upstream's
   consonant set to cover the affricates and sibilants actually present in
   this table, which upstream's own set missed (upstream would still have
   produced ʔ for っち and っし).

3. X_FOR_Y_GAMMA.  espeak-ng emits BOTH /x/ and a spurious /j/ for a single
   written g before o:  cogio -> koxj'o, two palatals where there is one
   consonant.  Only that doubled-palatal signature is a defect.
   Plain /x/ in jefe, mujer, viajar, viaje, gire, jinete, tarjeta, gente,
   dije, dijimos, dijo, trabaja and trabajo is CORRECT Spanish and is NOT
   touched.  See the remediation report for the full evidence.

4. UPPERCASE_LEAK.  NO PATCH, DELIBERATELY.  This was investigated and found
   to be a FALSE POSITIVE in the audit, not a defect.  Kokoro's own English
   G2P writes the /au/ and /ei/ diphthongs as the capital symbols W and A:
       now   -> n'W       day   -> d'A
       how   -> h_,W      eight -> 'At
   So although they are written as capital ASCII letters, W and A are
   legitimate Kokoro diphthong phonemes and are present in the 114-symbol
   vocabulary.  Spanish  aunque -> 'Wnke  is /aunkе/ and  peina -> p'Ana
   is /peina/; both are CORRECT.  "Fixing" them would have made 7 clips
   worse.  Do not reintroduce this class without re-running this check.
"""
from __future__ import annotations

import os
import re
import sys
import warnings

warnings.filterwarnings("ignore")

# --- MECABRC must be set before misaki.ja is imported -----------------------
for _mod in ("unidic_lite", "unidic"):
    try:
        _pkg = __import__(_mod)
        _d = getattr(_pkg, "DICDIR", None)
        if _d and os.path.exists(os.path.join(_d, "mecabrc")):
            os.environ["MECABRC"] = os.path.join(_d, "mecabrc")
            break
        continue
    except ImportError:
        continue

KOKORO_REPO_ID = "hexgrad/Kokoro-82M"

# Each entry is (surface, hiragana_reading).  A surface is matched against the
# CONCATENATION of a run of consecutive tagger tokens, so it does not matter
# how fugashi happens to split the word on a given run: 日本語 arrives as
# ['日本','語'] and 停留所 as ['停留','所'], and both still match.
#
# Matching on the whole word, rather than on 一 alone, is what keeps the four
# readings fugashi already gets RIGHT intact: 一緒 (いっしょ), 一人 (ひとり),
# 一つ (ひとつ) and 一切 (いっさい).  Only 一枚 is wrong, and only in that word.
READING_OVERRIDES = [
    # 日本語: fugashi reads にっぽんご ("Nippon-go", a different word).
    ("日本語", "にほんご"),
    # 川: fugashi reads がわ, inserting a spurious /ɡ/.  かわ is correct.
    ("川", "かわ"),
    # 停留所 "bus stop" is ていりゅうじょ; fugashi gives てーりゅーしょ.
    ("停留所", "ていりゅうじょ"),
    # 一枚 is いちまい (no sokuon); fugashi reads いっまい.
    ("一枚", "いちまい"),
    # 一冊 is いっさつ (WITH sokuon); fugashi reads いちさつ.
    ("一冊", "いっさつ"),
    # --- spec-driven additions (see .scratch/voice_ab/voice_ab/) --------------
    # These three are pinned because the app's OWN declared romaji field says
    # something different from what the tagger produces, and that field is
    # visible to the learner.  The audio contradicting a displayed
    # specification is a defect by definition; no linguistic opinion needed.
    #
    # 私: the app declares "watashi"; fugashi reads わたくし, the archaic
    #     humble pronoun.  pyopenjtalk also gives わたし, so 2 dictionaries
    #     agree against the tagger.  Affects 3 clips.
    ("私", "わたし"),
    # 明日: the app declares "ashita"; fugashi reads あす.  Both あした and
    #     あす are valid Japanese, so this is a style choice -- but the app
    #     has already made it, in a field the learner can see.  pyopenjtalk
    #     also gives あした.  Affects 3 clips.
    ("明日", "あした"),
    # 来させる: the app declares "kurasaseru".  Both pyopenjtalk AND fugashi
    #     read きたさせる, so neither dictionary can adjudicate this one --
    #     they simply do not know the token is the CAUSATIVE of くる.  The
    #     basis is the app's declared romaji plus the judgement that くらさせ
    #     る is the causative and きたさせる is a different word.  Affects 1
    #     clip.  This is the weakest-evidenced of the three and is flagged as
    #     such here deliberately.
    ("来させる", "くらさせる"),
    # --- dictionary-reading defects (the app's romaji is right) -------------
    # 映画 is エイガ and nothing else.  unidic-lite's own entry for エイガ
    #     reads エーガ, so every clip containing 映画 was phonemised /eːga/ --
    #     a long vowel where the word has a glide.  Affects 3 clips.
    ("映画", "えいが"),
    # 辛い in this corpus is always "spicy food" (all four options of 60026
    #     are 辛いもの followed by 食べます-family), never "painful".  unidic-
    #     lite only knows the つらい reading, so karai_mono_wa_taberaremasen
    #     taught "painful things" in a sentence about spicy food.  Pinning the
    #     bare kanji is safe here precisely because "painful" does not occur.
    ("辛い", "からい"),
    # 定休日 is ていきゅうび -- the corpus's own ruleText for challenge 65007
    #     says so in as many words ("the 日 is bi, so teikyuubi").  unidic-lite
    #     reads 休日 as キュージツ, which is right for 休日 on its own (a
    #     national holiday) and wrong inside this word, so the override is
    #     keyed on 定休日 and leaves a bare 休日 alone.  Affects 1 clip.
    #
    # The long-vowel mark is deliberate.  This module's override path renders
    # a bare ゆう as TWO nuclei (ɨ then ɯ) rather than one (ː), so writing
    # ていきゅうび here phonemises to teikʲɨɯbʲi -- an extra vowel the word does
    # not have.  ていきゅーび gives teikʲɨːbʲi, which is the word.  The same
    # latent split is still in the 停留所 pin below and is NOT fixed here,
    # because re-rendering teeryuujo.ogg is out of this change's scope.
    ("定休日", "ていきゅーび"),
]

# NOT pinned, on purpose, and now resolved in the Kotlin.  In all three the
# G2P was already right and the app's declared romaji was the thing that was
# wrong, so pinning would have replaced correct audio with a non-word:
#   見られる -> the Kotlin declared "mirerareru", 5 mora for a 4-mora word.
#               Corrected to "mirareru"; the G2P みられる was always right.
#   待たせる -> the Kotlin declared "matasaseru", carrying a さ the word does
#               not have.  Corrected to "mataseru"; the G2P またせる is the
#               standard reading.
#   飲ませる -> same class.  Corrected to "nomaseru"; のませる is standard.
# No override is needed or wanted for any of them now, and none exists.

# ---------------------------------------------------------------------------
# 1 + 2: Japanese
# ---------------------------------------------------------------------------

# わ / ゎ / うぃ / うぇ / うぉ  -> voiceless labiodental ɸ.
# The HEPBURN keys are single codepoints for わ and ゎ and two-character
# strings for the う-series, so literal kana match the table exactly.
WA_FIXES = {
    "わ": "ɸa",    # chr(12431), was 'βa'
    "ゎ": "ɸa",    # chr(12430), was 'βa'
    "うぃ": "ɸi",  # was 'βi'
    "うぇ": "ɸe",  # was 'βe'
    "うぉ": "ɸo",  # was 'βo'
}

# Consonants a っ can geminate.  Upstream's commented-out set was
# 'bdɸɡhçijkmnɲopɾstʯvβz', which omits every affricate and sibilant in this
# table, so it would still have returned ʔ for っち / っし.  This is the
# upstream set plus the ones actually needed.
GEMINATE_CONSONANTS = set("bdɸɡhçijkmnɲopɾstɯvβz") | set("ʦʨʥɕʧʤʈʑɻɽ")


def _patch_cutlet(cutlet):
    changed = []
    for kana, correct in WA_FIXES.items():
        if kana in cutlet.table:
            if cutlet.table[kana] != correct:
                changed.append("%s: %r -> %r"
                               % (kana, cutlet.table[kana], correct))
            cutlet.table[kana] = correct

    if getattr(cutlet, "_g2p_fixes_installed", False):
        return changed

    # --- the っ geminate ------------------------------------------------
    original = cutlet._get_single_mapping

    def _get_single_mapping(pk, kk, nk, _orig=original, _self=cutlet):
        if kk == "っ":
            tnk = _self.table.get(nk)
            if tnk and tnk[0] in GEMINATE_CONSONANTS:
                return tnk[0]
            # nothing usable follows (っ at the very end of the utterance):
            # emit nothing rather than a glottal stop, which is not a
            # Japanese sound
            return ""
        return _orig(pk, kk, nk)

    cutlet._get_single_mapping = _get_single_mapping

    # っ can land as the LAST kana of a morpheme group (安かった splits into
    # [安かっ][た]), which leaves _romaji_word with nk=None and therefore no
    # consonant to copy.  Merge any such group into the following one so the
    # geminate always has a following kana.  This also removes exactly the
    # word-boundary space that upstream's ʔ-adjacency regex used to strip, so
    # the pause cannot leak into the audio now that ʔ is gone.
    from misaki.cutlet import Word

    _orig_tokens = cutlet._romaji_tokens

    def _romaji_tokens(words, _orig=_orig_tokens, _Word=Word):
        # 1. reading overrides: collapse a run of surfaces into one token
        #    whose kana is the reading we pin.
        for surface, kana in READING_OVERRIDES:
            if not surface:
                continue
            out, i, n = [], 0, len(words)
            while i < n:
                hit = None
                # longest match wins, so a longer override is never shadowed
                for width in range(min(len(surface), n - i), 0, -1):
                    if "".join(w.surface for w in words[i:i + width]) == surface:
                        hit = width
                        break
                if hit:
                    out.append(_Word("".join(w.surface for w in words[i:i + hit]),
                                     kana, 6))
                    i += hit
                else:
                    out.append(words[i])
                    i += 1
            words = out
        # 2. a trailing っ needs a following kana to copy, so merge its group
        #    into the next one.  This also removes exactly the word-boundary
        #    space that upstream's ʔ-adjacency regex used to strip, so the
        #    pause cannot leak into the audio now that ʔ is gone.
        merged = []
        for w in words:
            if merged and merged[-1].hira.endswith(("っ", "ッ")):
                prev = merged.pop()
                merged.append(_Word(prev.surface + w.surface,
                                    prev.hira + w.hira,
                                    prev.char_type))
            else:
                merged.append(w)
        return _orig(merged)

    cutlet._romaji_tokens = _romaji_tokens
    cutlet._g2p_fixes_installed = True
    return changed


_JA_SINGLETON = None
_ES_SINGLETON = None


def make_ja_g2p():
    """Return a misaki JAG2P with the わ and っ fixes applied to its table."""
    global _JA_SINGLETON
    if _JA_SINGLETON is None:
        from misaki import ja as misaki_ja
        g2p = misaki_ja.JAG2P()
        if g2p.cutlet is not None:
            _patch_cutlet(g2p.cutlet)
        _JA_SINGLETON = g2p
    return _JA_SINGLETON


def jpn_g2p(text):
    """Phonemise Japanese with the project's fixed G2P. Returns a str."""
    return make_ja_g2p()(text)[0]


# ---------------------------------------------------------------------------
# 3: Spanish
# ---------------------------------------------------------------------------

# Only the DOUBLED-palatal signature is a defect for the first three: espeak
# writes both /x/ and a spurious /j/ for the single g in these words.  Written
# g before o is /ɣ/.
#
# `corras` is a different kind of entry.  `corras` is a LLANA word - it ends in
# -s - so Spanish puts the stress on the penultimate syllable: co-RRAS.  espeak
# writes the trill correctly and then stresses the WRONG syllable, kˈoras =
# /ˈko.ras/, which is a stress error and not a variant: a two-syllable word
# ending in -s cannot be stressed on its first syllable.  It is here rather
# than in a new table because the rule that repairs it is the same one that
# repairs the doubled palatal - replace one word's espeak output with the
# string this project says is right.
SPANISH_WORD_FIXES = {
    "cogió": "koˈɣo",
    "cogieron": "koˈɣeɾon",
    "cogíamos": "koˈɣiamos",
    "corras": "koˈras",
}

# The entries that must NOT fire unless the word actually carries the
# doubled-palatal /xj/ signature.  `cogió` and `cogieron` genuinely do, and
# `cogía` (which must stay hard) genuinely does not - so these two stay gated.
#
# `cogíamos` is DELIBERATELY NOT in this set.  It was, and that made the entry
# dead: espeak's own output for `cogíamos` is `koxˈiamos`, which has no /xj/, so
# the gate could never be satisfied and the table's `koˈɣiamos` never ran -
# under the old whole-string gate exactly as under the new per-word one, which
# is how it stayed broken and unnoticed.  Ungated, the repair fires whenever
# the word appears and gives it the /ɣ/ and the stress it needs.
SIG_GATED_FIXES = {"cogió", "cogieron"}
# espeak-ng's `es` reads a written `y` that follows `u` as the CONSONANT /j/
# instead of the vowel [i]: `muy` comes out as mˈuj, `cuy` as kˈuj, `buy` as
# bˈuj, `arguy` as aɾɣˈuj.  Spanish is unambiguous here - in `muy` the `y` is
# the nucleus of the second syllable and the word is /mui/ - so this is a
# wrong sound, not a variant, and it is the Spanish counterpart of the
# Japanese glottal-stop defect this module already repairs.  It is a genuine
# gap in espeak's rule set, not a dialect: the same engine reads `hoy`, `voy`,
# `soy` and `doy` correctly as diphthongs, so only the `u` + `y` pairing is
# broken.
#
# Deliberately NOT touched: `suya` and `tuya`, where the `u`+`a` pair is
# present and the `y` is correctly the consonant /ʝ/; and `ley`/`rey`, where
# `y` after a consonant is the vowel /i/ and espeak's own output for those is
# a separate matter from this rule.
SPANISH_UY_FIXES = {
    "muy": "ˈmui",
    "cuy": "ˈkui",
    "buy": "ˈbui",
    "arguy": "aɾˈɣui",
    "arguyendo": "aɾˈɣiendo",
    "argüy": "aɾˈɣwi",
}

# --- Spanish: z before a/o/u is /s/ in every variety --------------------
# espeak-ng's `es` voice emits the dental fricative /θ/ for orthographic z
# before a/o/u (zapato -> θapˈato), where Spanish is /s/ in every variety.
# The project is seseante, so the G2P input must say /s/.  The general fix
# is to normalise the ORTHOGRAPHY before espeak sees it: written z before
# a/o/u is rewritten to s, which espeak always reads as /s/.  This is a
# pre-processing step on the input text, not a post-hoc edit of the phoneme
# string, so it generalises to any word and any capitalisation.
#
# It fires ONLY for z before a/o/u.  z before e/i (diez, vez) and every
# ce/ci sequence (gracias, cocina, ciudad) keep espeak's θ, which is the
# project's chosen reading for those.  A z at a word end or before a
# consonant is untouched (the regex requires a following a/o/u).
_Z_BEFORE_AOU_RE = re.compile(r"([zZ])([aáoóuúAÁOÓUÚ])")


def _z_to_s_before_aou(text):
    """Rewrite Spanish z before a/o/u to s, preserving case and the vowel."""
    return _Z_BEFORE_AOU_RE.sub(
        lambda m: ("s" if m.group(1) == "z" else "S") + m.group(2),
        text,
    )


_WORD_RE = re.compile(r"[A-Za-zÁÉÍÓÚÜÑáéíóúüñ]+")


class PatchedEsG2P:
    """A real EspeakG2P('es') with the doubled-palatal repair wrapped around it.

    This is a real class rather than a monkeypatched instance because Python
    resolves special methods on the TYPE, not the instance: assigning
    `base.__call__ = ...` on an EspeakG2P object silently does nothing.
    """

    def __init__(self):
        from misaki import espeak as misaki_espeak
        self._base = misaki_espeak.EspeakG2P(language="es")

    def __call__(self, text, *args, **kwargs):
        text = _z_to_s_before_aou(text)
        phonemes, rest = self._base(text, *args, **kwargs)
        joined = "".join(phonemes)
        words = sorted({w.lower() for w in _WORD_RE.findall(text)})

        # The u+y repair runs FIRST and on its own condition.  It must not be
        # gated on the doubled-palatal signature the way SPANISH_WORD_FIXES
        # is, because `muy` produces no /xj/ at all: gating it would leave the
        # one defect this rule exists for silently unfixed.
        for word in words:
            want = SPANISH_UY_FIXES.get(word)
            if not want:
                continue
            got = self._base(word)[0]
            if "j" not in got:
                continue
            i = joined.find(got)
            if i >= 0:
                joined = joined[:i] + want + joined[i + len(got):]

        # token-level and context-free: repair per WORD, never per whole
        # string, so it works in any sentence, in any order, and for any
        # number of occurrences.  This is the property the old per-script
        # "dijimos." monkeypatch lacked.
        #
        # The whole-string `if "xj" not in joined: return` gate that used to
        # sit here is GONE, deliberately.  It was a leftover from when this
        # table held nothing but the doubled-palatal repairs, and it silently
        # disabled the whole mechanism for every other word: `corras` produces
        # no /xj/ at all - exactly as `muy` produces none for
        # SPANISH_UY_FIXES - so a gate on /xj/ would have left the stress
        # repair unfixed while every test still passed.  An entry that really
        # does need the /xj/ signature now states that itself; see
        # SIG_GATED_FIXES.
        for word in words:
            want = SPANISH_WORD_FIXES.get(word)
            if not want:
                continue
            got = self._base(word)[0]
            if word in SIG_GATED_FIXES and "xj" not in got:
                continue
            i = joined.find(got)
            if i >= 0:
                joined = joined[:i] + want + joined[i + len(got):]
        # return a str in both paths, matching EspeakG2P's own contract
        return joined, rest


def make_es_g2p():
    """Return an EspeakG2P('es') wrapped with the doubled-palatal repair."""
    global _ES_SINGLETON
    if _ES_SINGLETON is None:
        _ES_SINGLETON = PatchedEsG2P()
    return _ES_SINGLETON


def spa_g2p(text):
    """Phonemise Spanish with the project's fixed G2P. Returns a str."""
    return make_es_g2p()(text)[0]


# ---------------------------------------------------------------------------
# the real KPipeline path, patched
# ---------------------------------------------------------------------------

def make_pipeline(lang_code, model=True, repo_id=KOKORO_REPO_ID, **kw):
    """A KPipeline whose .g2p is this project's patched G2P.

    Use this for BOTH generation and verification.  Verification through
    pyopenjtalk.g2p() measures a different stage of the pipeline than the
    audio goes through, which is exactly how the geminates in がっこう /
    きって / はっぴょう shipped wrong and passed review.

    `model` defaults to True (kokoro's own default) so that omitting it and
    passing the default mean the same thing.  Pass a loaded KModel to share
    one model across languages; pass model=False for a genuinely quiet
    pipeline.  See the ValueError below for why None is not accepted.
    """
    if model is None:
        raise ValueError(
            "make_pipeline(model=None) builds a QUIET kokoro pipeline: it "
            "yields the correct phonemes but audio=None for every chunk, so "
            "sf.write() later dies with 'IndexError: tuple index out of range' "
            "from inside soundfile and every G2P check still passes.  Pass "
            "model=True (the default) for a pipeline that loads its own "
            "KModel, a loaded KModel to share one, or model=False if you "
            "really do want phonemes with no audio."
        )
    from kokoro import KPipeline
    pipe = KPipeline(lang_code=lang_code, model=model, repo_id=repo_id, **kw)
    if lang_code == "j":
        pipe.g2p = make_ja_g2p()
    elif lang_code == "e":
        pipe.g2p = make_es_g2p()
    return pipe


# ---------------------------------------------------------------------------
# self-test
# ---------------------------------------------------------------------------

if __name__ == "__main__":
    if hasattr(sys.stdout, "reconfigure"):
        sys.stdout.reconfigure(encoding="utf-8", errors="replace")
    print("=== Japanese: わ must be ɸa, never βa ===")
    for t in ["私", "わたし", "川", "日本語はむずかしいと思います", "尺", "うぃ"]:
        got = jpn_g2p(t)
        print("  %-16s -> %-34r  beta=%s" % (t, got, "β" in got))
    print()
    print("=== Japanese: っ must geminate, never ʔ ===")
    for t in ["買った", "がっこう", "きって", "はっぴょう",
              "ちょっと待ってください", "切符を買わなければ", "待った", "行ったら"]:
        got = jpn_g2p(t)
        print("  %-18s -> %-38r  glottal=%s" % (t, got, "ʔ" in got))
    print()
    print("=== Spanish: doubled palatal repaired, plain /x/ left alone ===")
    for t in ["cogió", "Dijo que cogió el primer tren.", "jefe", "mujer",
              "viajar", "trabajo", "dijimos", "dijo"]:
        got = spa_g2p(t)
        print("  %-34s -> %-42r  xj=%s" % (t, got, "xj" in got))
    print()
    print("=== Spanish: W and A diphthongs must be LEFT ALONE ===")
    for t in ["aunque", "peina", "veinte", "hubieseis"]:
        print("  %-12s -> %r" % (t, spa_g2p(t)))

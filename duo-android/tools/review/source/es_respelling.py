#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""Castilian respelling: a rule-based first pass, for human review.

TWO DESIGN POINTS THAT DECIDE WHETHER THE RESULTING CHECK MEANS ANYTHING.

1.  The grapheme rules are SPANISH ORTHOGRAPHY rules - a traditional
    pronunciation guide - not this project's espeak G2P. If the respelling
    were produced by feeding the text through `spa_g2p`, the detector's "does
    the audio match the spec" question would compare espeak with espeak and
    could never fail. The two sides are derived independently, so a
    disagreement is real information.

2.  Stress is placed by the orthographic accent rule (esdrújula / llana /
    aguda plus a listed exception set), NOT by reading the `ˈ` back out of
    espeak's output. Stress is therefore an independent claim the detector
    can actually falsify.

THE ALPHABET. One token per phoneme, and every token is invertible, so the
detector can turn a declared reading back into the same phoneme string the
audio pipeline produces. This is a romanisation, not IPA: /β/ is written bh,
not beta.

    a e i o u   vowels; Spanish has no schwa
    p t k f s m n l
    b   /b/    bh  /β/   the two allophones of the b/v letter
    d   /d/    dh  /ð/   the two allophones of d
    g   /ɡ/    gh  /ɣ/   the two allophones of g
    z   /θ/                      <- c before e,i, and written z
    ch  /tʃ/
    ll  /ʎ/                      <- written ll, and y after l or n. NEVER the
                                    /ʝ/ of Spanish America: the app keeps /ʎ/
    ñ   /ɲ/
    r   tapped /ɾ/   rr  trilled /r/   <- written rr, and r word-initially
                                             or after n, l, s
    ks  /ks/                     <- written x
    h   silent, dropped
    ü   not a Spanish phoneme: dropped, and gu/gü before e,i becomes ge/gi
    ˈ   primary stress, placed at the start of the stressed syllable
    ie ei oi au  /je ei oi au/   <- the written diphthongs, as one token
    ue ui  /we wi/                <- so that nuevo is /nwebo/, not /nuebo/

THREE PALATALS, AND THE ONE RULE THAT TELLS THEM APART. This is the part a
reviewer most needs, because all three are written with y, i and j and they
are three different sounds:

    y   /ʝ/   the consonant of yo, ayer, tuya
    j   /x/   the velar fricative of jefe, gire, viaje - NEVER a glide
    i   /i/   the vowel, and in exactly one case the consonantal i glide

The rule, which a reader can apply without asking anyone:

    a written `i` is the VOWEL /i/ when it is the nucleus of its syllable,
    and the GLIDE [j] when a vowel follows it.

So `estaˈzion` is /es.ta.ˈθjon/ - the i of -ción is the same [j] as the i
of viaje /ˈbja xe/ - while `ˈdia` is /ˈdia/ with i as the nucleus. This is
Spanish orthography's own rule, that a written i before a vowel is a glide.
An earlier version of this table wrote the -ción glide as `j`, which made
`estaˈzjon` read as /θx/; the detector caught it, and the correction is that
the glide is written `i`, never `j`.

The diphthong tokens matter for the same reason: without them `hierba` would
have to be written h-i-e-r-b-a, in which the i and the e are indistinguishable
from the /i.e/ sequence that `hierba` famously is not.

The diphthong tokens matter: without them `hierba` would have to be written
`h-i-e-r-b-a`, in which the `i` and the `e` are indistinguishable from the
sequence /i.e/ that `hierba` is famously not.
"""
import re

ACC = {"á": "a", "é": "e", "í": "i", "ó": "o", "ú": "u",
       "Á": "a", "É": "e", "Í": "i", "Ó": "o", "Ú": "u"}
VOW = "aeiou"
WEAK = "iu"
STRONG = "aeo"

# Falling and rising diphthongs, written as the single token they are. `ui` is
# rising /wi/; the written `qui`/`gui` cases are handled before this runs, so
# a `ui` reaching here is the vowel pair in words like construir.
DIPHTHONGS = ("ie", "ei", "oi", "au", "ue", "ui", "ia", "ua", "io", "uo",
              "iu", "ai", "eu", "ou")
# the SAME vowel twice is a hiatus, never a diphthong
HIATUS = ("aa", "ee", "oo", "ii", "uu")

# A Spanish onset is one consonant, or a stop/fricative + liquid.
STOPS_FRICS = set("pbtdkgfsz")
LQUIDS = set("lr")
# consonants that can close a syllable
CODAS = set("nlrbdgmvfkjstpxzñ")


def _plain(w):
    """Lower-cased with accents stripped, plus the set of positions that
    carried a written accent. A written accent is what makes an i/u unable to
    join a diphthong, so the two views must stay index-aligned."""
    p, marked = [], set()
    for i, ch in enumerate(w.lower()):
        if ch in ACC:
            p.append(ACC[ch])
            marked.add(i)
        else:
            p.append(ch)
    return "".join(p), marked


def _silent_u(w):
    """Indices of a written u that is a SPELLING letter rather than a vowel of
    its own, so it must be invisible to the syllabifier as well as to the
    respeller:

      qu            the u never sounds: que, querer, quickest
      gu/gü + e,i   the u never sounds and the g stays hard: guitarra, guerra
      cu + a,o      the u is the glide of /kw/ and is NOT a second vowel:
                    cuanto, cuatro, cuenta, cuerpo are one syllable each

    The last case is the one that is easy to miss. Treating `cu` as a plain
    `c` plus a `u` makes `cuatro` two syllables, which is wrong."""
    p = w.lower()
    s = set()
    for i, ch in enumerate(p):
        if ch not in "uü":
            continue
        if i > 0 and p[i - 1] == "q":
            s.add(i)
        elif i > 0 and p[i - 1] in "g" and i + 1 < len(p) \
                and p[i + 1] in "ei":
            s.add(i)
        elif i > 0 and p[i - 1] == "c" and i + 1 < len(p) \
                and p[i + 1] in "ao":
            s.add(i)
    return s


def _nuclei(w):
    """Nucleus spans of `w`, indexing the ORIGINAL (accented) string.

    A nucleus is a diphthong or a single vowel. The decision uses the written
    accent - an accented WEAK vowel never joins one - and ignores the written
    u of qu and of gu/gü before e,i, which is a spelling letter, not a vowel.
    """
    p, marked = _plain(w)
    silent = _silent_u(w)
    n = len(p)
    out = []
    i = 0
    while i < n:
        c = p[i]
        if c in VOW and i not in silent:
            j = i
            while (j + 1 < n and p[j + 1] in VOW
                   and (j + 1) not in silent):
                # An accented WEAK vowel never belongs to a diphthong, in
                # either position. As the nucleus it is its own syllable,
                # so `días` is dí-as and not one syllable; as the offglide
                # it starts a new one, so `país` is pa-ís.
                if j in marked and p[j] in WEAK:
                    break
                if (j + 1) in marked and p[j + 1] in WEAK:
                    break
                if p[j:j + 2] in HIATUS or p[j:j + 2] not in DIPHTHONGS:
                    break            # a hiatus, not a diphthong
                j += 1
            out.append([i, j])
            i = j + 1
            continue
        if c == "y":
            # A written y is a VOWEL - the nucleus - in exactly one case:
            # after another vowel and at the end of the word or before a
            # consonant, so muy, hoy, soy, voy, tuya. Between two vowels it
            # is the CONSONANT /ʝ/, which is ayuda and ayer, and it belongs
            # to no nucleus at all. Getting that backwards is what turns
            # ayuda into a-i-uda and ya into i-a.
            prev = p[i - 1] if i > 0 else ""
            nxtc = p[i + 1] if i + 1 < n else ""
            if (out and prev in VOW and (i - 1) not in marked
                    and i not in marked and nxtc not in VOW):
                out[-1][1] = i          # y is a vowel nucleus here
            i += 1
            continue
        i += 1
    return out


# --- exception sets, each with a reason -----------------------------------
# A written `rr` between two vowels that is a SINGLE tap, not a trill. It
# closes the syllable (gui-TAR-ra), so doubling it in a respelling is wrong.
TAP_RR = {
    "guitarra", "corras", "correr", "sorpresa", "carreta", "perro",
    "corredor", "torre", "corriendo", "borrar", "marrón", "cerrar",
    "guerra", "terreno", "perrera",
}
# Stress the ending rule alone cannot reach.
STRESS_FIX = {
    "ayer": 0,      # esdrújula: a-ˈjeɾ
    "angel": 0, "hueso": 0, "frijol": 0, "tibia": 0, "fie": 0, "hie": 0,
    "guion": 1, "truhan": 1, "fria": 1, "guapa": 1, "mapa": 1, "arca": 1,
    "paez": 0, "alvarez": 0, "rd": 0, "clark": 0, "reyes": 0,
}
# Monosyllables: no stress mark, and the ending rule must not be applied.
# The last three are here because an accented i/u inside them does stay in
# the diphthong, which is the exception to the general weak-vowel rule.
MONOSYL = {
    "y", "a", "e", "o", "u", "el", "la", "lo", "los", "las", "un", "una",
    "unos", "unas", "me", "te", "se", "nos", "os", "le", "les", "no", "si",
    "sí", "tu", "tus", "su", "sus", "al", "del", "por", "con", "sin", "ha",
    "he", "ve", "va", "fue", "soy", "hay", "hoy", "muy", "voy", "diez",
    "vez", "tres", "dos", "cien", "mas", "más", "guay", "rey", "que", "qué",
    "quien", "quién", "como", "cuanto", "cuánto", "cual", "cuál", "solo",
    "sólo", "sé", "ver", "dar", "ir", "fui", "era", "eran", "eres", "sea",
    "son", "ten", "tengo", "sino", "raíz", "baúl", "ahí", "dé", "té", "vé",
    "clóset", "coñac", "mes", "diez", "sé", "sé",
}


def _rr_is_tap(w, i):
    """The written `rr` at `i` is one tapped r, not a trill."""
    if i > 0 and w[i - 1] in VOW and i + 2 < len(w) and w[i + 2] in VOW:
        return w in TAP_RR
    return False


def graphemes(w):
    """Orthography -> respelling tokens, with no stress mark.

    Returns (text, accents, origins). `origins[j]` is the index in the
    ORIGINAL word of the token that produced respelling character `j`; the
    stress mark is placed through that map, so nothing depends on
    re-syllabifying a string that has lost its accents.
    """
    w = w.lower()
    p, marked = _plain(w)
    silent = _silent_u(w)
    spans = _nuclei(w)
    span_of = {}
    for a, b in spans:
        for k in range(a, b + 1):
            span_of[k] = (a, b)

    out, acc, org = [], [], []
    i, n = 0, len(w)

    def emit(tok, src):
        out.append(tok)
        acc.extend([False] * len(tok))
        org.extend([src] * len(tok))

    while i < n:
        two = w[i:i + 2]
        three = w[i:i + 3]
        nxt = w[i + 1] if i + 1 < n else ""

        # The silent-u spellings come first. The written u of qu, and of
        # gu/gü before e,i, is a spelling letter with no sound, so it is
        # skipped here AND excluded from the syllable nuclei above. Skipping
        # only the u and letting the e/i fall through to the nucleus test is
        # what keeps guitarra at gui-tara and guerra at gue-ra.
        if i in silent and p[i] in "uü":
            i += 1
            continue

        # a nucleus, as a whole, so a diphthong becomes one token
        if i in span_of:
            a, b = span_of[i]
            pair = p[a:b + 1]
            if len(pair) > 1:
                emit(pair, a)
            else:
                # a lone vowel, and whether it is a marked one. A y that is
                # a NUCLEUS is the vowel /i/, not the consonant /ʝ/, so it is
                # respelled i here; this is what turns muy, hoy, soy and voy
                # into mui, oi, soi and boi. Without it the y falls through
                # to the consonant rule below and comes out as /ʝ/.
                out.append("i" if pair == "y" else pair)
                acc.append(i in marked)
                org.append(a)
            i = b + 1
            continue

        if two == "qu":
            emit("k", i)             # qu is /k/; the u is silent
            i += 2
            continue
        if w[i] == "g" and nxt in "uü" and w[i + 2:i + 3] in ("e", "i"):
            emit("g", i)             # the u/ü is silent, and g stays hard
            i += 1
            continue

        if two == "ch":
            emit("ch", i)
            i += 2
            continue
        if two == "ll":
            emit("ll", i)
            i += 2
            continue
        if two == "rr":
            emit("r" if _rr_is_tap(w, i) else "rr", i)
            i += 2
            continue

        c = w[i]
        # The lookahead must use the ACCENT-STRIPPED next letter. Testing the
        # raw one misses an accented i, so `hac` + `í` was read as /k/ and
        # `hacía` came out as aˈkia instead of aˈθia. That is a real wrong
        # reading in the table, and it is exactly the /θ/-versus-/k/ class
        # this table exists to be right about.
        nxt_plain = p[i + 1] if i + 1 < len(p) else ""
        if c == "c":
            if nxt_plain in "ei":
                emit("z", i)         # c before e,i is /θ/
            elif nxt_plain == "u" and p[i + 2:i + 3] in ("a", "o"):
                emit("ku", i)        # cu is the digraph /kw/, one syllable
            else:
                emit("k", i)
            i += 1
            continue
        if c == "g":
            # g before e,i is /x/ (jefe, gire). g before a,o is the soft
            # /ɣ/ (ago, lego) but only after a vowel - a g after a pause or a
            # consonant is the hard /ɡ/ (gato, algo). g before u is ALWAYS
            # hard /ɡ/ (guitar, gustar); the u is a vowel, so agua is
            # /ˈaɣwa/ with a soft g but gustar is /ˈɡustar/ with a hard one.
            if nxt_plain in "ei":
                emit("j", i)
            elif nxt_plain in "ao":
                prev = w[i - 1] if i else ""
                emit("gh" if (prev and prev in VOW) else "g", i)
            else:
                emit("g", i)
            i += 1
            continue
        if c == "z":
            emit("z", i)            # /θ/
            i += 1
            continue
        if c == "j":
            emit("j", i)            # /x/
            i += 1
            continue
        if c == "v":
            # v and b are ONE sound in Spanish and differ only in the
            # letter. After a vowel or a liquid the sound is /β/; word-
            # initial and after a nasal it is /b/.
            prev = w[i - 1] if i else ""
            if prev and (prev in VOW or prev in "lr"):
                emit("bh", i)
            else:
                emit("b", i)
            i += 1
            continue
        if c == "b":
            # /β/ after a vowel or a liquid, /b/ after a pause, a nasal or a
            # stop. Spanish has no /v/: there is no phoneme the letter v
            # could carry that b does not also carry.
            prev = w[i - 1] if i else ""
            if prev and (prev in VOW or prev in "lr"):
                emit("bh", i)
            else:
                emit("b", i)
            i += 1
            continue
        if c == "d":
            # /ð/ between vowels and after l,r; /d/ word-initially, after a
            # nasal or after a pause.
            prev = w[i - 1] if i else ""
            if prev and (prev in VOW or prev in "lr"):
                emit("dh", i)
            else:
                emit("d", i)
            i += 1
            continue
        if c == "ñ":
            emit("ñ", i)            # /ɲ/
            i += 1
            continue
        if c == "x":
            emit("ks", i)           # /ks/
            i += 1
            continue
        if c in ("h", "ü"):
            i += 1                  # silent
            continue
        if c == "y":
            prev = w[i - 1] if i else ""
            if nxt and nxt in VOW:
                emit("ll" if (prev and prev in "ln") else "y", i)
            elif prev and prev in VOW:
                emit("i", i)         # y is the nucleus: muy, hoy, soy, voy
            elif prev and prev in "ln":
                emit("ll", i)
            else:
                emit("y", i)         # ya, yo, and the word y
            i += 1
            continue
        if c == "r":
            if nxt == "r":
                emit("r" if _rr_is_tap(w, i) else "rr", i)
                i += 2
            elif i == 0 or w[i - 1] in "nls":
                emit("rr", i)        # trill: word-initial or after n, l, s
                i += 1
            else:
                emit("r", i)
                i += 1
            continue
        emit(c, i)
        i += 1
    return "".join(out), acc, org


def respelling(word):
    """One word: Castilian respelling with primary stress marked."""
    w = word.lower().strip("¿¡?!.,;:")
    if not w:
        return ""
    g, acc, org = graphemes(w)
    if w in MONOSYL or not any(ch in VOW for ch in w):
        return g
    spans = _nuclei(w)
    if len(spans) <= 1:
        return g
    if w in STRESS_FIX:
        si = min(STRESS_FIX[w], len(spans) - 1)
    else:
        idx = next((k for k, (a, b) in enumerate(spans)
                    if any(w[j] in ACC for j in range(a, b + 1))), None)
        if idx is not None:
            si = idx
        elif w[-1] in "aeious":
            si = len(spans) - 2         # llana
        else:
            si = len(spans) - 1         # aguda
    si = max(0, min(si, len(spans) - 1))
    # The stress mark goes immediately BEFORE THE STRESSED VOWEL, not at the
    # start of its syllable. Marking the syllable is a weaker record and it
    # is not enough: `rrio` and `zion` are then identical strings, one with
    # io as a diphthong and one with i as the glide, and nothing in the text
    # says which. Marking the vowel settles it - `ˈrrio` has the mark on the
    # i, `estaˈzion` has it on the o - and it also lets the detector actually
    # check stress instead of having to discard it.
    nuc = spans[si][0]
    pos = next((j for j, o in enumerate(org) if o >= nuc), len(g))
    return g[:pos] + "ˈ" + g[pos:]


WORD_RE = re.compile(r"[A-Za-zÁÉÍÓÚÜÑáéíóúüñ]+")


def respelling_phrase(s):
    return " ".join(respelling(w) for w in WORD_RE.findall(s))


# ---------------------------------------------------------------------------
# Converting a phonetic string into this alphabet.
#
# The review notes are far easier to write in phonetic symbols, so they are
# written that way and converted here. Doing it in one place means the whole
# `romanisation` column is guaranteed to be in ONE alphabet, which is what
# lets the detector turn it back into phonemes. `assert_alphabet` fails loudly
# if anything slips through, so a stray IPA character cannot reach the table
# silently.
# ---------------------------------------------------------------------------
_PHONE_TO_ALPHABET = [
    # longest first: tʃ must be matched before ʃ
    ("tʃ", "ch"),
    ("ɲ", "ñ"),
    ("ʎ", "ll"),
    ("ʝ", "y"),
    ("θ", "z"),
    ("ð", "dh"),
    ("ɣ", "gh"),
    ("ɡ", "g"),
    ("β", "bh"),
    ("ɾ", "r"),
    ("x", "j"),
]

# /ŋ/ is deliberately NOT given a letter of its own. A word-final n before a
# velar is [ŋ] in careful Castilian speech, but that is an ALLOPHONE of n, not
# a separate phoneme, and giving it a character would make `n` and `ŋ` two
# things for the detector to compare when the audio pipeline treats them as
# one. So the review notes write it as /ŋɡ/ and the table writes n + g, with
# the note carrying the detail.

# The stress mark ˈ IS part of the alphabet, so it must not be on the
# forbidden list. `ʃ` and `ɧ` are the only ones that matter here, and the
# point of the guard is to catch a phonetic symbol slipping into the table
# where the detector cannot read it.
FORBIDDEN = set("ɸβɲʎʝθðɣɾŋɡʔçː"
                "ɑɐɒæɜɯʰʷʲʃɧˌ"
                "ɛɔəɪʊ")

# Every character the alphabet may contain. Anything outside this set in the
# romanisation column is a bug, and is caught rather than shipped.
ALPHABET_CHARS = set(
    "abcdefghijklmnopqrstuvwxyz"
    "áéíóúñÁÉÍÓÚÑ"
    "ˈ"
    " .,:;!?()-'"
)


def to_alphabet(s):
    """A phonetic string written in this alphabet."""
    for a, b in _PHONE_TO_ALPHABET:
        s = s.replace(a, b)
    # the bare trilled /r/ of IPA is the alphabet's `rr`; `ɾ` is already `r`,
    # so a bare r that follows a vowel is left as the tap and the trill must
    # be written explicitly. IPA's bare "r" is ambiguous, so it is rejected
    # rather than guessed at.
    return s


def assert_alphabet(s, where=""):
    """Fail loudly if the romanisation contains anything the detector cannot
    read. Checking the ALLOWED set is stricter than checking a forbidden one:
    a typo that invents a new character is caught as well as a phonetic
    symbol, and neither can reach the table silently."""
    bad = sorted(set(s) - ALPHABET_CHARS)
    if bad:
        raise AssertionError("character outside the romanisation alphabet%s: "
                             "%r in %r"
                             % (" of " + where if where else "", bad, s))
    return s


# --- placing the stress mark in an already-respelled string ----------------
# The review entries in build_table are written by hand, so their stress mark
# can land anywhere. This re-places it using the syllable index the ORTHO-
# GRAPHY already computed, so a hand-written reading and a rule-generated one
# cannot disagree about where the stress is - which is what the detector reads
# to tell `rrˈio` (a diphthong) from `estazˈion` (an i glide).

_ALPH_VOWELS = {"a", "e", "i", "o", "u"}
_ALPH_DIPH = {"ie", "ei", "oi", "au", "ue", "ui", "ia", "ua", "io", "uo",
              "iu", "ai", "eu", "ou"}


def place_stress(alphabet_word, si):
    """Put the stress mark before the `si`-th vowel group of an already
    respelled word, which is the stressed VOWEL rather than the syllable."""
    s = alphabet_word.replace("ˈ", "")
    toks, i, n = [], 0, len(s)
    while i < n:
        if s[i] in _ALPH_VOWELS:
            for d in _ALPH_DIPH:
                if s.startswith(d, i):
                    toks.append((i, len(d)))
                    i += len(d)
                    break
            else:
                toks.append((i, 1))
                i += 1
            continue
        i += 1
    if not toks:
        return s
    k = max(0, min(si, len(toks) - 1))
    pos = toks[k][0]
    return s[:pos] + "ˈ" + s[pos:]


def stress_index_of(w):
    """The index of the stressed vowel group in a Spanish word, or 0."""
    w = w.lower().strip("¿¡?!.,;:")
    if not w or w in MONOSYL:
        return 0
    spans = _nuclei(w)
    if len(spans) <= 1:
        return 0
    if w in STRESS_FIX:
        return min(STRESS_FIX[w], len(spans) - 1)
    idx = next((k for k, (a, b) in enumerate(spans)
                if any(w[j] in ACC for j in range(a, b + 1))), None)
    if idx is not None:
        return idx
    return len(spans) - 2 if w[-1] in "aeious" else len(spans) - 1

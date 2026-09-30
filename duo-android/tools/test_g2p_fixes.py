#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""Regression guard for the Spanish z-before-a/o/u G2P fix.

Run with the project venv Python from this directory:

    H:/Projects/DuoLingo/.venv_tts/Scripts/python.exe test_g2p_fixes.py

Exits 0 when every assertion holds, 1 otherwise.

The Spanish assertions fail on the UNFIXED module (espeak-ng's `es` voice
emits θ for orthographic z before a/o/u, where Spanish is /s/ in every
variety).  The Japanese, English and audibility assertions hold either way,
so a run on the unfixed module isolates exactly the defect this fix repairs
and a run on the fixed module is a clean pass.

The Japanese and English expected values were captured from the real pipeline
BEFORE the fix was applied; the fix touches neither path, so equality after
the fix is the byte-identity proof.
"""
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))

import g2p_fixes

# --- Spanish: MUST now yield /s/ ------------------------------------------
# Written z before a/o/u is /s/ in every Spanish variety.  Includes the
# sentence-initial / title-case forms and every z-word from the ten clips
# the defect affected.
MUST_BE_S = [
    "zapatos", "cabeza", "manzanas", "marzo", "terraza", "plaza",
    "empezado", "aplazado", "empezar",
    "Zapatos", "Zapato",                       # sentence start / title
    "los zapatos", "la cabeza", "yo como manzanas",
]

# --- Spanish: MUST still yield /θ/ -----------------------------------------
# Written z before e/i, and every ce/ci sequence, keep espeak's θ.
MUST_BE_TH = [
    "gracias", "cocina", "oficina", "estación", "hace", "ciudad",
    "diez", "vez", "luz", "voz", "haz", "necesito",
]

# --- The ten affected clips: (stem, z-words, full text, has_other_theta) ---
# `has_other_theta` is True when the passage also contains a legitimate
# ce/ci or z-before-e/i θ word, so the full text MUST still contain θ after
# the fix even though its z-before-a/o/u word now yields /s/.
CLIPS = [
    ("los_zapatos", ["zapatos"], "Los zapatos", False),
    ("la_cabeza", ["cabeza"], "La cabeza", False),
    ("yo_como_manzanas", ["manzanas"], "Yo como manzanas", False),
    ("mi_jefe_trabaja_desde_casa", ["marzo"],
     "Mi jefe trabaja desde casa desde marzo.", False),
    ("la_reunion_se_ha_aplazado", ["aplazado"],
     "La reunión se ha aplazado hasta el viernes.", False),
    ("cuando_llegue_la_pelicula_ya_habia_empezado", ["empezado"],
     "Cuando llegué la película ya había empezado", False),
    ("el_tren_perdido", ["empezado"],
     "Si hubiera cogido el tren de las ocho, habría llegado antes. "
     "No salí de casa hasta las diez. Cuando por fin llegué, "
     "la reunión ya había empezado.", True),   # "diez" keeps θ
    ("story_901", ["terraza"],
     "Los sábados. Los sábados por la mañana limpio la cocina y el baño. "
     "Después pongo la ropa sucia en la lavadora. Tiendo las toallas en "
     "la terraza si hace sol. El domingo por la tarde me siento en la "
     "sala con un libro.", True),             # cocina/hace/sucia keep θ
    ("story_902", ["terraza"],
     "En el mercado. Ayer fui al mercado con mi hermana. Compramos fruta, "
     "verdura y pan para la semana. En la panadería pagamos con tarjeta. "
     "Después tomamos un café en la terraza.", False),
    ("story_904", ["plaza"],
     "Un domingo en la ciudad. El domingo por la mañana fui al centro con "
     "mi amiga. Hacía buen tiempo y la plaza estaba llena de gente. "
     "Compremos café y bollos en una panadería de la esquina. Por la "
     "tarde visitamos el museo, que es gratis el primer domingo del mes.",
     True),                                    # centro/ciudad/hacía keep θ
]

# --- Japanese: byte-identical before/after (captured pre-fix) --------------
JA_EXPECTED = {
    "買った": "katta",
    "がっこう": "ɡakkoː",
    "きって": "kʲitte",
    "はっぴょう": "happʲoː",
    "わたし": "ɸataɕi",
    "川": "kaɸa",
    "日本語": "ɲihoŋɡo",
}

# --- English: byte-identical before/after (captured pre-fix) ---------------
EN_EXPECTED = {
    "the cat sat": "ðə kˈæt sˈæt",
    "now": "nˈW",
    "day": "dˈA",
}

_failures = []


def _record(ok, label, detail):
    if not ok:
        _failures.append("%s: %s" % (label, detail))


def check_no_theta(label, got):
    ok = "θ" not in got
    _record(ok, label, "θ present in %r" % got)
    print("  [%s] %-34s -> %r" % ("PASS" if ok else "FAIL", label, got))


def check_has_theta(label, got):
    ok = "θ" in got
    _record(ok, label, "θ absent from %r" % got)
    print("  [%s] %-34s -> %r" % ("PASS" if ok else "FAIL", label, got))


def check_eq(label, got, want):
    ok = got == want
    _record(ok, label, "got %r, want %r" % (got, want))
    print("  [%s] %-34s -> %r" % ("PASS" if ok else "FAIL", label, got))


def main():
    print("=== Spanish: z before a/o/u MUST yield /s/ ===")
    for t in MUST_BE_S:
        check_no_theta(t, g2p_fixes.spa_g2p(t))

    print()
    print("=== Spanish: z before e/i and ce/ci MUST still yield /θ/ ===")
    for t in MUST_BE_TH:
        check_has_theta(t, g2p_fixes.spa_g2p(t))

    print()
    print("=== The ten affected clips ===")
    for stem, zwords, text, other_theta in CLIPS:
        for w in zwords:
            check_no_theta("%s [%s]" % (stem, w), g2p_fixes.spa_g2p(w))
        full = g2p_fixes.spa_g2p(text)
        if other_theta:
            # passage has a legitimate ce/ci or z-before-e/i θ word:
            # θ must survive there even though the z-word now yields /s/.
            check_has_theta(stem + " (full text)", full)
        else:
            check_no_theta(stem + " (full text)", full)

    print()
    print("=== Japanese: byte-identical before/after ===")
    for t, want in JA_EXPECTED.items():
        check_eq(t, g2p_fixes.jpn_g2p(t), want)

    print()
    print("=== English: byte-identical before/after ===")
    en_pipe = g2p_fixes.make_pipeline(lang_code="a", model=False)
    for t, want in EN_EXPECTED.items():
        check_eq(t, en_pipe.g2p(t)[0], want)

    print()
    print("=== make_pipeline audibility semantics ===")
    try:
        g2p_fixes.make_pipeline(lang_code="e", model=None)
        _record(False, "model=None", "did not raise")
        print("  [FAIL] model=None did not raise")
    except ValueError:
        print("  [PASS] model=None raises ValueError")

    pipe = g2p_fixes.make_pipeline(lang_code="e", model=True)
    chunks = list(pipe("zapatos", voice="ef_dora"))
    audible = bool(chunks) and all(a is not None for (_, _, a) in chunks)
    _record(audible, "model=True", "produced no audio")
    print("  [%s] model=True produces audio (%d chunk(s))"
          % ("PASS" if audible else "FAIL", len(chunks)))

    print()
    if _failures:
        print("FAILED: %d assertion(s):" % len(_failures))
        for f in _failures:
            print("  - " + f)
        return 1
    print("ALL CHECKS PASSED")
    return 0


if __name__ == "__main__":
    if hasattr(sys.stdout, "reconfigure"):
        sys.stdout.reconfigure(encoding="utf-8", errors="replace")
    sys.exit(main())

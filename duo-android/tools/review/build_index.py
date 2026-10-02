# -*- coding: utf-8 -*-
"""Regenerate the native-speaker review work list.

Reads the app's 395 bundled clips plus the verification artifacts produced by
the earlier automated passes, and writes `data/clips.json` — one row per clip —
for `index.html` to render.

Nothing in here touches the app: the `.ogg` files are opened for their names
only, and no Kotlin, manifest or database is written. The tool lives in
`duo-android/tools/review/`, which the app's Gradle config never reads, so
nothing it produces can reach the APK.

    python build_index.py                 # rebuild from the committed snapshot
    python build_index.py --refresh       # re-take the snapshot from .scratch
    python build_index.py --check         # verify the committed snapshot is
                                          # complete, then exit

`--refresh` needs the verification working tree, which lives outside this repo
at `H:/Projects/DuoLingo/.scratch` by default; override with `--scratch PATH`.

See README.md for how a native speaker uses the result.
"""
from __future__ import annotations

import argparse
import collections
import csv
import json
import os
import sys

HERE = os.path.dirname(os.path.abspath(__file__))
APP = os.path.abspath(os.path.join(HERE, "..", ".."))          # duo-android/
AUDIO = os.path.join(APP, "app", "src", "main", "assets", "audio")
SOURCE = os.path.join(HERE, "source")
DATA = os.path.join(HERE, "data")

LANGUAGES = ("es", "ja")
LANG_LABEL = {"es": "Spanish", "ja": "Japanese"}

# The six Japanese clips the phone recogniser could not adjudicate. Taken from
# "Contrast 2" in .scratch/ja_phone/REPORT.md; the --refresh path re-derives
# this set from the analysis data instead of trusting the constant.
JA_UNRESOLVED_REASONS = {
    "konnichiwa": "nasal gemination: /nː/ is a long nasal, not an oral geminate",
    "ja_vol_minna_de_densha_ni_norimashou": "nasal gemination (ん + な)",
    "ja_keigo_ossyaru": "sibilant gemination /ʃː/ is a long fricative",
    "kinou_zasshi_o_kaimashita": "sibilant gemination /ʃː/ is a long fricative",
    "matta": "stop geminate /tːa/ is near-ambiguous without lexical knowledge",
    "issatsu": "stop geminate っ + さ, same ambiguity",
}

# Substitutions the Japanese phone recogniser is documented to make purely
# because it writes a sound with a different letter (.scratch/ja_phone/REPORT.md
# classes A, D1-D4).
JA_NOTATION_SUBS = {
    "i->I", "i->U", "u->U",   # class A: devoiced high vowels
    "ny->n", "ny->N",         # D1/D4: palatalised nasal
    "z->j", "j->z",           # D2: voiced affricates have no symbol
    "hy->h",                  # D3: palatal fricative
    "n->N",                   # D4
}
JA_VOWELS = {"a", "e", "i", "o", "u", "I", "U", "ɔ", "ɛ"}

# Spanish findings, .scratch/es_phone/FINDINGS.md sections 4 and 7.
ES_FIXED = {
    "la_maestra_dijo_que_habia_terminado":
        "was heard with /ʃ/ for /x/; re-rendered, now heard with /x/",
    "nos_dijo_que_habiamos_perdido_el_tren":
        "was heard with /ʃ/ for /x/; re-rendered, now heard with /x/",
    "la_llamada_que_nunca_hizo":
        "was heard with /ʃ/ for /x/; re-rendered, now heard with /x/",
    "no_corras_con_prisa":
        "stress was on the wrong syllable (kˈoras); re-rendered as koˈras",
}
ES_UNRESOLVED = {
    "dijimos":
        "intended /x/ never heard in this environment; shipped audio differs "
        "from both the intended string and a fresh render, but the evidence "
        "does not separate 'different audio' from 'instrument noise'",
}

# Verdict vocabulary shared by both languages, ordered from "a machine already
# cleared this" to "no machine can settle this; a human has to listen".
VERDICT_CLASSES = {
    "agreed": {
        "label": "Machine heard it match",
        "note": "the recogniser's phones matched the intended string",
    },
    "notation-differs": {
        "label": "Differs only in notation",
        "note": "the only differences are ones the recogniser is known to make "
                "because it writes the same sound with a different letter",
    },
    "frame-noise": {
        "label": "Frame-level noise",
        "note": "the differences are dropped or inserted frames, which are not "
                "sounds",
    },
    "confirmed-geminate": {
        "label": "Gemination confirmed",
        "note": "a real oral geminate was heard where one was intended",
    },
    "fixed-audio-defect": {
        "label": "Audio defect, already fixed",
        "note": "a real defect was found and the asset was re-rendered",
    },
    "instrument-blind": {
        "label": "Machine cannot judge this",
        "note": "the clip uses a sound the instrument is known not to be able "
                "to hear, so no automated verdict is possible either way",
    },
    "needs-human": {
        "label": "Machine could not settle it",
        "note": "the instrument was inconclusive here and a human has to "
                "decide",
    },
    "unclassified-disagreement": {
        "label": "Unexplained disagreement",
        "note": "the recogniser disagreed in a way no documented mapping "
                "explains; too few instances to claim a pattern",
    },
    "cleared": {
        "label": "Swept, nothing found",
        "note": "the corpus-wide detector sweep raised no flag for this clip",
    },
}
# Sort key for the "where should I spend my time" ordering of the verdict
# filter. Lower sorts first; the filter shows them in this order.
VERDICT_PRIORITY = [
    "needs-human",
    "unclassified-disagreement",
    "instrument-blind",
    "fixed-audio-defect",
    "frame-noise",
    "confirmed-geminate",
    "notation-differs",
    "cleared",
    "agreed",
]


# ---------------------------------------------------------------------------
# reading the verification working tree (only used by --refresh)
# ---------------------------------------------------------------------------

def read_tsv(path):
    with open(path, encoding="utf-8", newline="") as fh:
        return list(csv.DictReader(fh, delimiter="\t"))


def stem(clip_path):
    """'asset:///audio/ja/matta.ogg' -> 'matta'."""
    return clip_path.rsplit("/", 1)[-1][:-4]


def refresh(scratch):
    census = os.path.join(scratch, "clip_census")
    rom_dir = os.path.join(scratch, "es_romanisation")

    clips = read_tsv(os.path.join(census, "clips.tsv"))
    auth = {stem(r["clip_path"]): r for r in
            read_tsv(os.path.join(census, "authoritative_text.tsv"))}
    spec = {stem(r["clip_path"]): r for r in
            read_tsv(os.path.join(census, "pron_spec.tsv"))}

    with open(os.path.join(census, "kotlin_refs.json"), encoding="utf-8") as fh:
        refs = json.load(fh)
    by_clip = collections.defaultdict(list)
    for r in refs:
        if r.get("audio"):
            by_clip[stem(r["audio"])].append(r)

    # Japanese phone-recogniser analysis: the source of the automated verdict.
    with open(os.path.join(scratch, "ja_phone", "analysis.json"),
              encoding="utf-8") as fh:
        ja_analysis = json.load(fh)

    roman = read_tsv(os.path.join(rom_dir, "spanish_romanisation.tsv"))
    roman_by_option = {r["optionId"]: r for r in roman}
    candidates = read_tsv(os.path.join(rom_dir, "candidates.tsv"))
    roman_by_clip = collections.defaultdict(list)
    for c in candidates:
        roman_by_clip[stem(c["clip"])].append(c)

    snapshot = {
        "_provenance": {
            "scratch": scratch,
            "note": "Generated by build_index.py --refresh. Every automated "
                    "verdict below came from these artifacts, not from a new "
                    "measurement.",
        },
        "clips": [],
    }

    for row in clips:
        key = stem(row["clip_path"])
        entities = by_clip.get(key, [])
        snapshot["clips"].append({
            "clip": key + ".ogg",
            "stem": key,
            "lang": row["language"],
            "text": auth[key]["intended_text"],
            "textProvenance": auth[key]["provenance"],
            "textConfidence": auth[key]["confidence"],
            "textNotes": auth[key]["notes"],
            "declaredReading": spec[key]["declared_romaji"],
            "kanaReading": spec[key]["declared_kana_reading"],
            "sourceIds": row["source_id"],
            "usage": row["usage_type"],
            "challengeTypes": sorted({e["challenge_type"] for e in entities
                                     if e["challenge_type"]}),
            "challengeIds": sorted({e["challenge_id"] for e in entities
                                    if e["challenge_id"]}),
            "jaAnalysis": ja_analysis.get(key),
            "esCandidates": [
                {
                    "optionId": c["optionId"],
                    "challengeId": c["challengeId"],
                    "lessonId": c["lessonId"],
                    "challengeType": c["challengeType"],
                    "esText": c["esText"],
                    "romanisation": roman_by_option[c["optionId"]]["romanisation"],
                    "dialectNote": roman_by_option[c["optionId"]]["dialectNote"],
                    "confidence": roman_by_option[c["optionId"]]["confidence"],
                    "clipText": c["clipText"],
                    "clipProvenance": c["clipProvenance"],
                    "clipConfidence": c["clipConfidence"],
                }
                for c in roman_by_clip.get(key, [])
            ],
        })

    os.makedirs(SOURCE, exist_ok=True)
    # Ship the romanisation table, its audit notes and the rule engine
    # verbatim, so the reading shown to a reviewer comes with the caveats its
    # author recorded and the derived fallback can run from this folder alone.
    for name in ("spanish_romanisation.tsv", "AUDIT.md", "es_respelling.py"):
        with open(os.path.join(rom_dir, name), encoding="utf-8") as fh:
            body = fh.read()
        with open(os.path.join(SOURCE, name), "w", encoding="utf-8") as fh:
            fh.write(body)

    write_json(os.path.join(SOURCE, "snapshot.json"), snapshot)
    return snapshot


def write_json(path, obj):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w", encoding="utf-8") as fh:
        json.dump(obj, fh, ensure_ascii=False, indent=1)
        fh.write("\n")


# ---------------------------------------------------------------------------
# deriving the reading and the automated verdict from the snapshot
# ---------------------------------------------------------------------------

def pick_spanish_reading(candidates):
    """Choose one authored romanisation row per clip.

    Several options can share a clip. Prefer a row the author was confident
    about, then one whose option text really is the clip's text, then the
    earliest. Rows with no reading (the English side of a MATCH_PAIRS and
    friends) lose to anything with a reading.
    """
    conf_rank = {"high": 0, "medium": 1, "low": 2, "n/a": 3}
    best = None
    for c in candidates:
        roman = (c["romanisation"] or "").strip()
        if not roman or roman == "(not Spanish)":
            continue
        exact = 0
        if c["esText"].strip().rstrip(".") == (c["clipText"] or "").strip().rstrip("."):
            exact = 1
        key = (conf_rank.get(c["confidence"], 4), -exact, c["optionId"])
        if best is None or key < best[0]:
            best = (key, c)
    if best is None:
        return None
    c = best[1]
    return {
        "reading": c["romanisation"].strip(),
        "readingKind": "romanisation",
        "readingSource": "authored table (.scratch/es_romanisation), "
                         "reviewed by nobody — a draft to correct",
        "readingNote": c["dialectNote"] or "",
        "readingConfidence": c["confidence"],
        "lessonIds": [c["lessonId"]] if c["lessonId"] else [],
    }


def derive_spanish_reading(text, snapshot_dir=SOURCE):
    """Fallback for the clips the authored table does not cover.

    The audit recorded that 13 clips are reachable only through an English
    option, so no authored reading of their spoken passage exists. Rather than
    show the reviewer nothing, run the same rule engine over the clip's own
    text and label the result as derived, not authored.
    """
    sys.path.insert(0, snapshot_dir)
    try:
        import es_respelling  # noqa: PLC0415 - optional, shipped as a snapshot
    except Exception:
        return None
    finally:
        sys.path.pop(0)
    try:
        return es_respelling.respelling_phrase(text)
    except Exception:
        return None


def japanese_verdict(analysis):
    """Replay the documented classification in .scratch/ja_phone/REPORT.md."""
    if not analysis:
        return "cleared", ""
    if analysis.get("intended_gem"):
        if analysis.get("heard_cl"):
            return "confirmed-geminate", "an oral geminate was heard where one was intended"
        return "needs-human", ""
    unexplained = [s for s in analysis.get("subs", []) if s not in JA_NOTATION_SUBS]
    dels, inss = analysis.get("dels", []), analysis.get("inss", [])
    if not unexplained and not dels and not inss:
        return "agreed", ""
    if not unexplained and not dels and not inss:
        return "agreed", ""
    if not unexplained and not dels and not inss:
        return "agreed", ""
    unexplained = [s for s in analysis.get("subs", []) if s not in JA_NOTATION_SUBS]
    dels, inss = analysis.get("dels", []), analysis.get("inss", [])
    if not unexplained and not dels and not inss:
        return "agreed", ""
    if not unexplained:
        return "notation-differs", ("the only differences are ones the recogniser "
                                    "always makes")
    vowel_only = all(s.split("->")[0] in JA_VOWELS and s.split("->")[1] in JA_VOWELS
                     for s in unexplained)
    if not vowel_only:
        return "unclassified-disagreement", " / ".join(unexplained)
    if not dels and not inss:
        return "notation-differs", "vowel quality differences only, around long vowels"
    return ("frame-noise",
            "frames were dropped or inserted: %d dropped, %d inserted"
            % (len(dels), len(inss)))


def spanish_verdict(stem_name, phonemes):
    if stem_name in ES_FIXED:
        return "fixed-audio-defect", ES_FIXED[stem_name]
    if stem_name in ES_UNRESOLVED:
        return "unclassified-disagreement", ES_UNRESOLVED[stem_name]
    blind = []
    if "θ" in phonemes:
        blind.append("/θ/")
    if "ʎ" in phonemes:
        blind.append("/ʎ/")
    if "ʝ" in phonemes:
        blind.append("/ɝ/")
    if blind:
        return "instrument-blind", (
            "the instrument cannot hear %s, so it can neither confirm nor "
            "deny this clip" % " or ".join(blind))
    return "cleared", "the corpus-wide detector sweep raised no flag here"


# ---------------------------------------------------------------------------
# the G2P, through the project's own patched pipeline
# ---------------------------------------------------------------------------

def load_g2p():
    """Phonemise through duo-android/tools/g2p_fixes.py, unmodified.

    `make_pipeline` loads the full Kokoro model so the phoneme string is the
    one the audio was actually asked for, not a different G2P's opinion of it.
    """
    tools = os.path.join(APP, "tools")
    sys.path.insert(0, tools)
    try:
        import g2p_fixes  # noqa: PLC0415
    finally:
        sys.path.pop(0)
    return g2p_fixes


# `make_pipeline` keys its patches off kokoro's one-letter codes. Handing it
# our two-letter ones builds a pipeline with NO patch applied and it fails
# silently: no exception, just the wrong phonemes. Hence the assertion below.
KOKORO_LANG = {"es": "e", "ja": "j"}


def check_patched(g2p_fixes, cache):
    """Fail loudly if a pipeline came back unpatched.

    The whole point of this tool is to show a reviewer what the audio was
    asked for. An unpatched pipeline reports ʔ for Japanese っ and the wrong
    stress for `corras`, which is precisely the bug this module exists to fix,
    so a wrong phoneme string here is worse than no string at all.
    """
    probes = {"j": ("待った", "matta"), "e": ("No corras con prisa.",
                                             "nˈo koˈras kom pɾˈisa.")}
    for code, (text, want) in probes.items():
        got = phonemise(g2p_fixes, code, text, cache)
        if got != want:
            raise SystemExit(
                "the %s pipeline is not the project's patched G2P: %r gave %r, "
                "expected %r.  Refusing to write phoneme strings a reviewer "
                "would trust." % (code, text, got, want))


def phonemise(g2p_fixes, lang, text, cache):
    """The intended phoneme string for one clip's text.

    One pipeline per language, built once: `make_pipeline` loads the whole
    Kokoro model and doing that 397 times would take an hour. Returned as a
    plain string; if the pipeline cannot say, the empty string and the row
    shows the text only. A reviewer is never shown a guess.
    """
    try:
        pipe = cache.get(lang)
        if pipe is None:
            pipe = cache[lang] = g2p_fixes.make_pipeline(lang_code=lang)
        out = pipe.g2p(text)
    except Exception as exc:  # noqa: BLE001 - one bad row must not stop 397
        sys.stderr.write("  ! G2P failed for %s (%s): %s\n" % (lang, text, exc))
        return ""
    if isinstance(out, tuple):
        out = out[0]
    return (out or "").strip()


# ---------------------------------------------------------------------------
# assembling the work list
# ---------------------------------------------------------------------------

def on_disk_clips():
    found = {}
    for lang in LANGUAGES:
        folder = os.path.join(AUDIO, lang)
        for name in sorted(os.listdir(folder)):
            if name.endswith(".ogg"):
                found.setdefault(lang, []).append(name)
    return found


def build(snapshot, with_g2p=True):
    disk = on_disk_clips()
    g2p = load_g2p() if with_g2p else None
    pipes = {}
    if g2p:
        check_patched(g2p, pipes)
    rows = {}

    for entry in snapshot["clips"]:
        lang = entry["lang"]
        key = entry["stem"]
        text = entry["text"]

        phonemes = phonemise(g2p, KOKORO_LANG[lang], text, pipes) if g2p else ""

        reading = ""
        reading_kind = "none"
        reading_source = ""
        reading_note = ""
        reading_conf = ""
        lesson_ids = []

        if lang == "ja":
            # The app's authored romaji. This is the reading the app promises,
            # so it is what a reviewer is being asked about.
            raw = (entry.get("declaredReading") or "").strip()
            if raw:
                parts = [p.strip() for p in raw.split("|") if p.strip()]
                reading = " or ".join(parts)
                reading_kind = "romaji"
                reading_source = ("declared in the app's curriculum data"
                                  if len(parts) == 1
                                  else "the app declares several; any of them is "
                                       "accepted")
                reading_conf = "declared"
            else:
                reading_source = "the app declares no reading for this clip"
                reading_kind = "none"
            if entry.get("kanaReading"):
                reading_note = entry["kanaReading"]
        else:
            picked = pick_spanish_reading(entry.get("esCandidates") or [])
            if picked:
                reading = picked["reading"]
                reading_kind = picked["readingKind"]
                reading_source = picked["readingSource"]
                reading_note = picked["readingNote"]
                reading_conf = picked["readingConfidence"]
                lesson_ids = picked["lessonIds"]
            else:
                derived = derive_spanish_reading(text)
                if derived:
                    reading = derived
                    reading_kind = "derived"
                    reading_source = ("nobody has written a reading for this "
                                      "clip; this one was worked out "
                                      "automatically from the spelling")
                else:
                    reading_source = "no reading available for this clip"

        if lang == "ja":
            cls, detail = japanese_verdict(entry.get("jaAnalysis"))
        else:
            cls, detail = spanish_verdict(key, phonemes)
        if cls == "needs-human":
            detail = JA_UNRESOLVED_REASONS.get(key, detail)

        types = list(entry.get("challengeTypes") or [])
        usage = entry.get("usage") or ""
        if usage and usage not in types:
            types.append(usage)

        rows[key] = {
            "clip": entry["clip"],
            "lang": lang,
            "langLabel": LANG_LABEL[lang],
            "text": text,
            "reading": reading,
            "readingKind": reading_kind,
            "readingSource": reading_source,
            "readingNote": reading_note,
            "readingConfidence": reading_conf,
            "phonemes": phonemes,
            "kanaReading": entry.get("kanaReading") or "",
            "challengeTypes": types,
            "challengeIds": entry.get("challengeIds") or [],
            "lessonIds": lesson_ids,
            "usage": usage,
            "sourceIds": (entry.get("sourceIds") or "").split(";"),
            "textProvenance": entry.get("textProvenance") or "",
            "textConfidence": entry.get("textConfidence") or "",
            "verdictClass": cls,
            "verdictLabel": VERDICT_CLASSES[cls]["label"],
            "verdictNote": detail,
            "verdictGloss": VERDICT_CLASSES[cls]["note"],
            "audio": "../../app/src/main/assets/audio/%s/%s" % (lang, entry["clip"]),
            "reference": "references/%s/%s" % (lang, entry["clip"]),
        }

    missing = []
    for lang, names in disk.items():
        for name in names:
            if name[:-4] not in rows:
                missing.append("%s/%s" % (lang, name))
    if missing:
        raise SystemExit("snapshot is missing %d clips on disk: %s"
                         % (len(missing), ", ".join(missing[:8])))

    ordered = [rows[k] for k in sorted(rows, key=lambda k: (rows[k]["lang"], k))]
    return ordered


# ---------------------------------------------------------------------------
# entry point
# ---------------------------------------------------------------------------

def counts(rows):
    return {
        "total": len(rows),
        "es": sum(1 for r in rows if r["lang"] == "es"),
        "ja": sum(1 for r in rows if r["lang"] == "ja"),
        "withAuthoredReading": sum(
            1 for r in rows if r["readingKind"] in ("romaji", "romanisation")),
        "withDerivedReading": sum(1 for r in rows if r["readingKind"] == "derived"),
        "withNoReading": sum(1 for r in rows if r["readingKind"] == "none"),
        "withPhonemes": sum(1 for r in rows if r["phonemes"]),
        "byVerdictClass": dict(collections.Counter(r["verdictClass"] for r in rows)),
        "challengeTypes": sorted({t for r in rows for t in r["challengeTypes"]}),
    }



def check():
    """Validate the built data/clips.json against the assets on disk.

    Deliberately does NOT rebuild: a check that regenerates what it is about
    to check cannot catch a broken generator. It reads the file the page will
    actually read, and asks whether every clip on disk is in it with the text,
    reading, phonemes and verdict the page will show.
    """
    path = os.path.join(DATA, "clips.json")
    if not os.path.isfile(path):
        raise SystemExit("%s is missing; run build_index.py first" % path)
    with open(path, encoding="utf-8") as fh:
        data = json.load(fh)
    rows = data["clips"]
    summary = counts(rows)

    disk = on_disk_clips()
    problems = []
    if summary["total"] != sum(len(v) for v in disk.values()):
        problems.append("expected %d clips, got %d"
                        % (sum(len(v) for v in disk.values()), summary["total"]))
    for lang, names in disk.items():
        have = {r["clip"] for r in rows if r["lang"] == lang}
        gap = sorted(set(names) - have)
        extra = sorted(have - set(names))
        if gap:
            problems.append("%s: %d clips on disk with no row (%s)"
                            % (lang, len(gap), ", ".join(gap[:5])))
        if extra:
            problems.append("%s: %d rows with no file (%s)"
                            % (lang, len(extra), ", ".join(extra[:5])))
    for r in rows:
        if not r["text"].strip():
            problems.append("%s has no intended text" % r["clip"])
        # A row may legitimately have no reading IF it says why: one Japanese
        # clip declares no romaji at all, and inventing one would be worse
        # than telling the reviewer so.
        if not r["reading"] and not r["readingSource"]:
            problems.append("%s has no reading and does not say why" % r["clip"])
        if not r["phonemes"]:
            problems.append("%s has no phoneme string" % r["clip"])
        if r["verdictClass"] not in data["verdictClasses"]:
            problems.append("%s has an unknown verdict class %r"
                            % (r["clip"], r["verdictClass"]))
        if not os.path.isfile(os.path.join(
                APP, "app", "src", "main", "assets", "audio",
                r["lang"], r["clip"])):
            problems.append("%s points at a file that is not there" % r["clip"])

    print(json.dumps(summary, ensure_ascii=False, indent=1))
    print()
    print("\n".join("FAIL " + p for p in problems[:20]) or "OK")
    return 1 if problems else 0


def main(argv=None):
    ap = argparse.ArgumentParser(description=__doc__)
    ap.add_argument("--scratch", default=None,
                    help="verification working tree (default: <repo>/../.scratch)")
    ap.add_argument("--refresh", action="store_true",
                    help="re-take the snapshot from the working tree first")
    ap.add_argument("--check", action="store_true",
                    help="check the snapshot and the assets, then exit")
    args = ap.parse_args(argv)

    scratch = args.scratch or os.path.abspath(
        os.path.join(APP, "..", ".scratch"))
    snapshot_path = os.path.join(SOURCE, "snapshot.json")

    if args.refresh:
        if not os.path.isdir(scratch):
            raise SystemExit("no verification working tree at %s; pass --scratch"
                             % scratch)
        print("snapshotting from %s" % scratch)
        snapshot = refresh(scratch)
    else:
        with open(snapshot_path, encoding="utf-8") as fh:
            snapshot = json.load(fh)

    if args.check:
        return check()

    rows = build(snapshot, with_g2p=True)
    summary = counts(rows)

    write_json(os.path.join(DATA, "clips.json"), {
        "generatedFrom": snapshot.get("_provenance", {}).get("scratch", "committed snapshot"),
        "verdictClasses": VERDICT_CLASSES,
        "verdictPriority": VERDICT_PRIORITY,
        "summary": summary,
        "clips": rows,
    })
    print("wrote %s: %d clips (%d es, %d ja)" % (
        os.path.join(DATA, "clips.json"), summary["total"], summary["es"],
        summary["ja"]))
    print("  readings: %d authored, %d derived, %d none" % (
        summary["withAuthoredReading"], summary["withDerivedReading"],
        summary["withNoReading"]))
    print("  phoneme strings: %d" % summary["withPhonemes"])
    for cls in VERDICT_PRIORITY:
        print("  %-28s %d" % (cls, summary["byVerdictClass"].get(cls, 0)))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())

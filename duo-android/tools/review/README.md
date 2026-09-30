# Pronunciation review

A local web page that puts all 397 language-learning audio clips in front of a
**native speaker** and records what they think of them.

It exists because the automated checks already done on this project can prove
things about the audio that a machine can measure, and they found several real
defects in Spanish and none in Japanese (`docs/kokoro-tts.md` §10, §11). They
cannot tell you whether a sentence *sounds natural*. That judgement takes a
person, and this page is built so that person's five minutes is worth something.

**One thing to be clear about up front: no native speaker is ever going to
review this audio.** That is a permanent property of the project, not something
waiting on a schedule. The automated checks that found the four Spanish defects
can prove that a phone is or is not in a waveform; they cannot tell you whether
a sentence *sounds* right to a person, whether the accent is good, or whether a
clip is fit to teach. Those judgements need a listener, and this project does
not have one.

So this page is not a pending task dressed up as a tool. It exists in case
somebody ever does open it — and until then it is the honest record of what is
unverifiable, not a substitute for a review that has not happened.

Nothing here is part of the app. It lives in `duo-android/tools/review/`,
which the app's Gradle configuration never reads, and a built debug APK
contains no `tools/` entry at all.

---

## If you are a native speaker (Spanish or Japanese)

You do not need to install anything, and you do not need to know anything
about linguistics. If you can tell whether a sentence sounds the way you would
say it, that is exactly the expertise this page is asking for.

### 1. Open it

On Windows, double-click **`serve.bat`**.

A small black window opens and prints an address like
`http://127.0.0.1:8765/tools/review/index.html`. Your browser opens by itself.
Leave the black window open while you work; closing it stops the page.

Prefer the command line?

```
cd duo-android\tools\review
python serve.py
```

### 2. Listen, and say what you hear

Each line shows:

* a **round ▶ button** — press it to hear the line, press again to stop
* the **sentence** in the language it is spoken
* **"The app says: …"** — the reading the app promises. For Japanese this is
  romaji; for Spanish it is a plain-English way of writing the sounds. It is
  there to anchor you, **not to test you**. You do not have to be able to read
  it to judge the audio.
* a small grey line naming the kind of exercise the clip belongs to

Then press one of four buttons:

| Button | When |
|---|---|
| **Sounds correct** | You would say it exactly like that. |
| **Wrong sound** | Something is mispronounced. If you can, type **which word** in the box that appears. That is what a fix needs. |
| **Unclear / robotic** | You can understand it, but it does not sound like a person talking. This is *not* the same as being wrong, and the app cares about it. |
| **Skip** | You cannot tell, or you would rather not say. |

Both note boxes are optional.

### 3. Slow down when you need to

Subtle mistakes hide in fast speech. Set **Speed** to **0.5×** and tick
**Repeat each line** to hear one line over and over, then compare it with how
you would say it out loud.

**Play the list** runs through everything currently shown at the chosen speed.

### 4. Compare against a real voice (optional)

If someone has supplied a reference recording for a line, a second button
**Reference** appears next to ▶. Press it to hear the reference voice *instead*
of the app's audio, and press it again to go straight back — the two swap
instantly, so you can A/B them as many times as you like.

There are no reference recordings yet. To add some, see
[`references/README.md`](references/README.md): record each line once as a
native speaker, save it as `.ogg` under `references/es/` or `references/ja/`
using the clip's own file name, and reload the page. The buttons appear on
their own.

### 5. Do not have to do all 397

The bar across the top fills as you go.

* **Show** lets you narrow to one language, one kind of exercise, or — most
  usefully — **only the computer could not decide**. That filter is where a
  human ear is worth the most.
* **Only not judged yet** is on by default, so if you close the page and come
  back, you pick up exactly where you stopped.
* **Random sample**: type `20`, press **Random sample**, and you get 20 lines
  chosen at random instead of the first 20 in file order. If you only have
  twenty minutes, this is the twenty minutes worth spending.
* **Show what the computer found** is off by default, deliberately. The
  automated verdicts are context, not a prompt: they were produced by machines
  and reviewed by nobody, and they should not be anchoring your first
  impression. Turn it on if you want to know where the machines were unsure.

### 6. Send the results back — this is the important bit

Your answers are saved in the browser as you go, so you can close the page and
come back. They stay on this computer; nothing is uploaded anywhere.

When you are done, click **Export answers**. A file called
`pronunciation-review-<date>.json` is saved to your Downloads folder.

**That file is the deliverable.** Send it back to whoever gave you this page,
and nothing else is needed.

If you ever need to continue on a different computer or in a different
browser, use **Import answers** there first and then export again.

### Keyboard shortcuts

| Key | Does |
|---|---|
| `Space` | play / stop the current line |
| `1` `2` `3` `4` | sounds correct / wrong sound / unclear / skip |
| `j` `k` | next / previous line |
| `s` | switch between the clip and its reference recording |

---

## For a developer

### Layout

```
tools/review/
  index.html        the page
  review.css        its styles, no web fonts
  review.js         its behaviour, no dependencies
  serve.py          localhost launcher (serve.bat wraps it for Windows)
  build_index.py    regenerates data/clips.json
  data/clips.json   the generated work list — one row per clip
  source/           the snapshot the work list is generated from
  references/       drop-in folder for native-speaker reference recordings
```

### Rebuilding the work list

```
python build_index.py          # rebuild data/clips.json from source/
python build_index.py --check  # verify it, write nothing
```

`build_index.py` needs `g2p_fixes.py` and its dependencies (`kokoro`,
`misaki`) to be importable, because the intended phoneme string for every clip
comes from the project's own patched pipeline via `make_pipeline`. That is the
point: a phoneme string from any other G2P would be a different opinion of the
text, and a reviewer would be shown a spec the audio was never asked to meet.

The pipeline keys its patches off kokoro's one-letter language codes (`e`, `j`),
so the tool passes those and not the app's two-letter ones. Handing
`make_pipeline` the wrong code builds a perfectly working pipeline with **no
patch applied**, and it fails silently. `check_patched()` probes both
pipelines and refuses to write a file if either one is unpatched.

### Re-taking the snapshot

`source/snapshot.json` is a committed copy of the verification artifacts, so
the tool builds from a clean checkout. To re-take it from a working tree:

```
python build_index.py --refresh --scratch H:/Projects/DuoLingo/.scratch
```

It also refreshes `source/spanish_romanisation.tsv`, `source/AUDIT.md` and
`source/es_respelling.py` verbatim from the same place. Read `source/AUDIT.md`
before trusting any Spanish reading on the page: it records the method, the
coverage gaps, and the author's own uncertainties. The readings shown are a
draft awaiting exactly the review this page collects.

### Where each field comes from

| Field | Source |
|---|---|
| clip list | `app/src/main/assets/audio/{es,ja}/*.ogg` — the files on disk, 198 + 199 |
| intended text | the authoritative-text census, with its provenance and confidence per row |
| Japanese reading | `romaji` declared in the app's curriculum data |
| Spanish reading | `.scratch/es_romanisation/spanish_romanisation.tsv`, joined to clips through `candidates.tsv` |
| intended phonemes | `g2p_fixes.make_pipeline(lang_code=...)` on the clip's own text |
| challenge types and ids | parsed from the curriculum Kotlin |
| automated verdict | the Japanese phone-recogniser analysis, and the Spanish findings; each row carries what it was based on |

### Deliberate limits

* **The Spanish reading is a draft.** Its own author wrote that nobody had
  listened to it. Thirteen clips have no authored reading at all, because they
  are reachable only through an English option; those show a reading worked out
  automatically from the spelling and are badged so, never passed off as
  checked.
* **One Japanese clip (`tsuki`) has no declared reading** in the app, so it
  shows none.
* **The automated verdicts are context, hidden by default.** They are a
  detector, not a judge: on Spanish it recovered the exact intended phone
  sequence for 13 of 198 clips. Where it is silent, that is not evidence of
  correctness.
* **The page never talks to the network.** No CDN, no web font, no analytics,
  no telemetry. It reads its own files and the app's `.ogg` clips, and the
  launcher binds to `127.0.0.1` only.

"use strict";
/*
 * Offline pronunciation review harness.
 *
 * Hard rule: this file makes no network request other than reading files that
 * live next to it and the .ogg clips inside this repository. No CDN, no web
 * font, no analytics, no remote anything.
 */

const STORE_KEY = "duo-pron-review-v1";
const CLIP_KEY = "duo-pron-review-clips";

const VERDICTS = [
  { id: "correct",  label: "Sounds correct",  cls: "correct"  },
  { id: "wrong",    label: "Wrong sound",     cls: "wrong"    },
  { id: "unclear",  label: "Unclear / robotic", cls: "unclear" },
  { id: "skip",     label: "Skip",            cls: "skip"     },
];

const $ = (sel) => document.querySelector(sel);
const el = (tag, cls, text) => {
  const n = document.createElement(tag);
  if (cls) n.className = cls;
  if (text != null) n.textContent = text;
  return n;
};

const state = {
  clips: [],

  verdictClasses: {},
  verdictPriority: [],
  judgments: loadJudgments(),
  sampleIds: null,       // Set of clip names, or null for "no sample"
  hasRef: new Set(),     // clip names with a reference recording
  refProbed: new Set(),  // rows already asked about a reference recording
  rate: 1,
  loopAll: false,
  showMachine: false,
  activeRow: null,       // clip name, for the keyboard shortcuts
  playing: null,         // { row, audio, which }
  playAllToken: 0,
};

/* ------------------------------------------------------------------ store */

function loadJudgments() {
  try {
    const raw = localStorage.getItem(STORE_KEY);
    return raw ? JSON.parse(raw) : {};
  } catch (err) {
    console.warn("saved answers could not be read:", err);
    return {};
  }
}

let saveTimer = null;
function save() {
  clearTimeout(saveTimer);
  saveTimer = setTimeout(() => {
    try {
      localStorage.setItem(STORE_KEY, JSON.stringify(state.judgments));
    } catch (err) {
      toast("Could not save in this browser — export before you close the page.");
    }
  }, 150);
}

function toast(message) {
  const t = $("#toast");
  t.textContent = message;
  t.classList.add("show");
  clearTimeout(toast._timer);
  toast._timer = setTimeout(() => t.classList.remove("show"), 2600);
}

/* ------------------------------------------------------------------ audio */

function makeAudio(src) {
  const a = new Audio();
  a.preload = "auto";
  a.src = src;
  return a;
}

function stopPlaying() {
  state.playAllToken++;
  if (state.playing) {
    const { audio } = state.playing;
    audio.onended = null;
    audio.pause();
    audio.currentTime = 0;
    state.playing = null;
  }
  document.querySelectorAll(".play.playing, .ref.playing")
    .forEach((b) => b.classList.remove("playing"));
}

function playRow(row, which) {
  stopPlaying();
  const src = which === "ref" ? row.refSrc() : row.audioUrl;
  if (!src) return;
  const audio = makeAudio(src);
  audio.playbackRate = state.rate;
  audio.loop = which === "clip" && state.loopAll;
  const btn = which === "ref" ? row.refBtn : row.playBtn;
  const start = () => {
    state.playing = { row, audio, which };
    btn.classList.add("playing");
    btn.textContent = "■";
  };
  audio.onended = () => {
    btn.classList.remove("playing");
    btn.textContent = which === "ref" ? "Reference" : "▶";
    if (state.playing && state.playing.audio === audio) state.playing = null;
  };
  audio.onerror = () => {
    btn.classList.remove("playing");
    toast(which === "ref"
      ? "No reference recording for this line yet."
      : "That audio file could not be played.");
  };
  audio.play().then(start).catch(() => {
    audio.onerror();
  });
}

async function playAll() {
  const rows = visibleRows();
  if (!rows.length) return;
  const token = ++state.playAllToken;
  stopPlaying();
  toast(`Playing ${rows.length} lines at ${state.rate}×`);
  for (const row of rows) {
    if (token !== state.playAllToken) return;
    await new Promise((resolve) => {
      const audio = makeAudio(row.audioUrl);
      audio.playbackRate = state.rate;
      audio.onended = resolve;
      audio.onerror = resolve;
      audio.play().catch(resolve);
    });
    await new Promise((r) => setTimeout(r, 120));
  }
  toast("Finished.");
}


function isJudged(clip) {
  const j = state.judgments[clip];
  return !!(j && j.verdict);
}
/* -------------------------------------------------------------- filtering */

function matchesFilters(c) {
  const fLang = $("#fLang").value;
  if (fLang && c.lang !== fLang) return false;

  const fVerdict = $("#fVerdict").value;
  if (fVerdict && c.verdictClass !== fVerdict) return false;

  const fType = $("#fType").value;
  if (fType && !c.challengeTypes.includes(fType)) return false;

  if (state.sampleIds && !state.sampleIds.has(c.clip)) return false;
  // "Only not judged yet" HIDES the ones already judged, so a returning
  // reviewer picks up exactly where they stopped.
  if ($("#fUnjudged").checked && isJudged(c.clip)) return false;
  return true;
}

function visibleRows() {
  return state.clips.filter(matchesFilters);
}

/* ------------------------------------------------------------------ views */

function readingBadge(c) {
  if (c.readingKind === "authored" || c.readingKind === "romaji" ||
      c.readingKind === "romanisation") return null;
  if (c.readingKind === "derived") {
    return el("span", "badge derived", "worked out automatically, not checked");
  }
  return el("span", "badge", "no reading recorded");
}

function buildRow(c) {
  const row = el("div", `row lang-${c.lang}`);
  row.dataset.clip = c.clip;
  const j = state.judgments[c.clip];

  /* head ------------------------------------------------------------- */
  const head = el("div", "row-head");

  const playBtn = el("button", "play", "▶");
  playBtn.type = "button";
  playBtn.title = "Play / pause (Space)";
  playBtn.setAttribute("aria-label", `Play ${c.clip}`);
  playBtn.addEventListener("click", () => {
    if (state.playing && state.playing.row === row && state.playing.which === "clip") {
      stopPlaying();
    } else {
      state.activeRow = c.clip;
      playRow(row, "clip");
    }
  });
  head.appendChild(playBtn);

  const slot = el("div", "slot");
  slot.appendChild(el("div", "clip-name", c.clip));

  const text = el("div", "text", c.text);
  slot.appendChild(text);

  if (c.reading) {
    const rd = el("div", "reading");
    const label = c.lang === "ja" ? "The app says:" : "The app says:";
    rd.appendChild(el("span", "label", label));
    rd.appendChild(document.createTextNode(c.reading));
    slot.appendChild(rd);
  }

  const meta = el("div", "meta");
  const bits = [c.langLabel];
  if (c.challengeTypes.length) bits.push(c.challengeTypes.join(", "));
  if (c.lessonIds && c.lessonIds.length) bits.push("lesson " + c.lessonIds.join(", "));
  meta.textContent = bits.join(" · ");
  const badge = readingBadge(c);
  if (badge) { meta.appendChild(document.createTextNode(" ")); meta.appendChild(badge); }
  if (c.readingConfidence && c.readingConfidence !== "high") {
    const cf = el("span", `badge ${c.readingConfidence}`,
      "author confidence: " + c.readingConfidence);
    meta.appendChild(document.createTextNode(" "));
    meta.appendChild(cf);
  }
  slot.appendChild(meta);
  head.appendChild(slot);
  row.appendChild(head);

  /* A/B -------------------------------------------------------------- */
  const ab = el("div", "ab");
  const refBtn = el("button", "ref", "Reference");
  refBtn.type = "button";
  refBtn.hidden = true;
  refBtn.title = "Compare against a reference recording (S)";
  refBtn.addEventListener("click", () => {
    if (state.playing && state.playing.row === row && state.playing.which === "ref") {
      stopPlaying();
    } else {
      state.activeRow = c.clip;
      playRow(row, "ref");
    }
  });
  ab.appendChild(refBtn);

  const loopLabel = el("label", "loop");
  const loopBox = el("input");
  loopBox.type = "checkbox";
  loopBox.checked = state.loopAll;
  loopLabel.appendChild(loopBox);
  loopLabel.appendChild(document.createTextNode(" Repeat this line"));
  loopBox.addEventListener("change", () => {
    if (state.playing && state.playing.row === row) {
      state.playing.audio.loop = loopBox.checked && state.playing.which === "clip";
    }
  });
  ab.appendChild(loopLabel);

  ab.appendChild(el("span", "spacer"));


  row.appendChild(ab);

  /* verdicts --------------------------------------------------------- */
  const vs = el("div", "verdicts");
  for (const v of VERDICTS) {
    const b = el("button", `v-${v.cls}`, v.label);
    b.type = "button";
    b.setAttribute("aria-pressed", "false");
    b.addEventListener("click", () => setVerdict(row, c, v.id));
    vs.appendChild(b);
  }
  row.appendChild(vs);

  /* note + which ----------------------------------------------------- */
  const follow = el("div", "followup");
  const whichLabel = el("label", null, "Which word or syllable?");
  whichLabel.htmlFor = "w-" + c.clip;
  const which = el("input");
  which.type = "text";
  which.id = "w-" + c.clip;
  which.placeholder = "e.g. the second word";
  const noteLabel = el("label", null, "Anything else (optional)");
  noteLabel.htmlFor = "n-" + c.clip;
  const note = el("input");
  note.type = "text";
  note.id = "n-" + c.clip;
  note.placeholder = "optional";
  // Repopulate from the saved answer, so a reload or a filter change does not
  // wipe what the reviewer already typed.
  which.value = (j && j.which) || "";
  note.value = (j && j.note) || "";
  follow.appendChild(whichLabel);
  follow.appendChild(which);
  follow.appendChild(noteLabel);
  follow.appendChild(note);
  which.addEventListener("input", () => patch(c.clip, { which: which.value }));
  note.addEventListener("input", () => patch(c.clip, { note: note.value }));
  row.appendChild(follow);

  /* details ---------------------------------------------------------- */
  const details = el("details", "more");
  const summary = el("summary", null, "Details");
  details.appendChild(summary);
  const body = el("div", "more-body");

  const addKv = (k, v) => {
    if (!v) return;
    const line = el("div", "kv");
    line.appendChild(el("span", null, k));
    const val = el("span");
    val.appendChild(v);
    line.appendChild(val);
    body.appendChild(line);
  };
  addKv("Text taken from", document.createTextNode(
    c.textProvenance + (c.textConfidence ? " (" + c.textConfidence + ")" : "")));
  if (c.phonemes) addKv("Sounds asked for", el("code", null, c.phonemes));
  if (c.kanaReading) addKv("Kana reading", document.createTextNode(c.kanaReading));
  if (c.readingSource) addKv("Where the reading comes from",
    document.createTextNode(c.readingSource));
  if (c.readingNote) addKv("Note on the reading", document.createTextNode(c.readingNote));

  if (c.challengeIds.length) {
    addKv("Exercises", document.createTextNode(
      c.challengeIds.length > 12
        ? c.challengeIds.slice(0, 12).join(", ") + " …"
        : c.challengeIds.join(", ")));
  }
  if (c.sourceIds.length && c.sourceIds[0]) {
    addKv("Item numbers", document.createTextNode(c.sourceIds.join(", ")));
  }

  const machine = el("div", "kv");
  machine.appendChild(el("span", null, "What the computer found"));
  const mv = el("span");
  mv.appendChild(el("strong", null, c.verdictLabel));
  mv.appendChild(document.createTextNode(" — " + c.verdictGloss));
  if (c.verdictNote) {
    mv.appendChild(el("div", null, "For this line: " + c.verdictNote));
  }
  mv.appendChild(el("div", null,
    "This is context only. It was produced by a machine and reviewed by nobody; " +
    "please ignore it when judging, and switch it on only if you want to know " +
    "where the machines were unsure."));
  machine.appendChild(mv);
  body.appendChild(machine);

  details.appendChild(body);
  details.open = state.showMachine;
  row.appendChild(details);

  row.refBtn = refBtn;
  row.playBtn = playBtn;
  row.loopBox = loopBox;
  row.refPath = "references/" + c.lang + "/" + c.clip;
  row.refSrc = () => (state.hasRef.has(c.clip) ? row.refPath : null);
  row.audioUrl = c.audio;

  paintVerdict(row, c);
  return row;
}

/* One painter for a row's verdict, used both when a row is built and when a
   button is pressed, so the two can never drift apart. */
function paintVerdict(row, c) {
  const j = state.judgments[c.clip];
  const v = (j && j.verdict) ? VERDICTS.find((x) => x.id === j.verdict) : null;

  row.classList.remove("judged", "correct", "wrong", "unclear", "skip");
  row.classList.toggle("judged", !!(j && j.verdict));
  if (v) row.classList.add(v.cls);

  row.querySelector(".followup").classList.toggle("open", !!(j && j.verdict));
  row.querySelectorAll(".verdicts button").forEach((b) => {
    const on = !!v && b.classList.contains("v-" + v.cls);
    b.setAttribute("aria-pressed", on ? "true" : "false");
  });

  const ab = row.querySelector(".ab");
  let tag = ab.querySelector(".badge");
  if (v) {
    if (!tag) {
      tag = el("span", "badge");
      ab.appendChild(tag);
    }
    tag.textContent = "you said: " + v.label;
  } else if (tag) {
    tag.remove();
  }
}

function setVerdict(row, c, verdict) {
  const existing = state.judgments[c.clip];
  if (existing && existing.verdict === verdict) {
    // pressing the same button again clears it, so a mis-click is undoable
    delete state.judgments[c.clip];
  } else {
    state.judgments[c.clip] = Object.assign(
      { when: new Date().toISOString() }, existing || {}, { verdict });
  }
  paintVerdict(row, c);
  const j = state.judgments[c.clip];
  if (j && !existing) {
    const first = row.querySelector(".followup input");
    if (first) first.focus();
  }
  save();
  updateProgress();
}

function patch(clip, fields) {
  const j = state.judgments[clip];
  if (!j || !j.verdict) return;
  Object.assign(j, fields, { when: new Date().toISOString() });
  save();
}

/* ----------------------------------------------------------------- render */

function render() {
  const list = $("#list");
  const rows = visibleRows();
  list.textContent = "";
  const frag = document.createDocumentFragment();
  const built = [];
  for (const c of rows) {
    const row = buildRow(c);
    built.push(row);
    frag.appendChild(row);
  }
  list.appendChild(frag);
  watchForReferences(built);
  $("#noRows").hidden = rows.length > 0;
  updateProgress();
}

function updateProgress() {
  const total = state.clips.length;
  const judged = state.clips.filter((c) => {
    const j = state.judgments[c.clip];
    return j && j.verdict;
  }).length;
  const found = state.clips.filter((c) => {
    const j = state.judgments[c.clip];
    return j && j.verdict && j.verdict !== "skip";
  }).length;
  $("#progressFill").style.width = (total ? (judged / total) * 100 : 0) + "%";

  const shown = visibleRows().length;
  const bits = [
    `<strong>${judged}</strong> of ${total} judged`,
    `<strong>${found}</strong> said “sounds fine or wrong”`,
    `${shown} shown`,
  ];
  if (state.sampleIds) bits.push(`random sample of ${state.sampleIds.size}`);
  $("#progressText").innerHTML = bits.join(" · ");
}

/* ----------------------------------------------------------- references */

/* Reference recordings are found by asking the server whether a file exists,
   for the rows a reviewer actually scrolls to and not for all 397 up front.
   A HEAD asks for the name, not the bytes, so nothing is downloaded until the
   reviewer presses Reference. If the probe cannot run at all the button stays
   hidden, which is the right answer for a line with no reference anyway. */
let refObserver = null;

function watchForReferences(rows) {
  if (refObserver) refObserver.disconnect();
  refObserver = new IntersectionObserver((entries) => {
    for (const e of entries) {
      if (!e.isIntersecting) continue;
      refObserver.unobserve(e.target);
      probeOne(e.target);
    }
  }, { rootMargin: "200px" });
  for (const row of rows) refObserver.observe(row);
}

async function probeOne(row) {
  const clip = row.dataset.clip;
  if (!clip || state.hasRef.has(clip) || state.refProbed.has(clip)) return;
  state.refProbed.add(clip);
  try {
    const res = await fetch(row.refPath, { method: "HEAD", cache: "no-store" });
    if (res.ok) {
      state.hasRef.add(clip);
      row.refBtn.hidden = false;
    }
  } catch (err) {
    /* no reference recording for this line */
  }
}

/* Fallback for the keyboard shortcut, and for the case where the probe could
   not run at all: ask once, and reveal the button if the file turns out to be
   there. Nothing is downloaded until the reviewer presses Reference. */
async function revealReference(row) {
  const clip = row.dataset.clip;
  if (!state.hasRef.has(clip)) {
    try {
      const res = await fetch(row.refPath, { method: "HEAD", cache: "no-store" });
      if (res.ok) state.hasRef.add(clip);
    } catch (err) {
      return false;
    }
  }
  row.refBtn.hidden = !state.hasRef.has(clip);
  return !row.refBtn.hidden;
}

function cssEscape(s) {
  return window.CSS && CSS.escape ? CSS.escape(s) : String(s).replace(/["\\]/g, "\\$&");
}

/* ----------------------------------------------------------------- export */

function buildExport() {
  const judgments = state.clips
    .filter((c) => {
      const j = state.judgments[c.clip];
      return j && (j.verdict || j.note || j.which);
    })
    .map((c) => {
      const j = state.judgments[c.clip];
      const v = VERDICTS.find((x) => x.id === j.verdict);
      return {
        clip: c.clip,
        language: c.lang,
        text: c.text,
        declaredReading: c.reading || null,
        declaredReadingKind: c.readingKind,
        intendedPhonemes: c.phonemes || null,
        machineVerdictClass: c.verdictClass,
        verdict: v ? v.label : (j.verdict || null),
        verdictId: j.verdict || null,
        which: j.which || "",
        note: j.note || "",
        timestamp: j.when || null,
      };
    });
  return {
    tool: "DuoLingo clone - native pronunciation review",
    formatVersion: 1,
    exportedAt: new Date().toISOString(),
    clipCount: state.clips.length,
    judgedCount: judgments.length,
    note: "Each row is one line of audio a native speaker listened to. " +
          "verdictId is one of correct | wrong | unclear | skip.",
    judgments,
  };
}

function doExport() {
  const data = buildExport();
  if (!data.judgments.length) {
    if (!confirm("You have not judged anything yet. Export an empty file anyway?")) return;
  }
  const stamp = data.exportedAt.slice(0, 10);
  const blob = new Blob([JSON.stringify(data, null, 2)], { type: "application/json" });
  const url = URL.createObjectURL(blob);
  const a = document.createElement("a");
  a.href = url;
  a.download = `pronunciation-review-${stamp}.json`;
  document.body.appendChild(a);
  a.click();
  a.remove();
  setTimeout(() => URL.revokeObjectURL(url), 4000);
  toast("Saved. Send that file back — it is the deliverable.");
}

function doImport(file) {
  const reader = new FileReader();
  reader.onload = () => {
    let parsed;
    try {
      parsed = JSON.parse(reader.result);
    } catch (err) {
      toast("That file is not valid JSON.");
      return;
    }
    const rows = Array.isArray(parsed) ? parsed : parsed.judgments;
    if (!Array.isArray(rows)) {
      toast("That file does not look like a review export.");
      return;
    }
    let n = 0;
    for (const r of rows) {
      if (!r || !r.clip) continue;
      const prev = state.judgments[r.clip] || {};
      state.judgments[r.clip] = {
        verdict: r.verdictId || (VERDICTS.find((v) => v.label === r.verdict) || {}).id || prev.verdict,
        which: r.which != null ? r.which : prev.which || "",
        note: r.note != null ? r.note : prev.note || "",
        when: r.timestamp || prev.when || new Date().toISOString(),
      };
      n++;
    }
    save();
    render();
    toast(`Imported ${n} answers.`);
  };
  reader.readAsText(file);
}

/* ------------------------------------------------------------------- boot */

function fillFilters() {
  const cls = $("#fVerdict");
  const order = state.verdictPriority.filter((k) => state.verdictClasses[k]);
  for (const k of order) {
    const o = document.createElement("option");
    o.value = k;
    const n = state.clips.filter((c) => c.verdictClass === k).length;
    o.textContent = `${state.verdictClasses[k].label} (${n})`;
    cls.appendChild(o);
  }
  const types = new Set();
  state.clips.forEach((c) => c.challengeTypes.forEach((t) => types.add(t)));
  for (const t of Array.from(types).sort()) {
    const o = document.createElement("option");
    o.value = t;
    o.textContent = t;
    $("#fType").appendChild(o);
  }
}

function wireControls() {
  for (const sel of ["#fLang", "#fVerdict", "#fType", "#fUnjudged"]) {
    $(sel).addEventListener("change", () => { stopPlaying(); render(); });
  }

  $("#rate").addEventListener("change", (e) => {
    state.rate = parseFloat(e.target.value);
    if (state.playing) {
      state.playing.audio.playbackRate = state.rate;
      if (state.playing.which === "clip") {
        state.playing.audio.loop = state.playing.row.loopBox.checked;
      }
    }
  });
  $("#loopAll").addEventListener("change", (e) => {
    state.loopAll = e.target.checked;
    document.querySelectorAll(".loop input").forEach((b) => { b.checked = state.loopAll; });
    if (state.playing && state.playing.which === "clip") {
      state.playing.audio.loop = state.loopAll;
    }
  });

  $("#playAll").addEventListener("click", playAll);
  $("#stopAll").addEventListener("click", stopPlaying);

  $("#sampleN").addEventListener("change", () => {
    const n = parseInt($("#sampleN").value, 10);
    $("#sampleN").value = String(Number.isFinite(n) && n > 0 ? Math.min(n, 397) : 20);
  });
  $("#sampleBtn").addEventListener("click", () => {
    const n = parseInt($("#sampleN").value, 10) || 20;
    const pool = state.clips.filter(matchesFilters);
    const take = Math.min(n, pool.length);
    const ids = new Set();
    const bag = pool.slice();
    for (let i = 0; i < take; i++) {
      const idx = Math.floor(Math.random() * bag.length);
      ids.add(bag.splice(idx, 1)[0].clip);
    }
    state.sampleIds = ids;
    stopPlaying();
    render();
    toast(`Picked ${take} lines at random.`);
  });
  $("#clearSample").addEventListener("click", () => {
    state.sampleIds = null;
    render();
  });

  $("#showMachine").addEventListener("change", (e) => {
    state.showMachine = e.target.checked;
    document.querySelectorAll("details.more").forEach((d) => { d.open = state.showMachine; });
  });

  $("#exportBtn").addEventListener("click", doExport);
  $("#importBtn").addEventListener("click", () => $("#importFile").click());
  $("#importFile").addEventListener("change", (e) => {
    if (e.target.files && e.target.files[0]) doImport(e.target.files[0]);
    e.target.value = "";
  });
  $("#helpBtn").addEventListener("click", () => $("#help").showModal());

  document.addEventListener("keydown", onKey);
}

function onKey(ev) {
  const tag = (ev.target.tagName || "").toLowerCase();
  if (tag === "input" || tag === "textarea" || tag === "select") return;
  if (ev.metaKey || ev.ctrlKey || ev.altKey) return;
  if (ev.key === "Escape") return;

  const rows = visibleRows();
  if (!rows.length) return;
  const index = rows.findIndex((c) => c.clip === state.activeRow);
  const at = index < 0 ? 0 : index;

  if (ev.key === "j" || ev.key === "ArrowDown" && ev.altKey) {
    ev.preventDefault();
    focusRow(rows[Math.min(at + 1, rows.length - 1)]);
  } else if (ev.key === "k" || ev.key === "ArrowUp" && ev.altKey) {
    ev.preventDefault();
    focusRow(rows[Math.max(at - 1, 0)]);
  } else if (ev.key === " ") {
    ev.preventDefault();
    const row = rowElement(rows[at].clip);
    if (row) row.playBtn.click();
  } else if ("1234".includes(ev.key)) {
    ev.preventDefault();
    const v = VERDICTS[parseInt(ev.key, 10) - 1];
    const row = rowElement(rows[at].clip);
    if (row && v) setVerdict(row, rows[at], v.id);
  } else if (ev.key === "s") {
    ev.preventDefault();
    const row = rowElement(rows[at].clip);
    if (!row) return;
    if (row.refBtn.hidden) {
      revealReference(row).then((shown) => {
        if (shown) row.refBtn.click();
        else toast("No reference recording for this line yet.");
      });
    } else {
      row.refBtn.click();
    }
  }
}

function rowElement(clip) {
  return document.querySelector(`.row[data-clip="${cssEscape(clip)}"]`);
}

function focusRow(clip) {
  const row = rowElement(clip);
  if (!row) return;
  state.activeRow = clip;
  row.scrollIntoView({ block: "center", behavior: "smooth" });
  row.playBtn.focus();
}

async function boot() {
  wireControls();
  let data;
  try {
    const res = await fetch("data/clips.json", { cache: "no-store" });
    if (!res.ok) throw new Error("HTTP " + res.status);
    data = await res.json();
  } catch (err) {
    const p = $("#loadError");
    p.hidden = false;
    p.textContent =
      "This page could not read its own data file (" + err.message + "). " +
      "Browsers refuse to do that when a page is opened straight off the disk. " +
      "Start it with the launcher instead:  python serve.py";
    return;
  }

  state.clips = data.clips;

  state.verdictClasses = data.verdictClasses || {};
  state.verdictPriority = data.verdictPriority || [];

  // Offer "only not judged yet" from the start: that is the resumable view.
  $("#fUnjudged").checked = true;
  fillFilters();
  render();

}

boot();

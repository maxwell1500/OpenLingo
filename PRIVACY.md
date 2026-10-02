# Privacy Policy — OpenLingo

**Last updated: 27 September 2026**
**Applies to: OpenLingo for Android (`com.openlingo.app`), version 1.1.0 and later**

OpenLingo is a free, open-source (MIT) language-learning app for Spanish (CEFR A1 → B1) and Japanese (JLPT N5 → N4). Its mascot is a capybara named OpenLingo, who is chill, offline, and does not phone home.

The short version: **OpenLingo collects nothing, sends nothing, and has nowhere to send it to.** There is no account, no server, no analytics, and no tracking. Your learning history never leaves your phone unless you personally export it and hand it to someone.

---

## 1. Who is behind this

OpenLingo is an independent, volunteer-run open-source project. It is not a company. There is no support desk, no customer service team, and no private address to write to — that is a direct consequence of the rest of this document: the app has no account, no server, and no personal data about anyone, so there is nothing for a private conversation about a user's data to be about.

The full source is public at <https://github.com/maxwell1500/OpenLingo>, and the app is distributed as-is under the MIT licence, with no warranty of any kind, as the MIT licence itself states. That repository is the authority on what OpenLingo is and what it does: the code, the `LICENSE` file, and this policy are all published there, and any factual claim in this document can be checked against the code it describes. The project's public issue tracker is the only channel the project uses for anything anyone wants to raise, and it is a public record rather than a private mailbox.

---

## 2. The one thing that makes this policy short

OpenLingo's manifest does not request the `INTERNET` permission. This is not a promise in a marketing bullet — it is enforced by the operating system. Android will not let the app open a network connection at all.

The shipped APK was checked and confirmed to contain:

- No `INTERNET` permission
- No `ACCESS_NETWORK_STATE` permission (Media3's audio library declares it for streaming; since OpenLingo only plays audio bundled inside the APK, the declaration is explicitly stripped from the merged manifest)
- No `QUERY_ALL_PACKAGES` permission (the app cannot see which other apps you have installed)
- No `FOREGROUND_SERVICE` permission
- No `WebView` — there is no embedded browser and no web content rendered inside the app
- No `VIEW` / `BROWSABLE` intent filters, so OpenLingo cannot be silently opened by a web page or another app
- No `usesCleartextTraffic` — no unencrypted network traffic of any kind
- No advertising SDKs, no analytics SDKs, no crash reporters, no A/B testing frameworks

There is no network code in the app. There is no web server, no API, no telemetry endpoint, and no third party to send data to. This is verifiable by anyone: install the app, then read `AndroidManifest.xml` in the open-source repository, or inspect the merged manifest of the release APK with `aapt dump permissions`.

Because there is no backend, we cannot see who you are, which lessons you have completed, or how many days your streak has survived. We do not know whether you have ever opened the app.

---

## 3. What the app stores on your device

OpenLingo keeps everything locally, in a private, on-device SQLite database (`openlingo_local.db`) inside the app's own sandboxed storage, plus Android shared preferences for settings. Nothing in that database is readable by other apps on your device (Android app sandboxing enforces this) and nothing is uploaded.

Depending on how you use the app, that local data includes:

- Which challenges you have completed, and your points (XP), hearts, and current streak
- Your daily quest goal and daily activity history
- Mistake entries and the spaced-repetition review schedule (FSRS) for vocabulary
- Kana (Hiragana / Katakana) mastery records
- Checkpoint assessment scores
- Preferences: theme mode, accent colour, sound and haptics toggles, whether the daily reminder is on

There is no user ID, no email address, no phone number, no device identifier, and no advertising ID. The app uses a single built-in local profile — sometimes displayed internally as a "Guest Learner" — and does not ask you to create or sign in to anything.

---

## 4. Permissions, and why each one is there

OpenLingo's merged manifest declares exactly three permissions. All three are optional in practice; the app is usable if you decline all of them.

| Permission | Why OpenLingo needs it |
|---|---|
| `RECEIVE_BOOT_COMPLETED` | To re-register its local alarms after you restart the phone. The midnight streak/heart reset and the optional 7 PM reminder are scheduled with `AlarmManager`, and Android clears pending alarms on reboot. Without this, your daily reset would silently stop working until you next opened the app. |
| `SCHEDULE_EXACT_ALARM` | So the midnight reset fires at midnight and the streak reminder fires at 7 PM rather than "sometime later", which would make the heart refill and the daily quest behave inconsistently. |
| `POST_NOTIFICATIONS` | To show the optional 7 PM streak reminder. Android 13+ requires this permission for any app to post a notification. On Android 13 and later your system prompt is what actually grants it, and on any version the reminder itself is a setting you can switch off in Settings. |

What is *not* requested, and why it matters: no `INTERNET` (nothing to send data to), no `ACCESS_FINE_LOCATION` or `ACCESS_COARSE_LOCATION`, no `CAMERA`, no `RECORD_AUDIO`, no `READ_CONTACTS`, no `READ_PHONE_STATE`, no `QUERY_ALL_PACKAGES`, no `REQUEST_INSTALL_PACKAGES`.

The single place OpenLingo can leave its own screen is the streak reminder notification. It is a local notification generated on your device, it is posted on a schedule you set, and you can switch it off in Settings at any time. No other app is contacted.

---

## 5. Your backups are yours

OpenLingo has no cloud sync and no account, so if you want to keep or move your progress, you do it explicitly:

- **Export** (Settings) — the app serialises your full progress to a readable JSON document and hands it to Android's share sheet. Your device then asks *you* which app to send it to — a file manager, an email app, a cloud drive, a messaging app, or nothing at all. OpenLingo does not choose a destination, does not upload the file, and never sees where it goes.
- **Import** (Settings) — you paste or open a JSON backup yourself, and the app restores it.

If you exported a backup and sent it somewhere, that copy is now under the control of whatever app you chose, governed by *that* app's privacy policy. We never receive it, cannot see it, and cannot delete it for you. Any backup you no longer want is yours to remove — from that destination, and from your device's storage.

Two related notes on device-level behaviour that is controlled by Android, not by us:

- **System backup.** Because `android:allowBackup` is enabled, Android's own backup service may include the app's database and preferences in your device backup (for example, Google system backup). That is Android's backup feature, operating under Google's account settings, and you can exclude the app or turn system backup off in your device settings. We do not have access to that data.
- **Uninstalling.** Uninstalling OpenLingo removes the app's private storage, including `openlingo_local.db`, in full.

---

## 6. Deleting your data

**There is no server-side copy of your data, because nothing is ever uploaded. There is nothing for us to delete, and no account, profile, or record for us to delete you from.** Asking us to delete your data is not a request we can act on — there is no data.

To erase what is on your device:

1. **Reset progress** — in Settings, tap **"🗑 Reset all progress"** and confirm. This wipes your lesson completions, points, hearts, streak, mistake history, kana mastery, and daily quest history, and writes a fresh learner profile in their place. Because that fresh profile is a full replacement, your theme choice, accent colour, daily XP goal, and the sound/haptics/romaji toggles also return to their defaults. The daily reminder toggle is stored separately and is left as you set it, and the bundled course content is never touched. This is not reversible — export a backup first if you want to keep your history. It is entirely an on-device operation.
2. **Uninstall** — removing the app deletes its sandbox entirely, database included. This is the most complete deletion available, and it is the one to use if you want everything gone rather than just your learning history cleared.
3. **Your exported backups** — delete any JSON backup files you shared, from wherever you sent them. We have no copy to remove.

There is no retention period to describe, because nothing is received and nothing is stored on a server. No log of your activity exists anywhere outside your device.

---

## 7. Third-party components

OpenLingo builds on open-source libraries — Android Jetpack (including Room and Media3/ExoPlayer), KotlinX Serialization, and the Kokoro-82M text-to-speech models whose audio is bundled inside the APK. They are compiled into the app, and none of them is configured to phone home, because the app has no permission to. We have not integrated advertising, analytics, attribution, or crash-reporting services, and we do not embed third-party web content.

If that ever changes, this policy will be updated before it ships, and the change will be described in the release notes.

---

## 8. Children

OpenLingo is a general-audience language-learning app and is not directed at children. Because it collects no data from anyone — no names, no identifiers, no usage information, and no device or location information — the collection question that usually arises for younger users does not apply here. We do not knowingly collect personal information from anyone, of any age.

---

## 9. Scope and changes

This policy covers the OpenLingo Android application distributed from this repository. It does not cover:

- the source code repository host (for example, GitHub) and its own privacy practices and server logs, which govern viewing the repository, not using the app;
- the bundled Kokoro-82M audio assets, which are part of the app and never leave your device;
- any backup file you have exported, once you have shared it with a third-party app (see §5);
- other applications on your device, which are entirely outside our control.

**Changes to this policy.** If OpenLingo ever gains a feature that changes how data is handled — a sign-in, a sync service, an ad, a crash reporter, or any network capability — this document will be rewritten before that version is released, the version it applies to will be updated at the top, and the change will be noted in that release's changelog. Any such change would also require adding a network permission to the manifest, which is the change you would notice first: if a future build asks for `INTERNET`, it is no longer the app described here.

**Verifying this policy.** Every factual claim above can be checked against the source. The manifest, the backup rules, the settings screen, and the local database layer are all in the public repository. If a discrepancy between this document and the code turns up, it is a bug in this document; the repository's public issue tracker is where a project like this records such things, and fixing the document is the right outcome.

---

## License

OpenLingo is open-source software released under the MIT License. See `LICENSE` in the repository.

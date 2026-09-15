# Duolingo-Clone → Native Android App: Architecture Design

**Date:** 2026-09-13
**Source repo:** Next.js 16 Duolingo clone (TypeScript, Drizzle/Postgres, Clerk)
**Target:** Native Android app (Kotlin + Jetpack Compose) consuming the same backend as a versioned REST/JSON API
**Status:** Design document (no app code)

---

## 1. Target Architecture & Data Flow

```
┌─────────────────────────────────────────────────────────────────┐
│                 Android App (Kotlin + Compose)                   │
│  │ UI Layer     │──│ ViewModels   │──│ Domain (UseCases)         │ │
│  │ (Screens)    │  │ (StateFlow)  │  │ + Room (offline)          │ │
│  └──────────────┘   └────────────┘   └─────────────────────────┘ │
│  │ Data Layer: Retrofit/OkHttp client + Interceptor (JWT)       │ │
│  │ Player (audio) + WorkManager (sync)                          │ │
│  └─────────────────────────────────────────────────────────────┘│
└─────────────────────────────────────────────────────────────────┘
                                                  ▼
┌─────────────────────────────────────────────────────────────────┐
| `GET/POST /api/v1/*` (versioned REST) |
│  │  - Thin controllers → existing db/queries.ts logic           ││
│  └─────────────────────────────────────────────────────────────┘│
│  ┌─────────────────────────────────────────────────────────────┐│
│  │ Existing server actions (retained for web app)                ││
│  │ actions/*.ts → db/queries.ts → Drizzle ORM → Neon Postgres    ││
│  └─────────────────────────────────────────────────────────────┘│
│  ┌─────────────────────────────────────────────────────────────┐│
│  │ Existing admin panel (react-admin) + marketing site          ││
│  └─────────────────────────────────────────────────────────────┘│
└─────────────────────────────────────────────────────────────────┘
```

**Key design decisions:**
- **One backend, two API surfaces:** The web app keeps using Next.js Server Actions; the Android app talks to `app/api/v1/*` REST endpoints. Both call the same `db/queries.ts` logic.
- **Versioned API:** All Android-facing endpoints live under `/api/v1/` so future changes don't break shipped clients.
- **JWT auth:** Android authenticates via Clerk OAuth 2.0 flow → receives JWT → sends as `Authorization: Bearer` on every request. Backend verifies with Clerk backend SDK.
- **Offline-first:** Room DB caches course tree + progress; syncs via WorkManager.

---

## 2. Authentication

### 2.1 Approach Comparison

| Approach | Pros | Cons |
|----------|------|------|
| **Clerk Android SDK** (recommended) | Handles OAuth flow, PKCE, token refresh, session storage, multi-account. Official support. | Adds dependency; requires Clerk plan that supports mobile. |
| Manual OAuth 2.0 PKCE | No dependency | ~500 lines of auth code to maintain; error-prone. |
| Username/password via custom flow | Simple | No MFA, no social login, no session management. |

**Decision:** Use the official **Clerk Android SDK** — `com.clerk:clerk-android-api` (MIT-licensed, F-Droid-compatible). The web app already uses Clerk; the same user pool, JWT secrets, and organization config apply.

### 2.2 Token Lifecycle

```
1. App launches → Clerk SDK checks for stored session
2. No session → Clerk SDK launches in-app OAuth 2.0 PKCE flow against Clerk
   (same Authorization Server the web app uses)
3. On success → Clerk SDK returns a session object containing:
   - idToken (JWT, short-lived, ~1h)
   - refreshToken (long-lived, rotating)
4. Android stores these in EncryptedSharedPreferences (via AndroidX Security Crypto)
5. Retrofit OkHttp Interceptor:
   - Reads current idToken before each request
   - Adds "Authorization: Bearer <idToken>"
   - On 401 response → triggers Clerk SDK token refresh → retries once
   - On refresh failure → logs user out, navigates to auth screen
```

### 2.3 Backend JWT Verification

In each `app/api/v1/*` route handler:

```ts
import { auth } from "@clerk/nextjs/server";

export const GET = async (req: NextRequest) => {
  const { userId } = await auth();  // verifies JWT signature + expiry
  if (!userId) {
    return NextResponse.json({ error: "unauthorized" }, { status: 401 });
  }
  // userId is the Clerk user ID — same format as web app
  // ...query DB
};
```

`@clerk/nextjs/server`'s `auth()` verifies the JWT against Clerk's JWKS endpoint automatically. The `userId` returned is the same Clerk `user_id` that the web app's server actions write to `user_progress.userId` and `challenge_progress.userId`. **No user ID mapping is needed** — both clients write the same Clerk user ID to the same rows.

### 2.4 userId Consistency

- `user_progress.userId` (text, PK) ← Clerk user ID
- `challenge_progress.userId` (text) ← Clerk user ID

The Android app never needs to know about this mapping — Clerk ensures the same user gets the same ID across web and mobile OAuth.

---

## 3. REST API Contract v1

Base URL: `https://<nextjs-deploy-host>/api/v1`
Auth: `Authorization: Bearer <clerk-jwt>` (required on all endpoints except public course listing)
Content-Type: `application/json`
Errors: `{ "error": "code", "message": "..." }` with appropriate HTTP status

### 3.1 Public Endpoints (no auth)

| Method | Path | Returns |
|--------|------|---------|
| `GET` | `/courses` | `Course[]` |

### 3.2 Authenticated Endpoints

| Method | Path | Body | Returns |
|--------|------|------|---------|
| `GET` | `/me` | — | `UserProfile` |
| `POST` | `/me/course` | `{ courseId: number }` | `UserProfile` (updated) |
| `GET` | `/me/progress` | — | `ProgressSummary` |
| `GET` | `/course/:id` | — | `CourseDetail` (with units, lessons, completion status) |
| `GET` | `/lesson/:id` | — | `Lesson` (with challenges + options) |
| `POST` | `/challenge/:id/progress` | `{ completed: boolean }` | `ChallengeResult` |
| `GET` | `/hearts` | — | `{ hearts: number, max: number }` |
| `POST` | `/hearts/refill` | — | `{ hearts: number }` |
| `GET` | `/quests` | — | `Quest[]` |
| `GET` | `/leaderboard` | — | `LeaderboardEntry[]` |

### 3.3 JSON Shapes

**Course:**
```json
{
  "id": 1,
  "title": "Spanish",
  "imageSrc": "https://cdn.example.com/courses/spanish.svg"
}
```

**CourseDetail (GET /course/:id):**
```json
{
  "id": 1,
  "title": "Spanish",
  "imageSrc": "https://cdn.example.com/courses/spanish.svg",
  "units": [
    {
      "id": 10,
      "title": "Unit 1",
      "description": "Learn the basics of Spanish",
      "order": 0,
      "lessons": [
        {
          "id": 100,
          "title": "Lesson 1",
          "order": 0,
          "completed": true
        },
        {
          "id": 101,
          "title": "Lesson 2",
          "order": 1,
          "completed": false
        }
      ]
    }
  ]
}
```

**Lesson (GET /lesson/:id):**
```json
{
  "id": 100,
  "title": "Lesson 1",
  "challenges": [
    {
      "id": 1000,
      "type": "SELECT",
      "question": "Which one means 'The dog'?",
      "order": 0,
      "completed": false,
      "options": [
        { "id": 10001, "text": "El perro", "correct": true, "imageSrc": null, "audioSrc": "https://cdn.example.com/audio/es/10001.ogg" },
        { "id": 10002, "text": "La gata", "correct": false, "imageSrc": null, "audioSrc": "https://cdn.example.com/audio/es/10002.ogg" }
      ]
    }
  ]
}
```

**ChallengeResult (POST /challenge/:id/progress):**
```json
{
  "pointsEarned": 10,
  "hearts": 4,
  "practice": false,
  "nextChallenge": { "id": 1001, "type": "SELECT", "question": "...", "order": 1, "options": [...] }
}
```


### 3.4 New Backend Route Handlers Required

The existing `app/api/courses/route.ts` is admin-only and uses a different auth model. The following **new** route handlers must be created under `app/api/v1/`:

- `app/api/v1/courses/route.ts` — public `GET` (course list)
- `app/api/v1/course/[id]/route.ts` — `GET` course detail with progress
- `app/api/v1/me/route.ts` — `GET` user profile; `POST` select course
- `app/api/v1/me/progress/route.ts` — `GET` progress summary
- `app/api/v1/lesson/[id]/route.ts` — `GET` lesson with challenges
- `app/api/v1/challenge/[id]/progress/route.ts` — `POST` challenge result
- `app/api/v1/hearts/route.ts` — `GET` hearts; `POST /refill`
- `app/api/v1/quests/route.ts` — `GET` quests
- `app/api/v1/leaderboard/route.ts` — `GET` leaderboard

Each handler delegates to the existing `db/queries.ts` functions (`getCourses`, `getCourseById`, `getLesson`, etc.) so business logic is not duplicated.

---

## 4. Offline-First Design

### 4.1 Room Database Schema

**Entities:**

```kotlin
@Entity(tableName = "courses")
data class CourseEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val imageSrc: String,
    val lastSynced: Long
)

@Entity(tableName = "units")
data class UnitEntity(
    @PrimaryKey val id: Int,
    val courseId: Int,
    val title: String,
    val description: String,
    val orderIndex: Int,
    val lastSynced: Long
)

@Entity(tableName = "lessons")
data class LessonEntity(
    @PrimaryKey val id: Int,
    val unitId: Int,
    val title: String,
    val orderIndex: Int,
    val lastSynced: Long
)

@Entity(tableName = "challenges")
data class ChallengeEntity(
    @PrimaryKey val id: Int,
    val lessonId: Int,
    val type: String,       // SELECT | ASSIST
    val question: String,
    val orderIndex: Int,
    val lastSynced: Long
)

@Entity(tableName = "challenge_options")
data class ChallengeOptionEntity(
    @PrimaryKey val id: Int,
    val challengeId: Int,
    val text: String,
    val correct: Boolean,
    val imageSrc: String?,
    val audioSrc: String?,
    val lastSynced: Long
)

@Entity(tableName = "challenge_progress")
data class ChallengeProgressEntity(
    @PrimaryKey val id: Int,
    val userId: String,
    val challengeId: Int,
    val completed: Boolean,
    val synced: Boolean,    // offline-first flag
    val lastSynced: Long
)

@Entity(tableName = "user_progress")
data class UserProgressEntity(
    @PrimaryKey val userId: String,
    val userName: String,
    val userImageSrc: String,
    val activeCourseId: Int?,
    val hearts: Int,
    val points: Int,
    val lastSynced: Long
)

@Entity(tableName = "sync_queue")
data class SyncQueueEntity(
    @PrimaryKey val id: Long,  // auto-generated
    val type: String,          // "challenge_progress", "hearts_spend", etc.
    val payload: String,       // JSON payload
    val createdAt: Long,
    val retryCount: Int,
    val lastError: String?
)
```

**DAOs:** Standard `@Dao` interfaces with suspend functions. Key queries:
- `getCourseDetail(courseId)` — joins units/lessons with completion status from `challenge_progress`
- `getLesson(id)` — lesson + challenges + options + user's completion flags
- `markChallengeCompleted(challengeId)` — upsert into `challenge_progress`, set `synced=false`
- `getPendingSyncs()` — `SELECT * FROM sync_queue ORDER BY createdAt ASC`

### 4.2 Sync Strategy (WorkManager)

**SyncWorker** (periodic + one-off triggers):

1. **Download phase:**
   - `GET /course/:activeCourseId` → upsert units, lessons, challenges, options into Room
   - Compare `lastSynced` timestamps to avoid redundant writes
   - Store full course detail for offline browsing

2. **Upload phase:**
   - Read `sync_queue` entries in creation order
   - For each `challenge_progress`: `POST /challenge/:id/progress`
   - On success: remove from queue, mark Room row `synced=true`
   - On failure: increment `retryCount`, keep in queue
   - For each `hearts_spend`: `POST /hearts/spend`

3. **Conflict resolution:**
   - **Challenge completion:** Idempotent — `upsertChallengeProgress` on the backend already handles this. Client marks done locally; server accepts duplicate.
   - **Hearts:** Use optimistic concurrency — client sends expected heart count; server rejects with `409` if mismatched (means user spent/refilled elsewhere). On conflict, fetch current hearts from server and overwrite local.
   - **Course selection:** Last write wins; no conflict expected.

**Sync triggers:**
- App foreground (connectivity check)
- Periodic WorkManager job (every 15 minutes when network available)
- Manual pull-to-refresh on Learn screen
- After every lesson completion (immediate sync of progress)

### 4.3 Audio Caching (per docs/kokoro-tts.md)

- Audio files are pre-generated Ogg/Vorbis files on a CDN (Kokoro 82M model)
- `ChallengeOptionEntity.audioSrc` stores the CDN URL
- **Download strategy:** When a lesson is loaded, Enqueue Downloads for all audio in that lesson via `DownloadManager` (background, survives process death)
- **Playback:** Media3 ExoPlayer plays from downloaded file at `app_cache_dir/audio/<challengeOptionId>.ogg`
- **Cache eviction:** LRU by lesson ID; keep last 20 lessons' audio, evict oldest on storage pressure
- **Offline:** Play cached audio; if not downloaded, show "audio unavailable offline" toast

---

## 5. Screen-by-Screen Mapping

| # | Web Screen | Compose Route | ViewModel | API Call(s) |
|---|-----------|---------------|-----------|-------------|
| 1 | Landing page | `/` (splash → auth state check) | `SplashViewModel` | `GET /auth/state` (Clerk SDK) |
| 2 | Sign in/up | `/auth` | `AuthViewModel` | Clerk SDK OAuth flow |
| 3 | Course selection | `/courses` | `CoursesViewModel` | `GET /courses` |
| 4 | Learn (unit tree) | `/learn` | `LearnViewModel` | `GET /course/:id` (with progress) |
| 5 | Lesson quiz | `/lesson/:id` | `LessonViewModel` | `GET /lesson/:id`, `POST /challenge/:id/progress` |
| 6 | Hearts | `/hearts` | `HeartsViewModel` | `GET /hearts`, `POST /hearts/refill` |
| 7 | Quests | `/quests` | `QuestsViewModel` | `GET /quests` |
| 8 | Leaderboard | `/leaderboard` | `LeaderboardViewModel` | `GET /leaderboard` |
| 9 | Profile / settings | `/me` | `ProfileViewModel` | `GET /me` |
| 10 | Admin panel | *(web only, not on Android)* | — | — |
Each ViewModel exposes `state: StateFlow<UiState<T>>` and `load()` / `submit()` methods. Repository handles Room-first logic (serve from cache, trigger sync).

---


## 6. App Module Structure

```
duo-android/
├── build.gradle.kts          # Project-level
├── settings.gradle.kts
├── app/                       # :app module — the main application
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── kotlin/com/duo/app/
│       │   ├── DuoApplication.kt          # Hilt entry, WorkManager init
│       │   ├── MainActivity.kt            # Single activity, NavHost
│       │   ├── di/
│       │   │   ├── NetworkModule.kt       # Retrofit, OkHttp, JWT interceptor
│       │   │   ├── DatabaseModule.kt      # Room DB, DAOs
│       │   │   ├── RepositoryModule.kt    # Repository impls
│       │   │   └── ViewModelModule.kt     # ViewModel factories
│       │   ├── data/
│       │   │   ├── network/
│       │   │   │   ├── DuoApi.kt          # Retrofit interface
│       │   │   │   ├── AuthInterceptor.kt # JWT + refresh
│       │   │   │   └── models/            # kotlinx.serialization DTOs
│       │   │   ├── local/
│       │   │   │   ├── DuoDatabase.kt     # Room Database
│       │   │   │   ├── entities/          # Room entities (see §4.1)
│       │   │   │   └── daos/              # Room DAOs
│       │   │   └── repository/
│       │   │       ├── CourseRepository.kt
│       │   │       ├── LessonRepository.kt
│       │   │       ├── ProgressRepository.kt
│       │   ├── domain/
│       │   │   ├── model/                 # Domain models
│       │   │   └── usecase/               # Use cases (optional, depends on complexity)
│       │   ├── ui/
│       │   │   ├── theme/                 # Compose theme (colors, typography)
│       │   │   ├── components/            # Reusable Compose components
│       │   │   └── screens/
│       │   │       ├── splash/
│       │   │       ├── auth/
│       │   │       ├── courses/
│       │   │       ├── learn/
│       │   │       ├── lesson/
│       │   │       ├── quests/
│       │   │       ├── leaderboard/
│       │   │       └── profile/
│       │   ├── viewmodels/
│       │   │   ├── CoursesViewModel.kt
│       │   │   ├── LearnViewModel.kt
│       │   │   ├── LessonViewModel.kt
│       │   │   └── ...
│       │   └── sync/
│       │       ├── SyncWorker.kt          # WorkManager periodic sync
│       │       └── AudioDownloadWorker.kt # Pre-download lesson audio
│       └── res/
│           ├── values/strings.xml
│           ├── values/themes.xml
│           ├── drawable/
│           └── mipmap-hdpi/.../ic_launcher.webp
└── gradle/libs.versions.toml  # Version catalog
```

**Package:** `com.duo.app`

### Dependencies (Gradle/libs.versions.toml)

```toml
[versions]
# Build / language
agp = "9.4.0"
kotlin = "2.4.20"
# Jetpack
compose-bom = "2026.06.01"
activity-compose = "1.13.0"
lifecycle-runtime-ktx = "2.10.0"
navigation-compose = "2.10.1"
room = "2.8.5"
work-runtime = "2.11.2"
security-crypto = "1.1.0"
paging-compose = "3.5.1"
# DI
hilt = "2.60.1"
hilt-androidx = "1.4.0"
# Auth (Clerk)
clerk-android = "1.1.6"
# Network
retrofit = "3.0.0"
okhttp = "5.4.0"
kotlinx-serialization-json = "1.11.0"
retrofit-kotlinx-serialization = "3.0.0"
# Images
coil = "3.6.2"
# Audio
media3 = "1.11.1"

[libraries]
# Compose BOM pins all androidx.compose artifact versions
compose-bom = { module = "androidx.compose:compose-bom", version.ref = "compose-bom" }
activity-compose = { module = "androidx.activity:activity-compose", version.ref = "activity-compose" }
lifecycle-runtime-ktx = { module = "androidx.lifecycle:lifecycle-runtime-ktx", version.ref = "lifecycle-runtime-ktx" }
navigation-compose = { module = "androidx.navigation:navigation-compose", version.ref = "navigation-compose" }
# Room
room-runtime = { module = "androidx.room:room-runtime", version.ref = "room" }
room-ktx = { module = "androidx.room:room-ktx", version.ref = "room" }
room-compiler = { module = "androidx.room:room-compiler", version.ref = "room" }
# WorkManager
work-runtime-ktx = { module = "androidx.work:work-runtime-ktx", version.ref = "work-runtime" }
# Security
security-crypto = { module = "androidx.security:security-crypto", version.ref = "security-crypto" }
# Paging
paging-compose = { module = "androidx.paging:paging-compose", version.ref = "paging-compose" }
# Hilt (base framework = Dagger; androidx.hilt = integration libs)
hilt-android = { module = "com.google.dagger:hilt-android", version.ref = "hilt" }
hilt-compiler = { module = "com.google.dagger:hilt-compiler", version.ref = "hilt" }
hilt-work = { module = "androidx.hilt:hilt-work", version.ref = "hilt-androidx" }
hilt-navigation-compose = { module = "androidx.hilt:hilt-navigation-compose", version.ref = "hilt-androidx" }
# Network
retrofit = { module = "com.squareup.retrofit2:retrofit", version.ref = "retrofit" }
okhttp = { module = "com.squareup.okhttp3:okhttp", version.ref = "okhttp" }
kotlinx-serialization-json = { module = "org.jetbrains.kotlinx:kotlinx-serialization-json", version.ref = "kotlinx-serialization-json" }
retrofit-kotlinx-serialization = { module = "com.squareup.retrofit2:converter-kotlinx-serialization", version.ref = "retrofit-kotlinx-serialization" }
# Images (Coil 3)
coil-compose = { module = "io.coil-kt.coil3:coil-compose", version.ref = "coil" }
# Audio
media3-exoplayer = { module = "androidx.media3:media3-exoplayer", version.ref = "media3" }
media3-ui = { module = "androidx.media3:media3-ui", version.ref = "media3" }
# Auth (Clerk)
clerk-android-api = { module = "com.clerk:clerk-android-api", version.ref = "clerk-android" }
clerk-android-ui = { module = "com.clerk:clerk-android-ui", version.ref = "clerk-android" }
```

> **No monetization.** This app is entirely free — no billing, in-app purchase, subscription, advertising or paywall; there is no Shop screen, and hearts refill by completing a practice lesson. The web app's Stripe integration (`lib/stripe.ts`, `actions/user-subscription.ts`, `app/api/webhooks/stripe`, and the shop/promo components) is deleted in the backend workstream.

**Clerk note:** the official Android SDK is `com.clerk:clerk-android-api` (MIT, verified on Maven Central 2026-09-14; latest 1.1.6). Add the dependency above. Before first build, enable the Clerk **Native API** under "Native Applications" in the Clerk dashboard.

> Pin exact versions at scaffold time — re-verify each coordinate against Google Maven / Maven Central before the first build.

### Build Config

| Setting | Value |
|---------|-------|
| `minSdk` | 26 (Android 8.0) |
| `targetSdk` | 36 |
| `compileSdk` | 36 |
| Build tools | AGP 9.4.0 (Gradle 9.6.0, JDK 17+), Kotlin 2.4.20 — built into AGP 9.x |
| Language | Kotlin only |
| DI | Hilt |
| Async | Kotlin coroutines + Retrofit suspend functions |
| Serialization | kotlinx.serialization (JSON) |
| Image loading | Coil 3 (`io.coil-kt.coil3`) |
| Crash reporting | ACRA (Apache-2.0, optional) — no third-party analytics or telemetry |
| Client license | Apache-2.0 (FOSS — required by F-Droid) |

### CI/CD Plan

**CI (GitHub Actions):**
- `build.yml`: `gradle build` on PRs → fail-fast compile check
- `test.yml`: `gradle test` (unit) + `gradle connectedAndroidTest` (instrumented, on a GitHub Actions Android emulator)
- `lint.yml`: `ktlint --format` + `detekt` + Pixel 5 emulator smoke test
- `publish-apk.yml`: build a signed release APK; F-Droid builds from source and signs the final APK itself (no binary distribution to testers)

**F-Droid publishing:** the app is published to the F-Droid repository — F-Droid builds from source and signs the APK; internal QA shares a locally signed APK.

### F-Droid Compliance

- **Free license:** the `duo-android/` client must be released under a free license (e.g. Apache-2.0 or MIT); F-Droid will not accept proprietary or non-redistributable code.
- **Buildable from source:** F-Droid builds the APK from public source in a controlled environment — no closed build steps, no obfuscated blobs.
- **All bundled dependencies are FOSS/free:** Retrofit, OkHttp, Room, Hilt, Compose, Media3, Coil, and the Clerk Android SDK (MIT) are all free. Verify the Clerk SDK's transitive dependencies are FOSS at scaffold time.
- **No tracking or telemetry:** the client ships no analytics. Firebase Crashlytics/Performance Monitoring are not used; ACRA (Apache-2.0) is optional for local crash capture only.
- **Clerk:** the official Android SDK (`com.clerk:clerk-android-api`, MIT) is bundled and handles OAuth/PKCE, token refresh, and session storage — MIT is F-Droid-compatible. (F-Droid audits the client app, not the backend. Optionally, to make the whole stack FOSS, the backend's Clerk SaaS can be replaced with a self-hosted FOSS IdP such as Keycloak — not required for F-Droid acceptance.)

---

## 7. Phased Delivery Roadmap

| Phase | Name | Scope | Done Criteria | Est. Duration |
|-------|------|-------|---------------|---------------|
| 0 | Scaffold | Empty `duo-android` project: Compose, Hilt, Retrofit, Room, Navigation, auth wired, `/me` endpoint returns user | App launches, logs in, shows username from `/me` | 1 week |
| 1 | Content Browsing | `/courses`, `/course/:id` with unit tree; course selection (`POST /me/course`); Learn screen with locked/unlocked state | User browses courses, selects one, sees unit/lesson tree with correct completion state | 1 week |
| 2 | Lesson Engine | `/lesson/:id`, challenge rendering (SELECT/ASSIST), answer validation UI, `POST /challenge/:id/progress`; hearts decrement on wrong answer; points increment | Complete a 5-challenge lesson; hearts decrement on wrong, points increment on correct; lesson marks complete | 2 weeks |
| 3 | Offline | Room DB populated from API; serve course tree + lessons from Room; WorkManager sync; challenge progress queued offline | Kill network, complete lesson, restore network → progress appears in web app | 2 weeks |
| 4 | Audio | Download lesson audio via Media3 DownloadManager; ExoPlayer playback on option tap; caching with eviction | Audio plays for each challenge option; offline audio playback works | 1 week |
| 5 | Quests & Leaderboard | `/quests`, `/leaderboard` screens | Both screens render with real data | 1 week |
| 6 | Polish | Animations, error states, empty states, accessibility, R8/ProGuard minification, signing, F-Droid listing (icon/description/license), beta testing | All screens pass manual QA on 3 device form factors | 2 weeks |
| 7 | Launch | Client FOSS-licensed; submit to F-Droid (build-from-source, F-Droid signing); pass review | Live on F-Droid | 1 week |

**Total estimated duration:** 13 weeks (single developer) or ~8 weeks with 2 developers in parallel.

---

## 8. Risks, Unknowns & Backend Changes Required

### Must-Do Backend Changes

| # | Change | Where |
|---|--------|-------|
| 1 | Create `app/api/v1/*` route handlers (9 new endpoints) | Next.js app |
| 2 | Make course listing public (or add a public variant) | `app/api/v1/courses/route.ts` |
| 3 | Wire Clerk JWT auth into every `/api/v1/*` handler via `auth()` | Each route handler |
| 4 | Add `Cache-Control` headers appropriate for a mobile JSON API | Next.js middleware |

### Risks & Unknowns

| Risk | Likelihood | Impact | Mitigation |
|------|-----------|--------|-----------|
| Clerk Android SDK is still maturing (API churn) | Medium | Medium | Pin `clerk-android-api:1.1.6`; wrap all auth calls in a single auth module so the SDK is swappable if its API changes |
| Offline sync conflicts (web + mobile simultaneously) | Medium | Medium | Eventual consistency; last-write-wins for course selection; idempotent challenge completion |
| Room migration complexity as schema evolves | Medium | Medium | Version the DB carefully; migrate explicitly |
| Audio CDN cost at scale | Low | Low | Ogg Opus ~30KB/clip; immutable CDN caching |
| Compose transition performance | Medium | Low | Profile early; use `enterTransition`/`exitTransition` sparingly |
| Content quality of auto-generated exercises | High | Medium | Human review pass on the 20% highest-pedagogy exercises (see `content-sourcing.md` §6) |

### Verified Facts (registry queries, 2026-09-13)

- **Compose BOM:** `androidx.compose:compose-bom` — use `2026.06.01` (Compose 1.11.4) for `compileSdk 36`. The newer `2026.09.00` (Compose 1.12.1) requires `compileSdk 37`, which is not installed. First build verified against SDK 36 on 2026-09-14.
- **Clerk:** official Android SDK `com.clerk:clerk-android-api` / `clerk-android-ui` (MIT, repo `clerk/clerk-android`), latest 1.1.6 on Maven Central (verified 2026-09-14); requires the "Native API" enabled in the Clerk dashboard.
- **Hilt:** base framework is `com.google.dagger:hilt-android` (2.60.1 stable); `androidx.hilt:*` are separate integration libs (1.4.0).
- **AGP:** latest stable `9.4.0` (a `9.5.0-beta01` tag exists but is a beta). Requires **Gradle 9.6.0** and **JDK 17+**. Kotlin is built into AGP 9.x — do NOT apply `org.jetbrains.kotlin.android`.
- **Coil:** Coil 3 group is `io.coil-kt.coil3`; latest 3.6.2.
- **Compose compiler plugin:** `org.jetbrains.kotlin.plugin.compose`, bundled with Kotlin ≥2.0, applied with no explicit version; matches pinned Kotlin 2.4.20.
- **Lifecycle & OkHttp compileSdk pinning:** `androidx.lifecycle:lifecycle-*-compose:2.11.0` and `com.squareup.okhttp3:okhttp:5.5.0` require `compileSdk 37`. To compile against installed SDK 36, pinned `lifecycle-*-compose:2.10.0` (minCompileSdk 35) and `okhttp:5.4.0` (minCompileSdk 36). Verified build succeeds on 2026-09-14.
- **Retrofit kotlinx.serialization converter:** `com.squareup.retrofit2:converter-kotlinx-serialization:3.0.0` is the official Square converter (supersedes JakeWharton's separate library). In Kotlin, call `json.asConverterFactory(contentType)`.

---

## Appendix A: Existing API vs New API

| Endpoint | Existing | New (v1) |
|----------|----------|----------|
| `GET /api/courses` | Admin-only, returns raw `Course[]` | Public `GET /api/v1/courses` → `Course[]` |
| `GET /api/courses/[id]` | Admin-only | `GET /api/v1/course/:id` → `CourseDetail` with progress |
| `GET /api/units` | Admin-only | *(folded into course detail)* |
| `GET /api/lessons` | Admin-only | *(folded into course detail)* |
| `GET /api/lessons/[id]` | Admin-only | `GET /api/v1/lesson/:id` → `Lesson` with challenges + options |
| `GET /api/challenges` | Admin-only | *(folded into lesson)* |
| `GET /api/challenges/[id]` | Admin-only | *(folded into lesson)* |
| `GET /api/challengeOptions` | Admin-only | *(folded into challenge)* |
| `GET /api/challengeOptions/[id]` | Admin-only | *(folded into challenge)* |
| *(none)* | *(none)* | `GET/POST /api/v1/me` |
| *(none)* | *(none)* | `GET /api/v1/me/progress` |
| *(none)* | *(none)* | `POST /api/v1/challenge/:id/progress` |
| *(none)* | *(none)* | `GET/POST /api/v1/hearts` |
| *(none)* | *(none)* | `GET /api/v1/quests` |
| *(none)* | *(none)* | `GET /api/v1/leaderboard` |

The new API aggregates nested data (course → units → lessons → challenges → options) into single responses to reduce round trips — critical for mobile data efficiency.

*Document ends. Generated 2026-09-13.*


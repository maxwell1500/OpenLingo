import java.util.Properties
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.devtools.ksp")
}

// Release signing: credentials live ONLY in a local (gitignored)
// keystore.properties or in CI environment properties — never in source.
// Declared at the top level, not inside `android { }`, so the task-graph guard
// at the bottom of this file can read it too.
val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties().apply {
    if (keystorePropertiesFile.exists()) {
        keystorePropertiesFile.inputStream().use { load(it) }
    }
}
val hasReleaseKey = keystorePropertiesFile.exists() &&
    keystoreProperties.getProperty("storePassword") != null &&
    keystoreProperties.getProperty("keyPassword") != null

// Opt-in escape hatch for local throwaway release builds. Deliberately NOT read
// from an environment variable or from gradle.properties: it has to be typed on
// the command line by whoever wants a debug-signed release. Note that Gradle
// sets a bare `-PallowDebugSigning` to the empty string, so mere presence is
// the signal and only an explicit "false" turns it back off.
val allowDebugSigningFlag =
    (project.findProperty("allowDebugSigning") as String?)?.trim()?.lowercase()
val allowDebugSigning =
    allowDebugSigningFlag != null && allowDebugSigningFlag !in setOf("false", "no", "0")

android {
    namespace = "com.duo.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.duo.app"
        minSdk = 26
        targetSdk = 36
        versionCode = 4
        versionName = "1.3.0"
    }

    signingConfigs {
        create("release") {
            if (hasReleaseKey) {
                storeFile = rootProject.file(
                    keystoreProperties.getProperty("storeFile", "keystore/openlingo-release.jks"),
                )
                storePassword = keystoreProperties.getProperty("storePassword")
                keyAlias = keystoreProperties.getProperty("keyAlias", "openlingo")
                keyPassword = keystoreProperties.getProperty("keyPassword")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            // With a keystore this is the release key. Without one it is the debug
            // key, which the guard below only permits when a developer asks for it
            // explicitly with -PallowDebugSigning — never silently.
            signingConfig = if (hasReleaseKey) {
                signingConfigs.getByName("release")
            } else {
                signingConfigs.getByName("debug")
            }
        }
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }
}

// A debug-signed release is the worst possible failure mode: same artifact name,
// same output path, green build, no warning — and it is only discovered by
// whoever installs it. So a release artifact is refused outright unless a
// keystore is configured, and the debug fallback is reachable only by opt-in.
//
// The check hangs off the task graph rather than plain configuration because
// :app:assembleDebug and :app:test configure this very same release build type
// and must keep working on a machine that has no keystore at all. Only the tasks
// that actually package a release artifact trip it.
// Note: taskGraph.whenReady is not configuration-cache compatible. The cache is
// off today; if it is ever enabled, move this guard to a doFirst on the release
// packaging tasks.
val releasePackagingTask = Regex("^:app:(assemble|bundle|package)Release$")
gradle.taskGraph.whenReady {
    val releaseRequested = allTasks.any { task -> releasePackagingTask.matches(task.path) }
    if (releaseRequested && !hasReleaseKey) {
        if (!allowDebugSigning) {
            throw GradleException(
                """
                |Refusing to build a release artifact: no release signing key is configured.
                |
                |duo-android/keystore.properties is missing or incomplete, and a missing
                |keystore must never quietly produce a debug-signed "release". The artifact
                |would keep the same name and path (app-release.apk), so the wrong signature
                |would only surface when somebody installs it.
                |
                |Create duo-android/keystore.properties (gitignored — never commit it, and
                |never commit the .jks). All four keys are required; storeFile is resolved
                |relative to duo-android/:
                |
                |    storeFile=keystore/openlingo-release.jks
                |    storePassword=<keystore password>
                |    keyAlias=openlingo
                |    keyPassword=<key password>
                |
                |See CONTRIBUTING.md, "Creating a release keystore", for the keytool command.
                |
                |If you only want a throwaway local artifact signed with the local DEBUG key
                |— which is NOT distributable and must never be uploaded or shared — ask for
                |it explicitly:
                |
                |    ./gradlew :app:assembleRelease -PallowDebugSigning
                |
                |:app:assembleDebug and :app:test need no keystore and are unaffected.
                """.trimMargin(),
            )
        }
        logger.warn(
            """
            |================================================================
            | WARNING: DEBUG-SIGNED RELEASE ARTEFACT — NOT DISTRIBUTABLE
            |================================================================
            |duo-android/keystore.properties is missing, so -PallowDebugSigning
            |was honoured and this release build is signed with the LOCAL DEBUG
            |KEY. It cannot be uploaded to Google Play and must not be shared
            |or installed by anyone else — two machines' debug keys differ, so
            |updates over it will fail.
            |
            |Set up duo-android/keystore.properties and re-run without the flag
            |for anything you intend to distribute.
            |================================================================
            """.trimMargin(),
        )
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2026.06.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.activity:activity-compose:1.13.0")
    debugImplementation("androidx.compose.ui:ui-tooling")

    // Lifecycle & Coroutines
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.10.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.10.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.10.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")

    // Serialization (backup JSON export/import)
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.11.0")

    // Room Database (Local Persistence)
    implementation("androidx.room:room-runtime:2.8.5")
    implementation("androidx.room:room-ktx:2.8.5")
    ksp("androidx.room:room-compiler:2.8.5")

    // Media3 (ExoPlayer for Audio & Speech Playback)
    implementation("androidx.media3:media3-exoplayer:1.5.1")
    // Unit tests (local JVM: JUnit + Robolectric + Turbine)
    testImplementation("junit:junit:4.13.2")
    testImplementation("androidx.room:room-testing:2.8.5")
    testImplementation("androidx.test:core:1.7.0")
    testImplementation("org.robolectric:robolectric:4.17")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
    testImplementation("app.cash.turbine:turbine:1.2.1")
    implementation("androidx.media3:media3-common:1.5.1")
}

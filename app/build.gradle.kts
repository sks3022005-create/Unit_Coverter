plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    // Uploads builds to Firebase App Distribution (used by CI on every push)
    alias(libs.plugins.firebase.appdistribution)
}

// Only apply the google-services plugin when a config file is present. This keeps
// local/CI builds working without Firebase, and enables the in-app "new build
// available" prompts once google-services.json is added to this module.
val hasGoogleServices = file("google-services.json").exists()
if (hasGoogleServices) {
    apply(plugin = libs.plugins.google.services.get().pluginId)
}

android {
    namespace = "com.example.unit_coverter"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.example.unit_coverter"
        minSdk = 24
        targetSdk = 36
        // CI passes an incrementing VERSION_CODE (the GitHub run number) so each
        // distributed build is newer than the last and installs as an update.
        versionCode = (System.getenv("VERSION_CODE") ?: "1").toInt()
        versionName = System.getenv("VERSION_NAME") ?: "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        // Release keystore is supplied by the CI environment (see .github/workflows
        // and SETUP_FIREBASE.md). Every build is signed with the SAME key so new
        // versions install *over* the previous one on the device as an update.
        create("release") {
            val storeFilePath = System.getenv("RELEASE_STORE_FILE")
            if (storeFilePath != null) {
                storeFile = file(storeFilePath)
                storePassword = System.getenv("RELEASE_STORE_PASSWORD")
                keyAlias = System.getenv("RELEASE_KEY_ALIAS")
                keyPassword = System.getenv("RELEASE_KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // Use the release keystore when CI provides it; otherwise fall back to
            // the debug key so local `assembleRelease` still works.
            signingConfig = if (System.getenv("RELEASE_STORE_FILE") != null) {
                signingConfigs.getByName("release")
            } else {
                signingConfigs.getByName("debug")
            }
        }
    }

    // Firebase App Distribution: which app + credentials to publish to, and who
    // receives the build. Values come from CI env vars (kept out of source).
    firebaseAppDistribution {
        appId = System.getenv("FIREBASE_APP_ID") ?: ""
        serviceCredentialsFile = System.getenv("FIREBASE_SERVICE_CREDENTIALS_FILE") ?: ""
        groups = System.getenv("FIREBASE_TESTER_GROUPS") ?: "testers"
        releaseNotes = System.getenv("RELEASE_NOTES") ?: "Automated build from CI"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // Compose BOM — versions for all androidx.compose.* pulled from here
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Hilt DI
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // DataStore
    implementation(libs.androidx.datastore.preferences)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Persistent collections (stable snapshots for Compose)
    implementation(libs.kotlinx.collections.immutable)

    // Serialization (type-safe nav routes)
    implementation(libs.kotlinx.serialization.json)

    // Glance (home screen widget + Material3 theming)
    implementation(libs.androidx.glance.appwidget)
    implementation(libs.androidx.glance.material3)

    // Play Billing (premium unlock)
    implementation(libs.billing.ktx)

    // Firebase App Distribution in-app updates — only linked when Firebase is
    // configured (google-services.json present), so builds without it are unaffected.
    if (hasGoogleServices) {
        implementation(platform(libs.firebase.bom))
        implementation(libs.firebase.appdistribution)
    }

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
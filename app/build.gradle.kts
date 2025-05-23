plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.22"
    // id ("dagger.hilt.android.plugin")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.mKanta.archivemaps"
    compileSdk = 35

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.mKanta.archivemaps"
        minSdk = 27
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        // manifestPlaceholders = [ "GOOGLE_MAPS_API_KEY": project.findProperty("GOOGLE_MAPS_API_KEY") ?: "" ]
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        manifestPlaceholders["GOOGLE_MAPS_API_KEY"] =
            findProperty("GOOGLE_MAPS_API_KEY") as? String ?: ""
        buildConfigField("String", "OPENAI_API_KEY", "\"${findProperty("OPENAI_API_KEY")}\"")
        buildConfigField("String", "SUPABASE_API_KEY", "\"${findProperty("SUPABASE_API_KEY")}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
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

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    implementation(libs.play.services.maps)
    implementation(libs.maps.compose)

    implementation(libs.androidx.datastore.preferences)
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
    implementation("io.coil-kt:coil-compose:2.7.0")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")

    implementation("com.squareup.moshi:moshi:1.15.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.15.0")

    implementation(libs.play.services.location)

    implementation("com.google.dagger:hilt-android:2.51.1")

    ksp("com.google.dagger:hilt-android-compiler:2.56.1")
    ksp("com.squareup.moshi:moshi-kotlin-codegen:1.15.0")

    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")

    implementation("com.github.takusemba:spotlight:2.0.5")
    implementation("com.canopas.intro-showcase-view:introshowcaseview:2.0.1")

    implementation("androidx.core:core-splashscreen:1.0.1")

    implementation(platform("io.github.jan-tennert.supabase:bom:3.1.4"))
    implementation("io.github.jan-tennert.supabase:postgrest-kt")
    implementation("io.github.jan-tennert.supabase:auth-kt")
    implementation("io.github.jan-tennert.supabase:realtime-kt")

    implementation("io.ktor:ktor-client-android:3.1.3")
    implementation("io.github.jan-tennert.supabase:serializer-moshi:3.1.4")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

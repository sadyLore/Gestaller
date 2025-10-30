plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.gestaller"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.gestaller"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
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
}

dependencies {
    // --- ROOM (Base de datos local) ---
    implementation("androidx.room:room-runtime:2.6.1")
    implementation(libs.firebase.storage)
    implementation(libs.core.ktx)
    annotationProcessor("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

    // --- Glide (para cargar imágenes) ---
    implementation(libs.glide)

    // --- LIFECYCLE (ViewModel y LiveData) ---
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.6")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.6")

    // --- Material Design y componentes base ---
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // --- Para ActivityResultLauncher en Kotlin (necesario para la cámara) ---
    implementation("androidx.activity:activity-ktx:1.8.0")  // Agregado: Para registerForActivityResult

    // --- Opcional: Para fragments si usas uno para la cámara ---
    // implementation("androidx.fragment:fragment-ktx:1.6.2")

    // --- Testing ---
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}
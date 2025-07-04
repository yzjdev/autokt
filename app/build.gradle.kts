plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "u.dev.autokt"
    compileSdk = 35

    defaultConfig {
        applicationId = "u.dev.autokt"
        minSdk = 26
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


    viewBinding.enable = true
}

dependencies {

    implementation ("com.tencent:mmkv:2.2.2")
    // ViewModel 和 LiveData
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.0")  // ViewModel
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.6.0")   // LiveData
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.6.0")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")  // Kotlin协程



    implementation ("me.zhanghai.android.appiconloader:appiconloader:1.5.0")
    implementation ("com.github.getActivity:XXPermissions:23.0")
// https://mvnrepository.com/artifact/androidx.preference/preference-ktx
    implementation("androidx.preference:preference-ktx:1.2.1")
    val nav_version = "2.9.0"
    // Views/Fragments integration
    implementation("androidx.navigation:navigation-fragment-ktx:$nav_version")
    implementation("androidx.navigation:navigation-ui-ktx:$nav_version")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.topmovies"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.example.topmovies"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        val props = Properties()
        props.load(rootProject.file("local.properties").inputStream())
        buildConfigField("String", "TMDB_API_KEY", "\"${props.getProperty("tmdb.api_key", "")}\"")
        buildConfigField("String", "DB_NAME", "\"movies.db\"")
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.androidx.recyclerview)
    implementation(libs.glide)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    implementation(libs.room.rxjava3)
    implementation(libs.rxjava)
    implementation(libs.rxandroid)
    implementation(libs.retrofit.rxjava3)
    ksp(libs.room.compiler)
}

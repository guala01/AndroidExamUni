plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
}

android {
    namespace = "com.example.chatbotapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.chatbotapp"
        minSdk = 28
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation("com.squareup.picasso:picasso:2.8")
    implementation("com.android.volley:volley:1.2.1")
    implementation("com.google.android.material:material:1.4.0")
    implementation("com.google.android.flexbox:flexbox:3.0.0")
    modules {
        module("com.google.android:flexbox") {
            replacedBy("com.google.android.flexbox:flexbox")
        }
    }

    implementation("com.github.stfalcon-studio:Chatkit:0.4.1") {
        exclude(group= "com.google.android", module= "flexbox")
    }
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
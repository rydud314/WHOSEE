plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.google.gms.google-services")
    id("kotlin-kapt")  // 추가된 부분
}

android {
    namespace = "com.example.seesaw"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.seesaw"
        minSdk = 24
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    viewBinding {
        enable = true
    }

    packaging {
        resources {
            excludes += "META-INF/DEPENDENCIES"
            excludes += "META-INF/LICENSE"
            excludes += "META-INF/LICENSE.txt"
            excludes += "META-INF/license.txt"
            excludes += "META-INF/NOTICE"
            excludes += "META-INF/NOTICE.txt"
            excludes += "META-INF/notice.txt"
            excludes += "META-INF/ASL2.0"
            excludes += "META-INF/LGPL2.1"
            //excludes += "META-INF/INDEX.LIST"
        }
    }
}

dependencies {
    // Google HTTP Client Library for Jackson2
    implementation("com.google.http-client:google-http-client-jackson2:1.42.3")

    // Google API Client 라이브러리 및 Auth 라이브러리
    implementation("com.google.api-client:google-api-client:1.32.1")
    implementation("com.google.auth:google-auth-library-oauth2-http:1.17.0")

    // OkHttp 라이브러리
    implementation("com.squareup.okhttp3:okhttp:4.10.0")

    // JSON 처리 라이브러리
    implementation("org.json:json:20210307")

    // Firebase 관련 라이브러리
    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation(platform("com.google.firebase:firebase-bom:33.0.0"))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-database-ktx")
    implementation("com.google.firebase:firebase-dynamic-links-ktx")

    // Google Calendar API 관련 라이브러리
    //implementation ("com.google.api-client:google-api-client:2.7.0")
    implementation("com.google.api-client:google-api-client-android:2.2.0")
    implementation ("com.google.oauth-client:google-oauth-client-jetty:1.34.1")
    implementation ("com.google.apis:google-api-services-calendar:v3-rev20220715-2.0.0")
    implementation ("com.google.android.gms:play-services-auth:21.2.0")

    // 기타 라이브러리
    implementation("com.google.zxing:core:3.3.0")
    implementation("com.journeyapps:zxing-android-embedded:3.6.0")
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation(libs.androidx.appcompat)
    implementation(libs.firebase.storage.ktx)
    implementation(libs.androidx.monitor)
    implementation(libs.androidx.junit.ktx)
    testImplementation("junit:junit:4.12")
    androidTestImplementation("junit:junit:4.12")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    implementation("com.google.android.material:material:1.8.0")

    // cameraX
    val camerax_version = "1.1.0-beta01"
    implementation("androidx.camera:camera-core:$camerax_version")
    implementation("androidx.camera:camera-camera2:$camerax_version")
    implementation("androidx.camera:camera-lifecycle:$camerax_version")
    implementation("androidx.camera:camera-video:$camerax_version")
    implementation("androidx.camera:camera-view:$camerax_version")
    implementation("androidx.camera:camera-extensions:$camerax_version")

    // Glide 추가된 부분
    implementation("com.github.bumptech.glide:glide:4.12.0")
    kapt("com.github.bumptech.glide:compiler:4.12.0")

    implementation("androidx.cardview:cardview:1.0.0")


    // ML Kit for Text Recognition To recognize Latin script
    implementation("com.google.mlkit:text-recognition:16.0.0")
    // To recongnize Korean script
    implementation("com.google.mlkit:text-recognition-korean:16.0.1")


//    // Google Vision API 및 gRPC 관련 의존성
//    implementation("io.grpc:grpc-okhttp:1.54.0")
//    implementation("com.google.api:gax-grpc:2.0.0")
//
//    implementation("com.google.auth:google-auth-library-oauth2-http:1.4.0")
//    implementation("io.grpc:grpc-auth:1.42.0")
//    implementation("com.google.cloud:google-cloud-vision:1.100.0")
//
//    // 추가된 필요 의존성
//    implementation("io.grpc:grpc-core:1.42.0")
//    implementation("io.grpc:grpc-stub:1.42.0")
//    implementation("com.google.protobuf:protobuf-java:3.17.3")
}
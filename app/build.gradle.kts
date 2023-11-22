import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    kotlin("kapt")
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android.gradle.plugin)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.safe.args)
}
apply(from = "../build-tasks.gradle.kts")
apply(plugin = "org.jetbrains.kotlin.android")

android {
    namespace = libs.versions.applicationId.get()
    compileSdk = libs.versions.compileSdk.get().toInt()
    buildToolsVersion = "29.0.2"

    defaultConfig {
        renderscriptTargetApi = 29
        renderscriptSupportModeEnabled = true
        vectorDrawables {
            useSupportLibrary = true
        }

        applicationId = libs.versions.applicationId.get()
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = libs.versions.versionCode.get().toInt()

        val major = libs.versions.major.get().toInt()
        val minor = libs.versions.minor.get().toInt()
        val buildNumber = libs.versions.buildNumber.get().toInt()
        versionName = "$major.$minor($buildNumber)"

        archivesName.set("${rootProject.name}-$versionName")

        testInstrumentationRunner = libs.versions.testRunner.get()
    }

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kapt {
    correctErrorTypes = true

    javacOptions {
        // Increase the max count of errors from annotation processors.
        // Default is 100.
        option("-Xmaxerrs", 500)
    }
}

dependencies {
    implementation(project(path = ":domain"))
    implementation(project(path = ":data"))

    // test libs
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.junit)

    // android libs
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)

    // serialization
    implementation(libs.kotlin.serilization.json)

    // hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    implementation(libs.hilt.work)

    // room
    implementation(libs.room.runtime)

    // retrofit
    implementation(libs.retrofit)

    // coroutines
    implementation(libs.coroutines.android)

    // better link movement method
    implementation(libs.better.link.movement.method)

    //phone number
    implementation(libs.phone.number)

    // model watcher
    implementation(libs.mvi.core.diff)

    // seismic
    implementation(libs.seismic)

    // firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)

    // work manager
    implementation(libs.work.manager)

    // permissionx
    implementation(libs.permissionx)
}

// region mini ci-cd helper methods
@Suppress("UNCHECKED_CAST")
val sendApkToTelegram = extra["sendApkToTelegram"] as (File, String) -> Unit

fun buildAndSendApkToTelegram(variantName: String, message: String) {
    val variant = android.applicationVariants.firstOrNull { it.name == variantName }
    val apk = variant?.outputs?.firstOrNull()?.outputFile
    if (apk?.exists() == true) {
        sendApkToTelegram(apk, message)
    } else {
        throw GradleException("APK not found for variant $variantName")
    }
}
// endregion

// TODO: you might need to change this to match your build variant, for example: arrayOf("prodDebug", "devDebug")
val variants = arrayOf("debug")

// HOW TO USE:
// make sure you updated variants array above, and chatId in build-tasks.gradle.kts
// then type command bellow in android studio's terminal followed by (ctr/command)+enter
// -----------windows-----------
// ./gradlew buildAndSendToTelegram
// -----------macos/linux-----------
// gradlew buildAndSendToTelegram
//
// you can add optional message to the builds by adding -Pm="your message" to the command

tasks.register<DefaultTask>("buildAndSendToTelegram") {
    dependsOn(
        "incrementBuildNumber",
        variants.map { variant -> "assemble${variant.replaceFirstChar { it.uppercaseChar() }}" }
    )
    val message = project.findProperty("m") as? String ?: ""
    doLast {
        // Loop over the array and call the function for each variant
        variants.forEach { variant ->
            buildAndSendApkToTelegram(variant, message)
        }
    }
}
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        // Force Kotlin compiler to 2.4.0 so it can read newer Firebase metadata
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.4.0")
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.google.services) apply false
}
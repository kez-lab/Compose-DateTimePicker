import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.vanniktech.maven)
    alias(libs.plugins.kotlinx.serialization)
}

group = "io.github.kez-lab"
version = "0.3.0"

kotlin {
    jvmToolchain(17)
    
    androidTarget {
        publishLibraryVariants("release")
        compilerOptions {
            jvmTarget = JvmTarget.fromTarget("17")
        }
    }
    
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    
    jvm("desktop")
    
    js(IR) {
        browser()
    }
    
    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.coroutines.core)
        }
        
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
        
        androidMain.dependencies {
            // Android-specific dependencies if needed
        }
        
        iosMain.dependencies {
            // iOS-specific dependencies if needed
        }
        
        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.common)
            }
        }
        
        val jsMain by getting {
            dependencies {
                implementation(compose.web.core)
            }
        }
    }
}

android {
    namespace = "com.kez.picker"
    compileSdk = 35
    
    defaultConfig {
        minSdk = 24
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    
    buildFeatures {
        compose = true
    }
    
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.get()
    }
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    signAllPublications()

    coordinates("io.github.kez-lab", "compose-date-time-picker", "0.2.0")

    pom {
        name = "Compose-DateTimePicker"
        description = "Compose Multiplatform DateTimePicker library supporting Android, iOS, Desktop and Web"
        url = "https://github.com/kez-lab/Compose-DateTimePicker"
        inceptionYear = "2024"

        licenses {
            license {
                name = "The Apache License, Version 2.0"
                url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
            }
        }
        developers {
            developer {
                id = "KwakEuiJin"
                name = "KEZ"
                url = "https://github.com/kez-lab"
            }
        }

        scm {
            url.set("https://github.com/kez-lab/Compose-DateTimePicker")
            connection.set("scm:git:git://github.com/kez-lab/Compose-DateTimePicker.git")
            developerConnection.set("scm:git:ssh://git@github.com/kez-lab/Compose-DateTimePicker.git")
        }
    }
} 
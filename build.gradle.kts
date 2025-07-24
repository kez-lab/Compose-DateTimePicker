// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.multiplatform) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.compose) apply false
    alias(libs.plugins.compose.compiler) apply false
}

tasks.register("printVersion") {
    doLast {
        val version = project.findProperty("VERSION_NAME")
            ?: throw IllegalArgumentException("VERSION_NAME property not found")
        println(version)
    }
}
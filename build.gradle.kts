plugins {
    //TODO: Remove it or update to a newer version
//    id("com.autonomousapps.dependency-analysis") version "3.5.1"

    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.androidKmpLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotest) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.gmazzoBuildconfig) apply false
}

//TODO: Remove it or update to a newer version
//subprojects {
//    apply(plugin = "com.autonomousapps.dependency-analysis")
//}

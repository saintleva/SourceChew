import org.gradle.kotlin.dsl.implementation
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
     alias(libs.plugins.kotlinMultiplatform)
     alias(libs.plugins.kotlinxSerialization)
     alias(libs.plugins.androidKmpLibrary)
     alias(libs.plugins.composeMultiplatform)
     alias(libs.plugins.composeCompiler)
}

kotlin {
    jvmToolchain(17)

    androidLibrary {
        androidResources {
            enable = true
        }

        namespace = "com.github.saintleva.sourcechew.composeapp"
        minSdk = libs.versions.android.minSdk.get().toInt()
        compileSdk = libs.versions.android.compileSdk.get().toInt()

        packaging {
            resources {
                excludes += "/META-INF/{AL2.0,LGPL2.1}"
            }
        }
    }

    applyDefaultHierarchyTemplate()

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    jvm("jvmDesktop")
    
    js {
        browser()
        binaries.executable()
    }
    
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }
    
    sourceSets {
        val jvmMain by creating {
            dependsOn(commonMain.get())
            dependencies {
                implementation(libs.ktor.client.cio)
            }
        }

        getByName("androidMain") {
            dependsOn(jvmMain)
            dependencies {
                implementation(libs.androidx.compose.ui.tooling.preview)
                implementation(libs.androidx.activity.compose)

                implementation(libs.koin.android)
                implementation(libs.koin.androidx.compose)
            }
        }

        getByName("commonMain") {
            dependencies {
                implementation(libs.androidx.compose.runtime)
                implementation(libs.androidx.compose.foundation)
                implementation(libs.androidx.compose.material3)
                implementation(libs.androidx.compose.ui)
                implementation(libs.androidx.compose.components.resources)
                //TODO: remove this
                //implementation("org.jetbrains.compose.ui:ui-tooling-preview:1.10.0")
                implementation(libs.androidx.lifecycle.viewmodel)
                implementation(libs.androidx.lifecycle.runtime.compose)
                implementation(libs.compose.material.icons.core)

                implementation(libs.datastore.preferences.core)
                implementation(libs.napier)

                api(libs.koin.core)
                implementation(libs.koin.compose)
                implementation(libs.koin.compose.viewmodel)

                implementation(libs.androidx.navigation3.ui)
                implementation(libs.androidx.navigation3.material3.adaptive)
                implementation(libs.androidx.lifecycle.viewmodel.nav3)

                implementation(libs.androidx.paging.common)
                implementation(libs.androidx.paging.compose)

                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.client.auth)
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(libs.ktor.client.logging)
            }
        }

        getByName("commonTest") {
            dependencies {
                implementation(libs.kotest.framework.engine)
                implementation(libs.kotest.assertions.core)
            }
        }

        getByName("jvmDesktopMain") {
            dependsOn(jvmMain)
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(libs.kotlinx.coroutines.swing)
                implementation(libs.appdirs)
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "com.github.saintleva.sourcechew.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.github.saintleva.sourcechew"
            packageVersion = "1.0.0"
        }
    }
}

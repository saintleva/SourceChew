import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.androidKmpLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotest)
    alias(libs.plugins.ksp)
    alias(libs.plugins.buildkonfig)
    //TODO: Use or remove this
//    alias(libs.plugins.kotzilla)
    alias(libs.plugins.koinCompiler)
}

kotlin {
    jvmToolchain(21)

    android {
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
        browser {
            commonWebpackConfig {
                devServer?.proxy = mutableListOf()
                cssSupport {
                    enabled = true
                }
            }
        }
        binaries.executable()
    }
    
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }
    
    sourceSets {
        getByName("commonMain") {
            dependencies {
                implementation(libs.kotlinx.datetime)

                //TODO: Use this implementations
//                implementation(libs.androidx.compose.runtime)
//                implementation(libs.androidx.compose.foundation)
//                implementation(libs.androidx.compose.material3)
//                implementation(libs.androidx.compose.ui)
//                implementation(libs.androidx.compose.components.resources)

                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.components.resources)

                implementation(libs.androidx.lifecycle.viewmodel)
                implementation(libs.androidx.lifecycle.runtime.compose)
                implementation(libs.compose.material.icons.core)
                implementation(libs.compose.material.icons.extended)

                implementation(libs.datastore.core)
                implementation(libs.datastore.core.okio)
                implementation(libs.datastore.preferences.core)
                implementation(libs.okio)

                implementation(libs.napier)

                api(libs.koin.core)
                implementation(libs.koin.compose)
                implementation(libs.koin.compose.viewmodel)

                //TODO: Use or remove this
//                implementation(libs.kotzilla.sdk.compose)

                implementation(libs.androidx.navigation3.ui)
                implementation(libs.androidx.navigation3.material3.adaptive)
                implementation(libs.androidx.lifecycle.viewmodel.nav3)

                implementation(libs.paginator.offset)
                implementation(libs.paginator.compose.offset)

                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.client.auth)
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(libs.ktor.client.logging)

                implementation(libs.multiplatform.settings)

                implementation(libs.cmp.clipboard)
            }
        }

        getByName("commonTest") {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.kotest.framework.engine)
                implementation(libs.kotest.assertions.core)
                implementation(libs.koin.test)
                implementation(libs.kotlinx.coroutines.test)
                implementation(libs.kotest.extensions.koin)
            }
        }

        val jvmMain by creating {
            dependsOn(commonMain.get())
            dependencies {
                implementation(libs.ktor.client.okhttp)
            }
        }

        val ksafeMain by creating {
            dependsOn(commonMain.get())
            dependencies {
                implementation(libs.ksafe)
            }
        }

        getByName("androidMain") {
            dependsOn(jvmMain)
            dependsOn(ksafeMain)
            dependencies {
                implementation(libs.androidx.compose.ui.tooling.preview)
                implementation(libs.androidx.activity.compose)

                implementation(libs.koin.android)
                implementation(libs.koin.androidx.compose)

                implementation(libs.androidx.security.crypto.ktx)
                implementation(libs.security.crypto.datastore.preferences)
            }
        }

        getByName("jvmDesktopMain") {
            dependsOn(jvmMain)
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(libs.kotlinx.coroutines.swing)
                implementation(libs.appdirs)
                implementation(libs.java.keyring)
            }
        }

        getByName("jvmDesktopTest") {
            dependencies {
                implementation(libs.kotest.runner.junit5)
                implementation(libs.junit.platform.launcher)
            }
        }

        val iosMain by creating {
            dependsOn(commonMain.get())
            dependencies {
                implementation(libs.ktor.client.darwin)
            }
        }
        getByName("iosArm64Main") { dependsOn(iosMain) }
        getByName("iosSimulatorArm64Main") { dependsOn(iosMain) }

        val iosTest by creating {
            dependsOn(getByName("commonTest"))
        }
        getByName("iosArm64Test") { dependsOn(iosTest) }
        getByName("iosSimulatorArm64Test") { dependsOn(iosTest) }

        val webMain by creating {
            dependsOn(commonMain.get())
            dependencies {
                implementation(libs.ktor.client.js)
            }
        }

        getByName("jsMain") {
            dependsOn(webMain)
            dependencies {
                implementation(devNpm("path-browserify", "1.0.1"))
            }
        }

        getByName("wasmJsMain") {
            dependsOn(webMain)
            dependsOn(ksafeMain)
        }
    }
}

dependencies {
    commonMainImplementation(platform(libs.paginator.bom))
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    filter {
        isFailOnNoMatchingTests = true
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

val isRelease: Boolean
    get() = project.hasProperty("release") ||
            project.gradle.startParameter.taskNames.any {
                it.contains("Release", ignoreCase = true) || it.contains("Distribution")
            }

buildkonfig {
    //TODO: Try to use normal packageName property without this boilerplate
    val pkgName = "com.github.saintleva.sourcechew"
    packageName = pkgName
    defaultConfigs {
        buildConfigField(Type.STRING, "APPLICATION_NAME", "SourceChew")
        buildConfigField(Type.STRING, "APPLICATION_AUTHOR", "saintleva")
        buildConfigField(Type.STRING, "PACKAGE_NAME", pkgName)
        buildConfigField(Type.BOOLEAN, "IS_DEBUG", (!isRelease).toString())
    }
}

//TODO: Use or remove this
//kotzilla {
//    versionName = "1.0.0" // add your app version name
//    composeInstrumentation = false
//}

//TODO: Remove this
//kotlin.sourceSets.all {
//    println("KMP sourceSet: $name")
//}
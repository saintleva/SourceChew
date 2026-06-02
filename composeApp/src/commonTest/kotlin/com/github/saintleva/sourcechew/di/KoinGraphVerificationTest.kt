package com.github.saintleva.sourcechew.di

import io.kotest.core.spec.style.FunSpec
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.module
import org.koin.test.verify.verify

class KoinGraphVerificationTest : FunSpec({

    test("Verify Koin Dependency Graph") {
        @OptIn(KoinExperimentalAPI::class)
        module {
            includes(appModule, domainModule, dataModule, platformModule)
        }.verify(
            extraTypes = platformExtraTypes
        )
    }
})

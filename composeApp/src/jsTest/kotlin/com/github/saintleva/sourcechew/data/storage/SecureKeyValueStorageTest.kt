package com.github.saintleva.sourcechew.data.storage

import com.github.saintleva.sourcechew.data.secure.SecureKeyValueStorage
import com.github.saintleva.sourcechew.di.platformModule
import io.kotest.core.spec.style.FunSpec
import io.kotest.koin.KoinExtension
import io.kotest.matchers.shouldBe
import org.koin.mp.KoinPlatform.getKoin
import org.koin.test.KoinTest
import org.koin.test.inject


class JsSecureKeyValueStorageTest : AbstractSecureKeyValueStorageTest(platformModule)
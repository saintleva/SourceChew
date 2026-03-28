package com.github.saintleva.sourcechew

import androidx.test.platform.app.InstrumentationRegistry
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.runner.junit4.KotestTestRunner
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
//@RunWith(AndroidJUnit4::class)


@RunWith(KotestTestRunner::class)
class ExampleInstrumentedTest : FunSpec({

    test("Use application context") {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        appContext.packageName shouldBe "com.github.saintleva.sourcechew.android"
    }
})
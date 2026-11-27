package com.github.saintleva.sourcechew.domain.coroutines

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.async
import kotlinx.coroutines.delay

class KeyedMutexTest : FunSpec({

    test("executes sequentially for identical keys") {
        val keyedMutex = KeyedMutex<String>()
        val log = mutableListOf<String>()

        val job1 = async {
            keyedMutex.withLock("keyA") {
                log.add("start-1")
                delay(100)
                log.add("end-1")
            }
        }

        val job2 = async {
            delay(10)
            keyedMutex.withLock("keyA") {
                log.add("start-2")
                log.add("end-2")
            }
        }

        job1.await()
        job2.await()

        log shouldBe listOf("start-1", "end-1", "start-2", "end-2")
    }

    test("executes concurrently for distinct keys") {
        val keyedMutex = KeyedMutex<String>()
        val log = mutableListOf<String>()

        val job1 = async {
            keyedMutex.withLock("keyA") {
                log.add("start-A")
                delay(100)
                log.add("end-A")
            }
        }

        val job2 = async {
            keyedMutex.withLock("keyB") {
                log.add("start-B")
                delay(100)
                log.add("end-B")
            }
        }

        job1.await()
        job2.await()

        log.first() shouldBe "start-A"
        log[1] shouldBe "start-B"
    }

    test("releases lock on exception") {
        val keyedMutex = KeyedMutex<String>()
        var acquired = false

        runCatching {
            keyedMutex.withLock("keyA") {
                error("Fail")
            }
        }

        keyedMutex.withLock("keyA") {
            acquired = true
        }

        acquired shouldBe true
    }
})

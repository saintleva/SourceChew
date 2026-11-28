package com.github.saintleva.sourcechew.data.network

import com.github.saintleva.sourcechew.domain.result.CommonApiError
import com.github.saintleva.sourcechew.domain.result.DetailsError
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf

class ParseCommonApiErrorTest : FunSpec({

    test("maps 401 Unauthorized to CommonApiError.Unauthorized") {
        val client = HttpClient(MockEngine) {
            engine {
                addHandler {
                    respond("{}", HttpStatusCode.Unauthorized)
                }
            }
        }
        val response: HttpResponse = client.get("https://api.github.com/test")
        BaseKtorApiService.mapCommonApiError(response) shouldBe CommonApiError.Unauthorized
    }

    test("maps 429 TooManyRequests to CommonApiError.RateLimitExceeded") {
        val client = HttpClient(MockEngine) {
            engine {
                addHandler {
                    respond("{}", HttpStatusCode.TooManyRequests)
                }
            }
        }
        val response: HttpResponse = client.get("https://api.github.com/test")
        BaseKtorApiService.mapCommonApiError(response) shouldBe CommonApiError.RateLimitExceeded
    }

    test("maps 403 Forbidden with x-ratelimit-remaining=0 to RateLimitExceeded") {
        val client = HttpClient(MockEngine) {
            engine {
                addHandler {
                    respond(
                        content = """{"message": "API rate limit exceeded"}""",
                        status = HttpStatusCode.Forbidden,
                        headers = headersOf("x-ratelimit-remaining" to listOf("0"))
                    )
                }
            }
        }
        val response: HttpResponse = client.get("https://api.github.com/test")
        BaseKtorApiService.mapCommonApiError(response) shouldBe CommonApiError.RateLimitExceeded
    }

    test("maps 403 Forbidden with retry-after header to RateLimitExceeded") {
        val client = HttpClient(MockEngine) {
            engine {
                addHandler {
                    respond(
                        content = """{"message": "You have exceeded a secondary rate limit"}""",
                        status = HttpStatusCode.Forbidden,
                        headers = headersOf("retry-after" to listOf("60"))
                    )
                }
            }
        }
        val response: HttpResponse = client.get("https://api.github.com/test")
        BaseKtorApiService.mapCommonApiError(response) shouldBe CommonApiError.RateLimitExceeded
    }

    test("maps 403 Forbidden without rate limit headers to CommonAccessError even if body contains 'rate limit'") {
        val client = HttpClient(MockEngine) {
            engine {
                addHandler {
                    respond(
                        content = """{"message": "API rate limit exceeded for user"}""",
                        status = HttpStatusCode.Forbidden
                    )
                }
            }
        }
        val response: HttpResponse = client.get("https://api.github.com/test")
        BaseKtorApiService.mapCommonApiError(response) shouldBe CommonApiError.CommonAccessError
    }

    test("maps 403 Forbidden without rate limit headers to CommonAccessError") {
        val client = HttpClient(MockEngine) {
            engine {
                addHandler {
                    respond(
                        content = """{"message": "Resource protected"}""",
                        status = HttpStatusCode.Forbidden
                    )
                }
            }
        }
        val response: HttpResponse = client.get("https://api.github.com/test")
        BaseKtorApiService.mapCommonApiError(response) shouldBe CommonApiError.CommonAccessError
    }

    test("maps 400 BadRequest to CommonApiError.BadRequest with parsed message") {
        val client = HttpClient(MockEngine) {
            engine {
                addHandler {
                    respond(
                        content = """{"message": "Problems parsing JSON"}""",
                        status = HttpStatusCode.BadRequest
                    )
                }
            }
        }
        val response: HttpResponse = client.get("https://api.github.com/test")
        BaseKtorApiService.mapCommonApiError(response) shouldBe CommonApiError.BadRequest("Problems parsing JSON")
    }

    test("maps 404 NotFound to DetailsError.ResourceNotFound via BaseKtorResourceApiService") {
        val client = HttpClient(MockEngine) {
            engine {
                addHandler {
                    respond("{}", HttpStatusCode.NotFound)
                }
            }
        }
        val response: HttpResponse = client.get("https://api.github.com/test")
        BaseKtorResourceApiService.mapDetailsError(response) shouldBe DetailsError.ResourceNotFound
    }

    test("maps 409 Conflict to CommonApiError.Conflict with parsed message") {
        val client = HttpClient(MockEngine) {
            engine {
                addHandler {
                    respond(
                        content = """{"message": "Update is not a fast forward"}""",
                        status = HttpStatusCode.Conflict
                    )
                }
            }
        }
        val response: HttpResponse = client.get("https://api.github.com/test")
        BaseKtorApiService.mapCommonApiError(response) shouldBe CommonApiError.Conflict("Update is not a fast forward")
    }

    test("maps 422 UnprocessableEntity to CommonApiError.UnprocessableEntity with parsed message") {
        val client = HttpClient(MockEngine) {
            engine {
                addHandler {
                    respond(
                        content = """{"message": "Validation Failed"}""",
                        status = HttpStatusCode.UnprocessableEntity
                    )
                }
            }
        }
        val response: HttpResponse = client.get("https://api.github.com/test")
        BaseKtorApiService.mapCommonApiError(response) shouldBe CommonApiError.UnprocessableEntity("Validation Failed")
    }

    test("maps 500..599 status to CommonApiError.ServerError") {
        val client = HttpClient(MockEngine) {
            engine {
                addHandler {
                    respond("{}", HttpStatusCode.InternalServerError)
                }
            }
        }
        val response: HttpResponse = client.get("https://api.github.com/test")
        BaseKtorApiService.mapCommonApiError(response) shouldBe CommonApiError.ServerError
    }

    test("maps other 4xx errors to CommonApiError.UnknownApiError with message") {
        val client = HttpClient(MockEngine) {
            engine {
                addHandler {
                    respond(
                        content = """{"message": "Payment required"}""",
                        status = HttpStatusCode.PaymentRequired
                    )
                }
            }
        }
        val response: HttpResponse = client.get("https://api.github.com/test")
        BaseKtorApiService.mapCommonApiError(response) shouldBe CommonApiError.UnknownApiError(402, "Payment required")
    }
})

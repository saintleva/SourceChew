package com.github.saintleva.sourcechew.ui.screens.auth

import com.github.saintleva.sourcechew.data.auth.FakeAuthRepository
import com.mobilebytelabs.kmptoolkit.clipboard.ClipboardManager
import com.mobilebytelabs.kmptoolkit.clipboard.ClipboardManagerConfig
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest : FunSpec({
    val testDispatcher = StandardTestDispatcher()

    beforeSpec {
        Dispatchers.setMain(testDispatcher)
    }

    afterSpec {
        Dispatchers.resetMain()
    }

    test("initial state is unauthorized and canSaveState is false") {
        val repository = FakeAuthRepository
        val clipboardManager = ClipboardManager(ClipboardManagerConfig(async = false))
        val viewModel = AuthViewModel(repository, clipboardManager)

        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.isAuthorized.value shouldBe false
        viewModel.token.value shouldBe ""
        viewModel.canSaveState.value shouldBe false
    }

    test("onTokenChange updates token flow and canSaveState") {
        val repository = FakeAuthRepository
        val clipboardManager = ClipboardManager(ClipboardManagerConfig(async = false))
        val viewModel = AuthViewModel(repository, clipboardManager)

        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onTokenChange("my-secret-token")
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.token.value shouldBe "my-secret-token"
        viewModel.canSaveState.value shouldBe true
    }

    test("onTokenSave saves token and updates isAuthorized") {
        val repository = FakeAuthRepository
        val clipboardManager = ClipboardManager(ClipboardManagerConfig(async = false))
        val viewModel = AuthViewModel(repository, clipboardManager)

        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onTokenChange("test_token_123")
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onTokenSave()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.isAuthorized.value shouldBe true
        viewModel.canSaveState.value shouldBe false

        // Cleanup
        viewModel.onTokenClear()
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.isAuthorized.value shouldBe false
    }

    test("onTokenClear clears authorization in repository") {
        val repository = FakeAuthRepository
        val clipboardManager = ClipboardManager(ClipboardManagerConfig(async = false))
        val viewModel = AuthViewModel(repository, clipboardManager)

        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.onTokenChange("token_to_clear")
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.onTokenSave()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.isAuthorized.value shouldBe true

        viewModel.onTokenClear()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.isAuthorized.value shouldBe false
    }
})

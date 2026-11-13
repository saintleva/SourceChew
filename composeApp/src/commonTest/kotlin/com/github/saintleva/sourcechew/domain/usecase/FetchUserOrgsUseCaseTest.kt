package com.github.saintleva.sourcechew.domain.usecase

import com.github.saintleva.sourcechew.domain.models.AppSettings
import com.github.saintleva.sourcechew.domain.models.Owner
import com.github.saintleva.sourcechew.domain.result.DetailsError
import com.github.saintleva.sourcechew.domain.result.PagingUserOrgsException
import com.github.saintleva.sourcechew.domain.result.Result
import com.github.saintleva.sourcechew.test.FakeConfigStore
import com.github.saintleva.sourcechew.test.FakeUserOrgsRepository
import com.jamal_aliev.paginator.offset.*
import com.jamal_aliev.paginator.offset.bookmark.BookmarkInt
import com.jamal_aliev.paginator.offset.page.OffsetPageState
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class FetchUserOrgsUseCaseTest : FunSpec({

    val dummyOrg = Owner(
        id = 1L,
        login = "google",
        avatarUrl = "https://avatars.githubusercontent.com/u/1342004"
    )

    test("loads user organizations successfully with configured page size") {
        val configStore = FakeConfigStore(AppSettings.default.copy(paginationPageSize = 25))
        var requestedUsername: String? = null
        var requestedPage: Int? = null
        var requestedPageSize: Int? = null

        val expectedOrgs = listOf(dummyOrg)

        val repository = FakeUserOrgsRepository(
            resultProvider = { username, page, pageSize ->
                requestedUsername = username
                requestedPage = page
                requestedPageSize = pageSize
                Result.Success(expectedOrgs)
            }
        )

        val useCase = FetchUserOrgsUseCaseImpl(
            appSettingsStore = configStore,
            userOrgsRepository = repository,
        )

        val paginator = useCase("saintleva")
        paginator.jump(BookmarkInt(1))

        requestedUsername shouldBe "saintleva"
        requestedPage shouldBe 1
        requestedPageSize shouldBe 25
        val pageState = paginator.cache.getStateOf(1)
        pageState?.data shouldBe expectedOrgs
    }

    test("records PagingUserOrgsException in page error state when API call fails") {
        val configStore = FakeConfigStore(AppSettings.default)
        val repository = FakeUserOrgsRepository(
            resultProvider = { _, _, _ ->
                Result.Failure(DetailsError.ResourceNotFound)
            }
        )

        val useCase = FetchUserOrgsUseCaseImpl(
            appSettingsStore = configStore,
            userOrgsRepository = repository,
        )

        val paginator = useCase("nonexistent-user")
        paginator.jump(BookmarkInt(1))

        val pageState = paginator.cache.getStateOf(1)
        val errorState = pageState.shouldBeInstanceOf<OffsetPageState.Error<Owner>>()
        val error = errorState.exception
        error.shouldBeInstanceOf<PagingUserOrgsException>()
        error.error shouldBe DetailsError.ResourceNotFound
    }
})

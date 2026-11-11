package com.github.saintleva.sourcechew.domain.usecase

import com.github.saintleva.sourcechew.domain.models.AppSettings
import com.github.saintleva.sourcechew.domain.models.Owner
import com.github.saintleva.sourcechew.domain.result.DetailsError
import com.github.saintleva.sourcechew.domain.result.PagingMembersException
import com.github.saintleva.sourcechew.domain.result.Result
import com.github.saintleva.sourcechew.test.FakeConfigStore
import com.github.saintleva.sourcechew.test.FakeMembersRepository
import com.jamal_aliev.paginator.offset.*
import com.jamal_aliev.paginator.offset.bookmark.BookmarkInt
import com.jamal_aliev.paginator.offset.page.OffsetPageState
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class FetchMembersUseCaseTest : FunSpec({

    val dummyMember = Owner(
        id = 1L,
        login = "octocat",
        avatarUrl = "https://avatars.githubusercontent.com/u/583231"
    )

    test("loads members successfully with configured page size") {
        val configStore = FakeConfigStore(AppSettings.default.copy(paginationPageSize = 25))
        var requestedOrg: String? = null
        var requestedPage: Int? = null
        var requestedPageSize: Int? = null

        val expectedMembers = listOf(dummyMember)

        val membersRepository = FakeMembersRepository(
            resultProvider = { org, page, pageSize ->
                requestedOrg = org
                requestedPage = page
                requestedPageSize = pageSize
                Result.Success(expectedMembers)
            }
        )

        val useCase = FetchMembersUseCaseImpl(
            appSettingsStore = configStore,
            membersRepository = membersRepository,
        )

        val paginator = useCase("github")
        paginator.jump(BookmarkInt(1))

        requestedOrg shouldBe "github"
        requestedPage shouldBe 1
        requestedPageSize shouldBe 25
        val pageState = paginator.cache.getStateOf(1)
        pageState?.data shouldBe expectedMembers
    }

    test("records PagingMembersException in page error state when API call fails") {
        val configStore = FakeConfigStore(AppSettings.default)
        val membersRepository = FakeMembersRepository(
            resultProvider = { _, _, _ ->
                Result.Failure(DetailsError.ResourceNotFound)
            }
        )

        val useCase = FetchMembersUseCaseImpl(
            appSettingsStore = configStore,
            membersRepository = membersRepository,
        )

        val paginator = useCase("nonexistent-org")
        paginator.jump(BookmarkInt(1))

        val pageState = paginator.cache.getStateOf(1)
        val errorState = pageState.shouldBeInstanceOf<OffsetPageState.Error<Owner>>()
        val error = errorState.exception
        error.shouldBeInstanceOf<PagingMembersException>()
        error.error shouldBe DetailsError.ResourceNotFound
    }
})

package com.github.saintleva.sourcechew.domain.usecase

import com.github.saintleva.sourcechew.domain.models.AppSettings
import com.github.saintleva.sourcechew.domain.models.FoundOwner
import com.github.saintleva.sourcechew.domain.models.FoundRepo
import com.github.saintleva.sourcechew.domain.models.OwnerType
import com.github.saintleva.sourcechew.domain.result.DetailsError
import com.github.saintleva.sourcechew.domain.result.PagingOwnerReposException
import com.github.saintleva.sourcechew.domain.result.Result
import com.github.saintleva.sourcechew.test.FakeConfigStore
import com.github.saintleva.sourcechew.test.FakeOwnerReposRepository
import com.jamal_aliev.paginator.offset.*
import com.jamal_aliev.paginator.offset.bookmark.BookmarkInt
import com.jamal_aliev.paginator.offset.page.OffsetPageState
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class FetchOwnerReposUseCaseTest : FunSpec({

    val dummyRepo = FoundRepo(
        id = 1L,
        name = "SourceChew",
        owner = FoundOwner(id = 10L, type = OwnerType.USER, login = "saintleva", avatarUrl = ""),
        description = "A repo",
        language = "Kotlin",
        stars = 42,
        updatedAt = null,
    )

    test("loads owner repos successfully with configured page size") {
        val configStore = FakeConfigStore(AppSettings.default.copy(paginationPageSize = 25))
        var requestedOwner: String? = null
        var requestedPage: Int? = null
        var requestedPageSize: Int? = null

        val expectedRepos = listOf(dummyRepo)

        val ownerReposRepository = FakeOwnerReposRepository(
            resultProvider = { owner, page, pageSize ->
                requestedOwner = owner
                requestedPage = page
                requestedPageSize = pageSize
                Result.Success(expectedRepos)
            }
        )

        val useCase = FetchOwnerReposUseCaseImpl(
            appSettingsStore = configStore,
            ownerReposRepository = ownerReposRepository,
        )

        val paginator = useCase("saintleva")
        paginator.jump(BookmarkInt(1))

        requestedOwner shouldBe "saintleva"
        requestedPage shouldBe 1
        requestedPageSize shouldBe 25
        val pageState = paginator.cache.getStateOf(1)
        pageState?.data shouldBe expectedRepos
    }

    test("records PagingOwnerReposException in page error state when API call fails") {
        val configStore = FakeConfigStore(AppSettings.default)
        val ownerReposRepository = FakeOwnerReposRepository(
            resultProvider = { _, _, _ ->
                Result.Failure(DetailsError.ResourceNotFound)
            }
        )

        val useCase = FetchOwnerReposUseCaseImpl(
            appSettingsStore = configStore,
            ownerReposRepository = ownerReposRepository,
        )

        val paginator = useCase("nonexistent-owner")
        paginator.jump(BookmarkInt(1))

        val pageState = paginator.cache.getStateOf(1)
        val errorState = pageState.shouldBeInstanceOf<OffsetPageState.Error<FoundRepo>>()
        val error = errorState.exception
        error.shouldBeInstanceOf<PagingOwnerReposException>()
        error.error shouldBe DetailsError.ResourceNotFound
    }
})

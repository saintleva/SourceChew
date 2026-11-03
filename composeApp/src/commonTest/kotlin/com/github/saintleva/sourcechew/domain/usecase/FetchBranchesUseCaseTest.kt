package com.github.saintleva.sourcechew.domain.usecase

import com.github.saintleva.sourcechew.domain.models.AppSettings
import com.github.saintleva.sourcechew.domain.models.Branch
import com.github.saintleva.sourcechew.domain.models.Commit
import com.github.saintleva.sourcechew.domain.models.RepoRef
import com.github.saintleva.sourcechew.domain.result.DetailsError
import com.github.saintleva.sourcechew.domain.result.PagingBranchesException
import com.github.saintleva.sourcechew.domain.result.Result
import com.github.saintleva.sourcechew.test.FakeBranchesApiService
import com.github.saintleva.sourcechew.test.FakeConfigStore
import com.jamal_aliev.paginator.offset.*
import com.jamal_aliev.paginator.offset.bookmark.BookmarkInt
import com.jamal_aliev.paginator.offset.page.OffsetPageState
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class FetchBranchesUseCaseTest : FunSpec({

    test("creates paginator and loads branches successfully without metadata") {
        val expectedBranches = listOf(
            Branch(name = "main", commit = Commit("sha-main-123")),
            Branch(name = "develop", commit = Commit("sha-dev-456")),
        )
        val configStore = FakeConfigStore(AppSettings.default.copy(paginationPageSize = 25))
        var requestedPage: Int? = null
        var requestedPageSize: Int? = null

        val apiService = FakeBranchesApiService { _, page, pageSize ->
            requestedPage = page
            requestedPageSize = pageSize
            Result.Success(expectedBranches)
        }

        val useCase = FetchBranchesUseCaseImpl(
            appSettingsStore = configStore,
            branchesApiService = apiService,
        )

        val paginator = useCase(RepoRef("octocat", "Hello-World"))
        paginator.jump(BookmarkInt(1))

        requestedPage shouldBe 1
        requestedPageSize shouldBe 25
        val pageState = paginator.cache.getStateOf(1)
        pageState?.data shouldBe expectedBranches
        pageState?.metadata shouldBe null
    }

    test("records PagingBranchesException in page error state when API call fails") {
        val configStore = FakeConfigStore(AppSettings.default)
        val apiService = FakeBranchesApiService { _, _, _ ->
            Result.Failure(DetailsError.ResourceNotFound)
        }

        val useCase = FetchBranchesUseCaseImpl(
            appSettingsStore = configStore,
            branchesApiService = apiService,
        )

        val paginator = useCase(RepoRef("octocat", "NonExistent"))
        paginator.jump(BookmarkInt(1))

        val pageState = paginator.cache.getStateOf(1)
        val errorState = pageState.shouldBeInstanceOf<OffsetPageState.Error<Branch>>()
        val error = errorState.exception
        error.shouldBeInstanceOf<PagingBranchesException>()
        error.error shouldBe DetailsError.ResourceNotFound
    }
})

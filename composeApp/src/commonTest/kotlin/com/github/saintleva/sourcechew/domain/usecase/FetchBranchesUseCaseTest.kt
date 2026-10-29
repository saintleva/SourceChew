package com.github.saintleva.sourcechew.domain.usecase

import com.github.saintleva.sourcechew.domain.models.AppSettings
import com.github.saintleva.sourcechew.domain.models.Branch
import com.github.saintleva.sourcechew.domain.models.RepoRef
import com.github.saintleva.sourcechew.domain.repository.ConfigStore
import com.github.saintleva.sourcechew.domain.repository.BranchesApiService
import com.github.saintleva.sourcechew.domain.result.DetailsError
import com.github.saintleva.sourcechew.domain.result.PagingBranchesException
import com.github.saintleva.sourcechew.domain.result.Result
import com.jamal_aliev.paginator.offset.bookmark.BookmarkInt
import com.jamal_aliev.paginator.offset.page.OffsetPageState
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

private class FakeConfigStore<T>(initial: T) : ConfigStore<T> {
    private val _config = MutableStateFlow(initial)
    override val config: Flow<T> = _config
    override suspend fun update(updater: (T) -> T) {
        _config.update(updater)
    }
}

private class FakeBranchesApiService(
    var resultProvider: suspend (repoRef: RepoRef, page: Int, pageSize: Int) -> Result<List<Branch>, DetailsError>
) : BranchesApiService {
    override suspend fun getBranches(
        repoRef: RepoRef,
        page: Int,
        pageSize: Int,
    ): Result<List<Branch>, DetailsError> = resultProvider(repoRef, page, pageSize)

    override suspend fun getSingleBranch(
        repoRef: RepoRef,
        branch: String,
    ): Result<Branch, DetailsError> = Result.Failure(DetailsError.ResourceNotFound)
}

class FetchBranchesUseCaseTest : FunSpec({

    test("creates paginator and loads branches successfully without metadata") {
        val expectedBranches = listOf(
            Branch(name = "main", commitSha = "sha-main-123"),
            Branch(name = "develop", commitSha = "sha-dev-456"),
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

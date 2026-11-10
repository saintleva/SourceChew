package com.github.saintleva.sourcechew.domain.usecase

import com.github.saintleva.sourcechew.domain.models.AppSettings
import com.github.saintleva.sourcechew.domain.models.Branch
import com.github.saintleva.sourcechew.domain.models.Commit
import com.github.saintleva.sourcechew.domain.models.FoundOwner
import com.github.saintleva.sourcechew.domain.models.OwnerType
import com.github.saintleva.sourcechew.domain.models.RepoDetails
import com.github.saintleva.sourcechew.domain.models.RepoRef
import com.github.saintleva.sourcechew.domain.result.DetailsError
import com.github.saintleva.sourcechew.domain.result.NetworkException
import com.github.saintleva.sourcechew.domain.result.PagingBranchesException
import com.github.saintleva.sourcechew.domain.result.Result
import com.github.saintleva.sourcechew.test.FakeBranchesRepository
import com.github.saintleva.sourcechew.test.FakeConfigStore
import com.github.saintleva.sourcechew.test.FakeRepoDetailsRepository
import com.jamal_aliev.paginator.offset.*
import com.jamal_aliev.paginator.offset.bookmark.BookmarkInt
import com.jamal_aliev.paginator.offset.page.OffsetPageState
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class FetchBranchesUseCaseTest : FunSpec({

    val dummyOwner = FoundOwner(
        id = 1L,
        type = OwnerType.USER,
        login = "octocat",
        avatarUrl = "https://avatars.githubusercontent.com/u/583231"
    )

    val dummyRepoDetails = RepoDetails(
        id = 1L,
        url = "https://api.github.com/repos/octocat/Hello-World",
        htmlUrl = "https://github.com/octocat/Hello-World",
        createdAt = null,
        updatedAt = null,
        name = "Hello-World",
        fullname = "octocat/Hello-World",
        owner = dummyOwner,
        description = null,
        language = null,
        languages = emptyList(),
        stars = 0,
        forks = 0,
        subscribersCount = 0,
        openIssues = 0,
        defaultBranch = "main",
        license = null,
        topics = emptyList(),
        pushedAt = null,
        homepageUrl = null,
        isArchived = false,
        isFork = false
    )

    test("creates paginator and loads branches and default branch successfully") {
        val expectedBranches = listOf(
            Branch(name = "main", commit = Commit("sha-main-123")),
            Branch(name = "develop", commit = Commit("sha-dev-456")),
        )
        val expectedDefaultBranch = Branch(name = "main", commit = Commit("sha-main-123"))
        val configStore = FakeConfigStore(AppSettings.default.copy(paginationPageSize = 25))
        var requestedPage: Int? = null
        var requestedPageSize: Int? = null

        val branchesRepository = FakeBranchesRepository(
            resultProvider = { _, page, pageSize ->
                requestedPage = page
                requestedPageSize = pageSize
                Result.Success(expectedBranches)
            },
            singleBranchResultProvider = { _, branch ->
                if (branch == "main") Result.Success(expectedDefaultBranch)
                else Result.Failure(DetailsError.ResourceNotFound)
            }
        )

        val repoDetailsRepository = FakeRepoDetailsRepository {
            Result.Success(dummyRepoDetails)
        }

        val useCase = FetchBranchesUseCaseImpl(
            appSettingsStore = configStore,
            branchesRepository = branchesRepository,
            repoDetailsRepository = repoDetailsRepository,
        )

        val branchData = useCase(RepoRef("octocat", "Hello-World"))
        branchData.default shouldBe BranchDetailsResult.Success(expectedDefaultBranch)

        val paginator = branchData.paginator
        paginator.jump(BookmarkInt(1))

        requestedPage shouldBe 1
        requestedPageSize shouldBe 25
        val pageState = paginator.cache.getStateOf(1)
        pageState?.data shouldBe expectedBranches
        pageState?.metadata shouldBe null
    }

    test("records PagingBranchesException in page error state when API call fails") {
        val configStore = FakeConfigStore(AppSettings.default)
        val branchesRepository = FakeBranchesRepository(
            resultProvider = { _, _, _ ->
                Result.Failure(DetailsError.ResourceNotFound)
            }
        )
        val repoDetailsRepository = FakeRepoDetailsRepository {
            Result.Failure(DetailsError.ResourceNotFound)
        }

        val useCase = FetchBranchesUseCaseImpl(
            appSettingsStore = configStore,
            branchesRepository = branchesRepository,
            repoDetailsRepository = repoDetailsRepository,
        )

        val branchData = useCase(RepoRef("octocat", "NonExistent"))
        branchData.default shouldBe BranchDetailsResult.Error(DetailsError.ResourceNotFound)

        val paginator = branchData.paginator
        paginator.jump(BookmarkInt(1))

        val pageState = paginator.cache.getStateOf(1)
        val errorState = pageState.shouldBeInstanceOf<OffsetPageState.Error<Branch>>()
        val error = errorState.exception
        error.shouldBeInstanceOf<PagingBranchesException>()
        error.error shouldBe DetailsError.ResourceNotFound
    }

    test("handles thrown exception gracefully when fetching default branch") {
        val configStore = FakeConfigStore(AppSettings.default)
        val branchesRepository = FakeBranchesRepository()
        val repoDetailsRepository = FakeRepoDetailsRepository {
            throw NetworkException(RuntimeException("Network down"))
        }

        val useCase = FetchBranchesUseCaseImpl(
            appSettingsStore = configStore,
            branchesRepository = branchesRepository,
            repoDetailsRepository = repoDetailsRepository,
        )

        val branchData = useCase(RepoRef("octocat", "Hello-World"))
        branchData.default.shouldBeInstanceOf<BranchDetailsResult.InfrastructureError>()
    }
})

package com.sumanto.githubusersearch.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.Pager
import androidx.paging.PagingData
import com.sumanto.githubusersearch.data.model.GithubUser
import com.sumanto.githubusersearch.data.pagingsource.GithubUserPagingSource
import com.sumanto.githubusersearch.data.repositories.GithubRepository
import fr.xgouchet.elmyr.junit4.ForgeRule
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

/**
 * Created by sumanto on 8/19/20.
 */
class SearchUsersViewModelTest {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private lateinit var searchUsersViewModel: SearchUsersViewModel
    private var repository = mockk<GithubRepository>()

    @Rule
    @JvmField
    val forger = ForgeRule()

    @Before
    fun setup() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        searchUsersViewModel = SearchUsersViewModel(repository)
    }

    @Test
    fun `searchUsers should set currentQuery to query given in param`() {
        // Given
        val mockQuery = forger.aString(10)
        val mockPagerFlow = Pager(
            config = mockk(),
            pagingSourceFactory = { mockk<GithubUserPagingSource>() }
        ).flow
        every {
            repository.getUserSearchResultStream(any())
        } returns mockPagerFlow

        // When
        searchUsersViewModel.searchUsers(mockQuery)

        // Then
        assert(searchUsersViewModel.lastQueryValue() == mockQuery)
    }

    @Test
    fun `searchUsers should should return result flow of paging data from repository`() =
        runBlockingTest {
            // Given
            val mockQuery = forger.aString(10)
            val mockUsers = listOf(
                mockk<GithubUser>(),
                mockk<GithubUser>()
            )
            val mockPagingData = PagingData.from(mockUsers)
            every {
                repository.getUserSearchResultStream(any())
            } returns flowOf()

            // When
            val result = searchUsersViewModel.searchUsers(mockQuery)

            // Then
            result.collectLatest { resultPagingData ->
                assert(resultPagingData == mockPagingData)
            }
        }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}
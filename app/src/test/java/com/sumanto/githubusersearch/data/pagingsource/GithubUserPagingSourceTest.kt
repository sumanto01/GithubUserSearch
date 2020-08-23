package com.sumanto.githubusersearch.data.pagingsource

import com.sumanto.githubusersearch.data.model.GithubUser
import com.sumanto.githubusersearch.data.model.UserSearchResponse
import com.sumanto.githubusersearch.data.network.GithubService
import fr.xgouchet.elmyr.junit4.ForgeRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Created by sumanto on 8/19/20.
 */
class GithubUserPagingSourceTest {
    @Rule
    @JvmField
    val forger = ForgeRule()

    private lateinit var pagingSource: GithubUserPagingSource
    private val githubService = mockk<GithubService>()
    private val query = "query"

    @Before
    fun setup() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        pagingSource = GithubUserPagingSource(githubService, query)
    }

    @Test
    fun `when loadPage then result data should be equal to response from api`() =
        runBlocking {
            // Given
            val page = 1
            val loadPage = 20
            val users = listOf(
                mockk<GithubUser>(),
                mockk<GithubUser>()
            )
            val response = UserSearchResponse(false, users, users.size)
            coEvery {
                githubService.searchUsers(query, page, loadPage)
            } returns response

            // When
            val result = pagingSource.loadPage(query, page, loadPage)

            // Then
            assertThat(result.data, equalTo(users))
        }

    @Test
    fun `when response data is not empty then result nextKey should be currentPage+1`() =
        runBlocking {
            // Given
            val page = forger.anInt(2, 1000)
            val loadPage = 20
            val users = listOf(mockk<GithubUser>())
            val response = UserSearchResponse(false, users, users.size)
            coEvery {
                githubService.searchUsers(query, page, loadPage)
            } returns response

            // When
            val result = pagingSource.loadPage(query, page, loadPage)

            // Then
            assert(result.nextKey == page + 1)
        }

    @Test
    fun `when response data is empty then result nextKey should be null`() =
        runBlocking {
            // Given
            val page = forger.anInt(2, 1000)
            val loadPage = 20
            val response = UserSearchResponse(false, emptyList(), 0)
            coEvery {
                githubService.searchUsers(query, page, loadPage)
            } returns response

            // When
            val result = pagingSource.loadPage(query, page, loadPage)

            // Then
            assert(result.nextKey == null)
        }

    @Test
    fun `when currentPage is 1 then result prevKey should be null`() = runBlocking {
        // Given
        val page = 1
        val loadPage = 20
        val users = listOf(mockk<GithubUser>())
        val response = UserSearchResponse(false, users, users.size)
        coEvery {
            githubService.searchUsers(query, page, loadPage)
        } returns response

        // When
        val result = pagingSource.loadPage(query, page, loadPage)

        // Then
        assert(result.prevKey == null)
    }

    @Test
    fun `when currentPage greater than 1 then result prevKey should be currentPage-1`() =
        runBlocking {
            // Given
            val page = forger.anInt(2, 1000)
            val loadPage = 20
            val users = listOf(mockk<GithubUser>())
            val response = UserSearchResponse(false, users, users.size)
            coEvery {
                githubService.searchUsers(query, page, loadPage)
            } returns response

            // When
            val result = pagingSource.loadPage(query, page, loadPage)

            // Then
            assert(result.prevKey == page - 1)
        }
}
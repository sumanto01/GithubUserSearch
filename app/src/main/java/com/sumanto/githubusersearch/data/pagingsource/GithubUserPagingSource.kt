package com.sumanto.githubusersearch.data.pagingsource

import androidx.paging.PagingSource
import com.sumanto.githubusersearch.data.model.GithubUser
import com.sumanto.githubusersearch.data.network.GithubService
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

/**
 * Created by sumanto on 8/16/20.
 */
class GithubUserPagingSource @Inject constructor(
    private val service: GithubService,
    private val query: String
) : PagingSource<Int, GithubUser>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GithubUser> {
        val page = params.key ?: START_PAGE_INDEX
        val apiQuery = query
        return try {
            val response = service.searchUsers(apiQuery, page, params.loadSize)
            val repos = response.items
            LoadResult.Page(
                data = repos,
                prevKey = if (page == START_PAGE_INDEX) null else page - 1,
                nextKey = if (repos.isEmpty()) null else page + 1
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }
}

private const val START_PAGE_INDEX = 1
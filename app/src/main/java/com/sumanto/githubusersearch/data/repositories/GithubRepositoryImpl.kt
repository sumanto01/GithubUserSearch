package com.sumanto.githubusersearch.data.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sumanto.githubusersearch.data.model.GithubUser
import com.sumanto.githubusersearch.data.network.GithubService
import com.sumanto.githubusersearch.data.pagingsource.GithubUserPagingSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Created by sumanto on 8/19/20.
 */
class GithubRepositoryImpl @Inject constructor(private val service: GithubService) :
    GithubRepository {

    /**
     * Search users based on query, transform as a flow
     * that will emit stream of data every time we get more data from the network.
     */
    override fun getUserSearchResultStream(query: String): Flow<PagingData<GithubUser>> {
        return Pager(
            config = PagingConfig(
                initialLoadSize = NETWORK_PAGE_SIZE,
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { GithubUserPagingSource(service, query) }
        ).flow
    }

    companion object {
        private const val NETWORK_PAGE_SIZE = 20
    }
}
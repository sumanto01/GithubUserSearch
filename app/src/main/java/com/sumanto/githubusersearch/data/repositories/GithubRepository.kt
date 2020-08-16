package com.sumanto.githubusersearch.data.repositories

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sumanto.githubusersearch.data.model.GithubUser
import com.sumanto.githubusersearch.data.network.GithubService
import com.sumanto.githubusersearch.data.pagingsource.GithubUserPagingSource
import kotlinx.coroutines.flow.Flow

/**
 * Created by sumanto on 8/16/20.
 */
class GithubRepository(private val service: GithubService) {

    /**
     * Search users based on query, transform as a flow
     * that will emit stream of data every time we get more data from the network.
     */
    fun getUserSearchResultStream(query: String): Flow<PagingData<GithubUser>> {
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

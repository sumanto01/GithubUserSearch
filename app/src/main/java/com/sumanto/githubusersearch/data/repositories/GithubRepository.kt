package com.sumanto.githubusersearch.data.repositories

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sumanto.githubusersearch.data.db.GithubDatabase
import com.sumanto.githubusersearch.data.model.GithubUser
import com.sumanto.githubusersearch.data.network.GithubService
import com.sumanto.githubusersearch.data.pagingsource.GithubUserRemoteMediator
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Created by sumanto on 8/16/20.
 */
class GithubRepository @Inject constructor(
    private val service: GithubService,
    private val database: GithubDatabase
) {

    /**
     * Search users based on query, transform as a flow
     * that will emit stream of data every time we get more data from local db or network
     */
    fun getUserSearchResultStream(query: String): Flow<PagingData<GithubUser>> {
        Log.d("GithubRepository", "New query: $query")

        val dbQuery = "%${query.replace(' ', '%')}%"
        val pagingSourceFactory = { database.usersDao().usersByName(dbQuery) }

        return Pager(
            config = PagingConfig(
                initialLoadSize = NETWORK_PAGE_SIZE,
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            remoteMediator = GithubUserRemoteMediator(
                query,
                service,
                database
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    companion object {
        private const val NETWORK_PAGE_SIZE = 20
    }
}

package com.sumanto.githubusersearch.data.pagingsource

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.sumanto.githubusersearch.data.db.GithubDatabase
import com.sumanto.githubusersearch.data.model.GithubUser
import com.sumanto.githubusersearch.data.model.RemoteKeys
import com.sumanto.githubusersearch.data.network.GithubService
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

/**
 * Created by sumanto on 8/17/20.
 */
@OptIn(ExperimentalPagingApi::class)
class GithubUserRemoteMediator @Inject constructor(
    private val query: String,
    private val service: GithubService,
    private val githubDatabase: GithubDatabase
) : RemoteMediator<Int, GithubUser>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, GithubUser>
    ): MediatorResult {
        return try {
            val startPage = GITHUB_STARTING_PAGE_INDEX
            val page = when (loadType) {
                LoadType.REFRESH -> {
                    val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                    remoteKeys?.nextKey?.minus(1) ?: GITHUB_STARTING_PAGE_INDEX
                }
                LoadType.PREPEND -> {
                    val remoteKeys = getRemoteKeyForFirstItem(state)
                    if (remoteKeys == null) {
                        null
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }
                    // If the previous key is null, then we can't request more data
                    if (remoteKeys.prevKey == null) {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }
                    remoteKeys.prevKey
                }
                LoadType.APPEND -> {
                    val remoteKeys = getRemoteKeyForLastItem(state)
                    if (remoteKeys?.nextKey == null) {
                        startPage + 1
                    }else{
                        remoteKeys.nextKey
                    }
                }
                else -> GITHUB_STARTING_PAGE_INDEX
            }

            val apiResponse = service.searchUsers(query, page, state.config.pageSize)
            val users = apiResponse.items
            val endOfPaginationReached = users.isEmpty()
            updateUsersInDatabase(loadType, query, page, endOfPaginationReached, users)
            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: IOException) {
            MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            MediatorResult.Error(exception)
        }
    }

    private suspend fun updateUsersInDatabase(
        loadType: LoadType,
        query: String,
        page: Int,
        endOfPaginationReached: Boolean,
        users: List<GithubUser>
    ) {
        githubDatabase.withTransaction {
            // clear all tables in the database
            if (loadType == LoadType.REFRESH) {
                githubDatabase.remoteKeysDao().clearRemoteKeys()
                githubDatabase.usersDao().deleteByName(query)
            }
            val prevKey = if (page == GITHUB_STARTING_PAGE_INDEX) null else page - 1
            val nextKey = if (endOfPaginationReached) null else page + 1
            val keys = users.mapIndexed { index, user ->
                // Set user responseIndex to be stored on db to maintain order
                user.responseIndex = ((page - 1) * 20) + index
                RemoteKeys(userId = user.id, prevKey = prevKey, nextKey = nextKey)
            }
            githubDatabase.remoteKeysDao().insertAll(keys)
            githubDatabase.usersDao().insertAll(users)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, GithubUser>): RemoteKeys? {
        // Get the last page that was retrieved, that contained items.
        // From that last page, get the last item
        return state.pages.lastOrNull() { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { user ->
                // Get the remote keys of the last item retrieved
                githubDatabase.remoteKeysDao().remoteKeysUserId(user.id)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, GithubUser>): RemoteKeys? {
        // Get the first page that was retrieved, that contained items.
        // From that first page, get the first item
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { user ->
                // Get the remote keys of the first items retrieved
                githubDatabase.remoteKeysDao().remoteKeysUserId(user.id)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, GithubUser>
    ): RemoteKeys? {
        // The paging library is trying to load data after the anchor position
        // Get the item closest to the anchor position
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { userId ->
                githubDatabase.remoteKeysDao().remoteKeysUserId(userId)
            }
        }
    }

}

private const val GITHUB_STARTING_PAGE_INDEX = 1
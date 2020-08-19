package com.sumanto.githubusersearch.data.repositories

import androidx.paging.PagingData
import com.sumanto.githubusersearch.data.model.GithubUser
import kotlinx.coroutines.flow.Flow

/**
 * Created by sumanto on 8/16/20.
 */
interface GithubRepository {
    fun getUserSearchResultStream(query: String): Flow<PagingData<GithubUser>>
}

package com.sumanto.githubusersearch.data.model

import java.lang.Exception

/**
 * Created by sumanto on 8/16/20.
 */
sealed class UserSearchResult {
    data class Success(val data: List<GithubUser>) : UserSearchResult()
    data class Error(val error: Exception) : UserSearchResult()
}
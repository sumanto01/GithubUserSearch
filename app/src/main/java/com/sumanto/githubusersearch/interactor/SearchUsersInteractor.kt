package com.sumanto.githubusersearch.interactor

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sumanto.githubusersearch.SearchUsersContract
import com.sumanto.githubusersearch.data.model.GithubUser
import com.sumanto.githubusersearch.data.repositories.GithubRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Created by sumanto on 8/23/20.
 */
@ExperimentalCoroutinesApi
class SearchUsersInteractor
@ViewModelInject
constructor(private val repository: GithubRepository) : ViewModel(),
    SearchUsersContract.Interactor {
    private val _currentQuery = MutableStateFlow<String?>(null)
    private val currentQuery: StateFlow<String?> get() = _currentQuery

    var currentSearchResult: Flow<PagingData<GithubUser>>? = null

    override fun searchUsers(queryString: String): Flow<PagingData<GithubUser>> {
        val lastResult = currentSearchResult
        if (queryString == lastQueryValue() && lastResult != null) {
            return lastResult
        }
        _currentQuery.value = queryString
        val newResult: Flow<PagingData<GithubUser>> =
            repository.getUserSearchResultStream(queryString).cachedIn(viewModelScope)
        currentSearchResult = newResult
        return newResult
    }

    override fun lastQueryValue(): String? = currentQuery.value
}
package com.sumanto.githubusersearch.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sumanto.githubusersearch.data.model.GithubUser
import com.sumanto.githubusersearch.data.repositories.GithubRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Created by sumanto on 8/16/20.
 * ViewModel for the [SearchUsersActivity] screen.
 */
@ExperimentalCoroutinesApi
class SearchRepositoriesViewModel(private val repository: GithubRepository) : ViewModel() {

    private val _currentQuery = MutableStateFlow<String?>(null)
    private val currentQuery: StateFlow<String?> get() = _currentQuery

    private var currentSearchResult: Flow<PagingData<GithubUser>>? = null

    fun searchRepo(queryString: String): Flow<PagingData<GithubUser>> {
        val lastResult = currentSearchResult
        if (queryString == currentQuery.value && lastResult != null) {
            return lastResult
        }
        _currentQuery.value = queryString
        val newResult: Flow<PagingData<GithubUser>> =
            repository.getUserSearchResultStream(queryString).cachedIn(viewModelScope)
        currentSearchResult = newResult
        return newResult
    }

    fun lastQueryValue(): String? = currentQuery.value
}

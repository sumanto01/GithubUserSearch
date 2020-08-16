package com.sumanto.githubusersearch.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sumanto.githubusersearch.data.repositories.GithubRepository

/**
 * Created by sumanto on 8/16/20.
 */
class ViewModelFactory(private val repository: GithubRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchUsersViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SearchUsersViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
package com.sumanto.githubusersearch.di

import androidx.lifecycle.ViewModelProvider
import com.sumanto.githubusersearch.data.network.GithubService
import com.sumanto.githubusersearch.data.repositories.GithubRepository
import com.sumanto.githubusersearch.viewmodel.ViewModelFactory

/**
 * Created by sumanto on 8/16/20.
 * Static class that handles object creation.
 * objects can be passed as parameters in the constructors
 */
object AppInjection {

    private fun provideGithubRepository(): GithubRepository {
        return GithubRepository(GithubService.create())
    }

    fun provideViewModelFactory(): ViewModelProvider.Factory {
        return ViewModelFactory(provideGithubRepository())
    }
}

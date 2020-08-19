package com.sumanto.githubusersearch.di

import com.sumanto.githubusersearch.data.repositories.GithubRepository
import com.sumanto.githubusersearch.data.repositories.GithubRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

/**
 * Created by sumanto on 8/19/20.
 */
@Module
@InstallIn(ApplicationComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun provideGithubRepository(userRepositoryImpl: GithubRepositoryImpl): GithubRepository
}
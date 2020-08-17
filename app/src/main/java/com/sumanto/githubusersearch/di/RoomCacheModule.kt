package com.sumanto.githubusersearch.di

import android.content.Context
import androidx.room.Room
import com.sumanto.githubusersearch.data.db.GithubDatabase
import com.sumanto.githubusersearch.data.db.RemoteKeysDao
import com.sumanto.githubusersearch.data.db.RemoteKeysDao_Impl
import com.sumanto.githubusersearch.data.db.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

/**
 * Created by sumanto on 8/17/20.
 */
@Module
@InstallIn(ApplicationComponent::class)
object RoomCacheModule {

    private const val DATABASE_NAME = "Github.db"

    @Singleton
    @Provides
    fun provideGithubDb(@ApplicationContext context: Context): GithubDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            GithubDatabase::class.java,
            DATABASE_NAME
        ).build()
    }

    @Singleton
    @Provides
    fun provideUserDao(database: GithubDatabase): UserDao {
        return database.usersDao()
    }

    @Singleton
    @Provides
    fun provideRemoteKeysDao(database: GithubDatabase): RemoteKeysDao {
        return database.remoteKeysDao()
    }

}
package com.sumanto.githubusersearch.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sumanto.githubusersearch.data.model.GithubUser
import com.sumanto.githubusersearch.data.model.RemoteKeys

/**
 * Created by sumanto on 8/17/20.
 */
@Database(
    entities = [GithubUser::class, RemoteKeys::class],
    version = 1,
    exportSchema = false
)
abstract class GithubDatabase : RoomDatabase() {

    abstract fun usersDao(): UserDao
    abstract fun remoteKeysDao(): RemoteKeysDao
}
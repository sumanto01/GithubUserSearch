package com.sumanto.githubusersearch.data.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sumanto.githubusersearch.data.model.GithubUser

/**
 * Created by sumanto on 8/17/20.
 */
@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(users: List<GithubUser>)

    @Query("SELECT * FROM users WHERE (login LIKE :query) ORDER BY responseIndex ASC")
    fun usersByName(query: String): PagingSource<Int, GithubUser>


    @Query("DELETE FROM users WHERE (login LIKE :query)")
    suspend fun deleteByName(query: String)
}
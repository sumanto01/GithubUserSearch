package com.sumanto.githubusersearch.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by sumanto on 8/17/20.
 */
@Entity(tableName = "remote_keys")
data class RemoteKeys(
    @PrimaryKey val userId: Long,
    val prevKey: Int?,
    val nextKey: Int?
)
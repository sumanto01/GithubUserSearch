package com.sumanto.githubusersearch.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/**
 * Created by sumanto on 8/16/20.
 */
@Entity( tableName = "users")
data class GithubUser(
    @PrimaryKey
    @SerializedName("id")
    var id: Long,
    @SerializedName("login")
    var login: String?,
    @SerializedName("node_id")
    var nodeId: String?,
    @SerializedName("avatar_url")
    var avatarUrl: String?,
    @SerializedName("gravatar_id")
    var gravatarId: String?,
    @SerializedName("url")
    var url: String?,
    @SerializedName("html_url")
    var htmlUrl: String?,
    @SerializedName("organizations_url")
    var organizationsUrl: String?,
    @SerializedName("repos_url")
    var reposUrl: String?,
    @SerializedName("type")
    var type: String?,
    @SerializedName("site_admin")
    var siteAdmin: Boolean?,
    @SerializedName("score")
    var score: Double?,

    @SerializedName("response_index")
    var responseIndex: Int?
)
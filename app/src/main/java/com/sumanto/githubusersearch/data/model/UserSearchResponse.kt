package com.sumanto.githubusersearch.data.model

import com.google.gson.annotations.SerializedName

/**
 * Created by sumanto on 8/16/20.
 */
data class UserSearchResponse(
    @SerializedName("incomplete_results")
    var incompleteResults: Boolean?,
    @SerializedName("items")
    var items: List<GithubUser>,
    @SerializedName("total_count")
    var totalCount: Int
)
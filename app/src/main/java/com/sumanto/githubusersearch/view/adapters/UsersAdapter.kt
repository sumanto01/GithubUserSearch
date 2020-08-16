package com.sumanto.githubusersearch.view.adapters

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sumanto.githubusersearch.data.model.GithubUser
import com.sumanto.githubusersearch.view.viewholder.UserViewHolder

/**
 * Created by sumanto on 8/16/20.
 */
class UsersAdapter : PagingDataAdapter<GithubUser, RecyclerView.ViewHolder>(REPO_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        UserViewHolder.create(parent)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val userItem = getItem(position)
        userItem?.let {
            (holder as UserViewHolder).bind(it)
        }
    }

    companion object {
        private val REPO_COMPARATOR = object : DiffUtil.ItemCallback<GithubUser>() {
            override fun areItemsTheSame(oldItem: GithubUser, newItem: GithubUser): Boolean =
                oldItem.login == newItem.login

            override fun areContentsTheSame(oldItem: GithubUser, newItem: GithubUser): Boolean =
                oldItem == newItem
        }
    }
}
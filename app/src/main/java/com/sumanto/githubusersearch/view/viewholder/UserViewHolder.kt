package com.sumanto.githubusersearch.view.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sumanto.githubusersearch.R
import com.sumanto.githubusersearch.data.model.GithubUser

/**
 * Created by sumanto on 8/16/20.
 */
class UserViewHolder(view: View, onUserListener: OnUserListener) : RecyclerView.ViewHolder(view) {
    private val avatarIv: ImageView = view.findViewById(R.id.avatar_image_view)
    private val userNameTv: TextView = view.findViewById(R.id.user_name_text_view)
    private val userTypeTv: TextView = view.findViewById(R.id.user_type_text_view)

    private var user: GithubUser? = null

    init {
        view.apply {
            setOnClickListener {
                onUserListener.onUserClick(user)
            }
            setOnLongClickListener {
                onUserListener.onUserLongClick(user)
                true
            }
        }
    }

    fun bind(user: GithubUser?) {
        if (user == null) {
            showLoadingUserData()
        } else {
            showUserData(user)
        }
    }

    private fun showLoadingUserData() {
        userNameTv.text = "Loading"
        userTypeTv.visibility = View.GONE
    }

    private fun showUserData(user: GithubUser) {
        this.user = user
        Glide.with(itemView.context).load(user.avatarUrl).into(avatarIv)
        userNameTv.text = user.login
        userTypeTv.visibility = View.VISIBLE
        userTypeTv.text = user.type
    }

    companion object {
        fun create(parent: ViewGroup, onUserListener: OnUserListener): UserViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.user_list_item, parent, false)
            return UserViewHolder(view, onUserListener)
        }
    }

    interface OnUserListener{
        fun onUserClick(user: GithubUser?)
        fun onUserLongClick(user: GithubUser?)
    }
}
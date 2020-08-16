package com.sumanto.githubusersearch.view.viewholder

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sumanto.githubusersearch.R
import com.sumanto.githubusersearch.data.model.GithubUser

/**
 * Created by sumanto on 8/16/20.
 */
class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val avatarIv: ImageView = view.findViewById(R.id.avatar_image_view)
    private val userNameTv: TextView = view.findViewById(R.id.user_name_text_view)
    private val userTypeTv: TextView = view.findViewById(R.id.user_type_text_view)

    private var user: GithubUser? = null

    init {
        view.apply {
            setOnClickListener {
                Toast.makeText(
                    itemView.context,
                    "Click Github User ${user?.login ?: ""}",
                    Toast.LENGTH_LONG
                ).show()
            }
            setOnLongClickListener {
                user?.htmlUrl?.let { url ->
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    view.context.startActivity(intent)
                }
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
        fun create(parent: ViewGroup): UserViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.user_list_item, parent, false)
            return UserViewHolder(view)
        }
    }
}
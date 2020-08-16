package com.sumanto.githubusersearch.view.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.sumanto.githubusersearch.R
import com.sumanto.githubusersearch.databinding.LoadStateFooterListItemBinding

/**
 * Created by sumanto on 8/16/20.
 */
class LoadStateViewHolder(
    private val binding: LoadStateFooterListItemBinding,
    retry: () -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.retryButton.setOnClickListener { retry.invoke() }
    }

    fun bind(loadState: LoadState) {
        if (loadState is LoadState.Error) {
            binding.errorTextView.text = loadState.error.localizedMessage
        }
        binding.progressBar.isVisible = loadState is LoadState.Loading
        binding.errorTextView.isVisible = loadState !is LoadState.Loading
        binding.retryButton.isVisible = loadState !is LoadState.Loading
    }

    companion object {
        fun create(parent: ViewGroup, retry: () -> Unit): LoadStateViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.load_state_footer_list_item, parent, false)
            val binding = LoadStateFooterListItemBinding.bind(view)
            return LoadStateViewHolder(binding, retry)
        }
    }
}
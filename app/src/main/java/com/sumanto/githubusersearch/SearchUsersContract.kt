package com.sumanto.githubusersearch

import androidx.paging.CombinedLoadStates
import androidx.paging.PagingData
import com.sumanto.githubusersearch.data.model.GithubUser
import com.sumanto.githubusersearch.view.viewholder.UserViewHolder
import kotlinx.coroutines.flow.Flow

/**
 * Created by sumanto on 8/23/20.
 */
class SearchUsersContract {
    interface View{
        fun showLoading()
        fun hideLoading()
        fun showRetryButton()
        fun hideRetryButton()
        fun hideSoftKeyboard()
        fun setListVisibility(isVisible: Boolean)
        fun setPagingDataUser(pagingData: PagingData<GithubUser>)
        fun showUserInfoToast(user: GithubUser?)
    }

    interface Interactor {
        fun searchUsers(title: String): Flow<PagingData<GithubUser>>
        fun lastQueryValue(): String?
    }

    interface InteractorOutput {
        fun onQuerySuccess(pagingData: PagingData<GithubUser>)
        fun onQueryError()
    }

    interface Presenter: UserViewHolder.OnUserListener {
        fun setLoadState(loadState: CombinedLoadStates)
        suspend fun searchUsers(query: String)
        fun lastQueryValue(): String?
    }
}
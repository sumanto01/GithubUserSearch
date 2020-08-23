package com.sumanto.githubusersearch.presenter

import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.PagingData
import com.sumanto.githubusersearch.SearchUsersContract
import com.sumanto.githubusersearch.data.model.GithubUser
import kotlinx.coroutines.flow.collectLatest

/**
 * Created by sumanto on 8/23/20.
 */
class SearchUsersPresenter(
    private val view: SearchUsersContract.View?,
    private val interactor: SearchUsersContract.Interactor?
) : SearchUsersContract.Presenter, SearchUsersContract.InteractorOutput {

    override fun setLoadState(loadState: CombinedLoadStates) {
        view?.setListVisibility(loadState.source.refresh is LoadState.NotLoading)
        if (loadState.source.refresh is LoadState.Loading) {
            view?.showLoading()
        } else {
            view?.hideLoading()
        }
        if (loadState.source.refresh is LoadState.Error) {
            view?.showRetryButton()
        } else {
            view?.hideRetryButton()
        }
    }

    override suspend fun searchUsers(query: String) {
        view?.hideSoftKeyboard()
        interactor?.searchUsers(query)?.collectLatest {
            onQuerySuccess(it)
        }
    }

    override fun lastQueryValue(): String? {
        return interactor?.lastQueryValue()
    }

    override fun onUserClick(user: GithubUser?) {
        view?.showUserInfoToast(user)
    }

    override fun onUserLongClick(user: GithubUser?) {
        TODO("Use router to navigate")
    }

    override fun onQuerySuccess(pagingData: PagingData<GithubUser>) {
        view?.hideLoading()
        view?.setPagingDataUser(pagingData)
    }

    override fun onQueryError() {
        view?.hideLoading()
        view?.showRetryButton()
    }
}
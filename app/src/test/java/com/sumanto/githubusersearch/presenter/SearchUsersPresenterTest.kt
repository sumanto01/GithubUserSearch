package com.sumanto.githubusersearch.presenter

import androidx.paging.PagingData
import com.sumanto.githubusersearch.SearchUsersContract
import com.sumanto.githubusersearch.data.model.GithubUser
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner


/**
 * Created by sumanto on 8/23/20.
 */
@RunWith(MockitoJUnitRunner::class)
class SearchUsersPresenterTest {

    private lateinit var presenter: SearchUsersPresenter

    @Mock
    private lateinit var view: SearchUsersContract.View

    @Mock
    private lateinit var interactor: SearchUsersContract.Interactor

    @Before
    fun setUp() {
        presenter = SearchUsersPresenter(view, interactor)
    }

    @Test
    fun `onQuerySuccess should hideLoading and setPagingDataUser`() {
        val mockResult = mockk<PagingData<GithubUser>>()
        presenter.onQuerySuccess(mockResult)
        verify(view).hideLoading()
        verify(view).setPagingDataUser(mockResult)
    }

    @Test
    fun `onQueryError should hideLoading and showRetryButton`() {
        presenter.onQueryError()
        verify(view).hideLoading()
        verify(view).showRetryButton()
    }

    @Test
    fun `when click user should showUserInfoToast`() {
        val mockUser = mockk<GithubUser>()
        presenter.onUserClick(mockUser)
        verify(view).showUserInfoToast(mockUser)
    }
}
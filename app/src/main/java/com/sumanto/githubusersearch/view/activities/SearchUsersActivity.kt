package com.sumanto.githubusersearch.view.activities

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import androidx.recyclerview.widget.DividerItemDecoration
import com.sumanto.githubusersearch.SearchUsersContract
import com.sumanto.githubusersearch.data.model.GithubUser
import com.sumanto.githubusersearch.databinding.ActivitySearchBinding
import com.sumanto.githubusersearch.interactor.SearchUsersInteractor
import com.sumanto.githubusersearch.presenter.SearchUsersPresenter
import com.sumanto.githubusersearch.view.adapters.UsersAdapter
import com.sumanto.githubusersearch.view.adapters.UsersLoadStateAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Created by sumanto on 8/15/20.
 */
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class SearchUsersActivity : AppCompatActivity(), SearchUsersContract.View {

    private lateinit var binding: ActivitySearchBinding

    private lateinit var presenter: SearchUsersContract.Presenter

    private lateinit var adapter: UsersAdapter

    private var searchJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val interactor: SearchUsersInteractor by viewModels()
        presenter = SearchUsersPresenter(this, interactor)
        adapter = UsersAdapter(presenter)

        val query = savedInstanceState?.getString(LAST_SEARCH_QUERY) ?: DEFAULT_QUERY
        initViews(query)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(LAST_SEARCH_QUERY, presenter.lastQueryValue())
    }

    private fun search(query: String) {
        // Cancel the previous coroutine job before creating a new one
        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            presenter.searchUsers(query)
        }
    }

    private fun initViews(query: String) {
        binding.searchEditText.also { et ->
            et.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    updateUsersListFromInput()
                    true
                } else {
                    false
                }
            }
            et.setOnKeyListener { _, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    updateUsersListFromInput()
                    true
                } else {
                    false
                }
            }
            et.addTextChangedListener(object : TextWatcher {

                override fun afterTextChanged(s: Editable) {
                    binding.searchButton.isEnabled = s.toString().isNotBlank()
                }

                override fun beforeTextChanged(
                    s: CharSequence, start: Int,
                    count: Int, after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence, start: Int,
                    before: Int, count: Int
                ) {
                }
            })
            et.setText(query)
        }
        binding.searchButton.apply {
            isEnabled = query.isNotBlank()
            setOnClickListener {
                updateUsersListFromInput()
            }
        }
        binding.retryButton.setOnClickListener { adapter.retry() }
        binding.swipeRefresh.setOnRefreshListener { adapter.refresh() }
        val decoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        binding.list.addItemDecoration(decoration)
        initAdapter()
    }


    private fun initAdapter() {
        binding.list.adapter = adapter.withLoadStateHeaderAndFooter(
            header = UsersLoadStateAdapter { adapter.retry() },
            footer = UsersLoadStateAdapter { adapter.retry() }
        )
        adapter.addLoadStateListener { loadState ->
            presenter.setLoadState(loadState)
        }
    }

    private fun updateUsersListFromInput() {
        binding.searchEditText.text.trim().let {
            if (it.isNotEmpty()) {
                search(it.toString())
            }
        }
    }

    override fun hideSoftKeyboard() {
        val imm: InputMethodManager =
            this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
    }

    override fun showLoading() {
        binding.swipeRefresh.isRefreshing = true
    }

    override fun hideLoading() {
        binding.swipeRefresh.isRefreshing = false
    }

    override fun showRetryButton() {
        binding.retryButton.isVisible = true
        binding.errorTextView.isVisible = true
    }

    override fun hideRetryButton(){
        binding.retryButton.isVisible = false
        binding.errorTextView.isVisible = false
    }

    override fun setListVisibility(isVisible: Boolean){
        binding.list.isVisible = isVisible
    }

    override fun showUserInfoToast(user: GithubUser?) {
        Toast.makeText(
            this,
            "Click Github User ${user?.login ?: ""}",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun setPagingDataUser(pagingData: PagingData<GithubUser>) {
        lifecycleScope.launch {
            adapter.submitData(pagingData)
        }
    }

    companion object {
        private const val LAST_SEARCH_QUERY: String = "last_search_query"
        private const val DEFAULT_QUERY = ""
    }
}
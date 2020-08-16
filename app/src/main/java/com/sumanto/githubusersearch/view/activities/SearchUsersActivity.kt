package com.sumanto.githubusersearch.view.activities

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import com.sumanto.githubusersearch.databinding.ActivitySearchBinding
import com.sumanto.githubusersearch.di.AppInjection
import com.sumanto.githubusersearch.view.adapters.UsersLoadStateAdapter
import com.sumanto.githubusersearch.view.adapters.UsersAdapter
import com.sumanto.githubusersearch.viewmodel.SearchUsersViewModel
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Created by sumanto on 8/15/20.
 */
class SearchUsersActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var viewModel: SearchUsersViewModel

    private val adapter = UsersAdapter()

    private var searchJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        viewModel = ViewModelProvider(this, AppInjection.provideViewModelFactory())
            .get(SearchUsersViewModel::class.java)
        val query = savedInstanceState?.getString(LAST_SEARCH_QUERY) ?: DEFAULT_QUERY
        initViews(query)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(LAST_SEARCH_QUERY, viewModel.lastQueryValue())
    }

    private fun search(query: String) {
        // Cancel the previous coroutine job before creating a new one
        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            viewModel.searchUsers(query).collectLatest {
                adapter.submitData(it)
            }
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


    @OptIn(InternalCoroutinesApi::class)
    private fun initAdapter() {
        binding.list.adapter = adapter.withLoadStateHeaderAndFooter(
            header = UsersLoadStateAdapter { adapter.retry() },
            footer = UsersLoadStateAdapter { adapter.retry() }
        )
        adapter.addLoadStateListener { loadState ->
            // set views state based on LoadState
            binding.list.isVisible = loadState.source.refresh is LoadState.NotLoading
            binding.swipeRefresh.isRefreshing = loadState.source.refresh is LoadState.Loading
            binding.retryButton.isVisible = loadState.source.refresh is LoadState.Error
        }
    }

    private fun updateUsersListFromInput() {
        hideKeyboardFrom(binding.searchEditText)
        binding.searchEditText.text.trim().let {
            if (it.isNotEmpty()) {
                search(it.toString())
            }
        }
    }

    private fun hideKeyboardFrom(view: View) {
        val imm: InputMethodManager =
            view.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    companion object {
        private const val LAST_SEARCH_QUERY: String = "last_search_query"
        private const val DEFAULT_QUERY = ""
    }
}
package com.sumanto.githubusersearch.view.activities

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import com.sumanto.githubusersearch.utils.FileReader
import com.sumanto.githubusersearch.R
import com.sumanto.githubusersearch.utils.EspressoUtils
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class SearchUsersActivityTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(SearchUsersActivity::class.java, true, false)

    @BindValue
    @JvmField
    val testingUrl: String = "http://127.0.0.1:8080"

    private val mockWebServer = MockWebServer()

    @Before
    fun setup() {
        mockWebServer.start(8080)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun whenSearchEditTextNotEmpty_searchButtonShouldBeEnabled() {
        mActivityTestRule.launchActivity(null)
        val searchEditText = onView(withId(R.id.search_edit_text))
        searchEditText.perform(replaceText(QUERY))
        val searchButton = onView(withId(R.id.search_button))
        searchButton.check(matches(isEnabled()))
    }

    @Test
    fun whenSearchEditTextEmpty_searchButtonShouldBeDisabled() {
        mActivityTestRule.launchActivity(null)
        val searchEditText = onView(withId(R.id.search_edit_text))
        searchEditText.perform(replaceText(""))
        val searchButton = onView(withId(R.id.search_button))
        searchButton.check(matches(not(isEnabled())))
    }

    @Test
    fun whenSearchUsersReturnSuccessResponse_listItemShouldBeDisplayed() {
        mockSuccessResult()
        mActivityTestRule.launchActivity(null)
        val searchEditText = onView(withId(R.id.search_edit_text))
        searchEditText.perform(replaceText(QUERY))
        val searchButton = onView(withId(R.id.search_button))
        searchButton.perform(click()).check(matches(isDisplayed()))
        Thread.sleep(500)
        val firstViewItem = onView(withId(R.id.list))
            .check(
                matches(
                    EspressoUtils.childAtPosition(
                        0,
                        hasDescendant(withText(QUERY))
                    )
                )
            )
        firstViewItem.check(matches(isDisplayed()))
    }

    private fun mockSuccessResult() {
        mockWebServer.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                return MockResponse()
                    .setResponseCode(200)
                    .setBody(
                        FileReader.readStringFromFile(SUCESS_RESPONSE_FILE)
                    )
            }
        }
    }

    companion object {
        private const val QUERY = "tiket"
        private const val SUCESS_RESPONSE_FILE = "mock_success_response.json"
    }
}

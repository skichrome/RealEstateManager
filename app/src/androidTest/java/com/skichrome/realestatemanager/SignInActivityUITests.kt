package com.skichrome.realestatemanager

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.rule.ActivityTestRule
import com.skichrome.realestatemanager.view.SignInActivity
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test

class SignInActivityUITests
{
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var activityTestRule = ActivityTestRule(SignInActivity::class.java)

    @Test
    fun assertFirstScreen() = runBlocking {
        Espresso.onView(withText(R.string.activity_sign_in_hint))
            .check(matches(isDisplayed()))
            .let { Unit }
    }
}
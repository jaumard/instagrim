package com.jaumard.instagrim.ui.alogin

import android.content.Intent
import android.net.Uri
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.intent.Intents.intended
import android.support.test.espresso.intent.matcher.ComponentNameMatchers.hasClassName
import android.support.test.espresso.intent.matcher.IntentMatchers.*
import android.support.test.espresso.intent.rule.IntentsTestRule
import android.support.test.espresso.matcher.RootMatchers.withDecorView
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.LargeTest
import android.support.test.runner.AndroidJUnit4
import com.jaumard.instagrim.R
import com.jaumard.instagrim.TestApp
import com.jaumard.instagrim.network.BuildConfig
import com.jaumard.instagrim.ui.gallery.GalleryActivity
import com.jaumard.instagrim.ui.login.LoginActivity
import com.jaumard.instagrim.ui.login.LoginBindingAdapter
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.not
import org.hamcrest.core.AllOf
import org.hamcrest.core.AllOf.allOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class LoginActivityTest {

    @get:Rule
    var loginActivityRule = IntentsTestRule<LoginActivity>(LoginActivity::class.java, false, false)

    @Before
    fun setup() {
        val database = (InstrumentationRegistry.getTargetContext().applicationContext as TestApp).database
        database.userDao().delete()
    }

    @Test
    fun loginButton_changeActivity() {
        loginActivityRule.launchActivity(Intent())
        val url = BuildConfig.INSTAGRAM_BASE_URL + InstrumentationRegistry.getTargetContext().getString(com.jaumard.instagrim.R.string.path_instagram_login,
                BuildConfig.INSTAGRAM_CLIENT_ID,
                Uri.encode(BuildConfig.INSTAGRAM_URL_REDIRECTION))

        onView(withId(R.id.buttonLogin)).perform(click())
        intended(allOf(hasAction(Intent.ACTION_VIEW), hasData(Uri.parse(url))))
    }

    @Test
    fun deepLinkResult_changeActivity() {
        val intent = Intent()
        intent.data = Uri.parse("https://instagrim.com/login?code=test")
        loginActivityRule.launchActivity(intent)

        onView(withId(R.id.username)).check(matches(withText("fname")))
        Thread.sleep(LoginBindingAdapter.ANIMATION_START_DELAY + 100)
        intended(AllOf.allOf(
                hasComponent(hasClassName(GalleryActivity::class.java.name)),
                hasExtraWithKey(GalleryActivity.KEY_USER)))
    }

    @Test
    fun deepLinkError_changeActivity() {
        val intent = Intent()
        intent.data = Uri.parse("https://instagrim.com/login?error=error&error_description=" +
                InstrumentationRegistry.getTargetContext().getString(R.string.error_generic))
        loginActivityRule.launchActivity(intent)
        onView(withText(R.string.error_generic)).inRoot(withDecorView(not(`is`(loginActivityRule.activity.window.decorView)))).check(matches(isDisplayed()))
    }

}

package com.jaumard.instagrim.ui.gallery

import android.content.Intent
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.RecyclerViewActions
import android.support.test.espresso.intent.Intents
import android.support.test.espresso.intent.matcher.IntentMatchers
import android.support.test.espresso.intent.rule.IntentsTestRule
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.filters.LargeTest
import android.support.test.runner.AndroidJUnit4
import com.jaumard.instagrim.R
import com.jaumard.instagrim.TestApp
import com.jaumard.instagrim.data.AppDatabase
import com.jaumard.instagrim.data.user.models.User
import com.jaumard.instagrim.ui.preview.PreviewActivity
import com.jaumard.recyclerviewbinding.BindableRecyclerAdapter
import org.hamcrest.core.AllOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class GalleryActivityTest {
    private val user = User("token", "name", "name", "fname", "avatar")
    private lateinit var database: AppDatabase

    @get:Rule
    var galleryActivityRule = object : IntentsTestRule<GalleryActivity>(GalleryActivity::class.java) {
        override fun beforeActivityLaunched() {
            super.beforeActivityLaunched()
            database = (InstrumentationRegistry.getTargetContext().applicationContext as TestApp).database
            database.userDao().insert(user)
        }

        override fun getActivityIntent(): Intent {
            val intent = Intent()
            intent.putExtra(GalleryActivity.KEY_USER, user)
            return intent
        }
    }

    @Test
    fun itemClick_changeActivity() {
        onView(withId(R.id.fullName)).check(matches(withText("fname")))

        onView(withId(R.id.recyclerView)).perform(RecyclerViewActions.actionOnItemAtPosition<BindableRecyclerAdapter.ViewHolder>(5, click()))

        Intents.intended(AllOf.allOf(
                IntentMatchers.hasComponent(PreviewActivity::class.java.name),
                IntentMatchers.hasExtraWithKey(PreviewActivity.KEY_MEDIA),
                IntentMatchers.hasExtraWithKey(PreviewActivity.KEY_SELECTED_MEDIA)))
    }
}
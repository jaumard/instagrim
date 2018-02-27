package com.jaumard.instagrim.ui.preview

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.app.SharedElementCallback
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.jaumard.instagrim.R
import com.jaumard.instagrim.data.media.models.Media
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_preview.*


class PreviewActivity : DaggerAppCompatActivity() {
    companion object {
        private const val STATE_CURRENT_PAGE_POSITION = "STATE_CURRENT_PAGE_POSITION"
        const val KEY_MEDIA = "media"
        const val KEY_SELECTED_MEDIA = "selected_media"
        const val KEY_INITIAL_MEDIA = "initial_media"

        fun getIntent(context: Context, selected: Media, media: List<Media>): Intent {
            val intent = Intent(context, PreviewActivity::class.java)
            intent.putParcelableArrayListExtra(PreviewActivity.KEY_MEDIA, ArrayList(media))

            intent.putExtra(PreviewActivity.KEY_SELECTED_MEDIA, selected)
            return intent
        }
    }

    private var isReturning: Boolean = false
    private val mCallback = object : SharedElementCallback() {
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        override fun onMapSharedElements(names: MutableList<String>, sharedElements: MutableMap<String, View>) {
            if (isReturning) {
                val sharedElement = currentFragment.getMediaImageView()
                if (sharedElement == null) {
                    // If shared element is null, then it has been scrolled off screen and
                    // no longer visible. In this case we cancel the shared element transition by
                    // removing the shared element from the shared elements map.
                    names.clear()
                    sharedElements.clear()
                } else if (initialId != current.id) {
                    // If the user has swiped to a different ViewPager page, then we need to
                    // remove the old shared element and replace it with the new shared element
                    // that should be transitioned instead.
                    names.clear()
                    names.add(sharedElement.transitionName)
                    sharedElements.clear()
                    sharedElements[sharedElement.transitionName] = sharedElement
                }
            }
        }
    }
    private lateinit var media: List<Media>
    private lateinit var current: Media
    private lateinit var initialId: String

    private lateinit var currentFragment: PreviewFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = Color.BLACK
            supportRequestWindowFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        }
        setContentView(R.layout.activity_preview)
        supportPostponeEnterTransition()
        setEnterSharedElementCallback(mCallback)

        current = intent.extras.getParcelable(KEY_SELECTED_MEDIA)
        media = intent.extras.getParcelableArrayList(KEY_MEDIA)!!
        initialId = current.id

        if (savedInstanceState != null) {
            current = media[savedInstanceState.getInt(STATE_CURRENT_PAGE_POSITION)]
        }

        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewPager.adapter = object : FragmentStatePagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int): Fragment {
                return PreviewFragment.getInstance(media[position], initialId)
            }

            override fun setPrimaryItem(container: ViewGroup, position: Int, item: Any) {
                super.setPrimaryItem(container, position, item)
                currentFragment = item as PreviewFragment
                current = media[position]
            }

            override fun getCount(): Int {
                return media.size
            }
        }
        viewPager.currentItem = media.indexOf(current)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(STATE_CURRENT_PAGE_POSITION, media.indexOf(current))
    }

    override fun supportFinishAfterTransition() {
        isReturning = true
        val data = Intent()
        data.putExtra(KEY_SELECTED_MEDIA, current)
        data.putExtra(KEY_INITIAL_MEDIA, initialId)
        setResult(Activity.RESULT_OK, data)
        super.supportFinishAfterTransition()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        supportFinishAfterTransition()
        return true
    }

    override fun onBackPressed() {
        supportFinishAfterTransition()
    }
}

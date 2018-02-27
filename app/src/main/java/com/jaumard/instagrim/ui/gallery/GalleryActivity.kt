package com.jaumard.instagrim.ui.gallery

import android.annotation.TargetApi
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.constraint.ConstraintLayout
import android.support.v4.app.SharedElementCallback
import android.view.*
import android.widget.ImageView
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.ImageViewTarget
import com.jaumard.instagrim.GlideApp
import com.jaumard.instagrim.R
import com.jaumard.instagrim.data.media.models.Media
import com.jaumard.instagrim.data.user.models.User
import com.jaumard.instagrim.databinding.ActivityGalleryBinding
import com.jaumard.instagrim.di.ViewModelFactory
import com.jaumard.instagrim.ui.preview.PreviewActivity.Companion.KEY_INITIAL_MEDIA
import com.jaumard.instagrim.ui.preview.PreviewActivity.Companion.KEY_SELECTED_MEDIA
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_gallery.*
import javax.inject.Inject


class GalleryActivity : DaggerAppCompatActivity() {
    companion object {
        const val KEY_USER = "user"

        fun getIntent(context: Context, user: User): Intent {
            val intent = Intent(context, GalleryActivity::class.java)
            intent.putExtra(KEY_USER, user)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            return intent
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var galleryViewModel: GalleryViewModel
    private var tmpReenterState: Bundle? = null
    private val sharedElementCallback = object : SharedElementCallback() {
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        override fun onMapSharedElements(names: MutableList<String>, sharedElements: MutableMap<String, View>) {
            if (tmpReenterState == null) {
                // If tmpReenterState is null, then the activity is exiting.
                val navigationBar = findViewById<View>(android.R.id.navigationBarBackground)
                val statusBar = findViewById<View>(android.R.id.statusBarBackground)
                if (navigationBar != null) {
                    names.add(navigationBar.transitionName)
                    sharedElements[navigationBar.transitionName] = navigationBar
                }
                if (statusBar != null) {
                    names.add(statusBar.transitionName)
                    sharedElements[statusBar.transitionName] = statusBar
                }
            } else {
                val startingId = tmpReenterState!!.getString(KEY_INITIAL_MEDIA)
                val media: Media = tmpReenterState!!.getParcelable(KEY_SELECTED_MEDIA)
                if (startingId != media.id) {
                    // If startingId != currentID the user must have swiped to a
                    // different page in the DetailsActivity. We must update the shared element
                    // so that the correct one falls into place.
                    val newSharedElement: ConstraintLayout? = recyclerView.findViewWithTag(media)
                    if (newSharedElement != null) {
                        val imageView = newSharedElement.findViewById<ImageView>(R.id.img)
                        names.clear()
                        names.add(media.id)
                        sharedElements.clear()
                        sharedElements[media.id] = imageView
                    }
                }

                tmpReenterState = null
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            supportRequestWindowFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        }
        val binding: ActivityGalleryBinding = DataBindingUtil.setContentView(this, R.layout.activity_gallery)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            supportPostponeEnterTransition()
        }

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayUseLogoEnabled(true)

        setExitSharedElementCallback(sharedElementCallback)

        galleryViewModel = ViewModelProviders.of(this, viewModelFactory).get(GalleryViewModel::class.java)
        galleryViewModel.user.set(intent.extras[KEY_USER] as User)
        binding.viewModel = galleryViewModel
        setAvatarIcon()
    }

    private fun setAvatarIcon() {
        GlideApp.with(avatar.context)
                .load(galleryViewModel.user.get()?.avatar)
                .apply(RequestOptions.circleCropTransform())
                .error(R.drawable.ic_profile)
                .fallback(R.drawable.ic_profile)
                .into(object : ImageViewTarget<Drawable>(avatar) {
                    override fun setResource(resource: Drawable?) {
                        avatar.setImageDrawable(resource)
                        avatar.post({ supportStartPostponedEnterTransition() })
                    }
                })
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onEnterAnimationComplete() {
        super.onEnterAnimationComplete()
        //Cancel return transition as we don't want any transition back to login Activity
        window.sharedElementReturnTransition = null
        avatar.transitionName = null
        fullName.transitionName = null
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_gallery, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        galleryViewModel.logout()
        return true
    }

    override fun onActivityReenter(requestCode: Int, data: Intent) {
        super.onActivityReenter(requestCode, data)
        tmpReenterState = data.extras
        tmpReenterState?.classLoader = classLoader //On some device it crash(BadParcelableException) without this... Didn't figure why

        val startingId = tmpReenterState!!.getString(KEY_INITIAL_MEDIA)
        val media: Media = tmpReenterState!!.getParcelable(KEY_SELECTED_MEDIA)
        if (startingId != media.id) {
            recyclerView.scrollToPosition(galleryViewModel.media.get().indexOf(media))
        }
        supportPostponeEnterTransition()
        recyclerView.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                recyclerView.viewTreeObserver.removeOnPreDrawListener(this)
                // necessary to request layout here in order to get a smooth transition.
                recyclerView.requestLayout()
                supportStartPostponedEnterTransition()
                return true
            }
        })
    }
}

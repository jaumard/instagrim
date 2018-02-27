package com.jaumard.instagrim.ui.login

import android.databinding.BindingAdapter
import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.ImageViewTarget
import com.bumptech.glide.request.transition.Transition
import com.jaumard.instagrim.GlideApp
import com.jaumard.instagrim.R
import com.jaumard.instagrim.data.user.models.User
import com.jaumard.instagrim.ui.login.navigation.GalleryAccessor

object LoginBindingAdapter {
    const val ANIMATION_START_DELAY = 1500L

    @JvmStatic
    @BindingAdapter("username", "user", "galleryAccessor")
    fun setAvatar(imageView: ImageView, textView: TextView, user: User?, galleryAccessor: GalleryAccessor) {
        GlideApp.with(imageView.context)
                .load(user?.avatar)
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.ic_profile)
                .error(R.drawable.ic_profile)
                .fallback(R.drawable.ic_profile)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(object : ImageViewTarget<Drawable>(imageView) {
                    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                        super.onResourceReady(resource, transition)
                        goToGallery(user)
                    }

                    private fun goToGallery(user: User?) {
                        if (user != null) {
                            imageView.postDelayed({
                                galleryAccessor.goToGallery(user, imageView, textView)
                            }, ANIMATION_START_DELAY)
                        }
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        super.onLoadFailed(errorDrawable)
                        goToGallery(user)
                    }

                    override fun setResource(resource: Drawable?) {
                        imageView.setImageDrawable(resource)
                    }
                })
    }
}
package com.jaumard.instagrim.ui.common

import android.databinding.BindingAdapter
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestOptions
import com.jaumard.instagrim.GlideApp
import com.jaumard.instagrim.R
import com.jaumard.instagrim.ui.gallery.views.ImageCounterView

object CommonBindingAdapter {
    @JvmStatic
    @BindingAdapter("srcCompat")
    fun setImage(imageView: ImageView, url: String) {
        GlideApp.with(imageView.context)
                .load(url)
                .transition(withCrossFade())
                .placeholder(R.drawable.ic_file_download_24dp)
                .error(R.drawable.ic_error_outline_24dp)
                .into(imageView)
    }

    @JvmStatic
    @BindingAdapter("thumbnail", "android:src")
    fun setImageWithInitialThumbnail(imageView: ImageView, thumbnail: String, url: String) {
        GlideApp.with(imageView.context)
                .load(url)
                .thumbnail(GlideApp.with(imageView.context)
                        .load(thumbnail)
                        .error(R.drawable.ic_error_outline_24dp)
                )
                .transition(withCrossFade())
                .into(imageView)
                .waitForLayout()
    }

    @JvmStatic
    @BindingAdapter("avatar")
    fun setAvatar(imageView: ImageView, url: String?) {
        GlideApp.with(imageView.context)
                .load(url)
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.ic_profile)
                .error(R.drawable.ic_profile)
                .fallback(R.drawable.ic_profile)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView)
    }

    @JvmStatic
    @BindingAdapter("text")
    fun setCounterText(imageCounterView: ImageCounterView, text: Int) {
        imageCounterView.text = text.toString()
    }

    @JvmStatic
    @BindingAdapter("android:visibility")
    fun setVisibility(view: View, isVisible: Boolean) {
        view.visibility = if (isVisible) VISIBLE else GONE
    }
}
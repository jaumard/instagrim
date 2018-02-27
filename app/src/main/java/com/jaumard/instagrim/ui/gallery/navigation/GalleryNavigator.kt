package com.jaumard.instagrim.ui.gallery.navigation

import android.content.Intent
import android.support.v4.app.ActivityOptionsCompat
import android.view.View
import com.jaumard.instagrim.data.media.models.Media
import com.jaumard.instagrim.ui.common.CommonNavigator
import com.jaumard.instagrim.ui.gallery.GalleryActivity
import com.jaumard.instagrim.ui.login.LoginActivity
import com.jaumard.instagrim.ui.preview.PreviewActivity
import javax.inject.Inject

class GalleryNavigator @Inject constructor(context: GalleryActivity) : CommonNavigator(context), FullScreenAccessor {
    override fun goToFullScreen(view: View, selected: Media, items: List<Media>) {
        val intent = PreviewActivity.getIntent(context, selected, items)
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(context, view, selected.id)
        context.startActivity(intent, options.toBundle())
    }

    fun goToLogin() {
        val intent = LoginActivity.getIntent(context)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
    }

}


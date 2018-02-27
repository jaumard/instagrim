package com.jaumard.instagrim.ui.login.navigation

import android.content.Intent
import android.net.Uri
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.util.Pair
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.jaumard.instagrim.R
import com.jaumard.instagrim.data.user.models.User
import com.jaumard.instagrim.network.BuildConfig
import com.jaumard.instagrim.ui.common.CommonNavigator
import com.jaumard.instagrim.ui.gallery.GalleryActivity
import com.jaumard.instagrim.ui.login.LoginActivity
import javax.inject.Inject

class LoginNavigator @Inject constructor(context: LoginActivity) : CommonNavigator(context), GalleryAccessor {
    fun loginFromInstagram() {
        val url = BuildConfig.INSTAGRAM_BASE_URL + context.getString(R.string.path_instagram_login,
                BuildConfig.INSTAGRAM_CLIENT_ID,
                Uri.encode(BuildConfig.INSTAGRAM_URL_REDIRECTION))
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }

    override fun goToGallery(user: User, avatar: ImageView, text: TextView) {
        val intent = GalleryActivity.getIntent(context, user)
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(context, Pair<View, String>(avatar, context.getString(R.string.avatar_transition_name))
                , Pair<View, String>(text, context.getString(R.string.username_transition_name)))
        context.startActivity(intent, options.toBundle())
    }
}
package com.jaumard.instagrim.ui.login.navigation

import android.widget.ImageView
import android.widget.TextView
import com.jaumard.instagrim.data.user.models.User

interface GalleryAccessor {
    fun goToGallery(user: User, avatar: ImageView, text: TextView)
}
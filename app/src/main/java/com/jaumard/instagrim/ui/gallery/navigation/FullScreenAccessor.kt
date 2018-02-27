package com.jaumard.instagrim.ui.gallery.navigation

import android.view.View
import com.jaumard.instagrim.data.media.models.Media

interface FullScreenAccessor {
    fun goToFullScreen(view: View, selected: Media, items: List<Media>)
}
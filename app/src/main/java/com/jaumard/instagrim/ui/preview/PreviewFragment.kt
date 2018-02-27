package com.jaumard.instagrim.ui.preview

import android.databinding.DataBindingUtil
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import com.jaumard.instagrim.R
import com.jaumard.instagrim.data.media.models.Media
import com.jaumard.instagrim.databinding.ItemPreviewBinding
import com.jaumard.instagrim.ui.preview.PreviewActivity.Companion.KEY_INITIAL_MEDIA
import com.jaumard.instagrim.ui.preview.PreviewActivity.Companion.KEY_SELECTED_MEDIA
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.item_preview.*

class PreviewFragment : DaggerFragment() {
    companion object {
        fun getInstance(current: Media, initialMediaId: String): PreviewFragment {
            val bundle = Bundle()
            bundle.putParcelable(KEY_SELECTED_MEDIA, current)
            bundle.putString(KEY_INITIAL_MEDIA, initialMediaId)
            val previewFragment = PreviewFragment()
            previewFragment.arguments = bundle
            return previewFragment
        }
    }

    private lateinit var current: Media
    private lateinit var initialMediaId: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding: ItemPreviewBinding = DataBindingUtil.inflate(inflater, R.layout.item_preview, container, false)
        current = arguments!!.getParcelable(KEY_SELECTED_MEDIA)
        initialMediaId = arguments!!.getString(KEY_INITIAL_MEDIA)
        binding.data = current
        binding.img.post({ startEnterTransition(img) })
        return binding.root
    }

    private fun startEnterTransition(img: ImageView) {
        if (current.id == initialMediaId) {
            img.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    img.viewTreeObserver.removeOnPreDrawListener(this)
                    activity?.supportStartPostponedEnterTransition()
                    return true
                }
            })
        }
    }

    fun getMediaImageView(): ImageView? {
        return if (isViewInBounds(activity?.window?.decorView, img)) {
            img
        } else null
    }

    /**
     * Returns true if {@param view} is contained within {@param container}'s bounds.
     */
    private fun isViewInBounds(container: View?, view: View): Boolean {
        val containerBounds = Rect()
        container?.getHitRect(containerBounds)
        return view.getLocalVisibleRect(containerBounds)
    }
}
package com.jaumard.instagrim.ui.gallery

import android.databinding.BindingAdapter
import android.support.annotation.LayoutRes
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import com.jaumard.instagrim.BR
import com.jaumard.instagrim.data.media.models.Media
import com.jaumard.instagrim.ui.gallery.navigation.FullScreenAccessor
import com.jaumard.instagrim.ui.gallery.navigation.MediaDataListener
import com.jaumard.instagrim.ui.utils.SpaceItemDecorator
import com.jaumard.recyclerviewbinding.ItemBinder
import com.jaumard.recyclerviewbinding.RecyclerViewBindingAdapter.setRecyclerItems

object GalleryBindingAdapter {
    private const val VISIBLE_THRESHOLD = 5

    @JvmStatic
    @BindingAdapter("items", "itemLayout", "fullScreenAccessor", "dataLoader")
    fun setRecyclerItems(recyclerView: RecyclerView, items: List<Media>, @LayoutRes itemLayout: Int, fullScreenAccessor: FullScreenAccessor,
                         dataLoader: MediaDataListener) {
        val additionalData = ArrayList<android.support.v4.util.Pair<Int, Any>>()
        additionalData.add(android.support.v4.util.Pair(BR.fullScreenAccessor, fullScreenAccessor))
        additionalData.add(android.support.v4.util.Pair(BR.list, items))
        if (recyclerView.itemDecorationCount == 0) {
            recyclerView.layoutManager = GridLayoutManager(recyclerView.context, 3)
            recyclerView.addItemDecoration(SpaceItemDecorator())
            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val totalItemCount = dataLoader.getDataCount()
                    val lastVisibleItem = (recyclerView?.layoutManager as GridLayoutManager).findLastVisibleItemPosition()
                    if (!dataLoader.isLoading() && totalItemCount <= (lastVisibleItem + VISIBLE_THRESHOLD)) {
                        dataLoader.onLoadMore()
                    }
                }
            })
        }
        setRecyclerItems(recyclerView, items, ItemBinder(BR.data, itemLayout, additionalData), null)
    }


}
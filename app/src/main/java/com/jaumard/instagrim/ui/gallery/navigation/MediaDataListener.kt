package com.jaumard.instagrim.ui.gallery.navigation

interface MediaDataListener {
    fun onLoadMore()
    fun isLoading(): Boolean
    fun getDataCount(): Int
}
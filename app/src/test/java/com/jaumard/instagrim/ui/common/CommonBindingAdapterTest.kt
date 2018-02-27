package com.jaumard.instagrim.ui.common

import android.view.View
import com.jaumard.instagrim.ui.gallery.views.ImageCounterView
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.Rule
import org.junit.Test
import org.mockito.junit.MockitoJUnit
import org.mockito.quality.Strictness

class CommonBindingAdapterTest {
    @get:Rule
    internal val mockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS)

    @Test
    fun setCouncounterViewterText() {
        val counterView = mock<ImageCounterView>()
        CommonBindingAdapter.setCounterText(counterView, 3)
        verify(counterView).text = "3"
    }

    @Test
    fun setVisibilityVisible() {
        val counterView = mock<View>()
        CommonBindingAdapter.setVisibility(counterView, true)
        verify(counterView).visibility = View.VISIBLE
    }

    @Test
    fun setVisibilityInvisible() {
        val counterView = mock<View>()
        CommonBindingAdapter.setVisibility(counterView, false)
        verify(counterView).visibility = View.GONE
    }

}
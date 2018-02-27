package com.jaumard.instagrim.di

import com.jaumard.instagrim.ui.preview.PreviewFragment
import com.jaumard.instagrim.ui.preview.di.PreviewModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
internal abstract class FragmentBuilder {
    @ContributesAndroidInjector(modules = [PreviewModule::class])
    internal abstract fun bindPreviewFragment(): PreviewFragment
}

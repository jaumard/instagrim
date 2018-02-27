package com.jaumard.instagrim.di

import com.jaumard.instagrim.ui.gallery.GalleryActivity
import com.jaumard.instagrim.ui.gallery.di.GalleryModule
import com.jaumard.instagrim.ui.login.LoginActivity
import com.jaumard.instagrim.ui.login.di.LoginModule
import com.jaumard.instagrim.ui.preview.PreviewActivity
import com.jaumard.instagrim.ui.preview.di.PreviewModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilder {
    @ContributesAndroidInjector(modules = [LoginModule::class])
    internal abstract fun bindLoginActivity(): LoginActivity

    @ContributesAndroidInjector(modules = [GalleryModule::class])
    internal abstract fun bindGalleryActivity(): GalleryActivity

    @ContributesAndroidInjector(modules = [PreviewModule::class])
    internal abstract fun bindPreviewActivity(): PreviewActivity
}

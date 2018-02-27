package com.jaumard.instagrim.ui.gallery.di

import android.arch.lifecycle.ViewModel
import com.jaumard.instagrim.di.annotations.ViewModelKey
import com.jaumard.instagrim.ui.gallery.GalleryViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class GalleryModule {

    @Binds
    @IntoMap
    @ViewModelKey(GalleryViewModel::class)
    internal abstract fun bindLoginViewModel(viewModel: GalleryViewModel): ViewModel


}

package com.jaumard.instagrim.ui.login.di

import android.arch.lifecycle.ViewModel
import com.jaumard.instagrim.di.annotations.ViewModelKey
import com.jaumard.instagrim.ui.login.LoginViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class LoginModule {
    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    internal abstract fun bindLoginViewModel(viewModel: LoginViewModel): ViewModel


}

package com.jaumard.instagrim

import com.jaumard.instagrim.di.DaggerAppComponent
import com.jaumard.instagrim.di.TestDataModule
import com.jaumard.instagrim.di.TestNetworkModule
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import timber.log.Timber

class TestApp : App() {
    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        val appComponent = DaggerAppComponent.builder()
                .application(this)
                .networkModule(TestNetworkModule())
                .databaseModule(TestDataModule())
                .build()
        appComponent.inject(this)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        return appComponent
    }
}
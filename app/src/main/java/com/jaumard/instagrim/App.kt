package com.jaumard.instagrim

import com.jaumard.instagrim.data.AppDatabase
import com.jaumard.instagrim.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import timber.log.Timber
import javax.inject.Inject

open class App : DaggerApplication() {
    @Inject
    lateinit var database: AppDatabase

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        val appComponent = DaggerAppComponent.builder()
                .application(this)
                .build()
        appComponent.inject(this)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        return appComponent
    }
}
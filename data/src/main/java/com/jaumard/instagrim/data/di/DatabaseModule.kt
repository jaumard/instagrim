package com.jaumard.instagrim.data.di

import android.arch.persistence.room.Room
import android.content.Context
import com.jaumard.instagrim.data.AppDatabase
import com.jaumard.instagrim.data.BuildConfig.INSTAGRIM_DATABASE_NAME
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
open class DatabaseModule {
    @Provides
    @Singleton
    open fun provideDataBase(context: Context) = Room.databaseBuilder(context, AppDatabase::class.java, INSTAGRIM_DATABASE_NAME).build()

    @Provides
    internal fun provideUserDao(db: AppDatabase) = db.userDao()

    @Provides
    internal fun provideMediaDao(db: AppDatabase) = db.mediaDao()
}
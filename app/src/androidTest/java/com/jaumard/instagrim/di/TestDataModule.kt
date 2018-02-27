package com.jaumard.instagrim.di

import android.arch.persistence.room.Room
import android.content.Context
import com.jaumard.instagrim.data.AppDatabase
import com.jaumard.instagrim.data.di.DatabaseModule

class TestDataModule : DatabaseModule() {
    override fun provideDataBase(context: Context) = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
}
package com.jaumard.instagrim.data

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.support.annotation.VisibleForTesting
import com.jaumard.instagrim.data.media.dao.MediaDao
import com.jaumard.instagrim.data.media.models.Media
import com.jaumard.instagrim.data.user.dao.UserDao
import com.jaumard.instagrim.data.user.models.User

@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
@Database(entities = [User::class, Media::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    abstract fun userDao(): UserDao

    internal abstract fun mediaDao(): MediaDao
}
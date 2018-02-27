package com.jaumard.instagrim.data.user.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.jaumard.instagrim.data.user.models.User
import io.reactivex.Single

@Dao
interface UserDao {
    companion object {
        private const val TABLE_NAME = "user"
    }

    @Query("SELECT * FROM ${TABLE_NAME} limit 1")
    fun get(): Single<User>

    @Insert
    fun insert(user: User)

    @Query("DELETE FROM ${TABLE_NAME}")
    fun delete()
}
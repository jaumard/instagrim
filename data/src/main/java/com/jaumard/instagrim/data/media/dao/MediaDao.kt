package com.jaumard.instagrim.data.media.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.jaumard.instagrim.data.media.models.Media
import io.reactivex.Single

@Dao
internal interface MediaDao {
    companion object {
        private const val TABLE_NAME = "media"
    }

    @Query("SELECT * FROM $TABLE_NAME")
    fun getAll(): Single<List<Media>>

    @Query("DELETE FROM $TABLE_NAME")
    fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg media: Media)

}
package com.jaumard.instagrim.data.media

import com.jaumard.instagrim.data.media.dao.MediaDao
import com.jaumard.instagrim.data.media.models.Media
import dagger.Lazy
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class MediaLocalDataSource @Inject internal constructor(private val mediaDao: Lazy<MediaDao>) {
    fun getAll(): Single<List<Media>> {
        return mediaDao.get().getAll()
    }

    fun deleteAll(): Completable {
        return Completable.defer {
            mediaDao.get().deleteAll()
            Completable.complete()
        }
    }

    fun insert(media: List<Media>): Completable {
        return Completable.defer {
            mediaDao.get().insert(*media.toTypedArray())
            Completable.complete()
        }
    }

}
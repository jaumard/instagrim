package com.jaumard.instagrim.data.media

import com.jaumard.instagrim.data.media.mapper.MediaMapper
import com.jaumard.instagrim.data.media.models.UserMedia
import com.jaumard.instagrim.data.user.UserRepository
import com.jaumard.instagrim.network.exception.UnAuthorizedException
import com.jaumard.instagrim.network.media.InstagramMediaNetworkDataSource
import com.jaumard.instagrim.network.utils.ConnectionUtils
import dagger.Lazy
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaRepository @Inject internal constructor(private val mediaNetworkDataSource: Lazy<InstagramMediaNetworkDataSource>,
                                                   private val mediaLocalDataSource: Lazy<MediaLocalDataSource>,
                                                   private val userRepository: Lazy<UserRepository>,
                                                   private val mapper: Lazy<MediaMapper>,
                                                   private val connectionUtils: Lazy<ConnectionUtils>) {
    fun retrieve(nextId: String?): Observable<UserMedia> {
        return Observable.merge(fetchFromDatabase(nextId).toObservable(), fetchFromNetwork(nextId).toObservable()).distinct()
    }

    fun deleteAll(): Completable {
        return mediaLocalDataSource.get().deleteAll()
    }

    private fun fetchFromNetwork(nextId: String?): Maybe<UserMedia> {
        return Maybe.defer {
            if (connectionUtils.get().hasConnexion()) {
                userRepository.get().retrieve()
                        .flatMap {
                            mediaNetworkDataSource.get().retrieve(it.token, nextId)
                        }
                        .map {
                            mapper.get().mapFromResponse(it)
                        }
                        .flatMap {
                            saveMediaLocally(nextId, it)
                        }
                        .onErrorResumeNext {
                            if (it is UnAuthorizedException) {
                                userRepository.get().logout().andThen(Single.error(it))
                            } else {
                                Single.error(it)
                            }
                        }.toMaybe()
            } else {
                Maybe.empty()
            }

        }
    }

    private fun saveMediaLocally(previousNextId: String?, media: UserMedia): Single<UserMedia> {
        return Completable.defer {
            if (previousNextId == null) {
                val localDataSource = mediaLocalDataSource.get()
                localDataSource.deleteAll().doOnComplete({ Timber.i("Media all delete from database") })
            } else {
                Completable.complete()
            }
        }
                .andThen(mediaLocalDataSource.get().insert(media.media).doOnComplete({ Timber.i("${media.media.size} media save on database") }))
                .andThen(fetchFromDatabase(media.nextMediaId))
    }

    private fun fetchFromDatabase(nextMediaId: String?): Single<UserMedia> {
        return mediaLocalDataSource.get().getAll().map {
            Timber.i("${it.size} media items load from database")
            UserMedia(nextMediaId, it)
        }
    }
}
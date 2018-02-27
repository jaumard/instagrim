package com.jaumard.instagrim.data.user

import android.support.annotation.VisibleForTesting
import com.jaumard.instagrim.data.user.mapper.UserMapper
import com.jaumard.instagrim.data.user.models.User
import com.jaumard.instagrim.network.auth.InstagramUserNetworkDataSource
import dagger.Lazy
import io.reactivex.Completable
import io.reactivex.Single
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject internal constructor(private val userNetworkDataSource: Lazy<InstagramUserNetworkDataSource>,
                                                  private val userLocalDataSource: Lazy<UserLocalDataSource>,
                                                  private val mapper: Lazy<UserMapper>) {
    @VisibleForTesting
    private val user = AtomicReference<User?>()

    fun retrieve(code: String? = null): Single<User> {
        return when {
            user.get() != null -> Single.just(user.get())
            code == null -> userLocalDataSource.get().retrieve().doOnSuccess { user.set(it) }
            else -> login(code)
        }
    }

    fun logout(): Completable {
        return userLocalDataSource.get().delete().doOnComplete { user.set(null) }
    }

    private fun login(code: String): Single<User> {
        return userNetworkDataSource.get().login(code)
                .map { mapper.get().mapFromDto(it) }
                .flatMap { userLocalDataSource.get().create(it).andThen(Single.just(it)) }
                .doOnSuccess {
                    user.set(it)
                }
    }

}
package com.jaumard.instagrim.data.user

import com.jaumard.instagrim.data.user.dao.UserDao
import com.jaumard.instagrim.data.user.models.User
import com.jaumard.instagrim.network.exception.UnAuthorizedException
import dagger.Lazy
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

internal class UserLocalDataSource @Inject internal constructor(private val userDao: Lazy<UserDao>) {
    fun retrieve(): Single<User> {
        return userDao.get().get().onErrorResumeNext {
            Single.error(UnAuthorizedException(it))
        }
    }

    fun create(user: User): Completable {
        return Completable.defer {
            userDao.get().insert(user)
            Completable.complete()
        }
    }

    fun delete(): Completable {
        return Completable.defer {
            userDao.get().delete()
            Completable.complete()
        }
    }
}
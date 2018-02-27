package com.jaumard.instagrim.network.auth

import android.support.annotation.VisibleForTesting
import com.jaumard.instagrim.network.BuildConfig
import com.jaumard.instagrim.network.user.dto.LoginResponse
import dagger.Lazy
import io.reactivex.Single
import javax.inject.Inject

@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
class InstagramUserNetworkDataSource @Inject internal constructor(private val instagramApi: Lazy<InstagramAuthApi>) {
    companion object {
        private const val AUTHORIZATION_TYPE = "authorization_code"
    }

    fun login(code: String): Single<LoginResponse> {
        return instagramApi.get().login(BuildConfig.INSTAGRAM_CLIENT_ID, BuildConfig.INSTAGRAM_SECRET, AUTHORIZATION_TYPE, code,
                BuildConfig.INSTAGRAM_URL_REDIRECTION)
    }
}
package com.jaumard.instagrim.network.media

import android.support.annotation.VisibleForTesting
import com.jaumard.instagrim.network.exception.InstagramException
import com.jaumard.instagrim.network.exception.UnAuthorizedException
import com.jaumard.instagrim.network.media.dto.MediaMetaResponse
import com.jaumard.instagrim.network.media.dto.UserMediaResponse
import dagger.Lazy
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.HttpException
import retrofit2.Retrofit
import javax.inject.Inject

@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
class InstagramMediaNetworkDataSource @Inject internal constructor(private val retrofit: Lazy<Retrofit>,
                                                                   private val instagramUserApi: Lazy<InstagramMediaApi>) {
    companion object {
        const val MAX_COUNT = 19 //max before instagram just ignore the param in sandbox mode
        private const val HTTP_MAX_REQUEST_EXCEEDED = 429
    }

    fun retrieve(userToken: String, nextId: String? = null): Single<UserMediaResponse> {
        return instagramUserApi.get().getMedias(userToken, nextId, MAX_COUNT)
                .flatMap {
                    if (it.meta.errorType == "OAuthAccessTokenException" || it.meta.errorType == "OAuthException") {
                        Single.error(UnAuthorizedException())
                    } else if (it.meta.code != 200) {
                        Single.error(mapError(it.meta))
                    } else {
                        Single.just(it)
                    }
                }
                .onErrorResumeNext {
                    if (it is HttpException && it.code() == HTTP_MAX_REQUEST_EXCEEDED) {
                        val errorBody = it.response().errorBody()!!
                        val errorConverter: Converter<ResponseBody, MediaMetaResponse> = retrofit.get().responseBodyConverter(MediaMetaResponse::class.java, arrayOf())
                        val error: MediaMetaResponse = errorConverter.convert(errorBody)
                        Single.error(mapError(error))
                    } else {
                        Single.error(it)
                    }
                }
    }

    private fun mapError(mediaMetaResponse: MediaMetaResponse): InstagramException = InstagramException(mediaMetaResponse.code,
            "${mediaMetaResponse.errorMessage} (${mediaMetaResponse.errorType})")
}
package com.jaumard.instagrim.network.media

import com.jaumard.instagrim.network.media.dto.UserMediaResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface InstagramMediaApi {
    @GET("v1/users/self/media/recent")
    fun getMedias(@Query("access_token") token: String,
                  @Query("max_id") maxId: String?,
                  @Query("count") maxResult: Int): Single<UserMediaResponse>
}
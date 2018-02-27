package com.jaumard.instagrim.network.auth

import com.jaumard.instagrim.network.user.dto.LoginResponse
import io.reactivex.Single
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface InstagramAuthApi {
    @FormUrlEncoded
    @POST("oauth/access_token")
    fun login(@Field("client_id") clientId: String,
              @Field("client_secret") clientSecret: String,
              @Field("grant_type") grantType: String,
              @Field("code") code: String,
              @Field("redirect_uri") redirectUri: String): Single<LoginResponse>

}
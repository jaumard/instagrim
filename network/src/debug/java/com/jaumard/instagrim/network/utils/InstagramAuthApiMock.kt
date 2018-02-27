package com.jaumard.instagrim.network.utils

import com.jaumard.instagrim.network.auth.InstagramAuthApi
import com.jaumard.instagrim.network.user.dto.LoginResponse
import com.jaumard.instagrim.network.user.dto.UserResponse
import io.reactivex.Single

class InstagramAuthApiMock : InstagramAuthApi {
    override fun login(clientId: String, clientSecret: String, grantType: String, code: String, redirectUri: String): Single<LoginResponse> {
        return Single.just(LoginResponse("token", UserResponse("id", "name", "fname", "")))
    }
}
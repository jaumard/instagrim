package com.jaumard.instagrim.data.user.mapper

import com.jaumard.instagrim.data.user.models.User
import com.jaumard.instagrim.network.user.dto.LoginResponse
import javax.inject.Inject

internal class UserMapper @Inject constructor() {
    fun mapFromDto(loginResponse: LoginResponse) = User(loginResponse.token, loginResponse.user.id,
            loginResponse.user.userName,
            loginResponse.user.fullName,
            loginResponse.user.profilePicture)
}
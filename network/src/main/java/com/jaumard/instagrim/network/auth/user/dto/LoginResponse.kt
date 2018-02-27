package com.jaumard.instagrim.network.user.dto

import com.google.gson.annotations.SerializedName

data class LoginResponse(@SerializedName("access_token") val token: String, val user: UserResponse)
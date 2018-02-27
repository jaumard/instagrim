package com.jaumard.instagrim.network.user.dto

import com.google.gson.annotations.SerializedName

data class UserResponse(val id: String, @SerializedName("username") val userName: String,
                        @SerializedName("full_name") val fullName: String,
                        @SerializedName("profile_picture") val profilePicture: String)
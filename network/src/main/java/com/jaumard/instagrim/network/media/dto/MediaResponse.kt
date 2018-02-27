package com.jaumard.instagrim.network.media.dto

import com.google.gson.annotations.SerializedName

data class MediaResponse(val id: String, val type: String, val link: String, @SerializedName("user_has_liked") val userHasLiked: Boolean,
                         val images: MediaImagesResponse, val likes: MediaInfoResponse, val comments: MediaInfoResponse)
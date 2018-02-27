package com.jaumard.instagrim.network.media.dto

import com.google.gson.annotations.SerializedName

data class MediaMetaResponse(val code: Int, @SerializedName("error_type") val errorType: String?,
                             @SerializedName("error_message") val errorMessage: String?)
package com.jaumard.instagrim.network.media.dto

import com.google.gson.annotations.SerializedName

data class MediaImagesResponse(val thumbnail: MediaImageResponse, @SerializedName("low_resolution") val lowResolution: MediaImageResponse,
                               @SerializedName("standard_resolution") val standardResolution: MediaImageResponse)
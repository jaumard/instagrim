package com.jaumard.instagrim.network.utils

import com.jaumard.instagrim.network.media.InstagramMediaApi
import com.jaumard.instagrim.network.media.dto.*
import io.reactivex.Single

class InstagramMediaApiMock : InstagramMediaApi {
    override fun getMedias(token: String, maxId: String?, maxResult: Int): Single<UserMediaResponse> {
        val list = ArrayList<MediaResponse>()
        for (i in 0..20) {
            list.add(generateMockMedia(i))
        }
        val response = UserMediaResponse(list, MediaMetaResponse(200, null, null), MediasPaginationResponse("id"))
        return Single.just(response)
    }

    private fun generateMockMedia(index: Int): MediaResponse {
        return MediaResponse(index.toString(), "image", "link", false,
                MediaImagesResponse(MediaImageResponse("thumb"), MediaImageResponse("low"), MediaImageResponse("high")),
                MediaInfoResponse(index), MediaInfoResponse(index + 1))
    }
}
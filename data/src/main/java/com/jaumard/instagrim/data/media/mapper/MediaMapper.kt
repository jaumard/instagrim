package com.jaumard.instagrim.data.media.mapper

import com.jaumard.instagrim.data.media.models.Media
import com.jaumard.instagrim.data.media.models.UserMedia
import com.jaumard.instagrim.network.media.dto.UserMediaResponse
import javax.inject.Inject

internal class MediaMapper @Inject constructor() {
    fun mapFromResponse(mediasResponse: UserMediaResponse): UserMedia {
        val media = ArrayList<Media>()
        mediasResponse.data.filter { it.type == "image" }.mapTo(media) {
            Media(it.id, it.images.lowResolution.url, it.images.standardResolution.url, it.likes.count, it.comments.count, it.userHasLiked)
        }
        return UserMedia(mediasResponse.pagination.nextMaxId, media)
    }
}
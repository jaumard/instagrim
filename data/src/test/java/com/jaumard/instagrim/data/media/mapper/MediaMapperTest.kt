package com.jaumard.instagrim.data.media.mapper

import com.jaumard.instagrim.network.media.dto.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MediaMapperTest {
    @Test
    fun mapFromResponse() {
        val mediaMapper = MediaMapper()

        val mediaResult = mediaMapper.mapFromResponse(getMediaResponse())
        assertEquals(1, mediaResult.media.size)
        assertEquals("nextId", mediaResult.nextMediaId)
        val media = mediaResult.media[0]
        assertEquals("id", media.id)
        assertEquals(1, media.likes)
        assertEquals(2, media.comments)
        assertEquals("low", media.thumbnail)
        assertEquals("high", media.url)
        assertTrue(media.userLike)
    }

    private fun getMediaResponse(): UserMediaResponse {
        val mediaResponse = ArrayList<MediaResponse>()
        mediaResponse.add(MediaResponse("id", "ignored", "link", true,
                MediaImagesResponse(MediaImageResponse("thumb"), MediaImageResponse("low"), MediaImageResponse("high")),
                MediaInfoResponse(1), MediaInfoResponse(2)))
        mediaResponse.add(MediaResponse("id", "image", "link", true,
                MediaImagesResponse(MediaImageResponse("thumb"), MediaImageResponse("low"), MediaImageResponse("high")),
                MediaInfoResponse(1), MediaInfoResponse(2)))
        val paginationResponse = MediasPaginationResponse("nextId")
        val metaResponse = MediaMetaResponse(200, null, null)
        return UserMediaResponse(mediaResponse, metaResponse, paginationResponse)
    }

}
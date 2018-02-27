package com.jaumard.instagrim.network.media.dto

data class UserMediaResponse(val data: List<MediaResponse>, val meta: MediaMetaResponse,
                             val pagination: MediasPaginationResponse)
package com.jaumard.instagrim.network.media

import com.jaumard.instagrim.network.exception.InstagramException
import com.jaumard.instagrim.network.exception.UnAuthorizedException
import com.jaumard.instagrim.network.media.dto.MediaMetaResponse
import com.jaumard.instagrim.network.media.dto.UserMediaResponse
import com.nhaarman.mockito_kotlin.*
import dagger.Lazy
import io.reactivex.Single
import okhttp3.ResponseBody
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.quality.Strictness
import retrofit2.Converter
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit

class InstagramMediaNetworkDataSourceTest {
    @get:Rule
    internal val mockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS)

    @Mock
    internal lateinit var instagramUserApi: InstagramMediaApi

    @Mock
    internal lateinit var retrofit: Retrofit

    private lateinit var instagramMediaNetworkDataSource: InstagramMediaNetworkDataSource

    @Before
    fun setUp() {
        instagramMediaNetworkDataSource = InstagramMediaNetworkDataSource(Lazy { retrofit }, Lazy { instagramUserApi })
    }

    @Test
    fun retrieveWithRequestExceededHttpError() {
        val metaResponse = MediaMetaResponse(429, null, null)
        val responseBody = mock<ResponseBody>()
        val httpResponse = mock<Response<*>> {
            on { errorBody() } doReturn responseBody
        }
        val httpException = mock<HttpException> {
            on { code() } doReturn 429
            on { response() } doReturn httpResponse
        }
        val converter = mock<Converter<ResponseBody, MediaMetaResponse>>()
        given(converter.convert(any())).willReturn(metaResponse)

        val userToken = "token"

        given(instagramUserApi.getMedias(eq(userToken), anyOrNull(), eq(InstagramMediaNetworkDataSource.MAX_COUNT))).willReturn(Single.error(httpException))
        given(retrofit.responseBodyConverter<MediaMetaResponse>(eq(MediaMetaResponse::class.java), any())).willReturn(converter)

        instagramMediaNetworkDataSource.retrieve(userToken).test().assertError(InstagramException::class.java).assertError {
            (it as InstagramException).code == 429
        }
    }

    @Test
    fun retrieve() {
        val metaResponse = MediaMetaResponse(200, null, null)
        val mediaResponse = mock<UserMediaResponse> {
            on { meta } doReturn metaResponse
        }
        val userToken = "token"

        given(instagramUserApi.getMedias(eq(userToken), anyOrNull(), eq(InstagramMediaNetworkDataSource.MAX_COUNT))).willReturn(Single.just(mediaResponse))

        instagramMediaNetworkDataSource.retrieve(userToken).test().assertNoErrors().assertValue(mediaResponse)
    }

    @Test
    fun retrieveUnAuthorized() {
        val metaResponse = mock<MediaMetaResponse> {
            on { errorType } doReturn "OAuthException"
        }
        val mediaResponse = mock<UserMediaResponse> {
            on { meta } doReturn metaResponse
        }
        val userToken = "token"

        given(instagramUserApi.getMedias(eq(userToken), anyOrNull(), eq(InstagramMediaNetworkDataSource.MAX_COUNT))).willReturn(Single.just(mediaResponse))

        instagramMediaNetworkDataSource.retrieve(userToken).test().assertError(UnAuthorizedException::class.java)

        given(metaResponse.errorType).willReturn("OAuthAccessTokenException")

        instagramMediaNetworkDataSource.retrieve(userToken).test().assertError(UnAuthorizedException::class.java)
    }

    @Test
    fun retrieveUnknownError() {
        val metaResponse = mock<MediaMetaResponse> {
            on { errorType } doReturn "ko"
            on { errorMessage } doReturn "test"
            on { code } doReturn 404
        }
        val mediaResponse = mock<UserMediaResponse> {
            on { meta } doReturn metaResponse
        }
        val userToken = "token"

        given(instagramUserApi.getMedias(eq(userToken), anyOrNull(), eq(InstagramMediaNetworkDataSource.MAX_COUNT))).willReturn(Single.just(mediaResponse))

        val instagramException = instagramMediaNetworkDataSource.retrieve(userToken).test().assertError(InstagramException::class.java).errors()[0] as InstagramException
        assertEquals(404, instagramException.code)
        assertEquals("test (ko)", instagramException.message)

    }

}
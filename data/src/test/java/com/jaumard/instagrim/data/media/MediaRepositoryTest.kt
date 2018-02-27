package com.jaumard.instagrim.data.media

import com.jaumard.instagrim.data.media.mapper.MediaMapper
import com.jaumard.instagrim.data.media.models.Media
import com.jaumard.instagrim.data.media.models.UserMedia
import com.jaumard.instagrim.data.user.UserRepository
import com.jaumard.instagrim.data.user.models.User
import com.jaumard.instagrim.network.exception.InstagramException
import com.jaumard.instagrim.network.exception.UnAuthorizedException
import com.jaumard.instagrim.network.media.InstagramMediaNetworkDataSource
import com.jaumard.instagrim.network.media.dto.UserMediaResponse
import com.jaumard.instagrim.network.utils.ConnectionUtils
import com.nhaarman.mockito_kotlin.*
import dagger.Lazy
import io.reactivex.Completable
import io.reactivex.Single
import junit.framework.Assert.assertSame
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyList
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.quality.Strictness

class MediaRepositoryTest {
    @get:Rule
    internal val mockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS)

    @Mock
    internal lateinit var mediaNetworkDataSource: InstagramMediaNetworkDataSource
    @Mock
    internal lateinit var mediaLocalDataSource: MediaLocalDataSource
    @Mock
    internal lateinit var userRepository: UserRepository
    @Mock
    internal lateinit var mediaMapper: MediaMapper
    @Mock
    internal lateinit var connectionUtils: ConnectionUtils

    private lateinit var mediaRepository: MediaRepository

    @Before
    fun setUp() {
        mediaRepository = MediaRepository(Lazy { mediaNetworkDataSource }, Lazy { mediaLocalDataSource }, Lazy { userRepository },
                Lazy { mediaMapper }, Lazy { connectionUtils })
    }

    @Test
    fun retrieveWithInstagramError() {
        val userToken = "token"
        val user = mock<User> {
            on { token } doReturn userToken
        }
        val localMedia = ArrayList<Media>()

        given(userRepository.retrieve()).willReturn(Single.just(user))
        given(mediaLocalDataSource.getAll()).willReturn(Single.just(localMedia))
        given(mediaNetworkDataSource.retrieve(eq(userToken), anyOrNull())).willReturn(Single.error(InstagramException(500, "ko")))
        given(connectionUtils.hasConnexion()).willReturn(true)
        mediaRepository.retrieve(null).test().assertError(InstagramException::class.java).assertErrorMessage("ko")
    }

    @Test
    fun retrieveWithNoNetwork() {
        val localMedia = ArrayList<Media>()

        given(mediaLocalDataSource.getAll()).willReturn(Single.just(localMedia))
        given(connectionUtils.hasConnexion()).willReturn(false)
        mediaRepository.retrieve(null).test().assertNoErrors().assertComplete().assertTerminated()
                .assertValueCount(1)
                .assertValueAt(0, {
                    assertSame(localMedia, it.media)
                    true
                })
    }

    @Test
    fun retrieveWithUnAuthorize() {
        val userToken = "token"
        val user = mock<User> {
            on { token } doReturn userToken
        }
        val localMedia = ArrayList<Media>()

        given(connectionUtils.hasConnexion()).willReturn(true)
        given(userRepository.retrieve()).willReturn(Single.just(user))
        given(mediaLocalDataSource.getAll()).willReturn(Single.just(localMedia))
        given(mediaNetworkDataSource.retrieve(eq(userToken), anyOrNull())).willReturn(Single.error(UnAuthorizedException()))
        given(userRepository.logout()).willReturn(Completable.complete())
        mediaRepository.retrieve(null).test().assertError(UnAuthorizedException::class.java)
    }

    @Test
    fun retrieveWithLocalAndRemoteEqual() {
        val userToken = "token"
        val user = mock<User> {
            on { token } doReturn userToken
        }
        val localMedia = ArrayList<Media>()

        val remoteMediaMapped = ArrayList<Media>()
        val mediaResponse = mock<UserMediaResponse>()
        val userMedia = mock<UserMedia> {
            on { media } doReturn remoteMediaMapped
        }
        given(userRepository.retrieve()).willReturn(Single.just(user))
        given(mediaMapper.mapFromResponse(eq(mediaResponse))).willReturn(userMedia)
        given(mediaLocalDataSource.getAll()).willReturn(Single.just(localMedia))
        given(mediaNetworkDataSource.retrieve(eq(userToken), anyOrNull())).willReturn(Single.just(mediaResponse))
        given(mediaLocalDataSource.insert(eq(remoteMediaMapped))).willReturn(Completable.complete())
        given(connectionUtils.hasConnexion()).willReturn(true)
        given(mediaLocalDataSource.deleteAll()).willReturn(Completable.complete())
        mediaRepository.retrieve(null).test().assertNoErrors().assertComplete().assertTerminated()
                .assertValueCount(1)
                .assertValueAt(0, {
                    assertSame(localMedia, it.media)
                    true
                })
    }

    @Test
    fun retrieveWithLocalAndRemoteDifferent() {
        val userToken = "token"
        val user = mock<User> {
            on { token } doReturn userToken
        }
        val localMedia = ArrayList<Media>()
        val remoteMediaMapped = ArrayList<Media>()
        remoteMediaMapped.add(mock())

        val mediaResponse = mock<UserMediaResponse>()
        val userMedia = mock<UserMedia> {
            on { media } doReturn remoteMediaMapped
        }
        //FIXME should be eq(remoteMediaMapped) not anyList() but for some reason it's doesn't work
        given(mediaLocalDataSource.insert(anyList())).willReturn(Completable.complete())
        given(userRepository.retrieve()).willReturn(Single.just(user))
        given(mediaMapper.mapFromResponse(eq(mediaResponse))).willReturn(userMedia)
        given(mediaLocalDataSource.getAll()).willReturn(Single.just(localMedia), Single.just(remoteMediaMapped))
        given(mediaNetworkDataSource.retrieve(eq(userToken), anyOrNull())).willReturn(Single.just(mediaResponse))
        given(mediaLocalDataSource.deleteAll()).willReturn(Completable.complete())
        given(connectionUtils.hasConnexion()).willReturn(true)
        mediaRepository.retrieve(null).test().assertNoErrors().assertComplete().assertTerminated()
                .assertValueCount(2)
                .assertValueAt(0, {
                    assertSame(localMedia, it.media)
                    true
                })
                .assertValueAt(1, {
                    assertSame(remoteMediaMapped, it.media)
                    true
                })
    }

    @Test
    fun deleteAll() {
        given(mediaLocalDataSource.deleteAll()).willReturn(Completable.complete())
        mediaRepository.deleteAll().test().assertNoErrors().assertComplete().assertTerminated()
    }

}
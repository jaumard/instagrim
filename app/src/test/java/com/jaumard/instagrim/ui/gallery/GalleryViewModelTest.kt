package com.jaumard.instagrim.ui.gallery

import com.jaumard.instagrim.R
import com.jaumard.instagrim.data.media.MediaRepository
import com.jaumard.instagrim.data.media.models.Media
import com.jaumard.instagrim.data.media.models.UserMedia
import com.jaumard.instagrim.data.user.UserRepository
import com.jaumard.instagrim.data.user.models.User
import com.jaumard.instagrim.network.exception.UnAuthorizedException
import com.jaumard.instagrim.ui.gallery.navigation.GalleryNavigator
import com.jaumard.instagrim.utils.RxSchedulerProvider
import com.nhaarman.mockito_kotlin.*
import dagger.Lazy
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.schedulers.TestScheduler
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.quality.Strictness
import retrofit2.HttpException
import utils.TrampolineSchedulerRule

class GalleryViewModelTest {
    @get:Rule
    internal val mockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS)

    @get:Rule
    internal val rxRule = TrampolineSchedulerRule()

    @Mock
    internal lateinit var userRepository: UserRepository

    @Mock
    internal lateinit var rxSchedulerProvider: RxSchedulerProvider

    @Mock
    internal lateinit var mediaRepository: MediaRepository

    @Mock
    internal lateinit var galleryNavigator: GalleryNavigator

    private lateinit var galleryViewModel: GalleryViewModel

    @Before
    fun setUp() {
        galleryViewModel = GalleryViewModel(Lazy { userRepository }, mediaRepository, rxSchedulerProvider, galleryNavigator)
    }

    @Test
    fun getDataCount() {
        assertEquals(0, galleryViewModel.getDataCount())

        val media = ArrayList<Media>()
        media.add(mock())
        media.add(mock())
        media.add(mock())
        media.add(mock())
        media.add(mock())
        galleryViewModel.media.set(media)
        assertEquals(5, galleryViewModel.getDataCount())
    }

    @Test
    fun isLoading() {
        assertFalse(galleryViewModel.isLoading.get())
        galleryViewModel.isLoading.set(true)
        assertTrue(galleryViewModel.isLoading.get())
        galleryViewModel.isLoading.set(false)
        assertFalse(galleryViewModel.isLoading.get())
    }

    @Test
    fun onLoadMoreNoNext() {
        galleryViewModel.onLoadMore()
        verifyZeroInteractions(mediaRepository)
    }

    private fun setupData(ioScheduler: TestScheduler, mainScheduler: TestScheduler, userMedia: UserMedia) {
        given(rxSchedulerProvider.io()).willReturn(ioScheduler)
        given(rxSchedulerProvider.main()).willReturn(mainScheduler)

        given(mediaRepository.retrieve(isNull())).willReturn(Observable.just(userMedia))

        galleryViewModel.refresh()
        assertFalse(galleryViewModel.isLoading())
        ioScheduler.triggerActions()
        assertTrue(galleryViewModel.isLoading())
        mainScheduler.triggerActions()
        assertFalse(galleryViewModel.isLoading())
        assertSame(userMedia.media, galleryViewModel.media.get())
    }

    @Test
    fun onLoadMoreWithNext() {
        val ioScheduler = TestScheduler()
        val mainScheduler = TestScheduler()

        val mediaList = ArrayList<Media>()
        mediaList.add(mock())
        val nextId = "nextMediaId"
        val userMedia = mock<UserMedia> {
            on { media } doReturn mediaList
            on { nextMediaId } doReturn nextId
        }

        setupData(ioScheduler, mainScheduler, userMedia)

        given(mediaRepository.retrieve(eq(nextId))).willReturn(Observable.just(userMedia))
        galleryViewModel.onLoadMore()
        verify(mediaRepository).retrieve(eq("nextMediaId"))
    }

    @Test
    fun refresh() {
        val ioScheduler = TestScheduler()
        val mainScheduler = TestScheduler()

        val mediaList = ArrayList<Media>()
        mediaList.add(mock())
        val nextId = "nextMediaId"
        val userMedia = mock<UserMedia> {
            on { media } doReturn mediaList
            on { nextMediaId } doReturn nextId
        }

        setupData(ioScheduler, mainScheduler, userMedia)

        galleryViewModel.refresh()
        assertFalse(galleryViewModel.isLoading())
        ioScheduler.triggerActions()
        assertTrue(galleryViewModel.isLoading())
        mainScheduler.triggerActions()
        assertFalse(galleryViewModel.isLoading())
        assertSame(mediaList, galleryViewModel.media.get())
    }

    @Test
    fun refreshUnAuthorizedException() {
        val ioScheduler = TestScheduler()
        val mainScheduler = TestScheduler()
        given(rxSchedulerProvider.io()).willReturn(ioScheduler)
        given(rxSchedulerProvider.main()).willReturn(mainScheduler)

        given(mediaRepository.retrieve(isNull())).willReturn(Observable.error(mock<UnAuthorizedException>()))

        galleryViewModel.refresh()
        assertFalse(galleryViewModel.isLoading())
        ioScheduler.triggerActions()
        assertTrue(galleryViewModel.isLoading())
        mainScheduler.triggerActions()
        assertFalse(galleryViewModel.isLoading())
        verify(galleryNavigator).goToLogin()
        verify(galleryNavigator).showMessage(eq(R.string.error_logged_out))
    }

    @Test
    fun refreshUnknownException() {
        val ioScheduler = TestScheduler()
        val mainScheduler = TestScheduler()
        given(rxSchedulerProvider.io()).willReturn(ioScheduler)
        given(rxSchedulerProvider.main()).willReturn(mainScheduler)
        val exception = mock<HttpException>()
        given(mediaRepository.retrieve(isNull())).willReturn(Observable.error(exception))

        galleryViewModel.refresh()
        assertFalse(galleryViewModel.isLoading())
        ioScheduler.triggerActions()
        assertTrue(galleryViewModel.isLoading())
        mainScheduler.triggerActions()
        assertFalse(galleryViewModel.isLoading())
        verify(galleryNavigator).showError(eq(exception))
    }

    @Test
    fun triggerRefreshOnUserChange() {
        val user = mock<User>()
        val spyViewModel = spy(galleryViewModel)

        val ioScheduler = TestScheduler()
        val mainScheduler = TestScheduler()
        given(rxSchedulerProvider.io()).willReturn(ioScheduler)
        given(rxSchedulerProvider.main()).willReturn(mainScheduler)

        val media = ArrayList<Media>()
        media.add(mock())
        spyViewModel.media.set(media)
        assertEquals(1, spyViewModel.media.get().size)
        spyViewModel.user.set(user)
        ioScheduler.triggerActions()
        mainScheduler.triggerActions()
        assertEquals(0, spyViewModel.media.get().size)
        verify(mediaRepository).retrieve(anyOrNull())
    }

    @Test
    fun logout() {
        val user = mock<User>()
        val ioScheduler = TestScheduler()
        val mainScheduler = TestScheduler()
        given(rxSchedulerProvider.io()).willReturn(ioScheduler)
        given(rxSchedulerProvider.main()).willReturn(mainScheduler)

        //Initialize user before login
        galleryViewModel.user.set(user)

        given(userRepository.logout()).willReturn(Completable.complete())
        galleryViewModel.logout()

        ioScheduler.triggerActions()
        assertNotNull(galleryViewModel.user.get())
        mainScheduler.triggerActions()
        verify(galleryNavigator).goToLogin()
    }

    @Test
    fun logoutWithError() {
        val user = mock<User>()
        val ioScheduler = TestScheduler()
        val mainScheduler = TestScheduler()
        given(rxSchedulerProvider.io()).willReturn(ioScheduler)
        given(rxSchedulerProvider.main()).willReturn(mainScheduler)

        //Initialize user before login
        galleryViewModel.user.set(user)

        val error = mock<HttpException>()
        given(userRepository.logout()).willReturn(Completable.error(error))
        galleryViewModel.logout()

        ioScheduler.triggerActions()
        assertNotNull(galleryViewModel.user.get())
        mainScheduler.triggerActions()
        verify(galleryNavigator).showError(eq(error))
        assertNotNull(galleryViewModel.user.get())
    }

}
package com.jaumard.instagrim.data.media

import com.jaumard.instagrim.data.media.dao.MediaDao
import com.jaumard.instagrim.data.media.models.Media
import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.verify
import dagger.Lazy
import io.reactivex.Single
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.quality.Strictness

class MediaLocalDataSourceTest {
    @get:Rule
    internal val mockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS)

    @Mock
    internal lateinit var mediaDao: MediaDao

    private lateinit var mediaLocalDataSource: MediaLocalDataSource

    @Before
    fun setUp() {
        mediaLocalDataSource = MediaLocalDataSource(Lazy { mediaDao })
    }

    @Test
    fun getAll() {
        val media = ArrayList<Media>()
        given(mediaDao.getAll()).willReturn(Single.just(media))
        mediaLocalDataSource.getAll().test().assertNoErrors().assertComplete().assertTerminated().assertValue(media)
    }

    @Test
    fun deleteAll() {
        mediaLocalDataSource.deleteAll().test().assertNoErrors().assertComplete().assertTerminated()
        verify(mediaDao).deleteAll()
    }

    @Test
    fun insert() {
        val media = ArrayList<Media>()
        mediaLocalDataSource.insert(media).test().assertNoErrors().assertComplete().assertTerminated()
        verify(mediaDao).insert()
    }

}
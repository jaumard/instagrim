package com.jaumard.instagrim.network.auth

import com.jaumard.instagrim.network.BuildConfig
import com.jaumard.instagrim.network.user.dto.LoginResponse
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.mock
import dagger.Lazy
import io.reactivex.Single
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.quality.Strictness

class InstagramUserNetworkDataSourceTest {
    @get:Rule
    internal val mockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS)

    @Mock
    internal lateinit var instagramAuthApi: InstagramAuthApi

    private lateinit var instagramAuthNetworkDataSource: InstagramUserNetworkDataSource

    @Before
    fun setUp() {
        instagramAuthNetworkDataSource = InstagramUserNetworkDataSource(Lazy { instagramAuthApi })
    }

    @Test
    fun login() {
        val loginResponse = mock<LoginResponse>()
        val code = "code"
        given(instagramAuthApi.login(eq(BuildConfig.INSTAGRAM_CLIENT_ID), eq(BuildConfig.INSTAGRAM_SECRET),
                eq("authorization_code"), eq(code), eq(BuildConfig.INSTAGRAM_URL_REDIRECTION))).willReturn(Single.just(loginResponse))

        instagramAuthNetworkDataSource.login(code).test().assertNoErrors().assertValue(loginResponse).assertTerminated()
    }

}
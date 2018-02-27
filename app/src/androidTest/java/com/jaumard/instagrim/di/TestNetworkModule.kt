package com.jaumard.instagrim.di

import com.jaumard.instagrim.network.auth.InstagramAuthApi
import com.jaumard.instagrim.network.di.NetworkModule
import com.jaumard.instagrim.network.media.InstagramMediaApi
import com.jaumard.instagrim.network.utils.InstagramAuthApiMock
import com.jaumard.instagrim.network.utils.InstagramMediaApiMock
import retrofit2.Retrofit

class TestNetworkModule : NetworkModule() {
    override fun provideInstagramAuthApi(retrofit: Retrofit): InstagramAuthApi {
        return InstagramAuthApiMock()
    }

    override fun provideInstagramUserApi(retrofit: Retrofit): InstagramMediaApi {
        return InstagramMediaApiMock()
    }
}
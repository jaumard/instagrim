package com.jaumard.instagrim.network.di

import com.jaumard.instagrim.network.BuildConfig
import com.jaumard.instagrim.network.auth.InstagramAuthApi
import com.jaumard.instagrim.network.media.InstagramMediaApi
import com.jaumard.instagrim.network.utils.HttpEnhancer
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
open class NetworkModule {
    @Provides
    @Singleton
    fun provideRetrofit(httpEnhancer: HttpEnhancer): Retrofit {
        val builder = httpEnhancer.enhance(OkHttpClient.Builder())
        return Retrofit.Builder()
                .baseUrl(BuildConfig.INSTAGRAM_BASE_URL)
                .client(builder
                        .build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
    }

    @Provides
    open fun provideInstagramAuthApi(retrofit: Retrofit): InstagramAuthApi {
        return retrofit.create(InstagramAuthApi::class.java)
    }

    @Provides
    open fun provideInstagramUserApi(retrofit: Retrofit): InstagramMediaApi {
        return retrofit.create(InstagramMediaApi::class.java)
    }
}
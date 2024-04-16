package com.ciaorides.ciaorides.di

import com.ciaorides.ciaorides.BuildConfig
import com.ciaorides.ciaorides.api.UsersDataApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun providesRetrofit(): Retrofit {
        val httpClient = OkHttpClient.Builder()
        httpClient.readTimeout(180, TimeUnit.SECONDS)
        httpClient.connectTimeout(180, TimeUnit.SECONDS)
        httpClient.writeTimeout(180, TimeUnit.SECONDS)
        if (BuildConfig.DEBUG) {
            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            httpClient.addInterceptor(interceptor)
        }
        return Retrofit.Builder().baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
            .build()
    }

    @Provides
    fun providesTopHeadlinesApi(retrofit: Retrofit): UsersDataApi {
        return retrofit.create(UsersDataApi::class.java)
    }


}
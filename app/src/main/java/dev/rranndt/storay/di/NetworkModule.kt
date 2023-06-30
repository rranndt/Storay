package dev.rranndt.storay.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.rranndt.storay.BuildConfig
import dev.rranndt.storay.core.data.preferences.UserPreferences
import dev.rranndt.storay.core.data.remote.StoryApi
import dev.rranndt.storay.util.Constant.AUTHORIZATION
import dev.rranndt.storay.util.Helper.generateToken
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideInterceptor(
        userPreferences: UserPreferences,
    ): Interceptor {
        return Interceptor { chain ->
            runBlocking {
                val currentToken = userPreferences.getToken().first()
                chain.run {
                    proceed(
                        request()
                            .newBuilder()
                            .addHeader(AUTHORIZATION, currentToken.generateToken())
                            .build()
                    )
                }
            }
        }
    }

    @Provides
    @Singleton
    fun provideHttpClient(
        interceptor: Interceptor,
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(
                if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC)
                } else {
                    HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
                }
            )
            .addInterceptor(interceptor)
            .readTimeout(15, TimeUnit.SECONDS)
            .connectTimeout(15, TimeUnit.SECONDS)
            .build()

    @Provides
    @Singleton
    fun provideApiService(client: OkHttpClient): StoryApi =
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .build()
            .create(StoryApi::class.java)
}
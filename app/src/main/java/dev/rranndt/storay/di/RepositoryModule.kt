package dev.rranndt.storay.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.rranndt.storay.core.data.repository.AuthRepositoryImpl
import dev.rranndt.storay.core.data.repository.StoryRepositoryImpl
import dev.rranndt.storay.core.domain.repository.AuthRepository
import dev.rranndt.storay.core.domain.repository.StoryRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun provideAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun provideStoryRepository(storyRepositoryImpl: StoryRepositoryImpl): StoryRepository
}
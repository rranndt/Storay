package dev.rranndt.storay.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.rranndt.storay.core.domain.usecase.auth.AuthUseCase
import dev.rranndt.storay.core.domain.usecase.auth.AuthUseCaseImpl
import dev.rranndt.storay.core.domain.usecase.story.StoryUseCase
import dev.rranndt.storay.core.domain.usecase.story.StoryUseCaseImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UseCaseModule {

    @Binds
    @Singleton
    abstract fun provideAuthUseCase(authUseCaseImpl: AuthUseCaseImpl): AuthUseCase

    @Binds
    @Singleton
    abstract fun provideStoryUseCase(storyUseCaseImpl: StoryUseCaseImpl): StoryUseCase
}
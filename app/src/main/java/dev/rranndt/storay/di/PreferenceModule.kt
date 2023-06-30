package dev.rranndt.storay.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.rranndt.storay.core.data.preferences.UserPreferences
import dev.rranndt.storay.core.data.preferences.UserPreferencesImpl
import dev.rranndt.storay.util.Constant.USER_PREFERENCES
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = USER_PREFERENCES)

@Module
@InstallIn(SingletonComponent::class)
abstract class PreferenceModule {

    @Binds
    @Singleton
    abstract fun provideUserPreference(userPreferenceImpl: UserPreferencesImpl): UserPreferences

    companion object {
        @Provides
        @Singleton
        fun providePreferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
            context.dataStore
    }
}
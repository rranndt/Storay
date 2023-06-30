package dev.rranndt.storay.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.rranndt.storay.BuildConfig
import dev.rranndt.storay.core.data.local.db.StoryDatabase
import dev.rranndt.storay.util.Constant.DB_NAME
import dev.rranndt.storay.util.Constant.PASS_PHRASE
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideStoryDatabase(@ApplicationContext context: Context): StoryDatabase {
        val factory = SupportFactory(SQLiteDatabase.getBytes(PASS_PHRASE.toCharArray()))
        return Room.databaseBuilder(
            context,
            StoryDatabase::class.java,
            DB_NAME
        ).apply {
            if (!BuildConfig.DEBUG) {
                openHelperFactory(factory)
            }
        }.build()
    }
}
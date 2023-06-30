package dev.rranndt.storay.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.rranndt.storay.core.data.local.entity.RemoteKey
import dev.rranndt.storay.core.data.local.entity.StoryEntity

@Database(entities = [StoryEntity::class, RemoteKey::class], version = 1, exportSchema = false)
abstract class StoryDatabase : RoomDatabase() {
    abstract fun getStoriesDao(): StoryDao
    abstract fun getKeysDao(): RemoteKeysDao
}
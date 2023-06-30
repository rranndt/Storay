package dev.rranndt.storay.utils

import dev.rranndt.storay.core.data.local.entity.StoryEntity

object DataDummy {

    fun generateDummyStoryResponse(): List<StoryEntity> {
        return (0..50).map {
            StoryEntity(
                it.toString(),
                "name $it",
                "desc $it",
                "photoUrl $it",
                "createdAt $it",
                0.0,
                0.0,
            )
        }
    }
}
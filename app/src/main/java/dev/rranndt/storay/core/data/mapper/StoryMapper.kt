package dev.rranndt.storay.core.data.mapper

import androidx.paging.PagingData
import androidx.paging.map
import dev.rranndt.storay.core.data.local.entity.StoryEntity
import dev.rranndt.storay.core.data.remote.dto.AddStoryResponseDto
import dev.rranndt.storay.core.data.remote.dto.StoryResultDto
import dev.rranndt.storay.core.domain.model.AddStoryResponse
import dev.rranndt.storay.core.domain.model.StoryResult

fun StoryResultDto.toStoryResult(): StoryResult {
    return StoryResult(
        id = id,
        name = name,
        description = description,
        photoUrl = photoUrl,
        createdAt = createdAt,
        lat = lat,
        lon = lon
    )
}

fun AddStoryResponseDto.toStoryUpload(): AddStoryResponse {
    return AddStoryResponse(
        error = error,
        message = message
    )
}

fun StoryResultDto.toStoryEntity(): StoryEntity {
    return StoryEntity(
        id = id,
        name = name,
        description = description,
        photoUrl = photoUrl,
        createdAt = createdAt,
        lat = lat,
        lon = lon
    )
}

fun toStory(story: PagingData<StoryEntity>) = story.map {
    StoryResult(
        it.id,
        it.name,
        it.description,
        it.photoUrl,
        it.createdAt,
        it.lat,
        it.lon
    )
}

fun PagingData<StoryEntity>.toPagingStoryResult() = map {
    StoryResult(
        it.id,
        it.name,
        it.description,
        it.photoUrl,
        it.createdAt,
        it.lat,
        it.lon
    )
}

fun List<StoryEntity>.map() = map { story ->
    StoryResult(
        id = story.id,
        name = story.name,
        description = story.description,
        photoUrl = story.photoUrl,
        createdAt = story.createdAt,
        lat = story.lat,
        lon = story.lon,
    )
}
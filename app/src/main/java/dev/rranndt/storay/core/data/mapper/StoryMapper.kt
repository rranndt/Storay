package dev.rranndt.storay.core.data.mapper

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
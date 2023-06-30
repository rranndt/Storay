package dev.rranndt.storay.core.domain.usecase.story

import dev.rranndt.storay.core.domain.model.AddStoryRequest
import dev.rranndt.storay.core.domain.model.AddStoryResponse
import dev.rranndt.storay.core.domain.model.StoryResult
import dev.rranndt.storay.util.Result
import kotlinx.coroutines.flow.Flow

interface StoryUseCase {

    fun getStories(): Flow<Result<List<StoryResult>>>

    fun getDetailStory(id: String): Flow<Result<StoryResult>>

    fun addStory(story: AddStoryRequest): Flow<Result<AddStoryResponse>>

    fun widgetStories(): Flow<List<StoryResult>>
}
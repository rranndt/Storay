package dev.rranndt.storay.flow

import androidx.paging.PagingData
import dev.rranndt.storay.core.domain.model.AddStoryRequest
import dev.rranndt.storay.core.domain.model.AddStoryResponse
import dev.rranndt.storay.core.domain.model.StoryResult
import dev.rranndt.storay.core.domain.usecase.story.StoryUseCase
import dev.rranndt.storay.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeStoryUseCase : StoryUseCase {

    val delegate = FakeFlowDelegate<PagingData<StoryResult>>()

    override fun getStories(): Flow<PagingData<StoryResult>> = delegate.flow

    override fun getDetailStory(id: String): Flow<Result<StoryResult>> = flow { }

    override fun addStory(story: AddStoryRequest): Flow<Result<AddStoryResponse>> = flow { }

    override fun widgetStories(): Flow<List<StoryResult>> = flow { }

    override fun getStoriesWithLocation(): Flow<Result<List<StoryResult>>> = flow { }
}
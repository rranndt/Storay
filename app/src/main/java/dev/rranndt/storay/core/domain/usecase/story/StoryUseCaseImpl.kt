package dev.rranndt.storay.core.domain.usecase.story

import dev.rranndt.storay.core.domain.model.AddStoryRequest
import dev.rranndt.storay.core.domain.model.AddStoryResponse
import dev.rranndt.storay.core.domain.model.StoryResult
import dev.rranndt.storay.core.domain.repository.StoryRepository
import dev.rranndt.storay.util.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class StoryUseCaseImpl @Inject constructor(
    private val repository: StoryRepository,
) : StoryUseCase {

    override fun getStories(): Flow<Result<List<StoryResult>>> =
        repository.getStories()

    override fun getDetailStory(id: String): Flow<Result<StoryResult>> =
        repository.getDetailStory(id)

    override fun addStory(story: AddStoryRequest): Flow<Result<AddStoryResponse>> =
        repository.addStory(story)

    override fun widgetStories(): Flow<List<StoryResult>> = repository.widgetStories()
}
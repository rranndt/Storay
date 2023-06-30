package dev.rranndt.storay.presentation.main.addstory

import dev.rranndt.storay.core.domain.model.AddStoryResponse
import dev.rranndt.storay.util.Result

data class AddStoryState(
    val addStory: Result<AddStoryResponse>? = null
)

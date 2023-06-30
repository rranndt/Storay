package dev.rranndt.storay.presentation.main.detail

import dev.rranndt.storay.core.domain.model.StoryResult
import dev.rranndt.storay.util.Result

data class DetailState(
    val getDetailStory: Result<StoryResult>? = null
)

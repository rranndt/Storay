package dev.rranndt.storay.presentation.main.home

import dev.rranndt.storay.core.domain.model.StoryResult
import dev.rranndt.storay.util.Result

data class HomeState(
    val getStories: Result<List<StoryResult>>? = null
)

package dev.rranndt.storay.presentation.main.maps

import dev.rranndt.storay.core.domain.model.StoryResult
import dev.rranndt.storay.util.Result

data class MapsState(
    val getStoriesWithLocation: Result<List<StoryResult>>? = null
)
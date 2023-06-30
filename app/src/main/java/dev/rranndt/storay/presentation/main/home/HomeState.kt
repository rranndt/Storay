package dev.rranndt.storay.presentation.main.home

import androidx.paging.PagingData
import dev.rranndt.storay.core.domain.model.StoryResult

data class HomeState(
    val getStories: PagingData<StoryResult> = PagingData.empty(),
)

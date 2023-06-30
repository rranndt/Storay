package dev.rranndt.storay.presentation.main.addstory

import dev.rranndt.storay.core.domain.model.AddStoryRequest

sealed class AddStoryEvent {
    data class AddStory(val story: AddStoryRequest) : AddStoryEvent()
}

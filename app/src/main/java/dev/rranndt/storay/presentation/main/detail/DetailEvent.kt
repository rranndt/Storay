package dev.rranndt.storay.presentation.main.detail

sealed class DetailEvent {
    data class GetDetailStory(val id: String) : DetailEvent()
}

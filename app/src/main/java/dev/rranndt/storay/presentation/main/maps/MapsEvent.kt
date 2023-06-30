package dev.rranndt.storay.presentation.main.maps

sealed class MapsEvent {
    object GetStoriesWithLocation: MapsEvent()
}
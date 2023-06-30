package dev.rranndt.storay.presentation.main.maps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.rranndt.storay.core.domain.usecase.story.StoryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapsViewModel @Inject constructor(
    private val useCase: StoryUseCase,
) : ViewModel() {

    private val _getStoriesWithLocation = MutableStateFlow(MapsState())
    val getStoriesWithLocation: StateFlow<MapsState> = _getStoriesWithLocation

    fun onEvent(event: MapsEvent) {
        when (event) {
            MapsEvent.GetStoriesWithLocation -> {
                viewModelScope.launch {
                    useCase.getStoriesWithLocation().collect { location ->
                        _getStoriesWithLocation.update {
                            it.copy(getStoriesWithLocation = location)
                        }
                    }
                }
            }
        }
    }
}
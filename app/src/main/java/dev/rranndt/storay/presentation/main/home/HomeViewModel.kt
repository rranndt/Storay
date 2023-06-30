package dev.rranndt.storay.presentation.main.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.rranndt.storay.core.domain.usecase.story.StoryUseCase
import dev.rranndt.storay.util.Event
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val useCase: StoryUseCase,
) : ViewModel() {

    private val _getStories = MutableStateFlow(HomeState())
    val getStories: StateFlow<HomeState> = _getStories

    private val _errorText = MutableSharedFlow<Event<String?>>()
    val errorText: SharedFlow<Event<String?>> = _errorText

    init {
        onEvent(HomeEvent.GetStories)
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            HomeEvent.GetStories -> {
                viewModelScope.launch {
                    useCase.getStories().onEach { result ->
                        _getStories.update { it.copy(getStories = result) }
                        _errorText.emit(Event(result.message))
                    }.launchIn(viewModelScope)
                }
            }
        }
    }
}
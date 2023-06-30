package dev.rranndt.storay.presentation.main.addstory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.rranndt.storay.core.domain.usecase.story.StoryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddStoryViewModel @Inject constructor(
    private val useCase: StoryUseCase,
) : ViewModel() {

    private val _addStory = MutableStateFlow(AddStoryState())
    val addStory: StateFlow<AddStoryState> = _addStory.asStateFlow()

    fun onEvent(event: AddStoryEvent) {
        when (event) {
            is AddStoryEvent.AddStory -> {
                viewModelScope.launch {
                    useCase.addStory(event.story).onEach { result ->
                        _addStory.update { it.copy(addStory = result) }
                    }.launchIn(viewModelScope)
                }
            }
        }
    }
}
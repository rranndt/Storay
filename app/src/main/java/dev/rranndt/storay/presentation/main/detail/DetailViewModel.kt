package dev.rranndt.storay.presentation.main.detail

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
class DetailViewModel @Inject constructor(
    private val useCase: StoryUseCase,
) : ViewModel() {

    private val _getDetailStory = MutableStateFlow(DetailState())
    val getDetailStory: StateFlow<DetailState> = _getDetailStory

    fun onEvent(event: DetailEvent) {
        when (event) {
            is DetailEvent.GetDetailStory -> {
                viewModelScope.launch {
                    useCase.getDetailStory(event.id).collect { result ->
                        _getDetailStory.update { it.copy(getDetailStory = result) }
                    }
                }
            }
        }
    }

}
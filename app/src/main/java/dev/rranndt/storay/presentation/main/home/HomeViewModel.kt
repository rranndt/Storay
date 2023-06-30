package dev.rranndt.storay.presentation.main.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.rranndt.storay.core.domain.usecase.story.StoryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val useCase: StoryUseCase,
) : ViewModel() {

    private val _getStories = MutableStateFlow(HomeState())
    val getStories: StateFlow<HomeState> = _getStories

    init {
        onEvent(HomeEvent.GetStories)
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            HomeEvent.GetStories -> {
                viewModelScope.launch {
                    useCase.getStories().cachedIn(viewModelScope).collectLatest { result ->
                        _getStories.update { it.copy(getStories = result) }
                    }
                }
            }
        }
    }

}
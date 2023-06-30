package dev.rranndt.storay.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.rranndt.storay.core.domain.usecase.auth.AuthUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val useCase: AuthUseCase
): ViewModel() {

    fun signOut() = viewModelScope.launch { useCase.signOut() }
}
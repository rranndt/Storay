package dev.rranndt.storay.presentation.routing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.rranndt.storay.core.domain.usecase.auth.AuthUseCase
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val useCase: AuthUseCase
) : ViewModel(){

    fun getUserStatus() = useCase.getUserStatus().asLiveData()
}
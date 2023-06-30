package dev.rranndt.storay.presentation.auth.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.rranndt.storay.core.domain.usecase.auth.AuthUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val useCase: AuthUseCase,
) : ViewModel() {

    private val _signInState = MutableStateFlow(SignInState())
    val signInState: StateFlow<SignInState> = _signInState.asStateFlow()

    fun onEvent(event: SignInEvent) {
        when (event) {
            is SignInEvent.SignIn -> {
                viewModelScope.launch {
                    useCase.signIn(event.email, event.password)
                        .onEach { result ->
                            _signInState.update { it.copy(signInState = result) }
                        }.launchIn(viewModelScope)
                }
            }

            SignInEvent.GetSignInStatus -> {
                useCase.getUserStatus().onEach { result ->
                    _signInState.update { it.copy(signInStatus = result) }
                }
            }
        }
    }
}
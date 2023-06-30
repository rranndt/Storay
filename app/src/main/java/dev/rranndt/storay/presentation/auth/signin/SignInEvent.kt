package dev.rranndt.storay.presentation.auth.signin

sealed class SignInEvent {
    data class SignIn(val email: String, val password: String) : SignInEvent()
    object GetSignInStatus : SignInEvent()
}

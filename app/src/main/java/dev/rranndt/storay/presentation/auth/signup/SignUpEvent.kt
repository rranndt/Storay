package dev.rranndt.storay.presentation.auth.signup

sealed class SignUpEvent {
    data class SignUp(val name: String, val email: String, val password: String) : SignUpEvent()
}

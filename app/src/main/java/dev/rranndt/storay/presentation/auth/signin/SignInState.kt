package dev.rranndt.storay.presentation.auth.signin

import dev.rranndt.storay.core.domain.model.SignInResponse
import dev.rranndt.storay.util.Result

data class SignInState(
    val signInState: Result<SignInResponse>? = null,
    val signInStatus: Boolean = false
)

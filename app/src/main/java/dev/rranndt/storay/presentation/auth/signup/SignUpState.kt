package dev.rranndt.storay.presentation.auth.signup

import dev.rranndt.storay.core.domain.model.SignUpResponse
import dev.rranndt.storay.util.Result

data class SignUpState(
    val signUpState: Result<SignUpResponse>? = null,
)

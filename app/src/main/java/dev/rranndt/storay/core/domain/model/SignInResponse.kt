package dev.rranndt.storay.core.domain.model

data class SignInResponse(
    val error: Boolean,
    val signInResult: SignInResult,
    val message: String,
)

data class SignInResult(
    val name: String,
    val token: String,
    val userId: String,
)

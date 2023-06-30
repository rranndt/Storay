package dev.rranndt.storay.core.data.mapper

import dev.rranndt.storay.core.data.remote.dto.SignInResponseDto
import dev.rranndt.storay.core.data.remote.dto.SignInResultDto
import dev.rranndt.storay.core.data.remote.dto.SignUpResponseDto
import dev.rranndt.storay.core.domain.model.SignInResponse
import dev.rranndt.storay.core.domain.model.SignInResult
import dev.rranndt.storay.core.domain.model.SignUpResponse

fun SignUpResponseDto.toSignUpResponse(): SignUpResponse {
    return SignUpResponse(
        error = error,
        message = message
    )
}

fun SignInResponseDto.toSignInResponse(): SignInResponse {
    return SignInResponse(
        error = error,
        signInResult = signInResultDto.toSignInResult(),
        message = message,
    )
}

fun SignInResultDto.toSignInResult(): SignInResult {
    return SignInResult(
        name = name,
        token = token,
        userId = userId
    )
}
package dev.rranndt.storay.core.domain.usecase.auth

import dev.rranndt.storay.core.domain.model.SignInResponse
import dev.rranndt.storay.core.domain.model.SignUpResponse
import dev.rranndt.storay.util.Result
import kotlinx.coroutines.flow.Flow

interface AuthUseCase {

    fun signUp(
        name: String,
        email: String,
        password: String
    ): Flow<Result<SignUpResponse>>

    fun signIn(
        email: String,
        password:String
    ) : Flow<Result<SignInResponse>>

    fun getUserStatus(): Flow<Boolean>

    suspend fun signOut()
}
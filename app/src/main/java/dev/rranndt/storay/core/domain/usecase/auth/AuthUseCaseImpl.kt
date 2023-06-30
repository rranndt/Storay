package dev.rranndt.storay.core.domain.usecase.auth

import dev.rranndt.storay.core.domain.model.SignInResponse
import dev.rranndt.storay.core.domain.model.SignUpResponse
import dev.rranndt.storay.core.domain.repository.AuthRepository
import dev.rranndt.storay.util.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthUseCaseImpl @Inject constructor(
    private val repository: AuthRepository,
) : AuthUseCase {

    override fun signUp(name: String, email: String, password: String): Flow<Result<SignUpResponse>> =
        repository.signUp(name, email, password)

    override fun signIn(email: String, password: String): Flow<Result<SignInResponse>> =
        repository.signIn(email, password)

    override fun getUserStatus(): Flow<Boolean> =
        repository.getUserStatus()

    override suspend fun signOut() = repository.signOut()
}
package dev.rranndt.storay.core.data.repository

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.rranndt.storay.R
import dev.rranndt.storay.core.data.mapper.toSignInResponse
import dev.rranndt.storay.core.data.mapper.toSignUpResponse
import dev.rranndt.storay.core.data.preferences.UserPreferences
import dev.rranndt.storay.core.data.remote.RemoteDataSource
import dev.rranndt.storay.core.domain.model.SignInResponse
import dev.rranndt.storay.core.domain.model.SignUpResponse
import dev.rranndt.storay.core.domain.repository.AuthRepository
import dev.rranndt.storay.util.Helper.getErrorMessage
import dev.rranndt.storay.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val userPreferences: UserPreferences,
    @ApplicationContext private val context: Context,
) : AuthRepository {

    override fun signUp(
        name: String,
        email: String,
        password: String,
    ): Flow<Result<SignUpResponse>> =
        flow {
            emit(Result.Loading())
            try {
                val response = remoteDataSource.signUp(name, email, password)
                val result = response.toSignUpResponse()

                emit(Result.Success(result))
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Result.Error(e.getErrorMessage()))
            } catch (e: IOException) {
                e.printStackTrace()
                emit(Result.Error(context.getString(R.string.io_exception)))
            }
        }.flowOn(Dispatchers.IO)

    override fun signIn(email: String, password: String): Flow<Result<SignInResponse>> =
        flow {
            emit(Result.Loading())
            try {
                val response = remoteDataSource.signIn(email, password)
                val result = response.toSignInResponse()
                userPreferences.run {
                    saveUser(result.signInResult.token)
                    setSignInStatus(true)
                }

                emit(Result.Success(result))
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Result.Error(e.getErrorMessage()))
            } catch (e: IOException) {
                e.printStackTrace()
                emit(Result.Error(context.getString(R.string.io_exception)))
            }
        }.flowOn(Dispatchers.IO)

    override fun getUserStatus(): Flow<Boolean> =
        userPreferences.getSignInStatus()

    override suspend fun signOut() {
        userPreferences.run {
            deleteToken()
            setSignInStatus(false)
        }
    }

}
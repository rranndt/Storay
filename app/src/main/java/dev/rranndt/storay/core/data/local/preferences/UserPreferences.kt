package dev.rranndt.storay.core.data.local.preferences

import kotlinx.coroutines.flow.Flow

interface UserPreferences {

    suspend fun saveUser(token: String)

    suspend fun deleteToken()

    fun getToken(): Flow<String>

    suspend fun setSignInStatus(isLogin: Boolean)

    fun getSignInStatus(): Flow<Boolean>
}
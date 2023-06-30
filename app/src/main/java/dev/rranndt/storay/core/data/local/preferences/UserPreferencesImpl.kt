package dev.rranndt.storay.core.data.local.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import dev.rranndt.storay.util.Constant.LOGIN_STATUS
import dev.rranndt.storay.util.Constant.USER_TOKEN
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserPreferencesImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : UserPreferences {

    override suspend fun saveUser(token: String) {
        dataStore.edit {
            it[Keys.token] = token
        }
    }

    override suspend fun deleteToken() {
        dataStore.edit {
            it.remove(Keys.token)
        }
    }

    override fun getToken(): Flow<String> =
        dataStore.data.map {
            it[Keys.token] ?: ""
        }

    override suspend fun setSignInStatus(isLogin: Boolean) {
        dataStore.edit {
            it[Keys.status] = isLogin
        }
    }

    override fun getSignInStatus(): Flow<Boolean> =
        dataStore.data.map {
            it[Keys.status] ?: false
        }

    private object Keys {
        val token = stringPreferencesKey(USER_TOKEN)
        val status = booleanPreferencesKey(LOGIN_STATUS)
    }
}
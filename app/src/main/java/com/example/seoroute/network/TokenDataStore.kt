package com.example.seoroute.network

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.authDataStore: DataStore<Preferences> by preferencesDataStore(name = "seoroute_auth_prefs")

/**
 * Jetpack DataStore Preferences manager for persisting the backend JWT access_token and user session metadata.
 */
object TokenDataStore {
    private val KEY_ACCESS_TOKEN = stringPreferencesKey("access_token")
    private val KEY_USER_EMAIL = stringPreferencesKey("user_email")
    private val KEY_USER_NAME = stringPreferencesKey("user_name")
    private val KEY_USER_ROLE = stringPreferencesKey("user_role")

    @Volatile
    private var appContext: Context? = null

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    suspend fun saveToken(
        token: String,
        email: String? = null,
        name: String? = null,
        role: String? = null
    ) {
        val ctx = appContext ?: return
        ctx.authDataStore.edit { prefs ->
            prefs[KEY_ACCESS_TOKEN] = token
            if (email != null) prefs[KEY_USER_EMAIL] = email
            if (name != null) prefs[KEY_USER_NAME] = name
            if (role != null) prefs[KEY_USER_ROLE] = role
        }
    }

    suspend fun getToken(): String? {
        val ctx = appContext ?: return null
        return ctx.authDataStore.data.map { prefs -> prefs[KEY_ACCESS_TOKEN] }.first()
    }

    suspend fun clearToken() {
        val ctx = appContext ?: return
        ctx.authDataStore.edit { prefs ->
            prefs.remove(KEY_ACCESS_TOKEN)
            prefs.remove(KEY_USER_EMAIL)
            prefs.remove(KEY_USER_NAME)
            prefs.remove(KEY_USER_ROLE)
        }
    }
}

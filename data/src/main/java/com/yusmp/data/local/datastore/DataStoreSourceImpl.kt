package com.yusmp.data.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.yusmp.domain.dataStore.DataStoreSource
import com.yusmp.domain.dataStore.model.AppEnvironment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(DataStoreSourceImpl.STORAGE_NAME)

class DataStoreSourceImpl(context: Context) : DataStoreSource {
    private val dataStore = context.dataStore

    override val isFirstLaunch: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[IS_FIRST_LAUNCH] ?: true
        }

    override suspend fun updateIsFirstLaunch(isFirstLaunch: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_FIRST_LAUNCH] = isFirstLaunch
        }
    }

    override val appEnvironment: Flow<AppEnvironment> = dataStore.data
        .map { preferences ->
            val serverUrl = preferences[SERVER_URL] ?: ""
            AppEnvironment.values().firstOrNull { it.url == serverUrl } ?: AppEnvironment.DEV
        }

    override suspend fun updateAppEnvironment(environment: AppEnvironment) {
        dataStore.edit { preferences ->
            preferences[SERVER_URL] = environment.url
        }
    }

    override suspend fun clear() {
        dataStore.edit { preferences ->
            preferences.remove(SERVER_URL)
        }
    }

    companion object {
        const val STORAGE_NAME: String = "BASE_CODE"
        private val SERVER_URL = stringPreferencesKey("SERVER_URL")
        private val IS_FIRST_LAUNCH = booleanPreferencesKey("IS_FIRST_LAUNCH")
    }
}
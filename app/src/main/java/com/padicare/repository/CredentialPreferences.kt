package com.padicare.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.padicare.model.LoginResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit

class CredentialPreferences private constructor(private val dataStore: DataStore<Preferences>){

    fun getCredentials(): Flow<LoginResult> {
        return dataStore.data.map { preferences ->
            LoginResult(
                name = preferences[NAME_KEY].toString(),
                token = preferences[TOKEN_KEY].toString(),
                isLogin = preferences[STATE_KEY] ?: false
            )
        }
    }

    suspend fun saveCredential(name: String, token: String ) {
        dataStore.edit { preferences ->
            preferences[NAME_KEY] = name
            preferences[TOKEN_KEY] = token
            preferences[STATE_KEY] = true
        }
    }

    suspend fun clearCredential() {
        dataStore.edit { preferences ->
            preferences[NAME_KEY] = ""
            preferences[TOKEN_KEY] = ""
            preferences[STATE_KEY] = false
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: CredentialPreferences? = null

        private val NAME_KEY = stringPreferencesKey("name")
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val STATE_KEY = booleanPreferencesKey("state")

        fun getInstance(dataStore: DataStore<Preferences>): CredentialPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = CredentialPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}
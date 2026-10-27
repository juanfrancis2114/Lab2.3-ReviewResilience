package com.example.laboratorio1

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.reviewDataStore by preferencesDataStore(
    name = "review_preferences"
)

class DraftRepository(context: Context) {

    private val dataStore = context.applicationContext.reviewDataStore

    private val draftKey = stringPreferencesKey("draft_content")

    val draft: Flow<String> = dataStore.data.map { preferences ->
        preferences[draftKey] ?: ""
    }

    suspend fun saveDraft(content: String) {
        dataStore.edit { preferences ->
            preferences[draftKey] = content
        }
    }
}
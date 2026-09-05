package com.him.assistant.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.memoryDataStore by preferencesDataStore(
    name = "him_memory"
)

class MemoryRepository(
    private val context: Context
) {

    private val memoryKey = stringPreferencesKey("memory")

    suspend fun saveMemory(memory: String) {
        context.memoryDataStore.edit { preferences ->
            preferences[memoryKey] = memory
        }
    }

    suspend fun getMemory(): String {
        val preferences = context.memoryDataStore.data.first()
        return preferences[memoryKey] ?: ""
    }
}
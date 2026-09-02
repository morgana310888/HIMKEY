package com.him.assistant.data.repository

import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend

class MessageRepository {

    private val model = Firebase.ai(
        backend = GenerativeBackend.googleAI()
    ).generativeModel("gemini-3.7-flash")

    suspend fun getResponse(userMessage: String): String {
        return try {
            val response = model.generateContent(userMessage)

            response.text ?: "Não consegui gerar uma resposta."
        } catch (e: Exception) {
            "Erro ao falar com a IA: ${e.message}"
        }
    }
}
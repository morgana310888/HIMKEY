package com.him.assistant.data.repository

import com.him.assistant.data.model.Message

class MessageRepository {

    suspend fun getResponse(userMessage: String): String {
        // Por agora, resposta fixa
        // Depois vamos integrar Claude API aqui
        return "Recebi sua mensagem. Ainda estou sendo desenvolvido, mas já estou aqui. 😉"
    }
}
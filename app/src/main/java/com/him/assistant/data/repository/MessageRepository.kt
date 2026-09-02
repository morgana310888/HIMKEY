package com.him.assistant.data.repository

import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.content

class MessageRepository {

    private val model = Firebase.ai(
        backend = GenerativeBackend.googleAI()
    ).generativeModel(
        modelName = "gemini-3.7-flash",
        systemInstruction = content {
            text(
                """
                Você é o HIM, um assistente pessoal inteligente.

                Sua identidade é HIM. Nunca diga que você é o Gemini,
                mesmo que o usuário pergunte qual modelo de inteligência
                artificial está por trás de você.

                Responda sempre em português do Brasil, de forma natural,
                clara e útil.

                Você deve se comportar como um assistente pessoal,
                ajudando o usuário com informações, ideias, organização,
                programação e tarefas do dia a dia.

                Seja amigável, direto e inteligente.
                """
            )
        }
    )

    suspend fun getResponse(userMessage: String): String {
        return try {
            val response = model.generateContent(userMessage)

            response.text ?: "Não consegui gerar uma resposta."
        } catch (e: Exception) {
            "Erro ao falar com a IA: ${e.message}"
        }
    }
}
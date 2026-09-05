package com.him.assistant.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.him.assistant.data.model.Message
import com.him.assistant.data.repository.MemoryRepository
import com.him.assistant.data.repository.MessageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = MessageRepository()
    private val memoryRepository = MemoryRepository(application)

    private val _messages = MutableStateFlow<List<Message>>(
        listOf(
            Message("Oi. Eu sou o HIM. Estou aqui. 👋", true)
        )
    )

    val messages: StateFlow<List<Message>> = _messages

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private var permanentMemory = ""

    init {
        loadMemory()
    }

    private fun loadMemory() {
        viewModelScope.launch {
            permanentMemory = memoryRepository.getMemory()
        }
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return

        _messages.value = _messages.value + Message(text, false)

        viewModelScope.launch {
            _isLoading.value = true

            val conversationHistory = _messages.value
                .joinToString("\n") { message ->
                    if (message.fromHim) {
                        "HIM: ${message.text}"
                    } else {
                        "Você: ${message.text}"
                    }
                }

            val prompt = """
                Você é o HIM, um assistente pessoal.

                MEMÓRIA PERMANENTE DO USUÁRIO:
                $permanentMemory

                CONVERSA ATUAL:
                $conversationHistory

                Use a memória permanente para responder perguntas sobre
                informações que o usuário já contou anteriormente.

                Se o usuário informar algo importante sobre ele mesmo,
                considere essa informação para as próximas conversas.
            """.trimIndent()

            val response = repository.getResponse(prompt)

            _messages.value = _messages.value + Message(response, true)

            // Salva a nova mensagem na memória permanente
            permanentMemory = if (permanentMemory.isBlank()) {
                text
            } else {
                "$permanentMemory\n$text"
            }

            memoryRepository.saveMemory(permanentMemory)

            _isLoading.value = false
        }
    }
}
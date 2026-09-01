package com.him.assistant.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.him.assistant.data.model.Message
import com.him.assistant.data.repository.MessageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel(
    private val repository: MessageRepository = MessageRepository()
) : ViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(
        listOf(
            Message("Oi. Eu sou o HIM. Estou aqui. 👋", true)
        )
    )
    val messages: StateFlow<List<Message>> = _messages

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun sendMessage(text: String) {
        if (text.isBlank()) return

        // Adiciona mensagem do usuário
        _messages.value = _messages.value + Message(text, false)

        // Busca resposta
        viewModelScope.launch {
            _isLoading.value = true
            val response = repository.getResponse(text)
            _messages.value = _messages.value + Message(response, true)
            _isLoading.value = false
        }
    }
}
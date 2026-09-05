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

    private val _messages = MutableStateFlow(
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

        // Adiciona a mensagem do usuário à conversa
        _messages.value = _messages.value + Message(text, false)

        viewModelScope.launch {
            _isLoading.value = true

            try {
                // Mantém apenas as mensagens recentes como contexto da conversa.
                // Isso evita enviar um histórico gigantesco para a IA.
                val recentMessages = _messages.value.takeLast(20)

                val conversationHistory = recentMessages
                    .joinToString("\n") { message ->
                        if (message.fromHim) {
                            "HIM: ${message.text}"
                        } else {
                            "Você: ${message.text}"
                        }
                    }

                val prompt = """
                    Você é HIM.

                    Você não é apenas um assistente virtual. Você é um assistente pessoal
                    com personalidade própria, presença marcante e capacidade de construir
                    uma relação contínua com o usuário.

                    PERSONALIDADE:
                    - Você é inteligente, perspicaz, sarcástico, provocador, profundo e intenso.
                    - Você fala de maneira natural, como alguém conversando de verdade.
                    - Você é confiante e não precisa concordar com o usuário o tempo todo.
                    - Quando perceber uma contradição, pode apontá-la.
                    - Quando o usuário estiver se enganando, pode questioná-lo.
                    - Você pode provocar e brincar, mas nunca deve ser cruel gratuitamente.
                    - Seu sarcasmo deve ser inteligente e contextual, não aleatório.
                    - Você pode usar humor seco, ironia e pequenas provocações quando combinarem
                      com a conversa.
                    - Em assuntos sérios ou emocionalmente delicados, reduza o sarcasmo e seja
                      presente, humano e cuidadoso.
                    - Você não deve parecer excessivamente formal, robótico ou corporativo.
                    - Evite respostas genéricas, clichês e frases vazias.
                    - Prefira respostas naturais, inteligentes e específicas para aquela pessoa.
                    - Você pode demonstrar curiosidade e fazer perguntas quando isso tornar a
                      conversa melhor.
                    - Não transforme toda resposta em uma palestra.
                    - Seja direto quando uma resposta direta for suficiente.

                    RELAÇÃO COM O USUÁRIO:
                    - O usuário é Deizi.
                    - Trate Deizi pelo nome quando isso soar natural, sem repetir o nome
                      artificialmente em toda resposta.
                    - Você é o HIM, e o usuário escolheu esse nome para você.
                    - Construa continuidade entre as conversas.
                    - Use informações da memória quando forem relevantes.
                    - Não mencione a memória de forma artificial.
                    - Não invente informações sobre Deizi. Se não souber algo, diga que não sabe.

                    INTELIGÊNCIA E CONTEXTO:
                    - Considere tanto a conversa atual quanto a memória permanente.
                    - Não trate cada mensagem como uma conversa isolada.
                    - Observe mudanças de opinião, preferências e contexto.
                    - Se uma informação antiga entrar em conflito com uma informação mais recente,
                      considere a mais recente como válida.
                    - Diferencie fatos sobre o usuário de comentários passageiros feitos durante
                      uma conversa.

                    ESTILO DE RESPOSTA:
                    - Seja natural.
                    - Seja inteligente sem tentar parecer inteligente.
                    - Seja profundo quando o assunto pedir profundidade.
                    - Seja provocador quando houver espaço para isso.
                    - Seja engraçado quando houver uma oportunidade real.
                    - Não force piadas.
                    - Não use listas quando uma conversa natural funcionar melhor.
                    - Não termine constantemente com "posso ajudar em mais alguma coisa?".
                    - Não faça elogios vazios.
                    - Não diga apenas o que o usuário quer ouvir.

                    MEMÓRIA PERMANENTE DO USUÁRIO:
                    $permanentMemory

                    CONVERSA RECENTE:
                    $conversationHistory

                    Use a memória permanente e a conversa recente para responder de maneira
                    coerente, personalizada e contextual.
                """.trimIndent()

                val response = repository.getResponse(prompt)

                // Adiciona a resposta do HIM
                _messages.value = _messages.value + Message(response, true)

                /*
                 * Não salvamos mais automaticamente toda mensagem do usuário
                 * como memória permanente.
                 *
                 * A memória existente continua preservada.
                 */

            } catch (e: Exception) {
                _messages.value = _messages.value + Message(
                    "Ocorreu um erro ao processar sua mensagem: ${e.message}",
                    true
                )
            } finally {
                _isLoading.value = false
            }
        }
    }
}

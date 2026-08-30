
package com.him.assistant

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.him.assistant.ui.theme.AssistantTheme

data class Message(
    val text: String,
    val fromHim: Boolean
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AssistantTheme {
                HIMScreen()
            }
        }
    }
}

@Composable
fun HIMScreen() {

    var messageText by remember {
        mutableStateOf("")
    }

    val messages = remember {
        mutableStateListOf(
            Message(
                "Oi. Eu sou o HIM. Estou aqui. 👋",
                true
            )
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {

            Text(
                text = "HIM",
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = "Seu assistente pessoal",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                items(messages) { message ->

                    Text(
                        text = if (message.fromHim) {
                            "HIM: ${message.text}"
                        } else {
                            "Você: ${message.text}"
                        },
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                OutlinedTextField(
                    value = messageText,
                    onValueChange = {
                        messageText = it
                    },
                    modifier = Modifier.weight(1f),
                    placeholder = {
                        Text("Digite uma mensagem...")
                    }
                )

                Button(
                    onClick = {

                        if (messageText.isNotBlank()) {

                            messages.add(
                                Message(
                                    messageText,
                                    false
                                )
                            )

                            messages.add(
                                Message(
                                    "Recebi sua mensagem. Ainda estou sendo desenvolvido, mas já estou aqui. 😉",
                                    true
                                )
                            )

                            messageText = ""
                        }
                    }
                ) {
                    Text("Enviar")
                }
            }
        }
    }
}
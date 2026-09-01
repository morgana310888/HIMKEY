package com.him.assistant.data.model

data class Message(
    val text: String,
    val fromHim: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)
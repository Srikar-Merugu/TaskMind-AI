package com.taskmind.app.domain.model

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    var isTyping: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

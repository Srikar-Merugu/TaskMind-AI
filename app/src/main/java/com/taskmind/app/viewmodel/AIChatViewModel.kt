package com.taskmind.app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskmind.app.domain.model.Candidate
import com.taskmind.app.domain.model.ChatMessage
import com.taskmind.app.domain.model.Content
import com.taskmind.app.domain.model.GeminiRequest
import com.taskmind.app.domain.model.Part
import com.taskmind.app.network.GeminiRetrofitClient
import kotlinx.coroutines.launch

class AIChatViewModel : ViewModel() {

    private val apiKey = com.taskmind.app.BuildConfig.GEMINI_API_KEY
    
    // In a real app, do not commit API keys directly. For testing purposes of Phase 5, we'll need to pass one or mock it.
    // Assuming for execution verification, we need a functional or mocked setup.
    // If we want a completely real response, we'd need a real key.
    
    private val _chatMessages = MutableLiveData<List<ChatMessage>>(emptyList())
    val chatMessages: LiveData<List<ChatMessage>> = _chatMessages

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private var currentTaskContext: String = ""

    fun setTaskContext(tasks: List<com.taskmind.app.domain.model.Task>) {
        if (tasks.isEmpty()) {
            currentTaskContext = "The user currently has no tasks."
            return
        }
        val contextBuilder = java.lang.StringBuilder("Here are the user's current tasks:\n")
        tasks.forEach { task ->
            val status = if (task.completed) "Completed" else "Pending"
            contextBuilder.append("- [${status}] ${task.title}: ${task.description}\n")
        }
        currentTaskContext = contextBuilder.toString()
    }

    fun suggestSubtasks(task: com.taskmind.app.domain.model.Task) {
        val prompt = "Based on this task: '${task.title}' (${task.description}), suggest 3-5 concrete subtasks to help me complete it. Focus purely on actionable steps."
        sendMessage(prompt, isSystemPrompt = true)
    }

    fun mentorTask(task: com.taskmind.app.domain.model.Task) {
        val prompt = "I need mentorship for this task: '${task.title}'.\nDescription: ${task.description}\n\nPlease give me some quick, encouraging advice and 1-2 key tips on how to approach this efficiently."
        sendMessage(prompt, isSystemPrompt = true)
    }

    fun rewriteTask(task: com.taskmind.app.domain.model.Task) {
        val prompt = "Rewrite the following task title and description to be more professional, clear, and actionable.\nTitle: '${task.title}'\nDescription: '${task.description}'"
        sendMessage(prompt, isSystemPrompt = true)
    }

    fun sendMessage(text: String, isSystemPrompt: Boolean = false) {
        val displayMessage = if (isSystemPrompt) "Action: $text" else text
        val userMessage = ChatMessage(text = displayMessage, isUser = true)
        appendMessage(userMessage)

        val typingMessage = ChatMessage(text = "...", isUser = false, isTyping = true)
        appendMessage(typingMessage)

        viewModelScope.launch {
            try {
                // Construct Request
                val fullPrompt = "$currentTaskContext\n\nUser Question/Command: $text"
                val request = GeminiRequest(
                    contents = listOf(
                        Content(role = "user", parts = listOf(Part(text = fullPrompt)))
                    )
                )

                val response = GeminiRetrofitClient.apiService.generateContent(apiKey, request)
                
                removeTypingMessage()

                if (response.isSuccessful && response.body() != null) {
                    val candidates = response.body()?.candidates
                    if (!candidates.isNullOrEmpty()) {
                        val firstCandidate = candidates[0]
                        val parts = firstCandidate.content?.parts
                        if (!parts.isNullOrEmpty()) {
                            val aiText = parts[0].text
                            appendMessage(ChatMessage(text = aiText, isUser = false))
                        } else {
                            appendMessage(ChatMessage(text = "The AI returned an empty response.", isUser = false))
                        }
                    } else {
                        appendMessage(ChatMessage(text = "No candidates found in the response.", isUser = false))
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: ""
                    val errorMsg = when(response.code()) {
                        429 -> "API rate limit exceeded. Please try again later."
                        401 -> "Invalid API Key. Please check your local.properties file."
                        403 -> {
                            if (errorBody.contains("SERVICE_DISABLED", ignoreCase = true)) {
                                "AI service is disabled. Please enable 'Generative Language API' in your Google Console: https://console.developers.google.com/apis/api/generativelanguage.googleapis.com/overview?project=595471646334"
                            } else {
                                "Access denied (403): $errorBody"
                            }
                        }
                        else -> "Sorry, I couldn't reach the server. [${response.code()}]"
                    }
                    appendMessage(ChatMessage(text = errorMsg, isUser = false))
                    _error.value = errorMsg
                }

            } catch (e: Exception) {
                removeTypingMessage()
                
                val errorMsg = when (e) {
                    is java.net.UnknownHostException, is java.net.ConnectException -> "No internet connection. Please check your network."
                    is java.net.SocketTimeoutException -> "Request timed out. The server is taking too long."
                    else -> "An error occurred: ${e.message}"
                }
                
                val aiMessage = ChatMessage(text = errorMsg, isUser = false)
                appendMessage(aiMessage)
                _error.value = errorMsg
            }
        }
    }

    private fun appendMessage(message: ChatMessage) {
        val currentList = _chatMessages.value ?: emptyList()
        _chatMessages.value = currentList + message
    }

    private fun removeTypingMessage() {
        val currentList = _chatMessages.value ?: emptyList()
        _chatMessages.value = currentList.filterNot { it.isTyping }
    }
}

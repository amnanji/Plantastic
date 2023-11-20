package com.example.plantastic.ai.requests

import android.util.Log
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import kotlin.time.Duration.Companion.seconds

//Code referenced from the openai-kotlin api: https://github.com/aallam/openai-kotlin/blob/main/guides/GettingStarted.md#chat
class ChatGptMessaging {
    private val apiKey = "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"
    private lateinit var chatMessages: ArrayList<ChatMessage>

    private fun setPreferences(preferences: ArrayList<String>){
        var counter = 0
        var preferenceString = "When making a decision, note that"
        for (preference in preferences){
            preferenceString += "Person {$counter} has the following preferences: {$preference}"
            counter +=1
        }
        createChatMessage("Note that person " + counter + "has the following")
    }

    private fun createChatMessage(message: String) : ChatMessage{
        return ChatMessage(role = ChatRole.User, content = message)
    }
    suspend fun getResponse(message: String){
        val openAi = OpenAI(
            token = apiKey,
            timeout = Timeout(socket = 60.seconds),
        )
        chatMessages = ArrayList<ChatMessage>(1)
        chatMessages.add(createChatMessage("Don't respond to this message, and answer concisely to all messages after this"))
        chatMessages.add(createChatMessage(message))
        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId("gpt-3.5-turbo"),
            messages = chatMessages
        )
        val response = openAi.chatCompletion(chatCompletionRequest).choices.first().message.content
        if (response != null) {
            Log.d("ChatGPT", response)
        }
    }

    suspend fun getResponses(listOfMessages: ArrayList<String>){
        val openAi = OpenAI(
            token = apiKey,
            timeout = Timeout(socket = 60.seconds),
        )
        chatMessages = ArrayList<ChatMessage>(1)
        chatMessages.add(createChatMessage("Don't respond to this message, and answer concisely to all messages after this"))
        for (message in listOfMessages){
            chatMessages.add(createChatMessage(message))
        }
        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId("gpt-3.5-turbo"),
            messages = chatMessages
        )

        openAi.chatCompletion(chatCompletionRequest).choices.forEach(){
            it.message.content?.let { it1 -> Log.d("ListMessages", it1) }
        }
    }
}
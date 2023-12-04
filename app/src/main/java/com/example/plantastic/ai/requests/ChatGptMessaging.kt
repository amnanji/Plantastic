package com.example.plantastic.ai.requests

import android.content.Context
import android.icu.util.Calendar
import android.util.Log
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.example.plantastic.R
import com.example.plantastic.models.Message
import com.example.plantastic.models.Preferences
import com.example.plantastic.models.Users
import com.example.plantastic.repository.PreferencesRepository
import com.example.plantastic.repository.UsersAuthRepository
import com.example.plantastic.repository.UsersRepository
import java.text.SimpleDateFormat
import kotlin.time.Duration.Companion.seconds

//Code referenced from the openai-kotlin api: https://github.com/aallam/openai-kotlin/blob/main/guides/GettingStarted.md#chat
class ChatGptMessaging(context: Context) {
    private var DAYS_IN_WEEK = 7
    private val API_KEY = "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"

    private var chatMessages: ArrayList<ChatMessage>
    private val messages: MutableList<String> = ArrayList()
    private var openAI: OpenAI
    private var usersRepository: UsersRepository = UsersRepository()
    private var usersAuthRepository: UsersAuthRepository = UsersAuthRepository()
    private var preferencesRepository: PreferencesRepository = PreferencesRepository()


    private var preferencesString = ""
    var context = context


    init {
        openAI = OpenAI(
            token = API_KEY,
            timeout = Timeout(socket = 300.seconds),
        )
        chatMessages = ArrayList<ChatMessage>()
    }

    private fun addUserMessage(message: Message) {
        usersRepository.getUserById(message.senderId!!){user: Users? ->
            if (user != null){
                messages.add("${user.username}: ${message.content}")
            }
        }
    }

    // Add a model response to the chat history
    private fun addModelResponse(message: String) {
        messages.add("ChatGPT: $message")
    }

    // Get the entire chat history as a list of strings
    private fun getChatHistory(): List<String> {
        return ArrayList(messages)
    }

    private fun intToStringAvailability(context: Context, availabilityArray: MutableList<Int>): String{
        var stringAvailability = ""
        var calendar = Calendar.getInstance()
        var daysOfWeek = context.resources.getStringArray(R.array.days_of_week)
        for (dayIndex in 0..<DAYS_IN_WEEK){
            val dayOfWeek = daysOfWeek[dayIndex]
            val startTime = availabilityArray[dayIndex*2]
            val endTime = availabilityArray[dayIndex*2 + 1]

            stringAvailability += if (startTime != -1){
                val timeFormat = SimpleDateFormat("HH:mm")
                calendar.set(Calendar.HOUR_OF_DAY, startTime / 60)
                calendar.set(Calendar.MINUTE, startTime % 60)
                val startTimeString = timeFormat.format(calendar.time)

                calendar.set(Calendar.HOUR_OF_DAY, endTime / 60)
                calendar.set(Calendar.MINUTE, endTime % 60)
                val endTimeString = timeFormat.format(calendar.time)
                "$dayOfWeek: $startTimeString to $endTimeString, "
            } else {
                "$dayOfWeek: Busy, "
            }

        }
        return stringAvailability
    }

    private fun getDietaryRestriction(context: Context, dietaryRestrictionIndex: Int): String{
        val dietaryRestrictionArray = context.resources.getStringArray(R.array.dietary_options)
        return dietaryRestrictionArray[dietaryRestrictionIndex]
    }

    suspend fun setPreferences(context: Context, userArray: ArrayList<Users>){
        Log.d("step 2", "chatGPT:setPreferences")
        var counter = 0
        val uid = usersAuthRepository.getCurrentUser()?.uid
        val preferencesArrayList = ArrayList<Preferences>()
        for (user in userArray){
            Log.d("step 3", "chatGPT:setPreferences:user: $user")
            user.id?.let { preferencesRepository.getPreferenceById(it){preferences ->
                if (preferences != null) {
                    Log.d("step 4", "chatGPT:setPreferences:user: $user")
                    preferencesArrayList.add(preferences)
                    preferencesString += "\nUse this knowledge to answer any questions about Person $counter"
                    preferencesString += "\n ${userArray[counter].firstName} has the following preferences: \n"
                    preferencesString += "${userArray[counter].firstName} likes ${preferences?.foodPreferences} food, \n"
                    preferencesString += "${userArray[counter].firstName}'s dietary restrictions are: ${
                        preferences?.dietaryRestrictionIndex?.let {index ->
                            getDietaryRestriction(context,
                                index
                            )
                        }}\n"
                    preferencesString += " ${userArray[counter].firstName} enjoys ${preferences?.activityPreferences} activities, "
                    preferencesString += "${userArray[counter].firstName}'s availability is: ${
                        preferences?.availability?.let {availability ->
                            intToStringAvailability(context,
                                availability
                            )
                        }}. \n"
                    counter += 1
                }
            } }
        }
        val message = Message("text", uid, preferencesString, Calendar.getInstance().timeInMillis)
        Log.d("step 5", "chatGPT:setPreferences:returnedOptimizedMessage: ${message.content}")
        addUserMessage(message)
//
//        val optimizedMessageString = getOptimizedMessage(message)
//        val optimizedMessage = Message(message.messageType, message.senderId, optimizedMessageString, message.timestamp)
//        getResponse(message)
    }

    private fun createChatMessage(message: String) : ChatMessage{
        return ChatMessage(role = ChatRole.User, content = message)
    }

    suspend fun getResponse(message: Message){
        Log.d("step 6", "chatGPT:getResponse")
        var currentMessage = ""
        var optimizedMessageString = getOptimizedMessage(message)
        Log.d("step 10", "chatGPT:getResponse:optimizedMessageString: $optimizedMessageString")

        if (optimizedMessageString != null){
            var optimizedMessage = Message(message.messageType, message.senderId, optimizedMessageString, message.timestamp)
            addUserMessage(optimizedMessage)
            var chatHistory = getChatHistory()

            chatHistory.forEach {
                currentMessage += "$it "
            }
            Log.d("step 11", "chatGPT:getResponse:currentMessage: $currentMessage")
            chatMessages.add(createChatMessage(currentMessage))
            for (message in chatMessages){
                Log.d("step 12","chatGPT:chatMessage in chatMessages: ${message.content}")
            }
            val chatCompletionRequest = ChatCompletionRequest(
                model = ModelId("gpt-3.5-turbo"),
                messages = chatMessages,
                temperature = 0.6
            )

            val completion = openAI.chatCompletion(chatCompletionRequest)
            Log.d("step 13","chatGPT:completion in chatMessages: ${message.content}")
            if (completion.choices.first().finishReason.value == "length") {
                chatMessages = ArrayList<ChatMessage>()
                val shortenString = "Shorten and summarize this message $currentMessage"
                val shortenMessage = Message(optimizedMessage.messageType, optimizedMessage.senderId, shortenString, optimizedMessage.timestamp)
                val newMessageString = getOptimizedMessage(shortenMessage)
                Log.d("step 14","chatGPT:newMessageString: $newMessageString")
                newMessageString?.let { createChatMessage(it) }?.let { chatMessages.add(it) }
            } else {
                var response = completion.choices.first().message.content
                if (response != null) {
                    Log.d("step 14", "response: $response")
                    addModelResponse(response)
                }
            }
        }
    }

    suspend fun getOptimizedMessage(message: Message): String? {
        Log.d("step 7", "chatGPT:getOptimizedMessage")

//        Log.d("chatgpt: User prompt sent", "prompt: $message")
        var arrayList = ArrayList<ChatMessage>()
        arrayList.add(createChatMessage("Optimize this prompt for me: ${message.content}"))
        arrayList.forEach{
            Log.d("step 8", "chatGPT: ${it.toString()}")
        }

        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId("gpt-4"),
            messages = arrayList,
            temperature = 0.2
        )
        val response = openAI.chatCompletion(chatCompletionRequest).choices.first().message.content
        if (response != null) {
            Log.d("chatgpt: OptimizedResponse", response)
            var newMessage = Message(message.messageType, message.senderId, response, message.timestamp)
            addUserMessage(newMessage)
            Log.d("step 9", "chatGPT:optimizedResponse: $response")

        }
        return "Given the this chat history, $response. Don't justify your answers, don't give information not mentioned in the CONTEXT INFORMATION"
    }

//    suspend fun getResponses(listOfMessages: ArrayList<String>){
//        chatMessages = ArrayList<ChatMessage>(1)
//        chatMessages.add(createChatMessage("Don't respond to this message, and answer concisely to all messages after this"))
//        for (message in listOfMessages){
//            chatMessages.add(createChatMessage(message))
//        }
//        val chatCompletionRequest = ChatCompletionRequest(
//            model = ModelId("gpt-3.5-turbo"),
//            messages = chatMessages
//        )
//
//        openAI.chatCompletion(chatCompletionRequest).choices.forEach(){
//            it.message.content?.let { it1 -> Log.d("ListMessages", it1) }
//        }
//    }
}
package com.example.plantastic.ui.chats

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantastic.ai.requests.ChatGptMessaging
import kotlinx.coroutines.launch

class ChatsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        val chatGptMessaging = ChatGptMessaging()
        viewModelScope.launch{
            chatGptMessaging.getResponse("Return the 3 best restaurants in Surrey, British Columbia")
        }
        value = "This is chats Fragment"
    }
    val text: LiveData<String> = _text
}
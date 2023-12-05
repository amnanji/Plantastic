package com.example.plantastic.ui.chats

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.plantastic.models.Users
import com.example.plantastic.repository.GroupsRepository
import com.example.plantastic.repository.UsersAuthRepository
import com.example.plantastic.repository.UsersRepository

class ChatsViewModel : ViewModel() {

    private var groupsRepository: GroupsRepository = GroupsRepository()
    private var usersRepository: UsersRepository = UsersRepository()
    private var usersAuthRepository: UsersAuthRepository = UsersAuthRepository()

    private var id = usersAuthRepository.getCurrentUser()!!.uid
    private var user = null

    private var _user = MutableLiveData<Users>().apply {

        value = null
    }

    private val _text = MutableLiveData<String>().apply {
        value = "This is chats Fragment"
    }
    val text: LiveData<String> = _text
}
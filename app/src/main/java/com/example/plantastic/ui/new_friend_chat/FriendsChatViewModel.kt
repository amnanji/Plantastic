package com.example.plantastic.ui.new_friend_chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.plantastic.models.Users
import com.example.plantastic.repository.UsersRepository

class FriendsChatViewModel: ViewModel() {

    private val usersRepository = UsersRepository()

    private val _friendsList = MutableLiveData<List<Users>>()

    private val _filteredFriendsList = MutableLiveData<List<Users>>()
    val filteredFriendsList: LiveData<List<Users>> = _filteredFriendsList

     fun getFriendsList(id: String) {
        usersRepository.getFriendsList(id) {
            if (it != null){
                _friendsList.value = it
                _filteredFriendsList.value = it
            }
        }
    }

    fun filterFriendsList(search: String){

        if (search.isEmpty()){
            _filteredFriendsList.value = _friendsList.value
        }
        else{
            val filteredList = _friendsList.value?.filter { user ->
                user.username!!.startsWith(search, ignoreCase = true) // Change "name" to the actual field you want to filter
            }
            _filteredFriendsList.value = filteredList ?: emptyList()
        }
    }
}

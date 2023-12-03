package com.example.plantastic.ui.new_group

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.plantastic.models.Users
import com.example.plantastic.repository.UsersRepository

class NewGroupViewModel : ViewModel() {

    private val usersRepository = UsersRepository()

    private val _friendsList = MutableLiveData<List<Users>>()
    val friendsList: LiveData<List<Users>> = _friendsList

    private val _filteredFriendsList = MutableLiveData<List<Users>>()
    val filteredFriendsList: LiveData<List<Users>> = _filteredFriendsList

    private val _groupMembersList = MutableLiveData<List<Users>>()
    val groupMembersList: LiveData<List<Users>> = _groupMembersList

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

    fun addToMembersList(user: Users){
        val currentMembersList = _groupMembersList.value?.toMutableList() ?: mutableListOf()
        currentMembersList.add(user)
        _groupMembersList.value = currentMembersList
    }

    fun removeFromMembersList(user: Users) {
        val currentMembersList = _groupMembersList.value?.toMutableList() ?: mutableListOf()
        currentMembersList.remove(user)
        _groupMembersList.value = currentMembersList
    }


}
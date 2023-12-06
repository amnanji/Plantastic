package com.example.plantastic.ui.chats

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.plantastic.models.Groups
import com.example.plantastic.repository.GroupsRepository
import com.example.plantastic.repository.UsersAuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatsViewModel : ViewModel() {

    private val _groups = MutableLiveData<ArrayList<Groups>>()
    val groups: LiveData<ArrayList<Groups>> = _groups

    private var groupsRepository: GroupsRepository = GroupsRepository()

    init {
        val currUser = UsersAuthRepository().getCurrentUser()
        val userId = currUser!!.uid

        CoroutineScope(Dispatchers.IO).launch {
            groupsRepository.getAllGroupsByUserWithChatNames(userId) { newGroupsList ->
                if (newGroupsList != null) {
                    CoroutineScope(Dispatchers.Main).launch {
                        _groups.value?.clear()
                        _groups.value =
                                // Filtering out "Individual" group types that have no recent messages
                                // Sorting by latest message
                            ArrayList(newGroupsList
                                .filterNot { it.groupType == "individual" && it.latestMessage == null }
                                .sortedWith(compareByDescending {
                                    if (it.latestMessage != null) {
                                        it.latestMessage.timestamp ?: Long.MIN_VALUE
                                    } else {
                                        it.timestampGroupCreated ?: Long.MIN_VALUE
                                    }
                                })
                            )
                    }
                }
            }
        }
    }
}
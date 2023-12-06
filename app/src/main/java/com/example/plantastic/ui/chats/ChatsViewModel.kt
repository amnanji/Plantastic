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
        val groupsQuery = groupsRepository.getAllGroupsQueryForUser(userId)

        CoroutineScope(Dispatchers.IO).launch {
            groupsRepository.getAllGroupsByUserWithChatNames(userId) { newGroupsList ->
                if (newGroupsList != null) {
                    _groups.value =
                        ArrayList(newGroupsList.filterNot { it.groupType == "Individual" && it.latestMessage == null }
                            .sortedWith(compareBy {
                                it.latestMessage?.timestamp ?: Long.MAX_VALUE
                            }))
                }
            }
        }
    }
}
package com.example.plantastic.ui.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantastic.models.Preferences.Preferences
import com.example.plantastic.models.Users
import com.example.plantastic.repository.PreferencesRepository
import com.example.plantastic.repository.UsersAuthRepository
import com.example.plantastic.repository.UsersRepository
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private var usersRepository: UsersRepository = UsersRepository()
    private var usersAuthRepository: UsersAuthRepository = UsersAuthRepository()
    private var preferencesRepository: PreferencesRepository = PreferencesRepository()

    private val _user = MutableLiveData<Users>().apply {
        viewModelScope.launch {
            val currentUserUid = usersAuthRepository.getCurrentUser()!!.uid
            usersRepository.getUserById(currentUserUid){ user ->
                if (user != null){
                    value = user
                }
            }
        }
    }
    val user = _user

    private val _preferences = MutableLiveData<Preferences>().apply {
        viewModelScope.launch{
            val currUserUid = usersAuthRepository.getCurrentUser()!!.uid
            preferencesRepository.getPreferenceById(currUserUid) { preferences ->
                if (preferences != null) {
                    value = preferences
                } else {
                    val foodPreferences = "None"
                    val dietaryRestrictionIndex = 0
                    val activityPreferences = "None"
                    var availability = mutableListOf(-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1)
                    if (dietaryRestrictionIndex != null) {
                        preferencesRepository.createNewPreference(
                            currUserUid, foodPreferences, dietaryRestrictionIndex,
                            activityPreferences, availability
                        ) { creationSuccessful ->
                            if (creationSuccessful) {
                                preferencesRepository.getPreferenceById(currUserUid) { preferences ->
                                    if (preferences != null) {
                                        value = preferences
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    val preferences = _preferences
}

package com.example.plantastic.ui.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantastic.models.Preferences.Preferences
import com.example.plantastic.repository.PreferencesRepository
import com.example.plantastic.repository.UsersAuthRepository
import kotlinx.coroutines.launch
import java.sql.Types
import java.util.Calendar

class ProfileViewModel : ViewModel() {

    private var usersAuthRepository: UsersAuthRepository = UsersAuthRepository()
    private var preferencesRepository: PreferencesRepository = PreferencesRepository()

    // TODO: Implement the ViewModel
//    private val _foodPreferences = MutableLiveData<String>().apply {
//        value = "None"
//    }
//    val foodPreferences = _foodPreferences
//
//    private val _dietaryRestrictionIndex = MutableLiveData<Int>().apply {
//        value = 0
//    }
//    val dietaryRestrictionIndex = _dietaryRestrictionIndex
//
//    private val _activityPreferences = MutableLiveData<String>().apply{
//        value = "None"
//    }
//    val activityPreferences = _activityPreferences
//
//    private val _availability = MutableLiveData<MutableList<Int>>().apply {
//        value = mutableListOf(0,0,0,0,0,0,0,0,0,0,0,0,0,0)
//    }
//    val availability = _availability

    private val _preferences = MutableLiveData<Preferences>().apply {

        viewModelScope.launch{
            val currUserUid = usersAuthRepository.getCurrentUser()!!.uid
            preferencesRepository.getPreferenceById(currUserUid) { preferences ->
                if (preferences != null) {
                    Log.d("Preference Retrieval", "Preference for user with id:$currUserUid exists")
                    value = preferences
                    Log.d("Preferences", "foodPreference: ${preferences.foodPreferences}, activityPreference: ${preferences.activityPreferences}")
                } else {
                    Log.d("", "preferenceDoesntExist:82")
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
                                Log.d(
                                    "Preference creation",
                                    "Preference created for user with id: $currUserUid successful!"
                                )
                                preferencesRepository.getPreferenceById(currUserUid) { preferences ->
                                    if (preferences != null) {
                                        value = preferences
                                    }
                                }
                            } else {
                                Log.d(
                                    "Preference creation",
                                    "Preference created for user with id: $currUserUid unsuccessful :("
                                )
                            }
                        }
                    } else {
                        Log.d("Null check", "Dietary Restriction Index was null")
                    }
                }
            }
        }

    }
    val preferences = _preferences
}

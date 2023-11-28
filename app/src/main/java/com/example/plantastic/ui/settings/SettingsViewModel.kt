package com.example.plantastic.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SettingsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is settings Fragment"
    }
    val text: LiveData<String> = _text

    private val _foodPreferences = MutableLiveData<String>().apply {
        value = "Indian, Chinese, Japanese"
    }
    val foodPreferences: LiveData<String> = _foodPreferences

    private val _activityPreferences = MutableLiveData<String>().apply {
        value = "Ping-pong, Axe Throwing, Eating"
    }
    val activityPreferences: LiveData<String> = _activityPreferences

    private val _availabilityPreferences = MutableLiveData<String>().apply {
        value = "Monday 9am-5pm, Tuesday 7am-11pm, Weekends"
    }
    val availabilityPreferences: LiveData<String> = _availabilityPreferences
}
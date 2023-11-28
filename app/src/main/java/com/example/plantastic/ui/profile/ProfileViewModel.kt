package com.example.plantastic.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.sql.Types
import java.util.Calendar

class ProfileViewModel : ViewModel() {
    // TODO: Implement the ViewModel
    private val _foodPreferences = MutableLiveData<String>().apply {
        value = "None"
    }
    val foodPreferences = _foodPreferences

    private val _dietaryRestrictionIndex = MutableLiveData<Int>().apply {
        value = 0
    }
    val dietaryRestrictionIndex = _dietaryRestrictionIndex

    private val _activityPreferences = MutableLiveData<String>().apply{
        value = "None"
    }
    val activityPreferences = _activityPreferences

    private val _availability = MutableLiveData<Array<IntArray>>().apply {
        var monday: IntArray = intArrayOf(0, 1440)
        var tuesday: IntArray = intArrayOf(0, 1440)
        var wednesday: IntArray = intArrayOf(0, 1440)
        var thursday: IntArray = intArrayOf(0, 1440)
        var friday: IntArray = intArrayOf(0, 1440)
        var saturday: IntArray = intArrayOf(0, 1440)
        var sunday: IntArray = intArrayOf(0, 1440)
        value = arrayOf(monday,tuesday,wednesday,thursday,friday,saturday,sunday)
    }
    val availability = _availability

}
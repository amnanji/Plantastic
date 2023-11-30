package com.example.plantastic.ui.profile

import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.plantastic.R
import com.example.plantastic.databinding.FragmentProfileBinding
import com.example.plantastic.models.Preferences.Preferences
import com.example.plantastic.repository.PreferencesRepository
import com.example.plantastic.repository.UsersAuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private var usersAuthRepository: UsersAuthRepository = UsersAuthRepository()
    private var preferencesRepository: PreferencesRepository = PreferencesRepository()
    private var profileViewModel: ProfileViewModel = ProfileViewModel()

    private lateinit var saveButton: Button
    private lateinit var foodPreferencesEditText: EditText
    private lateinit var dietaryRestrictionsSpinner: Spinner
    private lateinit var activityPreferencesEditText: EditText

    private lateinit var startButtonList: ArrayList<Button>
    private lateinit var endButtonList: ArrayList<Button>
    private lateinit var busyButtonList: ArrayList<Button>
    private lateinit var availabilityTextViewList: ArrayList<TextView>

    private var currentUserId: String = usersAuthRepository.getCurrentUser()!!.uid
    private lateinit var currentPreferences: Preferences
    private var currentAvailability: MutableList<Int> = mutableListOf(-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1)
    private var currentDietaryRestrictionIndex = -1
    private var currentFoodPreferences = ""
    private var currentActivityPreferences = ""
    private var daysInTheWeek: Int = 7

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        startButtonList = ArrayList<Button>(daysInTheWeek)
        endButtonList = ArrayList<Button>(daysInTheWeek)
        busyButtonList = ArrayList<Button>(daysInTheWeek)
        availabilityTextViewList = ArrayList<TextView>(daysInTheWeek)

        foodPreferencesEditText = binding.foodPreferencesInput
        dietaryRestrictionsSpinner = binding.dietaryRestrictionsInput
        activityPreferencesEditText = binding.activityPreferencesInput

        initializeAvailabilityTextViews()
        initializeButtons()

        profileViewModel.preferences.observe(viewLifecycleOwner){ preferences ->
            if (preferences != null) {
                currentPreferences = preferences
                currentAvailability = currentPreferences.availability!!
                logList("Availability:64", currentAvailability)
                currentDietaryRestrictionIndex = currentPreferences.dietaryRestrictionIndex!!
                currentFoodPreferences = currentPreferences.foodPreferences!!
                currentActivityPreferences = currentPreferences.activityPreferences!!

                foodPreferencesEditText.setText(currentFoodPreferences)
                activityPreferencesEditText.setText(currentActivityPreferences)
                dietaryRestrictionsSpinner.setSelection(currentDietaryRestrictionIndex)

                for (i in 0..<daysInTheWeek){
                    setAvailability(i)
                }
            }
        }

        populateStoredValues()

        saveButton = binding.profileSaveButton
        saveButton.setOnClickListener {
            var foodPreferences = "None"
            if (foodPreferencesEditText.text.toString().isNotBlank()){
                foodPreferences = foodPreferencesEditText.text.toString()
            }
            var activityPreferences = "None"
            if (activityPreferencesEditText.text.toString().isNotBlank()){
                activityPreferences = activityPreferencesEditText.text.toString()
            }
            val dietaryRestrictionsIndex = dietaryRestrictionsSpinner.selectedItemId.toInt()
            val currentUserUid = usersAuthRepository.getCurrentUser()!!.uid
            val availability = currentAvailability
            if(setAndValidateAvailability()){
                CoroutineScope(Dispatchers.IO).launch {
                    preferencesRepository.updatePreference(currentUserUid, foodPreferences, dietaryRestrictionsIndex,
                        activityPreferences, availability){ preferences ->
                        profileViewModel.preferences.value = preferences
                    }
                }
            }
        }
        val root: View = binding.root
        return root
    }

    private fun populateStoredValues() {
        for(i in 0..<daysInTheWeek){
            setAvailability(i)
        }
    }

    private fun logList(tag: String, arrayList: MutableList<Int>){
        var string = "{"
        for(item in arrayList){
            string += item.toString()
            string += ","
        }
        string = string.dropLast(1)
        string += "}"
        Log.d(tag, string)
    }

    private fun setAndValidateAvailability(): Boolean {
        val weekArray = resources.getStringArray(R.array.days_of_week)
        var isValid = true
        for(i in 0..< currentAvailability.size step 2){
            var startTime = currentAvailability[i]
            var endTime = currentAvailability[i+1]
            val currentDayIndex = i/2
            val currentDay = weekArray[currentDayIndex]
            if(isBusy(startTime, endTime)){
                isValid = false
//                setAvailability(currentDayIndex, -1, -1,"Busy")
                Toast.makeText(this.context,
                    "The availability for $currentDay is invalid, start time:${minutesToTime(startTime)} end time: ${minutesToTime(endTime)}, please check start and end times",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        return isValid
    }

    private fun setAvailability(dayIndex: Int) {
        val startTime = currentAvailability[dayIndex * 2]
        val endTime = currentAvailability[dayIndex * 2 + 1]
        var availability = if (startTime == -1){
            getString(R.string.available_from_string, "Unknown", minutesToTime(endTime))
        }else if (endTime == -1){
            getString(R.string.available_from_string, minutesToTime(startTime), "Unknown")
        } else {
            getString(R.string.available_from_string, minutesToTime(startTime), minutesToTime(endTime))
        }
        availabilityTextViewList[dayIndex].text = availability
    }

    private fun setAvailabilityBusy(dayIndex: Int){
        currentAvailability[dayIndex*2] = -1
        currentAvailability[dayIndex*2 + 1] = -1
        availabilityTextViewList[dayIndex].text = "Busy"

    }

    private fun isBusy(startTime: Int, endTime: Int): Boolean{
        return endTime <= startTime || startTime == -1 || endTime == -1
    }

    private fun minutesToTime(minutes: Int): String{
        val startHours = TimeUnit.MINUTES.toHours(minutes.toLong())
        val startMinutes = minutes - TimeUnit.HOURS.toMinutes(startHours)
        return String.format("%02d:%02d", startHours, startMinutes)
    }

    private fun initializeAvailabilityTextViews() {
        availabilityTextViewList.add(binding.mondayAvailabilityTextview)
        availabilityTextViewList.add(binding.tuesdayAvailabilityTextview)
        availabilityTextViewList.add(binding.wednesdayAvailabilityTextview)
        availabilityTextViewList.add(binding.thursdayAvailabilityTextview)
        availabilityTextViewList.add(binding.fridayAvailabilityTextview)
        availabilityTextViewList.add(binding.saturdayAvailabilityTextview)
        availabilityTextViewList.add(binding.sundayAvailabilityTextview)
    }
    
    private fun initializeButtons(){
        startButtonList.add(binding.mondayStartTimeButton)
        startButtonList.add(binding.tuesdayStartTimeButton)
        startButtonList.add(binding.wednesdayStartTimeButton)
        startButtonList.add(binding.thursdayStartTimeButton)
        startButtonList.add(binding.fridayStartTimeButton)
        startButtonList.add(binding.saturdayStartTimeButton)
        startButtonList.add(binding.sundayStartTimeButton)

        endButtonList.add(binding.mondayEndTimeButton)
        endButtonList.add(binding.tuesdayEndTimeButton)
        endButtonList.add(binding.wednesdayEndTimeButton)
        endButtonList.add(binding.thursdayEndTimeButton)
        endButtonList.add(binding.fridayEndTimeButton)
        endButtonList.add(binding.saturdayEndTimeButton)
        endButtonList.add(binding.sundayEndTimeButton)

        busyButtonList.add(binding.mondayBusyButton)
        busyButtonList.add(binding.tuesdayBusyButton)
        busyButtonList.add(binding.wednesdayBusyButton)
        busyButtonList.add(binding.thursdayBusyButton)
        busyButtonList.add(binding.fridayBusyButton)
        busyButtonList.add(binding.saturdayBusyButton)
        busyButtonList.add(binding.sundayBusyButton)

        for(i in 0..<busyButtonList.size){
            var currentStartButton = startButtonList[i]
            currentStartButton.setOnClickListener {
                getTime(i, false)
//                setAvailability(i)
            }
            var currentEndButton = endButtonList[i]
            currentEndButton.setOnClickListener {
                getTime(i, true)
//                setAvailability(i)
            }
            var currentBusyButton = busyButtonList[i]
            currentBusyButton.setOnClickListener {
                setAvailabilityBusy(i)
            }
        }
    }

    private fun getTime(dayIndex: Int, isEnd: Boolean) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val timePickerDialog = TimePickerDialog(
            requireContext(),
            TimePickerDialog.OnTimeSetListener { _, hourSelected, minuteSelected ->
                var timeSelected = hourSelected*60 + minuteSelected
                currentAvailability[dayIndex*2 + isEnd.toInt()] = timeSelected
                setAvailability(dayIndex)
                logList("After Get Time: 247", currentAvailability)
            },
            hour,
            minute,
            true
        )
        timePickerDialog.show()
    }

    // Function below taken from: https://stackoverflow.com/questions/46401879/boolean-int-conversion-in-kotlin
    private fun Boolean.toInt() = if (this) 1 else 0

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
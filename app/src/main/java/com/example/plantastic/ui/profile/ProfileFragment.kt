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
import androidx.fragment.app.Fragment
import com.example.plantastic.R
import com.example.plantastic.databinding.FragmentProfileBinding
import com.example.plantastic.models.Preferences
import com.example.plantastic.models.Users
import com.example.plantastic.repository.PreferencesRepository
import com.example.plantastic.repository.UsersAuthRepository
import com.example.plantastic.repository.UsersRepository
import com.example.plantastic.utilities.CustomAlertDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private var usersAuthRepository: UsersAuthRepository = UsersAuthRepository()
    private var usersRepository: UsersRepository = UsersRepository()
    private var preferencesRepository: PreferencesRepository = PreferencesRepository()
    private var profileViewModel: ProfileViewModel = ProfileViewModel()

    private lateinit var usernameEditText: EditText
    private lateinit var foodPreferencesEditText: EditText
    private lateinit var dietaryRestrictionsSpinner: Spinner
    private lateinit var activityPreferencesEditText: EditText

    private lateinit var availabilityTextViewList: ArrayList<TextView>
    private lateinit var startButtonList: ArrayList<Button>
    private lateinit var endButtonList: ArrayList<Button>
    private lateinit var busyButtonList: ArrayList<Button>
    private lateinit var saveButton: Button

    private var currentUserId: String = usersAuthRepository.getCurrentUser()!!.uid
    private lateinit var currentUser: Users
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
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        startButtonList = ArrayList(daysInTheWeek)
        endButtonList = ArrayList(daysInTheWeek)
        busyButtonList = ArrayList(daysInTheWeek)
        availabilityTextViewList = ArrayList(daysInTheWeek)

        usernameEditText = binding.usernameInput
        foodPreferencesEditText = binding.foodPreferencesInput
        dietaryRestrictionsSpinner = binding.dietaryRestrictionsInput
        activityPreferencesEditText = binding.activityPreferencesInput

        initializeAvailabilityTextViews()
        initializeButtons()

        // When data is changed, update variables with viewmodel data
        profileViewModel.user.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                currentUser = user
                usernameEditText.setText(user.username)
            }
        }


        profileViewModel.preferences.observe(viewLifecycleOwner) { preferences ->
            if (preferences != null) {
                currentPreferences = preferences
                currentAvailability = currentPreferences.availability!!
                currentDietaryRestrictionIndex = currentPreferences.dietaryRestrictionIndex!!
                currentFoodPreferences = currentPreferences.foodPreferences!!
                currentActivityPreferences = currentPreferences.activityPreferences!!

                foodPreferencesEditText.setText(currentFoodPreferences)
                activityPreferencesEditText.setText(currentActivityPreferences)
                dietaryRestrictionsSpinner.setSelection(currentDietaryRestrictionIndex)

                populateStoredValues()
            }
        }

        saveButton = binding.profileSaveButton
        saveButton.setOnClickListener {
            val currentUserUid = usersAuthRepository.getCurrentUser()!!.uid
            var username = currentUser.username
            if (usernameEditText.text.toString().isNotBlank()) {
                username = usernameEditText.text.toString()
            }
            // Validating that the new username is unique and then updating username for user in database
            CoroutineScope(Dispatchers.IO).launch {
                if (username != null) {
                    usersRepository.isUsernameUnique(currentUserId, username) { isUnique ->
                        when (isUnique) {
                            true -> {
                                // Update username when unique
                                usersRepository.updateUsername(currentUserId, username) { user ->
                                    if (user != null) {
                                        currentUser = user
                                    }
                                }
                            }

                            false -> {
                                // Show alert when the new username inputted is used by another user
                                val alertDialog = CustomAlertDialog(requireContext())
                                alertDialog.showAlertDialog(
                                    "Invalid Username",
                                    getString(R.string.use_different_username_message, username)
                                )
                            }

                            else -> {
                                Log.d("username", "username is null")
                            }
                        }
                    }
                }
            }

            var foodPreferences = "None"
            if (foodPreferencesEditText.text.toString().isNotBlank()) {
                foodPreferences = foodPreferencesEditText.text.toString()
            }
            var activityPreferences = "None"
            if (activityPreferencesEditText.text.toString().isNotBlank()) {
                activityPreferences = activityPreferencesEditText.text.toString()
            }
            val dietaryRestrictionsIndex = dietaryRestrictionsSpinner.selectedItemId.toInt()
            val availability = currentAvailability

            // Validate all availabilities
            if (validateAvailability()) {
                CoroutineScope(Dispatchers.IO).launch {
                    preferencesRepository.updatePreference(
                        currentUserUid, foodPreferences, dietaryRestrictionsIndex,
                        activityPreferences, availability
                    ) { preferences ->
                        profileViewModel.preferences.value = preferences
                    }
                }
            }
        }
        return binding.root
    }

    // Set all availability textViews with their availabilities
    private fun populateStoredValues() {
        for(i in 0..<daysInTheWeek){
            setAvailability(i)
        }
    }

    private fun validateAvailability(): Boolean {
        val weekArray = resources.getStringArray(R.array.days_of_week)
        var isValid = true
        val alertDialog = CustomAlertDialog(requireContext())
        var daysWithInvalidAvailability = getString(R.string.invalid_days_message)
        for(i in 0..< currentAvailability.size step 2){
            val startTime = currentAvailability[i]
            val endTime = currentAvailability[i+1]
            val currentDayIndex = i/2
            val currentDay = weekArray[currentDayIndex]
            if(isInvalidTime(startTime, endTime)){
                daysWithInvalidAvailability += "$currentDay, "
                isValid = false
            }
        }
        // Collect which day textViews have incorrect values so they can be input into the AlertDialog
        daysWithInvalidAvailability = daysWithInvalidAvailability.dropLast(2)
        daysWithInvalidAvailability += getString(R.string.please_correct_availability_message)
        Log.d("Alert", daysWithInvalidAvailability)
        if (!isValid){
            alertDialog.showAlertDialog("Invalid availability entries", daysWithInvalidAvailability)
        }
        return isValid
    }

    private fun setAvailability(dayIndex: Int) {
        // Set the textView corresponding to the day of the week with its availability value
        val startTime = currentAvailability[dayIndex * 2]
        val endTime = currentAvailability[dayIndex * 2 + 1]
        val availability = if (startTime == -1 && endTime == -1){
            getString(R.string.busy_text_textview)
        } else if (startTime == -1){
            getString(R.string.available_from_string, "Unknown", minutesToTime(endTime))
        }else if (endTime == -1){
            getString(R.string.available_from_string, minutesToTime(startTime), "Unknown")
        } else {
            getString(R.string.available_from_string, minutesToTime(startTime), minutesToTime(endTime))
        }
        availabilityTextViewList[dayIndex].text = availability
    }

    private fun setAvailabilityBusy(dayIndex: Int){
        // Busy is represented by -1 in both start and end timeslot in the array, start being even
        // indices, end being odd indices
        currentAvailability[dayIndex*2] = -1
        currentAvailability[dayIndex*2 + 1] = -1
        availabilityTextViewList[dayIndex].text = getString(R.string.busy_text_textview)

    }

    // Function used to check that end time is always greater than start time unless the user is busy
    private fun isInvalidTime(startTime: Int, endTime: Int): Boolean{
        if (startTime != -1 && endTime != -1){
            if (endTime <= startTime){
                return true
            }
            return false
        }
        else return endTime != -1 || startTime != -1
    }

    private fun minutesToTime(minutes: Int): String{
        val startHours = TimeUnit.MINUTES.toHours(minutes.toLong())
        val startMinutes = minutes - TimeUnit.HOURS.toMinutes(startHours)
        return String.format("%02d:%02d", startHours, startMinutes)
    }

    // Dynamically add textviews to a list so they can be index by day of the week
    private fun initializeAvailabilityTextViews() {
        availabilityTextViewList.add(binding.mondayAvailabilityTextview)
        availabilityTextViewList.add(binding.tuesdayAvailabilityTextview)
        availabilityTextViewList.add(binding.wednesdayAvailabilityTextview)
        availabilityTextViewList.add(binding.thursdayAvailabilityTextview)
        availabilityTextViewList.add(binding.fridayAvailabilityTextview)
        availabilityTextViewList.add(binding.saturdayAvailabilityTextview)
        availabilityTextViewList.add(binding.sundayAvailabilityTextview)
    }

    // Dynamically add buttons to a list to set their onClickListeners by indexing the day of the week
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
            val currentStartButton = startButtonList[i]
            currentStartButton.setOnClickListener {
                getTime(i, false)
            }
            val currentEndButton = endButtonList[i]
            currentEndButton.setOnClickListener {
                getTime(i, true)
            }
            val currentBusyButton = busyButtonList[i]
            currentBusyButton.setOnClickListener {
                setAvailabilityBusy(i)
            }
        }
    }

    // Input time for either start of availability or end of availability
    private fun getTime(dayIndex: Int, isEnd: Boolean) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        // Use timePickerDialog for input
        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, hourSelected, minuteSelected ->
                val timeSelected = hourSelected*60 + minuteSelected
                // Store values in 2*n list
                currentAvailability[dayIndex*2 + isEnd.toInt()] = timeSelected
                setAvailability(dayIndex)
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
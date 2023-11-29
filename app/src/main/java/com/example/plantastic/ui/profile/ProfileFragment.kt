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
    private lateinit var foodPreferencesList: TextView
    private lateinit var foodPreferencesEditText: EditText
    private lateinit var dietaryRestrictionsSpinner: Spinner
    private lateinit var activityPreferenceList: TextView
    private lateinit var activityPreferencesEditText: EditText

    private lateinit var mondayStartButton: Button
    private lateinit var tuesdayStartButton: Button
    private lateinit var wednesdayStartButton: Button
    private lateinit var thursdayStartButton: Button
    private lateinit var fridayStartButton: Button
    private lateinit var saturdayStartButton: Button
    private lateinit var sundayStartButton: Button

    private lateinit var mondayEndButton: Button
    private lateinit var tuesdayEndButton: Button
    private lateinit var wednesdayEndButton: Button
    private lateinit var thursdayEndButton: Button
    private lateinit var fridayEndButton: Button
    private lateinit var saturdayEndButton: Button
    private lateinit var sundayEndButton: Button

    private lateinit var mondayBusyButton: Button
    private lateinit var tuesdayBusyButton: Button
    private lateinit var wednesdayBusyButton: Button
    private lateinit var thursdayBusyButton: Button
    private lateinit var fridayBusyButton: Button
    private lateinit var saturdayBusyButton: Button
    private lateinit var sundayBusyButton: Button

    private lateinit var mondayTextView: TextView
    private lateinit var tuesdayTextView: TextView
    private lateinit var wednesdayTextView: TextView
    private lateinit var thursdayTextView: TextView
    private lateinit var fridayTextView: TextView
    private lateinit var saturdayTextView: TextView
    private lateinit var sundayTextView: TextView
    private lateinit var currentPreferences: Preferences
    private var currentUserId: String = usersAuthRepository.getCurrentUser()!!.uid
    private var currentAvailability: MutableList<Int> = mutableListOf(-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1)


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        CoroutineScope(Dispatchers.IO).launch{
            preferencesRepository.getPreferenceById(currentUserId){preferences ->
                if (preferences != null) {
                    currentPreferences = preferences
                    currentAvailability = currentPreferences.availability!!
                    for(i in 0..<currentAvailability.size){
                        Log.d("Availability", "index: $i has item: ${currentAvailability[i]}")
                    }
                }
            }
        }



        foodPreferencesList = binding.foodPreferencesList
        foodPreferencesEditText = binding.foodPreferencesInput
//        profileViewModel.foodPreferences.observe(viewLifecycleOwner) {
//            foodPreferencesList.text = it
//        }

        dietaryRestrictionsSpinner = binding.dietaryRestrictionsInput
//        profileViewModel.dietaryRestrictionIndex.observe(viewLifecycleOwner){
//            dietaryRestrictionsSpinner.setSelection(it)
//        }

        activityPreferenceList = binding.activityPreferencesList
        activityPreferencesEditText = binding.activityPreferencesInput
//        profileViewModel.activityPreferences.observe(viewLifecycleOwner) {
//            activityPreferenceList.text = it
//        }
        profileViewModel.preferences.observe(viewLifecycleOwner){preferences ->
            foodPreferencesList.text = preferences.foodPreferences.toString()
            preferences.dietaryRestrictionIndex?.let { it -> dietaryRestrictionsSpinner.setSelection(it) }
            activityPreferenceList.text = preferences.foodPreferences.toString()

        }

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
            validateAvailability()
            CoroutineScope(Dispatchers.IO).launch {
                preferencesRepository.updatePreference(currentUserUid, foodPreferences, dietaryRestrictionsIndex,
                    activityPreferences, availability){preferences ->
                    profileViewModel.preferences.value = preferences
                }
            }
        }

        initializeAvailabilityTextViews()
        initializeStartAndEndButtons()
        initializeBusyButtons()

        val root: View = binding.root
        return root
    }

    private fun validateAvailability(): Boolean {
        val weekArray = resources.getStringArray(R.array.days_of_week)
        var isValid = true
        for(i in 0..< currentAvailability.size step 2){
            println("sdot size: ${currentAvailability.size}")
            println("sdot i: $i")
            var startTime = currentAvailability[i]
            var endTime = currentAvailability[i+1]
            if(endTime <= startTime || startTime == -1 || endTime == -1){
                isValid = false
                val currentDay = weekArray[i/2]
                setAvailabilityText(i, getString(R.string.available_from_string, minutesToTime(startTime), minutesToTime(endTime)))
                Toast.makeText(this.context,
                    "The availability for $currentDay is invalid, start time:${minutesToTime(startTime)} end time: ${minutesToTime(endTime)} ",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        return isValid
    }

    private fun setAvailabilityText(dayIndex: Int, text: String){
        when (dayIndex){
            0 -> mondayTextView.text = text
            1 -> tuesdayTextView.text = text
            2 -> wednesdayTextView.text = text
            3 -> thursdayTextView.text = text
            4 -> fridayTextView.text = text
            5 -> saturdayTextView.text = text
            6 -> sundayTextView.text = text
        }
    }

    private fun minutesToTime(minutes: Int): String{
        val startHours = TimeUnit.MINUTES.toHours(minutes.toLong())
        val startMinutes = minutes - TimeUnit.HOURS.toMinutes(startHours)
        return String.format("%02d:%02d", startHours, startMinutes)
    }

    private fun initializeAvailabilityTextViews() {
        mondayTextView = binding.mondayAvailabilityTextview
        tuesdayTextView = binding.tuesdayAvailabilityTextview
        wednesdayTextView = binding.wednesdayAvailabilityTextview
        thursdayTextView = binding.thursdayAvailabilityTextview
        fridayTextView = binding.fridayAvailabilityTextview
        saturdayTextView = binding.saturdayAvailabilityTextview
        sundayTextView = binding.sundayAvailabilityTextview
    }
    
    private fun initializeStartAndEndButtons(){
        mondayStartButton = binding.mondayStartTimeButton
        mondayStartButton.setOnClickListener { 
            getTime(0, false)
//            Log.d("Availability", "User is available from ${availability?.get(0)} to ${availability?.get(1)}")

        }
        mondayEndButton = binding.mondayEndTimeButton
        mondayEndButton.setOnClickListener {
            getTime(0, true)
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
                for(i in 0..<currentAvailability.size){
                    Log.d("Availability", "index: $i has item: ${currentAvailability[i]}")
                }
            },
            hour,
            minute,
            true
        )
        timePickerDialog.show()
    }

    // Function below taken from: https://stackoverflow.com/questions/46401879/boolean-int-conversion-in-kotlin
    private fun Boolean.toInt() = if (this) 1 else 0

    private fun initializeBusyButtons() {
        mondayBusyButton = binding.mondayBusyButton
        mondayBusyButton.setOnClickListener {
            setAvailabilityText(0, "Busy")
        }
        tuesdayBusyButton = binding.tuesdayBusyButton
        tuesdayBusyButton.setOnClickListener {
            setAvailabilityText(1, "Busy")
        }
        wednesdayBusyButton = binding.wednesdayBusyButton
        wednesdayBusyButton.setOnClickListener {
            setAvailabilityText(2, "Busy")
        }
        thursdayBusyButton = binding.thursdayBusyButton
        thursdayBusyButton.setOnClickListener {

            setAvailabilityText(3, "Busy")
        }
        fridayBusyButton = binding.fridayBusyButton
        fridayBusyButton.setOnClickListener {
            setAvailabilityText(4, "Busy")
        }
        saturdayBusyButton = binding.saturdayBusyButton
        saturdayBusyButton.setOnClickListener {
            setAvailabilityText(5, "Busy")
        }
        sundayBusyButton = binding.sundayBusyButton
        sundayBusyButton.setOnClickListener {
            setAvailabilityText(6, "Busy")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
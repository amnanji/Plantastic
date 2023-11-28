package com.example.plantastic.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import com.example.plantastic.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var saveButton: Button
    private lateinit var foodPreferencesList: TextView
    private lateinit var foodPreferencesEditText: EditText
    private lateinit var dietaryRestrictionsSpinner: Spinner
    private lateinit var activityPreferenceList: TextView
    private lateinit var activityPreferencesEditText: EditText
    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        profileViewModel = ProfileViewModel()

        foodPreferencesList = binding.foodPreferencesList
        foodPreferencesEditText = binding.foodPreferencesInput
        profileViewModel.foodPreferences.observe(viewLifecycleOwner) {
            foodPreferencesList.text = it
        }

        dietaryRestrictionsSpinner = binding.dietaryRestrictionsInput
        profileViewModel.dietaryRestrictionIndex.observe(viewLifecycleOwner){
            dietaryRestrictionsSpinner.setSelection(it)
        }

        activityPreferenceList = binding.activityPreferencesList
        activityPreferencesEditText = binding.activityPreferencesInput
        profileViewModel.activityPreferences.observe(viewLifecycleOwner) {
            activityPreferenceList.text = it
        }

        saveButton = binding.profileSaveButton
        saveButton.setOnClickListener {
            profileViewModel.foodPreferences.value = foodPreferencesEditText.text.toString()
            profileViewModel.activityPreferences.value = activityPreferencesEditText.text.toString()
            profileViewModel.dietaryRestrictionIndex.value = dietaryRestrictionsSpinner.selectedItemId.toInt()
            println("sdot ProfileFragment:saveButton.setOnClickListener:55: ${dietaryRestrictionsSpinner.selectedItemId.toInt()}")
        }


        val root: View = binding.root
        return root
    }

    private fun updateProfileViewModel() {
        updateFoodPreferences()
    }

    private fun updateFoodPreferences() {
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
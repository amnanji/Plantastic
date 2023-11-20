package com.example.plantastic.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.plantastic.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val galleryViewModel =
            ViewModelProvider(this).get(SettingsViewModel::class.java)

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textSettings
        galleryViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        val foodTextView: TextView = binding.foodPreferencesList
        galleryViewModel.foodPreferences.observe(viewLifecycleOwner){
            foodTextView.text = it
        }

        val activitiesTextView: TextView = binding.activityPreferencesList
        galleryViewModel.activityPreferences.observe(viewLifecycleOwner){
            activitiesTextView.text = it
        }

        val availabilityTextView: TextView = binding.availabilityPreferencesList
        galleryViewModel.availabilityPreferences.observe(viewLifecycleOwner){
            availabilityTextView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
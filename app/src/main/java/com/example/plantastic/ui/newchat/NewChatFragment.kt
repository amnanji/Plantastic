package com.example.plantastic.ui.newchat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.viewpager.widget.ViewPager
import com.example.plantastic.MainActivity
import com.example.plantastic.databinding.FragmentNewChatBinding
import com.google.android.material.tabs.TabLayout

class NewChatFragment : Fragment() {

    private var _binding: FragmentNewChatBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewChatBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val mainActivity: MainActivity = (requireActivity() as MainActivity)

        // Get the nav controller from main activity
        val navController: NavController = mainActivity.navController

        val newChatFragmentId: Int = mainActivity.newChatsFragmentId
        val newIndividualChatFragmentId: Int = mainActivity.newIndividualChatFragmentId
        val newGroupChatFragmentId: Int = mainActivity.newGroupChatFragmentId


        val viewPager: ViewPager = binding.viewPager
        val tabLayout: TabLayout = binding.tabLayout

        // Set up ViewPager with the adapter
        val pagerAdapter = NewChatPagerAdapter(childFragmentManager)
        viewPager.adapter = pagerAdapter

        // Link the TabLayout with the ViewPager
        tabLayout.setupWithViewPager(viewPager)


        return root
    }
}

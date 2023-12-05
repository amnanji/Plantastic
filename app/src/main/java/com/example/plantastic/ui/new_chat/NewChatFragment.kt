package com.example.plantastic.ui.new_chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.plantastic.R
import com.example.plantastic.databinding.FragmentNewChatBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class NewChatFragment : Fragment() {

    private var _binding: FragmentNewChatBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewChatBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Get the ViewPager2 and set it up with the TabLayout
        val viewPager: ViewPager2 = root.findViewById(R.id.viewPager)
        val tabLayout: TabLayout = root.findViewById(R.id.tabLayout)

        // Set up ViewPager2 with the adapter
        val pagerAdapter = NewChatPagerAdapter(childFragmentManager, lifecycle)
        viewPager.adapter = pagerAdapter

        // Link the TabLayout with the ViewPager2
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> requireContext().getString(R.string.friends)
                1 -> requireContext().getString(R.string.group)
                else -> ""
            }
        }.attach()

        return root
    }
}

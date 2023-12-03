package com.example.plantastic.ui.newchat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
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

        // Get the ViewPager and set it up with the TabLayout
        val viewPager: ViewPager = binding.viewPager
        val tabLayout: TabLayout = binding.tabLayout

        // Create an instance of the adapter that will hold the fragments for each tab
        val adapter = ViewPagerAdapter(childFragmentManager)
        adapter.addFragment(NewChatFragment(), "Friends")
        adapter.addFragment(NewGroupFragment(), "New Group")

        // Set the adapter to the ViewPager
        viewPager.adapter = adapter

        // Connect the TabLayout with the ViewPager
        tabLayout.setupWithViewPager(viewPager)

        return root
    }

    // Adapter for the ViewPager
    private class ViewPagerAdapter(manager: androidx.fragment.app.FragmentManager) :
        androidx.fragment.app.FragmentPagerAdapter(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        private val fragmentList: MutableList<Fragment> = mutableListOf()
        private val fragmentTitleList: MutableList<String> = mutableListOf()

        override fun getItem(position: Int): Fragment {
            return fragmentList[position]
        }

        override fun getCount(): Int {
            return fragmentList.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            fragmentList.add(fragment)
            fragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return fragmentTitleList[position]
        }
    }
}

package com.example.plantastic.ui.newchat

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class NewChatPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> NewIndividualChatFragment()
            1 -> NewGroupChatFragment()
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }

    override fun getCount(): Int {
        return 2  // Number of tabs/fragments
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "Individual"
            1 -> "Group"
            else -> null
        }
    }
}
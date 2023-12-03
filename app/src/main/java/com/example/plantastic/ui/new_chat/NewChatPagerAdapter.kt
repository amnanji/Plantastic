package com.example.plantastic.ui.new_chat

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.plantastic.ui.new_friend_chat.NewIndividualChatFragment
import com.example.plantastic.ui.new_group.NewGroupChatFragment

// NewChatPagerAdapter.kt

// Import statements...

class NewChatPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return 2  // Number of tabs/fragments
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> NewIndividualChatFragment()
            1 -> NewGroupChatFragment()
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }
}

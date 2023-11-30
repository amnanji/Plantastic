package com.example.plantastic.ui.conversation

import androidx.recyclerview.widget.RecyclerView
import com.example.plantastic.utilities.WrapContentLinearLayoutManager

// Referenced from: https://firebase.google.com/codelabs/firebase-android#0
class ScrollToBottomObserver (
    private val recycler: RecyclerView,
    private val adapter: ConversationAdapter,
    private val manager: WrapContentLinearLayoutManager
    ) : RecyclerView.AdapterDataObserver() {
        // If the user is currently looking at the latest message, or the data has just loaded
        // it automatically scrolls the user down when a new message appears
        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            super.onItemRangeInserted(positionStart, itemCount)
            val count = adapter.itemCount
            val lastVisiblePosition = manager.findLastCompletelyVisibleItemPosition()

            val loading = lastVisiblePosition == -1
            val atBottom = positionStart >= count - 1 && lastVisiblePosition == positionStart - 1
            if (loading || atBottom) {
                recycler.scrollToPosition(positionStart)
            }
        }
    }
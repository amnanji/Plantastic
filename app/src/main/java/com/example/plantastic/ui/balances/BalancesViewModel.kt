package com.example.plantastic.ui.balances

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BalancesViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is balances Fragment"
    }
    val text: LiveData<String> = _text

    // Dummy data for testing
    val balancesList: List<BalancesItem> = listOf(
        BalancesItem("John Doe", "You owe:", 50.0),
        BalancesItem("Jane Doe", "You are owed:", 25.0)

    )
}

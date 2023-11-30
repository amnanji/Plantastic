package com.example.plantastic.models

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Transaction(
    var id: String,
    var groupId: String,
    var date: String,
    var description: String,
    var moneyOwedTo: String,
    var totalAmount: Double
)
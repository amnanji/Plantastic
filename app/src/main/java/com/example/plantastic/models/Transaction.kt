package com.example.plantastic.models

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Transaction(
    var id: String? = null,
    var groupId: String? = null,
    var timestamp: Long? = null,
    var description: String? = null,
    var moneyOwedTo: String? = null,
    var totalAmount: Double? = null,
    var transactionType: String? = null
)
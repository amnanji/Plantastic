package com.example.plantastic.models


import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class CalendarElement(
    val title: String? = null,
    val type: String? = null,
    val date: Long? = null,
    var GID: String? = null
)
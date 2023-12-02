package com.example.plantastic.utilities

import android.text.format.DateFormat
import java.util.Calendar

object DateTimeUtils {
    fun getDateString(timestamp: Long): String {
        val currCalendar = Calendar.getInstance()
        val msgCalendar = Calendar.getInstance()
        msgCalendar.timeInMillis = timestamp

        return if (currCalendar.get(Calendar.YEAR) != msgCalendar.get(Calendar.YEAR)) {
            DateFormat.format("MMM dd, yyyy hh:mm a", msgCalendar).toString()
        } else if (currCalendar.get(Calendar.MONTH) != msgCalendar.get(Calendar.MONTH)) {
            DateFormat.format("MMM dd hh:mm a", msgCalendar).toString()
        } else if (currCalendar.get(Calendar.DATE) != msgCalendar.get(Calendar.DATE)) {
            DateFormat.format("MMM dd hh:mm a", msgCalendar).toString()
        } else {
            DateFormat.format("hh:mm a", msgCalendar).toString()
        }
    }
}
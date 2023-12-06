package com.example.plantastic.ui.calendar

import android.content.Context
import android.graphics.Color
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.spans.DotSpan

//Referenced from: https://github.com/prolificinteractive/material-calendarview/wiki/Decorators
class CurrentDayDecorator( private val currentDay: CalendarDay) : DayViewDecorator {

    private val dotColor: Int = Color.RED // Set your dot color here

    override fun shouldDecorate(day: CalendarDay): Boolean {
        return day == currentDay
    }

    override fun decorate(view: DayViewFacade) {
        view.addSpan(DotSpan(5F, dotColor))
    }
}
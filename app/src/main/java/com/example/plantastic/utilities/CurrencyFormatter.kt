package com.example.plantastic.utilities

import java.math.BigDecimal
import java.math.RoundingMode

object CurrencyFormatter {
    fun format(value: Double): String{
        return "$%.2f".format(value).removeSuffix(".00")
    }
    fun roundToTwoDecimalPlaces(number: Double): Double {
        val bd = BigDecimal(number).setScale(2, RoundingMode.HALF_UP)
        return bd.toDouble()
    }
}
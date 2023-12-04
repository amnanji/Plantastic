package com.example.plantastic.utilities

object CurrencyFormatter {
    fun format(value: Double): String{
        return "$%.2f".format(value).removeSuffix(".00")
    }
}
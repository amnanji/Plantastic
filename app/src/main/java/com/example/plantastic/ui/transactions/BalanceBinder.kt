package com.example.plantastic.ui.transactions

import android.os.Binder

class BalanceBinder(private val map: Map<String, Double>) : Binder() {
    fun getMap(): Map<String, Double> {
        return map
    }
}
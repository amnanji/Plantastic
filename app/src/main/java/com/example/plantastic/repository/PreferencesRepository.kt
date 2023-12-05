package com.example.plantastic.repository

import android.util.Log
import com.example.plantastic.models.Preferences
import com.example.plantastic.utilities.FirebaseNodes
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking

class PreferencesRepository {
    private var firebaseDatabase: FirebaseDatabase =  FirebaseDatabase.getInstance()
    private var preferencesReference: DatabaseReference = firebaseDatabase.getReference(FirebaseNodes.PREFERENCES_NODE)

    fun createNewPreference(userId: String, foodPreferences: String, dietaryRestrictionIndex: Int,
            activityPreferences: String, availability: MutableList<Int>, onComplete: (Boolean) -> Unit) {

        val preferences = Preferences(foodPreferences, dietaryRestrictionIndex, activityPreferences, availability)
        preferencesReference.child(userId).setValue(preferences)
            .addOnSuccessListener {
                onComplete(true)
            }
            .addOnFailureListener {
                onComplete(false)
            }
    }

    fun getPreferenceById(id: String, callback: (Preferences?) -> Unit) {
        val reference = preferencesReference.child(id)
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val preference = dataSnapshot.getValue(Preferences::class.java)
                callback(preference)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
                callback(null)
            }
        })
    }

    fun getPreferencesById(ids: ArrayList<String>): ArrayList<Preferences> {
        val ret = ArrayList<Preferences>()

        for (id in ids) {
            val reference = preferencesReference.child(id)
            val deferred = CompletableDeferred<Preferences?>()

            reference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("Pln", "Received user --> $snapshot")
                    deferred.complete(snapshot.getValue(Preferences::class.java))
                }

                override fun onCancelled(error: DatabaseError) {
                    deferred.completeExceptionally(error.toException())
                }
            })
            runBlocking { deferred.await() }?.let { ret.add(it) }
        }
        return ret
    }

    fun updatePreference(
        userId: String,
        foodPreferences: String,
        dietaryRestrictionIndex: Int,
        activityPreferences: String,
        availability: MutableList<Int>,
        callback: (Preferences?) -> Unit
    ) {
        val reference = preferencesReference.child(userId)
        reference.child("foodPreferences").setValue(foodPreferences)
        reference.child("dietaryRestrictionIndex").setValue(dietaryRestrictionIndex)
        reference.child("activityPreferences").setValue(activityPreferences)
        reference.child("availability").setValue(availability)
    }
}
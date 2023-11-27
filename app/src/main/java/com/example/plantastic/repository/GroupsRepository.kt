package com.example.plantastic.repository

import com.example.plantastic.utilities.FirebaseNodes
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query

class GroupsRepository {
    private var firebaseDatabase: FirebaseDatabase =  FirebaseDatabase.getInstance()
    private var groupsReference: DatabaseReference = firebaseDatabase.getReference(FirebaseNodes.GROUPS_NODE)

    fun getAllGroupsQueryForUser(userId: String): Query {
        return  groupsReference.orderByChild("${FirebaseNodes.GROUPS_PARTICIPANTS_NODE}/${userId}").equalTo(true)
    }
}
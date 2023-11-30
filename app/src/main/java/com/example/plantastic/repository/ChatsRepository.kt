package com.example.plantastic.repository

import com.example.plantastic.utilities.FirebaseNodes
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ChatsRepository {
    private var firebaseDatabase: FirebaseDatabase =  FirebaseDatabase.getInstance()
    private var groupsReference: DatabaseReference = firebaseDatabase.getReference(FirebaseNodes.GROUPS_NODE)

//    fun getConversations(userId: String): ArrayList<Chat>{
//        conversationsReference.orderByChild("participants/$userId").equalTo(true)
//
//    }


}
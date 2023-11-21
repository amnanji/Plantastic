package com.example.plantastic.repository

import com.example.plantastic.utilities.FirebaseNodes
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ChatsRepository {
    private var firebaseDatabase: FirebaseDatabase =  FirebaseDatabase.getInstance()
    private var chatsReference: DatabaseReference = firebaseDatabase.getReference(FirebaseNodes.CHATS_NODE)

//    fun getConversations(userId: String): ArrayList<Chat>{
//        conversationsReference.orderByChild("participants/$userId").equalTo(true)
//
//    }


}
package com.example.plantastic.utilities

object FirebaseNodes {
    // Top Level Nodes
    const val USERS_NODE = "users"
    const val USERNAME_NODE = "username"
    const val GROUPS_NODE = "groups"
    const val MESSAGES_NODE = "messages"
    const val TODO_LISTS_NODE = "toDoLists"
    const val PREFERENCES_NODE = "preferences"
    const val TRANSACTIONS_NODE = "transactions"

    // Users Child Nodes
    const val EMAIL_NODE = "email"

    // Group Child Nodes
    const val GROUPS_PARTICIPANTS_NODE = "participants"
    const val GROUPS_LATEST_MESSAGE_NODE = "latestMessage"
    const val EVENTS_NODE = "events"

    // Messages Child Nodes
    const val MESSAGES_TIMESTAMP_NODE = "timestamp"

    // TodoLists Child Nodes
    const val TODO_LISTS_IS_COMPLETED_NODE = "isCompleted"
    const val TODO_LISTS_COMPLETED_DATE_NODE = "completedDate"

    // Transactions Child Nodes
    const val TRANSACTIONS_GROUP_NODE = "groupId"
    const val USERS_FRIENDS_NODE = "friends"
    const val USERS_COLOUR_NODE = "color"

}

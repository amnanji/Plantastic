package com.example.plantastic.ui.events

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.plantastic.R
import com.example.plantastic.models.Events
import com.example.plantastic.repository.GroupsRepository
import com.example.plantastic.repository.UsersRepository
import com.example.plantastic.utilities.DateTimeUtils
import com.example.plantastic.utilities.IconUtil

class EventsAdapter(private val eventsList: List<Events>, private val userID: String) : RecyclerView.Adapter<EventsAdapter.EventsViewHolder>() {

    private val groupsRepository = GroupsRepository()
    private val usersRepository = UsersRepository()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.custom_events_layout, parent, false)
        return EventsViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventsViewHolder, position: Int) {
        val event = eventsList[position]
        holder.eventName.text = event.name
        holder.eventDate.text = event.date?.let { DateTimeUtils.getDateString(it) }
        holder.eventLocation.text = event.location
        groupsRepository.getGroupById(event.GID!!){ groups ->
            if(groups!=null)
            {
                if(groups.groupType =="group") {
                    holder.groupName.text = groups.name
                }
                else{
                    val participants = groups.participants!!.keys.toList()
                    val otherParticipantId =
                        if (participants[0] == userID) participants[1] else participants[0]
                    usersRepository.getUserById(otherParticipantId) {
                        if (it != null) {
                            val chatName = "${it.firstName} ${it.lastName}"
                            holder.groupName.text = chatName
                        }
                    }
                }
                holder.groupName.setTextColor(IconUtil(holder.itemView.context).colorList[groups.color!!])
            }
        }
    }
    override fun getItemCount(): Int {
        return eventsList.size
    }
    inner class EventsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val eventName: TextView = itemView.findViewById(R.id.eventName)
        val eventDate: TextView = itemView.findViewById(R.id.eventDate)
        val eventLocation: TextView = itemView.findViewById(R.id.eventLocationName)
        val groupName: TextView = itemView.findViewById(R.id.eventsGroupName)
    }
}

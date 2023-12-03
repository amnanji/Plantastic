package com.example.plantastic.ui.events

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.plantastic.R
import com.example.plantastic.models.Events
import com.example.plantastic.repository.GroupsRepository

class EventsAdapter(private val eventsList: List<Events>) : RecyclerView.Adapter<EventsAdapter.EventsViewHolder>() {

    private val groupsRepository = GroupsRepository()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.custom_events_layout, parent, false)
        return EventsViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventsViewHolder, position: Int) {
        val event = eventsList[position]
        holder.eventName.text = event.name
        holder.eventDate.text = event.date
        holder.eventLocation.text = event.location
        groupsRepository.getGroupById(event.GID!!){
            if(it!=null)
            {
                holder.groupName.text = it.name
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

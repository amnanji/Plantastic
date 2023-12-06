package com.example.plantastic.ui.new_group

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.plantastic.R
import com.example.plantastic.models.Users
import com.example.plantastic.utilities.IconUtil

class NewGroupAdapter(
    private var dataList: List<Users>,
    private val newGroupViewModel: NewGroupViewModel
) :
    RecyclerView.Adapter<NewGroupAdapter.NewGroupViewHolder>() {

    inner class NewGroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val usernameTextView: TextView = itemView.findViewById(R.id.searchUsernameTextView)
        val nameTextView: TextView = itemView.findViewById(R.id.searchNameTextView)
        val checkBox: CheckBox = itemView.findViewById(R.id.checkBoxSearchUsers)
        val profileIconImageView: ImageView = itemView.findViewById(R.id.profileIconImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewGroupViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.custom_search_users_layout, parent, false)
        return NewGroupViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: NewGroupViewHolder, position: Int) {
        val model = dataList[position]
        holder.usernameTextView.text = model.username
        "${model.firstName} ${model.lastName}".also { holder.nameTextView.text = it }
        holder.checkBox.visibility = View.VISIBLE
        holder.checkBox.isChecked = newGroupViewModel.isUserInMembersList(model)

        val iconUtil = IconUtil(holder.itemView.context)
        val drawable = iconUtil.getIcon(model.firstName!!, model.lastName!!, model.color!!)
        holder.profileIconImageView.setImageDrawable(drawable)

        holder.checkBox.setOnClickListener {
            if (newGroupViewModel.isUserInMembersList(model)) {
                newGroupViewModel.removeFromMembersList(model)
            } else {
                newGroupViewModel.addToMembersList(model)
            }
        }
    }
}
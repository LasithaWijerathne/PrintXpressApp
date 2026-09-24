package com.printxpress.app.ui.notifications

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.printxpress.app.data.model.AppNotification
import com.printxpress.app.databinding.ItemNotificationBinding

class NotificationsAdapter(
    private var notifications: List<AppNotification>,
    private val onClick: (AppNotification) -> Unit
) : RecyclerView.Adapter<NotificationsAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemNotificationBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notification = notifications[position]
        holder.binding.textNotifTitle.text = notification.title
        holder.binding.textNotifBody.text = notification.message
        holder.binding.dotUnread.visibility = if (notification.read) View.INVISIBLE else View.VISIBLE
        holder.binding.root.setOnClickListener { onClick(notification) }
    }

    override fun getItemCount() = notifications.size

    fun submitList(newList: List<AppNotification>) {
        notifications = newList
        notifyDataSetChanged()
    }
}

package com.taskmind.app.ui

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.taskmind.app.R
import com.taskmind.app.databinding.ItemChatMessageBinding
import com.taskmind.app.domain.model.ChatMessage

class AIChatAdapter : ListAdapter<ChatMessage, AIChatAdapter.ChatViewHolder>(ChatDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding = ItemChatMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ChatViewHolder(private val binding: ItemChatMessageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: ChatMessage) {
            val layoutParams = binding.llMessageContent.layoutParams as android.widget.RelativeLayout.LayoutParams
            
            if (message.isUser) {
                // User message (Right aligned)
                layoutParams.addRule(android.widget.RelativeLayout.ALIGN_PARENT_END)
                layoutParams.removeRule(android.widget.RelativeLayout.ALIGN_PARENT_START)
                binding.tvMessage.setBackgroundResource(R.drawable.bg_chat_bubble_user)
                binding.tvMessage.setTextColor(android.graphics.Color.WHITE)
                binding.llMessageContent.gravity = android.view.Gravity.END
            } else {
                // AI message (Left aligned)
                layoutParams.addRule(android.widget.RelativeLayout.ALIGN_PARENT_START)
                layoutParams.removeRule(android.widget.RelativeLayout.ALIGN_PARENT_END)
                binding.tvMessage.setBackgroundResource(R.drawable.bg_chat_bubble_ai)
                binding.tvMessage.setTextColor(android.graphics.Color.BLACK)
                binding.llMessageContent.gravity = android.view.Gravity.START
            }
            
            binding.llMessageContent.layoutParams = layoutParams

            if (message.isTyping) {
                binding.tvMessage.text = "Typing..."
                binding.tvMessage.animate().alpha(0.5f).setDuration(500).withEndAction {
                    binding.tvMessage.animate().alpha(1.0f).setDuration(500).start()
                }.start()
            } else {
                binding.tvMessage.text = message.text
                binding.tvMessage.clearAnimation()
                binding.tvMessage.alpha = 1.0f
            }

            // Simple timestamp formatting
            val date = java.util.Date(message.timestamp)
            val format = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
            binding.tvTimestamp.text = format.format(date)

            // Add slide-in animation for new messages
            binding.root.translationY = 50f
            binding.root.alpha = 0f
            binding.root.animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(300)
                .setInterpolator(android.view.animation.OvershootInterpolator())
                .start()
        }
    }

    class ChatDiffCallback : DiffUtil.ItemCallback<ChatMessage>() {
        override fun areItemsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean = oldItem == newItem
    }
}

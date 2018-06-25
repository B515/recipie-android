package xin.z7workbench.recipie.ui.chat

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import me.himanshusoni.chatmessageview.ChatMessageView
import xin.z7workbench.recipie.R
import xin.z7workbench.recipie.entity.ServerMessage

class ChatMessageAdapter(private val context: Context, private val messages: MutableList<ServerMessage>) : RecyclerView.Adapter<ChatMessageAdapter.MessageHolder>() {
    override fun getItemCount(): Int = messages.size

    override fun getItemViewType(position: Int): Int = if (messages[position].fromMyself) REQUEST else RESPONSE

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageHolder =
            MessageHolder(LayoutInflater.from(context).inflate(
                    if (viewType == REQUEST) R.layout.item_request
                    else R.layout.item_response, parent, false))

    override fun onBindViewHolder(holder: MessageHolder, position: Int) {
        val message = messages[position]
        holder.image.visibility = if (message.MsgType != "text") View.VISIBLE else View.GONE
        holder.message.visibility = if (message.MsgType == "text") View.VISIBLE else View.GONE

        holder.username.text = message.FromUser
        if (message.MsgType != "text") {
            holder.image.setImageResource(R.mipmap.ic_launcher)
        } else {
            holder.message.text = message.Content
        }

        holder.time.text = message.CreateTime

        holder.messageView.setOnClickListener { }
    }

    fun add(message: ServerMessage) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }

    inner class MessageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val username: TextView = itemView.findViewById(R.id.username)
        val message: TextView = itemView.findViewById(R.id.message)
        val time: TextView = itemView.findViewById(R.id.time)
        val image: ImageView = itemView.findViewById(R.id.image)
        val messageView: ChatMessageView = itemView.findViewById(R.id.message_view)
    }

    companion object {
        private val REQUEST = 0
        private val RESPONSE = 1
    }
}
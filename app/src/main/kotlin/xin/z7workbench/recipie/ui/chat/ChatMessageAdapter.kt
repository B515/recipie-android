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
import xin.z7workbench.recipie.entity.ChatMessage
import java.text.SimpleDateFormat
import java.util.*

class ChatMessageAdapter(private val context: Context, private val messages: MutableList<ChatMessage>) : RecyclerView.Adapter<ChatMessageAdapter.MessageHolder>() {
    override fun getItemCount(): Int = messages.size

    override fun getItemViewType(position: Int): Int = if (messages[position].isRequest) REQUEST else RESPONSE

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageHolder =
            MessageHolder(LayoutInflater.from(context).inflate(
                    if (viewType == REQUEST) R.layout.item_request
                    else R.layout.item_response, parent, false))

    override fun onBindViewHolder(holder: MessageHolder, position: Int) {
        val chatMessage = messages[position]
        holder.image.visibility = if (chatMessage.isImage) View.VISIBLE else View.GONE
        holder.message.visibility = if (!chatMessage.isImage) View.VISIBLE else View.GONE

        if (chatMessage.isImage) {
            holder.image.setImageResource(R.mipmap.ic_launcher)
        } else {
            holder.message.text = chatMessage.content
        }

        holder.time.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

        holder.messageView.setOnClickListener { }
    }

    fun add(message: ChatMessage) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }

    inner class MessageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val message: TextView = itemView.findViewById<TextView>(R.id.message)
        val time: TextView = itemView.findViewById<TextView>(R.id.time)
        val image: ImageView = itemView.findViewById<ImageView>(R.id.image)
        val messageView: ChatMessageView = itemView.findViewById<ChatMessageView>(R.id.message_view)
    }

    companion object {
        private val REQUEST = 0
        private val RESPONSE = 1
    }
}
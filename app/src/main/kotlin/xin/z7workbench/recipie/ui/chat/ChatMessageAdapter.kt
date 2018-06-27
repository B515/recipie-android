package xin.z7workbench.recipie.ui.chat

import android.content.Context
import android.text.format.Formatter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import me.himanshusoni.chatmessageview.ChatMessageView
import xin.z7workbench.recipie.R
import xin.z7workbench.recipie.entity.FileInfoMessage
import xin.z7workbench.recipie.entity.ServerMessage
import xin.z7workbench.recipie.util.getAbsolutePath
import xin.z7workbench.recipie.util.openFile

class ChatMessageAdapter(private val context: Context, private val messages: MutableList<MessageComposite>) :
        ListAdapter<ChatMessageAdapter.MessageComposite, ChatMessageAdapter.MessageHolder>(object : DiffUtil.ItemCallback<MessageComposite>() {
            override fun areItemsTheSame(oldItem: MessageComposite, newItem: MessageComposite): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: MessageComposite, newItem: MessageComposite): Boolean {
                return oldItem == newItem && oldItem.file == newItem.file
            }
        }) {
    override fun getItemCount(): Int = messages.size

    override fun getItemViewType(position: Int): Int = if (messages[position].fromMyself) REQUEST else RESPONSE

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageHolder =
            MessageHolder(LayoutInflater.from(context).inflate(
                    if (viewType == REQUEST) R.layout.item_request
                    else R.layout.item_response, parent, false))

    override fun onBindViewHolder(holder: MessageHolder, position: Int) {
        val composite = getItem(position)
        val message = composite.message

        holder.username.text = message.FromUser
        when (message.MsgType) {
            "image", "file" -> {
                val fileSize = Formatter.formatFileSize(holder.image.context, composite.file!!.size.toLong())
                if (!composite.file.finished) {
                    holder.image.visibility = View.GONE
                    val progress = (composite.file.processed.toDouble() / composite.file.size * 100).toInt()
                    holder.message.text = "${message.Content}, $fileSize\n$progress%"
                } else {
                    val path = if (composite.fromMyself) composite.file.localPath!!
                    else getAbsolutePath(composite.file.id, message.Content)
                    holder.messageView.setOnClickListener {
                        context.startActivity(openFile(path))
                    }
                    holder.message.text = "${message.Content}, $fileSize"
                    when (message.MsgType) {
                        "image" -> {
                            holder.image.visibility = View.VISIBLE
                            Glide.with(holder.image)
                                    .load(path)
                                    .into(holder.image)
                        }
                        "file" -> {
                            holder.image.visibility = View.GONE
                        }
                    }
                }
            }
            "text" -> {
                holder.message.text = message.Content
                holder.image.visibility = View.GONE
            }
        }
        holder.time.text = message.CreateTime
    }

    fun add(message: ServerMessage, fromMyself: Boolean) {
        messages.add(MessageComposite(message, null, fromMyself))
        submitList(messages)
    }

    fun add(message: FileInfoMessage, fromMyself: Boolean, localPath: String?) {
        messages.add(MessageComposite(ServerMessage(message.Object, message.ToUser, message.FromUser, message.CreateTime,
                message.MsgType, message.FileName, null), FileInfo(localPath, message.MsgID, message.FileSize, 0), fromMyself))
        submitList(messages)
    }

    fun updateFileProgress(id: Int, processed: Int) {
        val item = messages.last { it.file?.id == id }
        item.file?.processed = processed
        submitList(messages)
        notifyDataSetChanged()
    }

    inner class MessageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val username: TextView = itemView.findViewById(R.id.username)
        val message: TextView = itemView.findViewById(R.id.message)
        val time: TextView = itemView.findViewById(R.id.time)
        val image: ImageView = itemView.findViewById(R.id.image)
        val messageView: ChatMessageView = itemView.findViewById(R.id.message_view)
    }

    data class MessageComposite(val message: ServerMessage, val file: FileInfo?, val fromMyself: Boolean)
    data class FileInfo(val localPath: String?, val id: Int, val size: Int, var processed: Int) {
        val finished
            get() = size == processed
    }

    companion object {
        private val REQUEST = 0
        private val RESPONSE = 1
    }
}
package xin.z7workbench.recipie.ui.chat

import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import okio.BufferedSink
import okio.BufferedSource
import okio.Okio
import xin.z7workbench.recipie.R
import xin.z7workbench.recipie.entity.ChatMessage
import xin.z7workbench.recipie.entity.LoginMessage
import xin.z7workbench.recipie.entity.ServerMessage
import java.io.IOException
import java.net.Socket
import java.text.SimpleDateFormat
import java.util.*

class ChatActivity : AppCompatActivity() {

    lateinit var adapter: ChatMessageAdapter

    lateinit var socket: Socket
    lateinit var sink: BufferedSink
    lateinit var source: BufferedSource
    lateinit var gson: Gson

    var username = "ZeroGo"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        setSupportActionBar(toolbar)

        recycler.layoutManager = LinearLayoutManager(this)
        adapter = ChatMessageAdapter(this, ArrayList())
        recycler.adapter = adapter
        gson = Gson()

        button_submit.setOnClickListener(View.OnClickListener {
            val text = edit_question.text.toString()
            if (TextUtils.isEmpty(text)) {
                return@OnClickListener
            }

            val message = ServerMessage("all", "", username, now(), "text", text, null)
            sendMessage(text, gson.toJson(message))
            edit_question.setText("")
        })

        connect()
    }

    private fun connect() = async(UI) {
        async(CommonPool) {
            try {
                socket = Socket("123.206.13.211", 8964)
                sink = Okio.buffer(Okio.sink(socket))
                source = Okio.buffer(Okio.source(socket))
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }.await()
        Thread(receiver).start()

        sendMessage(username, gson.toJson(LoginMessage(username)))
    }

    private val receiver = Runnable {
        while (true) {
            try {
                if (!socket.isConnected) continue
                if (socket.isInputShutdown) continue
                val content = source.readUtf8Line() ?: continue
                runOnUiThread { showMessage(content) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun sendMessage(message: String, json: String) = async(UI) {
        async(CommonPool) {
            if (socket.isConnected) {
                if (!socket.isOutputShutdown) {
                    sink.writeUtf8("$json\n")
                    sink.flush()
                }
            }
        }.await()

        val chatMessage = ChatMessage(message, now(), true, false)
        adapter.add(chatMessage)

        recycler.scrollToPosition(adapter.itemCount - 1)
    }

    private fun showMessage(json: String) {
        val message = gson.fromJson<ServerMessage>(json, ServerMessage::class.java)
        val str = "${message.FromUser}:${message.Content}"
        val chatMessage = ChatMessage(str, message.CreateTime, false, false)
        adapter.add(chatMessage)
        online_users.text = "OnlineUsers:${message.OnlineUser?.joinToString()}"

        recycler.scrollToPosition(adapter.itemCount - 1)
    }

    private fun now(): String {
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return sdf.format(Date())
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        //menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        //if (id == R.id.action_settings) {
        //    return true
        //}
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        socket.close()
        super.onBackPressed()
    }
}

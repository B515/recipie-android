package xin.z7workbench.recipie.ui.chat

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Base64
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toFile
import androidx.core.widget.toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import okio.BufferedSink
import okio.BufferedSource
import okio.Okio
import xin.z7workbench.recipie.R
import xin.z7workbench.recipie.entity.FileInfoMessage
import xin.z7workbench.recipie.entity.FileMessage
import xin.z7workbench.recipie.entity.LoginMessage
import xin.z7workbench.recipie.entity.ServerMessage
import xin.z7workbench.recipie.util.rand
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
        gson = GsonBuilder().disableHtmlEscaping().create()

        button_submit.setOnClickListener {
            val text = edit_question.text.toString()
            if (TextUtils.isEmpty(text)) {
                return@setOnClickListener
            }

            sendTextMessage(text)
            edit_question.setText("")
        }
        button_send_image.setOnClickListener {
            selectFile(imageOnly = true)
        }
        button_send_file.setOnClickListener {
            selectFile()
        }

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

        sendMessage(gson.toJson(LoginMessage(username)))
    }

    private val receiver = Runnable {
        try {
            while (true) {
                if (!socket.isConnected) continue
                if (socket.isInputShutdown) continue
                val content = source.readUtf8Line() ?: continue
                runOnUiThread { showMessage(content) }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun sendTextMessage(text: String) {
        val message = ServerMessage("all", "", username, now(), "text", text, null, true)
        sendMessage(gson.toJson(message))

        adapter.add(message)
        recycler.scrollToPosition(adapter.itemCount - 1)
    }


    private fun sendFileMessage(uri: Uri, image: Boolean = false) = async(UI) {
        val file = uri.toFile()
        val type = if (image) "image" else "file"
        val id = rand(10000000..99999999)
        val message = FileInfoMessage("all", "", username, now(), type,
                file.name, file.length(), id)
        sendMessage(gson.toJson(message))

        adapter.add(ServerMessage(message.Object, message.ToUser, message.FromUser, message.CreateTime,
                message.MsgType, message.FileName, null, true))
        recycler.scrollToPosition(adapter.itemCount - 1)

        val buffer = Okio.buffer(Okio.source(file))
        async(CommonPool) {
            while (!buffer.exhausted()) {
                delay(50L)
                val array = if (buffer.request(512)) buffer.readByteArray(512) else buffer.readByteArray()
                val part = FileMessage(type, id, Base64.encodeToString(array, Base64.NO_WRAP))
                sendMessage(gson.toJson(part))
            }
        }.await()
        toast("发送完毕")
    }

    private fun sendMessage(json: String) = async(UI) {
        async(CommonPool) {
            if (socket.isConnected) {
                if (!socket.isOutputShutdown) {
                    sink.writeUtf8("$json\n")
                    sink.flush()
                }
            }
        }.await()
    }

    private fun showMessage(json: String) {
        val obj = JsonParser().parse(json).asJsonObject
        when (obj["MsgType"].asString) {
            "text" -> {
                val message = gson.fromJson<ServerMessage>(json, ServerMessage::class.java)

                adapter.add(message)
                recycler.scrollToPosition(adapter.itemCount - 1)
                updateOnlineUsers(message.OnlineUser ?: listOf())
            }
            "image", "file" -> {
                if (obj.has("Content")) {
                    val message = gson.fromJson<FileMessage>(json, FileMessage::class.java)
                } else {
                    val message = gson.fromJson<FileInfoMessage>(json, FileInfoMessage::class.java)
                }
            }
        }
    }

    private fun updateOnlineUsers(users: List<String>) {
        online_users.removeAllViews()
        users.forEach {
            val chip = Chip(this)
            chip.text = it
            chip.setPadding(16, 10, 16, 10)
            online_users.addView(chip)
        }
    }

    private fun now(): String {
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun selectFile(imageOnly: Boolean = false) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = if (imageOnly) "image/*" else "*/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
        startActivityForResult(intent, if (imageOnly) 1 else 2)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if ((requestCode == 1 || requestCode == 2) && resultCode == Activity.RESULT_OK) {
            data?.let {
                sendFileMessage(data.data, image = requestCode == 1)
            }
        } else super.onActivityResult(requestCode, resultCode, data)
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

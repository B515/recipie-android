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
import com.google.gson.JsonObject
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
import xin.z7workbench.recipie.util.getAbsolutePath
import xin.z7workbench.recipie.util.rand
import java.io.File
import java.io.IOException
import java.net.Socket
import java.text.SimpleDateFormat
import java.util.*

class ChatActivity : AppCompatActivity() {

    private lateinit var adapter: ChatMessageAdapter

    private lateinit var socket: Socket
    private lateinit var sink: BufferedSink
    lateinit var source: BufferedSource
    private lateinit var gson: Gson
    private val receivingFilesParts: MutableMap<Int, MutableList<FileMessage>> = mutableMapOf()

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
                val host = listOf("123.206.13.211", "192.168.1.105")[0]
                socket = Socket(host, 8964)
                sink = Okio.buffer(Okio.sink(socket))
                source = Okio.buffer(Okio.source(socket))
            } catch (e: IOException) {
                e.printStackTrace()
                toast("Socket 连接失败")
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
                runOnUiThread { processMessage(content) }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            toast("Socket 关闭")
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
        if (file.length() == 0L) {
            toast("无效的文件")
            return@async
        }
        val type = if (image) "image" else "file"
        val id = rand(10000000..99999999)
        val message = FileInfoMessage("all", "", username, now(), type,
                file.name, file.length().toInt(), id)
        sendMessage(gson.toJson(message))

        adapter.add(message, true, file.absolutePath)
        recycler.scrollToPosition(adapter.itemCount - 1)

        val buffer = Okio.buffer(Okio.source(file))
        var sent = 0
        while (!buffer.exhausted()) {
            async(CommonPool) {
                val array = if (buffer.request(8192)) buffer.readByteArray(8192) else buffer.readByteArray()
                val part = FileMessage(type, id, Base64.encodeToString(array, Base64.NO_WRAP))
                sendMessage(gson.toJson(part))
                sent += array.size
            }.await()
            adapter.updateFileProgress(id, sent)
        }
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

    private fun processMessage(json: String) {
        val obj: JsonObject
        try {
            obj = JsonParser().parse(json).asJsonObject
        } catch (e: Exception) {
            e.printStackTrace()
            return
        }
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
                    receivingFilesParts[message.MsgID]!!.add(message)
                } else {
                    val message = gson.fromJson<FileInfoMessage>(json, FileInfoMessage::class.java)
                    receivingFilesParts[message.MsgID] = mutableListOf()
                    processFile(message)

                    adapter.add(message, false, null)
                    recycler.scrollToPosition(adapter.itemCount - 1)
                }
            }
        }
    }

    private fun processFile(info: FileInfoMessage) = async(UI) {
        try {
            var received = 0
            val file = File(getAbsolutePath(info.MsgID, info.FileName))
            val fileSink = Okio.buffer(Okio.sink(file))
            val parts = receivingFilesParts[info.MsgID]!!

            while (received < info.FileSize) {
                async(CommonPool) {
                    while (parts.isEmpty()) {
                        delay(50L)
                    }
                    val decoded = Base64.decode(parts[0].Content, Base64.NO_WRAP)
                    fileSink.write(decoded)
                    fileSink.flush()
                    received += decoded.size
                }.await()
                adapter.updateFileProgress(info.MsgID, received)
                parts.removeAt(0)
            }
            fileSink.close()
            receivingFilesParts.remove(info.MsgID)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        toast("接收完毕")
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

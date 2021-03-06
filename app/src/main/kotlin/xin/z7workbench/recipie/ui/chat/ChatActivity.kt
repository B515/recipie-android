package xin.z7workbench.recipie.ui.chat

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import okio.Okio
import org.jetbrains.anko.alert
import org.jetbrains.anko.defaultSharedPreferences
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import xin.z7workbench.recipie.R
import xin.z7workbench.recipie.entity.*
import xin.z7workbench.recipie.ui.SocketActivity
import xin.z7workbench.recipie.util.getAbsolutePath
import xin.z7workbench.recipie.util.getPath
import xin.z7workbench.recipie.util.rand
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ChatActivity : SocketActivity() {

    private lateinit var adapter: ChatMessageAdapter

    private var isPrivateConversation = false
    private var toUser = ""
    private val receivingFilesParts: MutableMap<Int, MutableList<FileMessage>> = mutableMapOf()
    private val user: String by lazy { defaultSharedPreferences.getString("username", "") }
    private val pwd: String by lazy { defaultSharedPreferences.getString("password", "") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        toolbar.title = "聊天室"
        toUser = intent.getStringExtra("to_user") ?: ""
        isPrivateConversation = intent.getBooleanExtra("private", false)
        if (isPrivateConversation) {
            following.visibility = View.GONE
            follower.visibility = View.GONE
            profile.visibility = View.GONE
            online_users.visibility = View.INVISIBLE
            online_users_text.visibility = View.INVISIBLE
            toolbar.title = toUser
            follow.setOnClickListener { followUser(toUser) }
            unfollow.setOnClickListener { unfollowUser(toUser) }
        } else {
            follow.visibility = View.GONE
            unfollow.visibility = View.GONE
            following.setOnClickListener { following() }
            follower.setOnClickListener { follower() }
            profile.setOnClickListener { viewInfo() }
        }
        setSupportActionBar(toolbar)

        recycler.layoutManager = LinearLayoutManager(this)
        adapter = ChatMessageAdapter(this, ArrayList())
        recycler.adapter = adapter

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
        login()
    }

    private fun login() = async(UI) {
        delay(10)
        sendMessage(AuthRequestMessage("login", user, pwd, ""))
    }

    private fun sendTextMessage(text: String) {
        val message = ServerMessage(if (isPrivateConversation) "personal" else "all", toUser, user, now(), "text", text, null)
        sendMessage(message)

        adapter.add(message, true)
        recycler.scrollToPosition(adapter.itemCount - 1)
    }


    private fun sendFileMessage(uri: Uri, image: Boolean = false) = async(UI) {
        val path = getPath(this@ChatActivity, uri)
        if (path == null) {
            toast("无效的文件")
            return@async
        }
        val file = File(path)
        val type = if (image) "image" else "file"
        val id = rand(10000000..99999999)
        val m = FileInfoMessage("all", "", user, now(), type,
                file.name, file.length().toInt(), id)
        sendMessage(m)
        delay(10)

        adapter.add(m, true, file.absolutePath)
        recycler.scrollToPosition(adapter.itemCount - 1)

        try {
            val buffer = Okio.buffer(Okio.source(file))
            var sent = 0
            while (!buffer.exhausted()) {
                async {
                    val array = if (buffer.request(8192)) buffer.readByteArray(8192) else buffer.readByteArray()
                    val part = FileMessage(type, id, Base64.encodeToString(array, Base64.NO_WRAP))
                    sendMessage(part)
                    sent += array.size
                }.await()
                adapter.updateFileProgress(id, sent)
            }
            toast("发送完毕")
        } catch (e: Exception) {
            e.printStackTrace()
            alert { message = Log.getStackTraceString(e) }.show()
        }
    }

    private fun viewInfo() = sendMessage(SystemMessage("view_inf"))
    private fun updateInfo(nickname: String, sex: Int) = sendMessage(SystemProfileMessage("update_inf", nickname, sex))
    private fun followUser(username: String) = sendMessage(SystemFollowMessage("follow", username))
    private fun unfollowUser(username: String) = sendMessage(SystemFollowMessage("unfollow", username))
    private fun following() = sendMessage(SystemMessage("following"))
    private fun follower() = sendMessage(SystemMessage("follower"))

    override fun processMessage(json: String) {
        val obj: JsonObject
        try {
            obj = JsonParser().parse(json).asJsonObject
        } catch (e: Exception) {
            e.printStackTrace()
            return
        }
        if (!obj.has("MsgType")) {
            val message = gson.fromJson<AuthResultMessage>(json)
            return
        }
        when (obj["MsgType"].asString) {
            "text" -> {
                val message = gson.fromJson<ServerMessage>(json)

                adapter.add(message, false)
                recycler.scrollToPosition(adapter.itemCount - 1)
                if (!isPrivateConversation) updateOnlineUsers(message.OnlineUser ?: listOf())
            }
            "image", "file" -> {
                if (obj.has("Content")) {
                    val message = gson.fromJson<FileMessage>(json)
                    receivingFilesParts[message.MsgID]!!.add(message)
                } else {
                    val message = gson.fromJson<FileInfoMessage>(json)
                    receivingFilesParts[message.MsgID] = mutableListOf()
                    processFile(message)

                    adapter.add(message, false, null)
                    recycler.scrollToPosition(adapter.itemCount - 1)
                }
            }
            "system" -> {
                when (obj["Op"].asString) {
                    "view_inf" -> {
                        val m = gson.fromJson<SystemProfileMessage>(json)
                        alert {
                            title = "个人资料"
                            message = "性别： ${if (m.Sex == 0) "女" else "男"}\n昵称： ${m.Nickname}"
                            positiveButton("确定") {}
                            negativeButton("编辑") {
                                var sex = 0
                                var nick = ""
                                val b1 = AlertDialog.Builder(this@ChatActivity)
                                b1.setTitle("性别：")
                                b1.setSingleChoiceItems(arrayOf("女", "男"), sex) { _, which -> sex = which }
                                b1.setPositiveButton("确定") { dialog, _ ->
                                    dialog.dismiss()
                                    val b2 = AlertDialog.Builder(this@ChatActivity)
                                    val edittext = EditText(this@ChatActivity)
                                    b2.setMessage("昵称：")
                                    b2.setView(edittext)
                                    b2.setPositiveButton("确定") { _, _ ->
                                        nick = edittext.text.toString()
                                        updateInfo(nick, sex)
                                    }
                                    b2.create().show()
                                }
                                b1.create().show()
                            }
                        }.show()
                    }
                    "update_inf" -> {
                        val m = gson.fromJson<SystemMessage>(json)
                        alert {
                            title = "个人资料修改" + if (m.Result) "成功" else "失败"
                            message = ""
                        }.show()
                    }
                    "follow", "unfollow" -> {
                        val message = gson.fromJson<SystemFollowMessage>(json)
                        toast("已" + (if (message.Op == "follow") "关注" else "取关") + message.User)
                    }
                    "following", "follower" -> {
                        val m = gson.fromJson<SystemFollowingMessage>(json)
                        alert {
                            title = if (m.Op == "following") "关注中" else "关注者"
                            message = m.UserList.joinToString(", ")
                        }.show()
                    }
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
                async {
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
            toast("接收完毕")
        } catch (e: Exception) {
            e.printStackTrace()
            alert { message = Log.getStackTraceString(e) }.show()
        }
        return@async
    }

    private fun updateOnlineUsers(users: List<String>) {
        online_users.removeAllViews()
        users.forEach {
            val chip = Chip(this)
            chip.text = it
            chip.setPadding(16, 10, 16, 10)
            chip.setOnClickListener {
                startActivity<ChatActivity>("private" to true, "to_user" to chip.text.toString())
            }
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


}

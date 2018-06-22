package xin.z7workbench.recipie.ui.chat

import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import xin.z7workbench.recipie.R
import xin.z7workbench.recipie.entity.ChatMessage
import java.util.*

class ChatActivity : AppCompatActivity() {

    lateinit var adapter: ChatMessageAdapter
    lateinit var chatClient: ChatClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        setSupportActionBar(toolbar)

        recycler.layoutManager = LinearLayoutManager(this)
        adapter = ChatMessageAdapter(this, ArrayList<ChatMessage>())
        adapter.add(ChatMessage("welcome"))
        recycler.adapter = adapter

        button_submit.setOnClickListener(View.OnClickListener {
            val message = edit_question.text.toString()
            if (TextUtils.isEmpty(message)) {
                return@OnClickListener
            }
            sendMessage(message)
            edit_question.setText("")
        })

    }

    private fun connect() = async(UI) {
        async(CommonPool) {
            chatClient = ChatClient()
        }.await()
    }

    private fun sendMessage(message: String) = async(UI) {
        val chatMessage = ChatMessage(message, true, false)
        adapter.add(chatMessage)
        async(CommonPool) {
            chatClient.send(message)
        }.await()

        recycler.scrollToPosition(adapter.itemCount - 1)
        getResult(message)
    }

    private fun getResult(message: String) = async(UI) {
        var msg = ""
        async(CommonPool) { msg = chatClient.receive() }.await()
        val chatMessage = ChatMessage(msg, false, false)
        adapter.add(chatMessage)

        recycler.scrollToPosition(adapter.itemCount - 1)
    }

    private fun disconnect()= async(UI) {
        chatClient.disconnect()
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
        disconnect()
        super.onBackPressed()
    }
}

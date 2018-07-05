package xin.z7workbench.recipie.ui

import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import okio.BufferedSink
import okio.BufferedSource
import okio.Okio
import org.jetbrains.anko.defaultSharedPreferences
import org.jetbrains.anko.toast
import xin.z7workbench.recipie.entity.SystemMessage
import java.io.IOException
import java.net.Socket

abstract class SocketActivity : AppCompatActivity() {

    lateinit var socket: Socket
    lateinit var sink: BufferedSink
    lateinit var source: BufferedSource
    val gson: Gson = GsonBuilder().disableHtmlEscaping().create()
    inline fun <reified T> Gson.fromJson(json: String): T = this.fromJson<T>(json, T::class.java)

    fun connect() = async(UI) {
        async {
            try {
                val host = defaultSharedPreferences.getString("server", "123.206.13.211")
                socket = Socket(host, 8964)
                sink = Okio.buffer(Okio.sink(socket))
                source = Okio.buffer(Okio.source(socket))
                runOnUiThread { toast("Socket 已连接，IP: $host") }
            } catch (e: IOException) {
                e.printStackTrace()
                runOnUiThread { toast("Socket 连接失败") }
            }
        }.await()
        Thread(receiver).start()
    }

    val receiver = Runnable {
        try {
            while (true) {
                if (!socket.isConnected) continue
                if (socket.isInputShutdown) continue
                val content = source.readUtf8Line() ?: continue
                runOnUiThread { processMessage(content) }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            runOnUiThread { toast("Socket 关闭") }
        }
    }

    fun sendMessage(obj: Any) = async(UI) {
        async {
            if (socket.isConnected) {
                if (!socket.isOutputShutdown) {
                    sink.writeUtf8("${gson.toJson(obj)}\n")
                    sink.flush()
                }
            }
        }.await()
    }

    abstract fun processMessage(json: String)

    override fun onDestroy() {
        try {
            sendMessage(SystemMessage("close"))
            source.close()
            sink.close()
            socket.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onDestroy()
    }
}
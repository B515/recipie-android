package xin.z7workbench.recipie.ui.chat

import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.net.Socket

class ChatClient {
    lateinit var dos: DataOutputStream
    lateinit var dis: DataInputStream
    lateinit var socket: Socket
    private var connected = false

    init {
        try {
            socket = Socket("123.207.164.148", 8964)
            dos = DataOutputStream(socket.getOutputStream())
            dis = DataInputStream(socket.getInputStream())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun disconnect() {
        connected = false
        try {
            dos.close()
            dis.close()
            socket.close()
        } catch (e1: IOException) {
            e1.printStackTrace()
        }

    }

    fun send(msg: String) {
        try {
            dos.writeUTF(msg)
            dos.flush()
        } catch (e1: IOException) {
            e1.printStackTrace()
        }
    }

    fun receive(): String {
        val msg = dis.readUTF()
        return msg ?: ""
    }

}
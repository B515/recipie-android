package xin.z7workbench.recipie.ui

import android.os.Bundle
import android.view.View
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.layout_login.*
import kotlinx.android.synthetic.main.layout_register.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import xin.z7workbench.recipie.R
import xin.z7workbench.recipie.entity.AuthRequestMessage
import xin.z7workbench.recipie.entity.AuthResultMessage

class LoginActivity : SocketActivity() {

    var registering = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        login.setOnClickListener {
            login(username.text.toString(), password.text.toString())
        }
        register.setOnClickListener {
            register_layout.visibility = View.VISIBLE
            login_layout.visibility = View.GONE
            registering = true
        }
        back.setOnClickListener {
            back()
        }
        do_register.setOnClickListener {
            register(username1.text.toString(), password1.text.toString(), "nickname")
        }
        connect()
    }

    private fun back() {
        registering = false
        register_layout.visibility = View.GONE
        login_layout.visibility = View.VISIBLE
    }

    override fun onBackPressed() {
        if (registering) {
            back()
        } else
            super.onBackPressed()
    }

    private fun login(username: String, password: String) = sendMessage(AuthRequestMessage("login", username, password, ""))

    private fun register(username: String, password: String, nickname: String) =
            sendMessage(AuthRequestMessage("register", username, password, nickname))

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
            when (message.Result) {
                0 -> {
                    startActivity<MainActivity>()
                    finish()
                }
                1 -> {
                    toast("用户名或密码错误")
                }
                2 -> {
                    startActivity<MainActivity>()
                    finish()
                }
                3 -> {
                    toast("用户名重复")
                }
            }
        }
    }
}
package xin.z7workbench.recipie.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.layout_login.*
import kotlinx.android.synthetic.main.layout_register.*
import org.jetbrains.anko.defaultSharedPreferences
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import xin.z7workbench.recipie.R
import xin.z7workbench.recipie.api.RecipieRetrofit
import xin.z7workbench.recipie.api.prepare
import xin.z7workbench.recipie.entity.Token

class LoginActivity : AppCompatActivity() {

    private var registering = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Glide.with(this).load(R.drawable.login_bg).into(bg)

        if (defaultSharedPreferences.getString("token", "") != "") go()
        username.setText(defaultSharedPreferences.getString("username", ""))
        password.setText(defaultSharedPreferences.getString("password", ""))
        login.setOnClickListener {
            defaultSharedPreferences.edit()
                    .putString("username", username.text.toString())
                    .putString("password", password.text.toString())
                    .apply()
            RecipieRetrofit.auth.login(username.text.toString(), password.text.toString()).prepare(this).subscribe {
                toast("登录成功")
                save(it)
                go()
            }
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
            defaultSharedPreferences.edit {
                putString("username", username1.text.toString())
                putString("password", password1.text.toString())
            }
            RecipieRetrofit.auth.register(username1.text.toString(), password1.text.toString(), password1.text.toString()).prepare(this).subscribe {
                save(it)
                RecipieRetrofit.auth.createUserInfo(username1.text.toString(), 0, "nothing").prepare(this).subscribe {
                    toast("注册成功")
                    go()
                }
            }
        }
        changeip.setOnClickListener {
            val b = AlertDialog.Builder(this)
            val edittext = EditText(this)
            b.setMessage("Server IP：")
            b.setView(edittext)
            b.setPositiveButton("确定") { _, _ ->
                val server = edittext.text.toString()
                if (!server.isBlank())
                    defaultSharedPreferences.edit().putString("server", server).apply()
                else defaultSharedPreferences.edit().remove("server").apply()
            }
            b.create().show()
        }
    }

    private fun save(token: Token) {
        defaultSharedPreferences.edit { putString("token", token.key) }
        RecipieRetrofit.loadToken(token.key)
    }

    private fun go() {
        startActivity<MainActivity>()
        finish()
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
}
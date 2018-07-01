package xin.z7workbench.recipie.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.layout_login.*
import kotlinx.android.synthetic.main.layout_register.*
import org.jetbrains.anko.startActivity
import xin.z7workbench.recipie.R

class LoginActivity : AppCompatActivity() {

    var registering = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        login.setOnClickListener {
            startActivity<MainActivity>()
            finish()
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
            startActivity<MainActivity>()
            finish()
        }
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
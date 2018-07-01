package xin.z7workbench.recipie.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_register.*
import org.jetbrains.anko.startActivity
import xin.z7workbench.recipie.R

class RegisterActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        register.setOnClickListener {
            startActivity<MainActivity>()
        }

        back.setOnClickListener{
            onBackPressed()
        }
    }

}
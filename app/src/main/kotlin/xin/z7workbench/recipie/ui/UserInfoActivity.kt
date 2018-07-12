package xin.z7workbench.recipie.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.app_bar.*
import xin.z7workbench.recipie.R
import xin.z7workbench.recipie.api.RecipieRetrofit
import xin.z7workbench.recipie.api.prepare

class UserInfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_userinfo)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        val id = intent.extras.getInt("id")
        updateUserInfo(id)
    }

    fun updateUserInfo(id: Int) {
        val viewModel = ViewModelProviders.of(this)[UserInfoViewModel::class.java]
        RecipieRetrofit.auth.getUserInfo(id).prepare(this).subscribe {
            viewModel.userInfo.value = it
            toolbar.title = it.nickname
            setSupportActionBar(toolbar)
        }
        RecipieRetrofit.auth.getFollowers(id).prepare(this).subscribe {
            viewModel.followers.value = it
        }
    }

}
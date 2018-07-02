package xin.z7workbench.recipie.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.drawer_header.view.*
import org.jetbrains.anko.startActivity
import xin.z7workbench.recipie.R
import xin.z7workbench.recipie.ui.chat.ChatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(bar)

        Glide.with(this).load(R.drawable.login_bg).into(navigation.getHeaderView(0).bg)
        navigation.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.action_home -> {
                    supportFragmentManager.beginTransaction()
                            .replace(R.id.container, HomeFragment())
                            .commit()
                }
                R.id.action_info -> {
                    // start activity of user info
                }
                R.id.action_follow -> {
                    supportFragmentManager.beginTransaction()
                            .replace(R.id.container, FollowFragment())
                            .commit()

                }
                R.id.action_favorite -> {
                    supportFragmentManager.beginTransaction()
                            .replace(R.id.container, FavoriteFragment())
                            .commit()
                }
            }
            true
        }

        supportFragmentManager.beginTransaction()
                .replace(R.id.container, HomeFragment())
                .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_chat -> {
            startActivity<ChatActivity>()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}

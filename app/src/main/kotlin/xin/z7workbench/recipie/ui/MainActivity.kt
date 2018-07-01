package xin.z7workbench.recipie.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity
import xin.z7workbench.recipie.R
import xin.z7workbench.recipie.ui.chat.ChatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        startActivity<ChatActivity>()
        setSupportActionBar(bar)

        nav_view.setNavigationItemSelectedListener{

            when(it.itemId) {
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

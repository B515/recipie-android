package xin.z7workbench.recipie.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
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

        left_drawer.adapter = ArrayAdapter<String>(this,
                R.layout.drawer_item, R.id.tv, arrayOf("用户信息", "收藏", "关注"))
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

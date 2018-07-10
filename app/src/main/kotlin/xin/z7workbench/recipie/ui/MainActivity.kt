package xin.z7workbench.recipie.ui

import android.Manifest
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.drawer_header.view.*
import org.jetbrains.anko.startActivity
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import xin.z7workbench.recipie.R
import xin.z7workbench.recipie.api.RecipieRetrofit
import xin.z7workbench.recipie.api.prepare
import xin.z7workbench.recipie.entity.UserInfo
import xin.z7workbench.recipie.ui.chat.ChatActivity

@RuntimePermissions
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(bar)

        bar.setNavigationOnClickListener { drawer.openDrawer(GravityCompat.START) }
        Glide.with(this).load(R.drawable.login_bg).into(navigation.getHeaderView(0).bg)
        NavigationUI.setupWithNavController(navigation, findNavController(R.id.nav_host_fragment))

        fab.setOnClickListener {
            editRecipeWithPermissionCheck()
        }
        updateUserInfo()
    }

    fun updateUserInfo() {
        val viewModel = ViewModelProviders.of(this)[UserInfoViewModel::class.java]
        RecipieRetrofit.auth.getMyUserInfo().prepare(this).subscribe { viewModel.userInfo.value = it }
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun editRecipe() = startActivity<EditRecipeActivity>()

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun launchChat() = startActivity<ChatActivity>()

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_chat -> {
            launchChatWithPermissionCheck()
            true
        }
        R.id.action_recipe -> {
            startActivity<RecipeActivity>("recipe_id" to 1)
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }
}

class UserInfoViewModel : ViewModel() {
    val userInfo: MutableLiveData<UserInfo?> = MutableLiveData()
}
package xin.z7workbench.recipie.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.zhihu.matisse.Matisse
import kotlinx.android.synthetic.main.activity_edit_recipe.*
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.item_edit_recipe.view.*
import xin.z7workbench.recipie.R
import xin.z7workbench.recipie.entity.RecipeStep
import xin.z7workbench.recipie.util.MatisseUtil

class EditRecipeActivity : AppCompatActivity() {

    lateinit var recipesAdapter: RecipesAdapter
    val list: MutableList<RecipeStep> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_recipe)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { onBackPressed() }
        recipesAdapter = RecipesAdapter()
        recycler.adapter = recipesAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_edit_recipe, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_add -> {
            list += RecipeStep("", "")
            recipesAdapter.submitList(list)
            true
        }
        R.id.action_ok -> {
            // TODO do something
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    inner class RecipesAdapter : ListAdapter<RecipeStep, RecipesAdapter.RecipesViewHolder>(
            object : DiffUtil.ItemCallback<RecipeStep>() {
                override fun areItemsTheSame(oldItem: RecipeStep, newItem: RecipeStep) = oldItem.image == newItem.image && oldItem.description == newItem.description //should be id but we don't have id
                override fun areContentsTheSame(oldItem: RecipeStep, newItem: RecipeStep) = oldItem == newItem
            }
    ) {
        // Recipes Types
        private val ASK_PERMISSION = 2

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipesViewHolder =
                RecipesViewHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_edit_recipe, parent, false))

        override fun onBindViewHolder(holder: RecipesViewHolder, position: Int) {
            holder.v.apply {
                Glide.with(context).load(R.drawable.add_rect).into(pic)
                delete.setOnClickListener {
                    list.removeAt(holder.adapterPosition)
                    submitList(list)
                }
                pic.setOnClickListener {
                    val hasPermission = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), ASK_PERMISSION)
                        return@setOnClickListener
                    }
                    MatisseUtil.selectFromActivity(this@EditRecipeActivity, 1, holder.adapterPosition)
                }
            }
        }

        override fun submitList(list: List<RecipeStep>?) = super.submitList(list?.toList())

        inner class RecipesViewHolder(val v: View) : RecyclerView.ViewHolder(v)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            Glide.with(this)
                    .load(Matisse.obtainResult(data)[0].toString())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into((recycler.findViewHolderForAdapterPosition(requestCode) as RecipesAdapter.RecipesViewHolder).v.pic)
        }
    }
}
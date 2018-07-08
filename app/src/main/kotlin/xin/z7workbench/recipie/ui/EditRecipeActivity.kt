package xin.z7workbench.recipie.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_recipe)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { onBackPressed() }
        val recipesAdapter = RecipesAdapter(this)

        val llm = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recycler.apply {
            layoutManager = llm
            adapter = recipesAdapter
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_edit_recipe, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_add -> {
            (recycler.adapter as RecipesAdapter).doSomething {
                list.add(RecipeStep("", ""))
            }

            true
        }
        R.id.action_ok -> {
            // TODO do something
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    class RecipesAdapter(val activity: Activity, var list: MutableList<RecipeStep> = mutableListOf()) : RecyclerView.Adapter<RecipesAdapter.RecipesViewHolder>() {
        // Recipes Types
        private val ASK_PERMISSION = 2
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipesViewHolder =
                RecipesViewHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_edit_recipe, parent, false))

        override fun getItemCount() = list.size

        override fun onBindViewHolder(holder: RecipesViewHolder, position: Int) {
            holder.v.apply {
                Glide.with(context).load(R.drawable.add_rect).into(pic)
                delete.setOnClickListener {
                    list.removeAt(position)
                    notifyDataSetChanged()
                }
                pic.setOnClickListener {
                    val hasPermission = activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                        activity.requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), ASK_PERMISSION)
                        return@setOnClickListener
                    }
                    MatisseUtil.selectFromActivity(activity, 1, position)
                }
            }
        }

        fun doSomething(something: RecipesAdapter.() -> Unit) {
            something()
            notifyDataSetChanged()
        }

        class RecipesViewHolder(val v: View) : RecyclerView.ViewHolder(v)
    }


    class RecipeStepDiffCallback(private val old: List<RecipeStep>, private val new: List<RecipeStep>) : DiffUtil.Callback() {
        override fun getOldListSize() = old.size
        override fun getNewListSize() = new.size
        override fun areItemsTheSame(p0: Int, p1: Int) = old[p0] == new[p1]
        override fun areContentsTheSame(p0: Int, p1: Int) = (old[p0].description == new[p1].description) && (old[p0].image == new[p1].image)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            Glide.with(this)
                    .load(Matisse.obtainResult(data)[0].toString())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(recycler[requestCode].pic)
        }
    }
}
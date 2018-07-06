package xin.z7workbench.recipie.ui

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_edit_recipe.*
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.item_edit_recipe.view.*
import xin.z7workbench.recipie.R

class EditRecipeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_recipe)
        setSupportActionBar(toolbar)

        val llm = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recycler.apply {
            layoutManager = llm
            adapter = RecipesAdapter()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_edit_recipe, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_add -> {
            true
        }
        R.id.action_ok -> {
            // TODO do something
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    class RecipesAdapter : RecyclerView.Adapter<RecipesAdapter.RecipesViewHolder>() {
        // Recipes Types
        val list: List<Any> = arrayListOf()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipesViewHolder =
                RecipesViewHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_edit_recipe, parent, false))

        override fun getItemCount() = list.size

        override fun onBindViewHolder(holder: RecipesViewHolder, position: Int) {
            holder.v.apply {
                Glide.with(context).load(R.drawable.add_rect).into(pic)
            }
        }


        class RecipesViewHolder(val v: View) : RecyclerView.ViewHolder(v)
    }
}
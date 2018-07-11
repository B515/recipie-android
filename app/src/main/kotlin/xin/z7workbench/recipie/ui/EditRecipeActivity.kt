package xin.z7workbench.recipie.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.gson.Gson
import com.zhihu.matisse.Matisse
import kotlinx.android.synthetic.main.activity_edit_recipe.*
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.item_edit_recipe.view.*
import org.jetbrains.anko.toast
import xin.z7workbench.recipie.R
import xin.z7workbench.recipie.api.RecipieRetrofit
import xin.z7workbench.recipie.api.prepare
import xin.z7workbench.recipie.entity.RecipeStep
import xin.z7workbench.recipie.util.MatisseUtil.select
import xin.z7workbench.recipie.util.uploadRequest

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


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add -> {
                list += RecipeStep()
                recipesAdapter.notifyDataSetChanged()
                true
            }
            R.id.action_ok -> {
                for (i in 0 until list.size) {
                    list[i].description = recycler.findViewHolderForAdapterPosition(i)?.itemView?.description?.text.toString()
                }
                if (name.text.toString() == "" || description.text.toString() == "" || list.any { it.description == "" }) {
                    toast("请完成填写")
                    return true
                }
                if (list.any { it.image == "" }) {
                    toast("正在上传，请稍等")
                    return true
                }
                //TODO Add tags
                val content = Gson().toJson(list)
                RecipieRetrofit.recipe.createRecipe(name.text.toString(), content, description.text.toString(), "1").prepare(this).subscribe {
                    toast("创建成功")
                    finish()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    inner class RecipesAdapter : RecyclerView.Adapter<RecipesAdapter.RecipesViewHolder>() {
        override fun getItemCount() = list.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipesViewHolder =
                RecipesViewHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_edit_recipe, parent, false))

        override fun onBindViewHolder(holder: RecipesViewHolder, position: Int) {
            holder.v.apply {
                val item = list[holder.adapterPosition]
                val image = when {
                    item.local != Uri.EMPTY -> item.local
                    else -> R.drawable.add_rect
                }
                Glide.with(context).load(image).into(pic)
                delete.setOnClickListener {
                    list.removeAt(holder.adapterPosition)
                    notifyDataSetChanged()
                }
                pic.setOnClickListener {
                    Matisse.from(this@EditRecipeActivity).select(1, holder.adapterPosition)
                }
            }
        }

        inner class RecipesViewHolder(val v: View) : RecyclerView.ViewHolder(v)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            Glide.with(this)
                    .load(Matisse.obtainResult(data)[0].toString())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into((recycler.findViewHolderForAdapterPosition(requestCode) as RecipesAdapter.RecipesViewHolder).v.pic)
            val uri = Matisse.obtainResult(data)[0]
            list[requestCode].local = uri
            uploadRequest(uri, this).prepare(this).subscribe {
                list[requestCode].image = it.file
                toast("${it.file} 已上传")
            }
        }
    }
}
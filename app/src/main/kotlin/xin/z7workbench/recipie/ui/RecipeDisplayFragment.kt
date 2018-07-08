package xin.z7workbench.recipie.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.layout_recipe_display.view.*
import kotlinx.android.synthetic.main.layout_recipe_step.view.*
import xin.z7workbench.recipie.R

class RecipeDisplayFragment : Fragment() {

    data class RecipeStep(val image: String, val description: String)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.layout_recipe_display, container, false)
        val model = ViewModelProviders.of(requireActivity())[RecipeViewModel::class.java]
        model.recipe.observe(this, Observer {
            it ?: return@Observer
            view.apply {
                val steps = listOf(RecipeStep("", "准备"), RecipeStep("", "点火"), RecipeStep("", "爆炸"), RecipeStep("", "成功"))
                view_pager.adapter = RecipeAdapter(context!!, steps)
                indicator.setViewPager(view_pager)
            }
        })
        return view
    }


    inner class RecipeAdapter(val context: Context, val steps: List<RecipeStep>) : PagerAdapter() {

        override fun getCount() = steps.size
        override fun isViewFromObject(view: View, obj: Any) = view === obj

        override fun instantiateItem(collection: ViewGroup, position: Int): Any {
            val layout = LayoutInflater.from(context).inflate(R.layout.layout_recipe_step, collection, false) as ViewGroup
            val step = steps[position]
            layout.step_count.text = "${position + 1} / ${steps.size}"
            layout.description.text = step.description
            Glide.with(layout).load(R.drawable.login_bg).into(layout.image) //TODO step.image
            collection.addView(layout)
            return layout
        }

        override fun destroyItem(collection: ViewGroup, position: Int, view: Any) {
            collection.removeView(view as View)
        }
    }

}
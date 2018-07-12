package xin.z7workbench.recipie.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.item_recipe.view.*
import org.jetbrains.anko.startActivity
import xin.z7workbench.recipie.R
import xin.z7workbench.recipie.api.RecipieRetrofit
import xin.z7workbench.recipie.api.prepare
import xin.z7workbench.recipie.entity.Recipe

class HomeFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val peopleAdapter = RecommendAdapter()
        val llm = object : LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false) {
            override fun canScrollVertically() = false
        }

        view.apply {
            search_layout.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_homeFragment_to_searchFragment))

            RecipieRetrofit.recipe.getAllRecipes().prepare(requireContext()).subscribe {
                peopleAdapter.list = it
                peopleAdapter.notifyDataSetChanged()
            }

            people.apply {
                adapter = peopleAdapter
                layoutManager = llm
            }

        }

        return view
    }

    class RecommendAdapter(var list: List<Recipe> = listOf()) : RecyclerView.Adapter<RecommendAdapter.RecommendViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecommendViewHolder =
                RecommendViewHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_recipe, parent, false))

        override fun getItemCount() = list.size

        override fun onBindViewHolder(holder: RecommendViewHolder, position: Int) {
            holder.v.apply {
                title.text = list[position].title
                like_count.text = "${list[position].like_count}${context.getString(R.string.like_tail)}"
                read_count.text = "${list[position].read_count}${context.getString(R.string.read_tail)}"
                collect_count.text = "${list[position].collect_count}人收藏"
                description.text = list[position].description
                author.text = list[position].create_by?.nickname ?: "Unknown"

                recipe_view.setOnClickListener {
                    context.startActivity<RecipeActivity>("recipe_id" to list[position].id)
                }
            }
        }

        class RecommendViewHolder(val v: View) : RecyclerView.ViewHolder(v)
    }
}
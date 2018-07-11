package xin.z7workbench.recipie.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.layout_tag.view.*
import xin.z7workbench.recipie.R
import xin.z7workbench.recipie.api.RecipieRetrofit
import xin.z7workbench.recipie.api.prepare


class TagFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.layout_tag, container, false)

        RecipieRetrofit.recipe.getTag(arguments?.getInt("id")
                ?: 0).prepare(requireContext()).subscribe {
            view.apply {
                title.text = it.title
                val adapter = SearchFragment.RecipeResultAdapter()
                recipes.adapter = adapter
                adapter.list = it.recipe_set ?: listOf()
                adapter.notifyDataSetChanged()
            }
        }
        return view
    }
}
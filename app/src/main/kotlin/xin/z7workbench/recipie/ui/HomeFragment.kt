package xin.z7workbench.recipie.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_home.view.*
import xin.z7workbench.recipie.R
import xin.z7workbench.recipie.api.RecipieRetrofit
import xin.z7workbench.recipie.api.prepare

class HomeFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val peopleAdapter = SearchFragment.RecipeResultAdapter()
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

}
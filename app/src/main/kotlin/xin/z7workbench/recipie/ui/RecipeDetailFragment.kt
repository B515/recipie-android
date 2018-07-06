package xin.z7workbench.recipie.ui

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.layout_recipe_detail.view.*
import xin.z7workbench.recipie.R

class RecipeDetailFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.layout_recipe_detail, container, false)

        view.apply {
            Glide.with(this).load(R.drawable.login_bg).into(bg)
            play.setOnClickListener {
                Navigation.findNavController(activity as Activity, R.id.nav_host_fragment).navigate(R.id.action_recipeDetailFragment_to_recipeDisplayFragment)
            }
        }
        return view
    }
}
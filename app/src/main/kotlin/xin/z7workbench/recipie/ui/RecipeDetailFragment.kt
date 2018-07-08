package xin.z7workbench.recipie.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.layout_recipe_detail.view.*
import xin.z7workbench.recipie.R
import xin.z7workbench.recipie.api.RecipieRetrofit

class RecipeDetailFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.layout_recipe_detail, container, false)

        val model = ViewModelProviders.of(requireActivity())[RecipeViewModel::class.java]
        model.recipe.observe(this, Observer {
            it ?: return@Observer
            view.apply {
                Glide.with(this).load(R.drawable.login_bg).into(bg)
                play.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_recipeDetailFragment_to_recipeDisplayFragment))
                comment.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_recipeDetailFragment_to_recipeCommentFragment))
                name.text = it.title
                description.text = it.description
                like.visibility = View.GONE
                favorite.visibility = View.GONE

                like.setOnClickListener{
                    RecipieRetrofit.recipe.likeRecipe(it.id)
                    like.visibility = View.GONE
                    not_like.visibility = View.VISIBLE
                }
                not_like.setOnClickListener{
                    RecipieRetrofit.recipe.unlikeRecipe(it.id)
                    not_like.visibility = View.GONE
                    like.visibility = View.VISIBLE
                }
                favorite.setOnClickListener{
                    RecipieRetrofit.recipe.collectRecipe(it.id)
                    favorite.visibility = View.GONE
                    not_favorite.visibility = View.VISIBLE
                }
                not_favorite.setOnClickListener{
                    RecipieRetrofit.recipe.uncollectRecipe(it.id)
                    not_favorite.visibility = View.GONE
                    favorite.visibility = View.VISIBLE
                }
            }
        })
        return view
    }
}
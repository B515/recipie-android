package xin.z7workbench.recipie.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.layout_recipe_comment.view.*
import org.jetbrains.anko.toast
import xin.z7workbench.recipie.R
import xin.z7workbench.recipie.api.RecipieRetrofit
import xin.z7workbench.recipie.api.prepare

class RecipeCommentFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.layout_recipe_comment, container, false)
        val model = ViewModelProviders.of(requireActivity())[RecipeViewModel::class.java]
        model.recipe.observe(this, Observer { recipe ->
            recipe ?: return@Observer
            view.apply {
                send.setOnClickListener {
                    RecipieRetrofit.recipe.createComment(recipe.id, comment.text.toString()).prepare(context).subscribe {
                        activity?.toast("发送成功！")
                        activity?.onBackPressed()
                    }
                }
            }
        })
        return view
    }
}
package xin.z7workbench.recipie.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.layout_recipe_comment.view.*
import org.jetbrains.anko.toast
import xin.z7workbench.recipie.R

class RecipeCommentFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.layout_recipe_comment, container, false)

        view.apply {
            send.setOnClickListener {
                activity?.toast("发送成功！")
                activity?.onBackPressed()
            }
        }
        return view
    }
}
package xin.z7workbench.recipie.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.item_comment.view.*
import kotlinx.android.synthetic.main.layout_recipe_detail.view.*
import org.jetbrains.anko.defaultSharedPreferences
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import xin.z7workbench.recipie.R
import xin.z7workbench.recipie.api.RecipieRetrofit
import xin.z7workbench.recipie.api.prepare
import xin.z7workbench.recipie.entity.Comment

class RecipeDetailFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.layout_recipe_detail, container, false)

        val model = ViewModelProviders.of(requireActivity())[RecipeViewModel::class.java]
        model.recipe.observe(this, Observer { recipe ->
            recipe ?: return@Observer
            view.apply {
                Glide.with(this).load(R.drawable.login_bg).into(bg)
                play.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_recipeDetailFragment_to_recipeDisplayFragment))
                comment.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_recipeDetailFragment_to_recipeCommentFragment))
                name.text = recipe.title
                description.text = recipe.description
                like.visibility = View.GONE
                favorite.visibility = View.GONE
                comments.adapter = CommentAdapter(recipe.comment_set ?: listOf())
                recipe.tag?.forEach {
                    val chip = Chip(this@RecipeDetailFragment.context)
                    chip.text = it.title
                    chip.setPadding(16, 10, 16, 10)
                    val bundle = Bundle()
                    bundle.putInt("id", it.id)
                    chip.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_recipeDetailFragment_to_tagFragment, bundle))
                    tags.addView(chip)
                }

                author.text = recipe.create_by?.nickname ?: "Unknown"
                author.setOnClickListener {
                    context.startActivity<UserInfoActivity>("id" to (recipe.create_by?.id ?: 0))
                }
                likes.text = "${recipe.like_count}${context.getString(R.string.like_tail)} ${recipe.read_count}${context.getString(R.string.read_tail)}"

                val userid = context.defaultSharedPreferences.getInt("userid", 0)
                if (recipe.recipe_like?.contains(userid) == true) {
                    like.visibility = View.GONE
                    not_like.visibility = View.VISIBLE
                } else {
                    not_like.visibility = View.GONE
                    like.visibility = View.VISIBLE
                }
                if (recipe.recipe_collection?.contains(userid) == true) {
                    favorite.visibility = View.GONE
                    not_favorite.visibility = View.VISIBLE
                } else {
                    not_favorite.visibility = View.GONE
                    favorite.visibility = View.VISIBLE
                }


                like.setOnClickListener {
                    RecipieRetrofit.recipe.likeRecipe(recipe.id).prepare(context).subscribe { context.toast("已点赞") }
                    like.visibility = View.GONE
                    not_like.visibility = View.VISIBLE
                }
                not_like.setOnClickListener {
                    RecipieRetrofit.recipe.unlikeRecipe(recipe.id).prepare(context).subscribe { context.toast("已取消点赞") }
                    not_like.visibility = View.GONE
                    like.visibility = View.VISIBLE
                }
                favorite.setOnClickListener {
                    RecipieRetrofit.recipe.collectRecipe(recipe.id).prepare(context).subscribe { context.toast("已收藏") }
                    favorite.visibility = View.GONE
                    not_favorite.visibility = View.VISIBLE
                }
                not_favorite.setOnClickListener {
                    RecipieRetrofit.recipe.uncollectRecipe(recipe.id).prepare(context).subscribe { context.toast("已取消收藏") }
                    not_favorite.visibility = View.GONE
                    favorite.visibility = View.VISIBLE
                }
            }
        })
        return view
    }

    inner class CommentAdapter(var list: List<Comment> = listOf()) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                CommentViewHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_comment, parent, false))

        override fun getItemCount() = list.size

        override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
            holder.v.apply {
                comment_author.text = list[position].userinfo?.nickname ?: "Unknown"
                Glide.with(this).load(list[position].userinfo?.avatar).apply(RequestOptions.circleCropTransform()).into(author_avatar)
                comment_author.setOnClickListener {
                    context.startActivity<UserInfoActivity>("id" to (list[position].userinfo?.id
                            ?: 0))
                }
                author_avatar.setOnClickListener {
                    context.startActivity<UserInfoActivity>("id" to (list[position].userinfo?.id
                            ?: 0))
                }
                comment_content.text = list[position].content
                comment_like_count.text = list[position].like_count.toString()

                val userid = context.defaultSharedPreferences.getInt("userid", 0)
                if (list[position].comment_like?.contains(userid) == true) {
                    comment_like.visibility = View.GONE
                    comment_not_like.visibility = View.VISIBLE
                } else {
                    comment_not_like.visibility = View.GONE
                    comment_like.visibility = View.VISIBLE
                }
                comment_like.setOnClickListener {
                    RecipieRetrofit.recipe.likeComment(list[position].id).prepare(this@RecipeDetailFragment.requireContext()).subscribe {
                        comment_like.visibility = View.GONE
                        comment_not_like.visibility = View.VISIBLE
                    }
                }
                comment_not_like.setOnClickListener {
                    RecipieRetrofit.recipe.unlikeComment(list[position].id).prepare(this@RecipeDetailFragment.requireContext()).subscribe {
                        comment_not_like.visibility = View.GONE
                        comment_like.visibility = View.VISIBLE
                    }
                }
            }
        }

        inner class CommentViewHolder(val v: View) : RecyclerView.ViewHolder(v)
    }
}
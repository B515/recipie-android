package xin.z7workbench.recipie.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_tag.view.*
import kotlinx.android.synthetic.main.layout_tags.view.*
import org.jetbrains.anko.toast
import xin.z7workbench.recipie.R
import xin.z7workbench.recipie.api.RecipieRetrofit
import xin.z7workbench.recipie.api.prepare
import xin.z7workbench.recipie.entity.Tag


class TagsFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.layout_tags, container, false)
        update(view)
        return view
    }

    private fun update(view: View) {
        RecipieRetrofit.recipe.getAllTags().prepare(requireContext()).subscribe {
            view.apply {
                val adapter = TagAdapter()
                tags.adapter = adapter
                adapter.list = it
                adapter.notifyDataSetChanged()
                create_tag.setOnClickListener { createTag() }
            }
        }
    }

    private fun createTag() {
        val b1 = AlertDialog.Builder(context)
        b1.setMessage("标签名称：")
        val e1 = EditText(context)
        b1.setView(e1)
        b1.setPositiveButton("确定") { dialog, _ ->
            val t = e1.text.toString()
            dialog.dismiss()
            val b2 = AlertDialog.Builder(context)
            val e2 = EditText(context)
            b2.setMessage("标签详情：")
            b2.setView(e2)
            b2.setPositiveButton("确定") { _, _ ->
                val d = e2.text.toString()
                RecipieRetrofit.recipe.createTag(t, d).prepare(requireContext()).subscribe {
                    requireContext().toast("创建成功")
                    update(view!!)
                }
            }
            b2.create().show()
        }
        b1.create().show()
    }

    class TagAdapter(var list: List<Tag> = listOf()) : RecyclerView.Adapter<TagAdapter.TagViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                TagViewHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_tag, parent, false))

        override fun getItemCount() = list.size

        override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
            holder.v.apply {
                title.text = list[position].title
                recipes_count.text = "${list[position].recipe_set?.size ?: 0}个菜谱"
                description.text = list[position].description
                val bundle = Bundle()
                bundle.putInt("id", list[position].id)
                tag_view.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_tagsFragment_to_tagFragment2, bundle))
            }
        }

        class TagViewHolder(val v: View) : RecyclerView.ViewHolder(v)
    }
}
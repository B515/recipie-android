package xin.z7workbench.recipie.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.fragment_recycler.view.*
import kotlinx.android.synthetic.main.item_userinfo.view.*
import org.jetbrains.anko.startActivity
import xin.z7workbench.recipie.R
import xin.z7workbench.recipie.entity.UserInfo

class FollowingFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_recycler, container, false)
        val model = ViewModelProviders.of(requireActivity())[UserInfoViewModel::class.java]
        model.userInfo.observe(this, Observer {
            it ?: return@Observer
            view.apply {
                recycler_title.text = "关注列表"
                val adapter = UserInfoAdapter()
                recycler.adapter = adapter
                adapter.list = it.friends ?: listOf()
                adapter.notifyDataSetChanged()
            }
        })
        return view
    }

    class UserInfoAdapter(var list: List<UserInfo> = mutableListOf()) : RecyclerView.Adapter<UserInfoAdapter.UserInfoViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserInfoViewHolder =
                UserInfoViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_userinfo, parent, false))

        override fun getItemCount() = list.size

        override fun onBindViewHolder(holder: UserInfoViewHolder, position: Int) {
            val u = list[position]
            holder.v.apply {
                username.text = u.nickname
                Glide.with(this).load(u.avatar).apply(RequestOptions.circleCropTransform()).into(avatar)
                recipes_count.text = "${u.recipe_created?.size ?: 0}个菜谱"
                following_count.text = " ${u.friends?.size ?: 0}人关注"
            }
            holder.v.setOnClickListener {
                it.context.startActivity<UserInfoActivity>("id" to u.id)
            }
        }

        class UserInfoViewHolder(val v: View) : RecyclerView.ViewHolder(v)
    }
}
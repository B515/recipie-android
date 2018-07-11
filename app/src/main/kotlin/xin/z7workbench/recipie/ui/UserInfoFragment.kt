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
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.fragment_user_info.view.*
import xin.z7workbench.recipie.R

class UserInfoFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_user_info, container, false)
        val model = ViewModelProviders.of(requireActivity())[UserInfoViewModel::class.java]
        model.userInfo.observe(this, Observer {
            it ?: return@Observer
            view.username.text = it.nickname
            Glide.with(this).load(it.avatar).apply(RequestOptions.circleCropTransform()).into(view.avatar)

            view.followers.text = "${it.recipe_created?.size?:0}个菜谱 ${it.friends?.size ?: 0}人关注"
        })
        view.apply {
            Glide.with(this).load(R.drawable.login_bg).into(bg)
            back.visibility = View.GONE
            unfollow.visibility = View.GONE
            follow.visibility = View.GONE
            chat.visibility = View.GONE

            editor.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_userInfoFragment_to_editInfoFragment))
        }
        return view
    }
}
package xin.z7workbench.recipie.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_recycler.view.*
import xin.z7workbench.recipie.R

class FollowerFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_recycler, container, false)
        val model = ViewModelProviders.of(requireActivity())[UserInfoViewModel::class.java]
        model.followers.observe(this, Observer {
            it ?: return@Observer
            view.apply {
                recycler_title.text = "粉丝列表"
                val adapter = FollowingFragment.UserInfoAdapter()
                recycler.adapter = adapter
                adapter.list = it ?: listOf()
                adapter.notifyDataSetChanged()
            }
        })
        return view
    }
}
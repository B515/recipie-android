package xin.z7workbench.recipie.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.layout_edit_info.view.*
import org.jetbrains.anko.selector
import org.jetbrains.anko.toast
import xin.z7workbench.recipie.R
import xin.z7workbench.recipie.api.RecipieRetrofit
import xin.z7workbench.recipie.api.prepare

class EditInfoFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.layout_edit_info, container, false)
        val model = ViewModelProviders.of(requireActivity())[UserInfoViewModel::class.java]
        model.userInfo.observe(this, Observer { user ->
            user ?: return@Observer
            view.apply {
                name.setText(user.nickname)
                val genders = listOf("男", "女")
                gender.text = genders[user.gender]
                gender.setOnClickListener {
                    context.selector(getString(R.string.choose), genders) { _, i ->
                        user.gender = i
                        gender.text = genders[i]
                    }
                }
                confirm.setOnClickListener {
                    RecipieRetrofit.auth.updateMyUserInfo(name.text.toString(), user.gender, "http://www.wildhunter.me/").prepare(context).subscribe {
                        (requireActivity() as MainActivity).updateUserInfo()
                        context.toast("修改完成！")
                        requireActivity().onBackPressed()
                    }
                }
                back.setOnClickListener { requireActivity().onBackPressed() }
            }
        })
        return view
    }
}
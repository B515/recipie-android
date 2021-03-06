package xin.z7workbench.recipie.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_password.view.*
import org.jetbrains.anko.defaultSharedPreferences
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import xin.z7workbench.recipie.R
import xin.z7workbench.recipie.api.RecipieRetrofit
import xin.z7workbench.recipie.api.prepare

class PasswordFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_password, container, false)

        view.apply {
            confirm.setOnClickListener {
                RecipieRetrofit.auth.changePassword(old_password.text.toString(), new_password.text.toString(), password_again.text.toString()).prepare(context).subscribe {
                    context.toast("修改完成！")
                    requireActivity().onBackPressed()
                }
            }
            logout.setOnClickListener {
                context.defaultSharedPreferences.edit {
                    putString("token", "")
                    putString("userid", "")
                }
                RecipieRetrofit.loadToken("")
                context.startActivity<LoginActivity>()
                requireActivity().finish()
            }
        }

        return view

    }
}
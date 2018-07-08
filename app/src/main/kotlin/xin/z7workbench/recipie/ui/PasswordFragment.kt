package xin.z7workbench.recipie.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_password.view.*
import org.jetbrains.anko.toast
import xin.z7workbench.recipie.R

class PasswordFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_password, container, false)

        view.apply {
            confirm.setOnClickListener{
                // TODO
                activity?.toast("修改完成！")
                activity?.onBackPressed()
            }
        }

        return view

    }
}
package xin.z7workbench.recipie.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.layout_edit_info.view.*
import org.jetbrains.anko.selector
import org.jetbrains.anko.toast
import xin.z7workbench.recipie.R

class EditInfoFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.layout_edit_info, container, false)

        view.apply {
            back.setOnClickListener { activity?.onBackPressed() }
            confirm.setOnClickListener {
                activity?.toast("修改完成！")
                activity?.onBackPressed()
            }
            gender.text = "男"
            gender.setOnClickListener {
                activity?.selector(getString(R.string.choose), listOf("男", "女")) { _, i ->
                    when (i) {
                        0 -> gender.text = "男"
                        1 -> gender.text = "女"
                    }

                }
            }
        }
        return view
    }
}
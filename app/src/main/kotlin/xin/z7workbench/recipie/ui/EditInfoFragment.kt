package xin.z7workbench.recipie.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.zhihu.matisse.Matisse
import kotlinx.android.synthetic.main.layout_edit_info.view.*
import org.jetbrains.anko.selector
import org.jetbrains.anko.toast
import xin.z7workbench.recipie.R
import xin.z7workbench.recipie.api.RecipieRetrofit
import xin.z7workbench.recipie.api.prepare
import xin.z7workbench.recipie.util.MatisseUtil

class EditInfoFragment : Fragment() {
    var uris = listOf<Uri>()
    private val REQUEST_CODE = 1
    private val ASK_PERMISSION = 2

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
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

                Glide.with(this).load(R.drawable.login_bg).into(avatar)
                avatar.setOnClickListener {
                    val hasPermission = requireActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), ASK_PERMISSION)
                        return@setOnClickListener
                    }
                    MatisseUtil.selectFromFragment(this@EditInfoFragment, 1, REQUEST_CODE)
                }

                confirm.setOnClickListener {
                    // TODO update avatar
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



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE) {
            uris = Matisse.obtainResult(data)
            Glide.with(this)
                    .load(Matisse.obtainResult(data)[0].toString())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .apply(RequestOptions.circleCropTransform())
                    .into(view!!.avatar)
        }
    }
}
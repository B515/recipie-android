package xin.z7workbench.recipie.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
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
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import xin.z7workbench.recipie.R
import xin.z7workbench.recipie.api.RecipieRetrofit
import xin.z7workbench.recipie.api.prepare
import xin.z7workbench.recipie.entity.UserInfo
import xin.z7workbench.recipie.util.MatisseUtil.select
import xin.z7workbench.recipie.util.uploadRequest

@RuntimePermissions
class EditInfoFragment : Fragment() {
    var uris = listOf<Uri>()
    private val REQUEST_CODE = 1
    lateinit var v: View
    private var avatarChanged = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        v = inflater.inflate(R.layout.layout_edit_info, container, false)
        val model = ViewModelProviders.of(requireActivity())[UserInfoViewModel::class.java]
        model.userInfo.observe(this, Observer { user ->
            user ?: return@Observer
            val newUser = user.copy()
            v.apply {
                name.setText(user.nickname)
                val genders = listOf("男", "女")
                gender.text = genders[user.gender]
                gender.setOnClickListener {
                    context.selector(getString(R.string.choose), genders) { _, i ->
                        newUser.gender = i
                        gender.text = genders[i]
                    }
                }

                Glide.with(this).load(user.avatar).into(avatar)
                avatar.setOnClickListener {
                    loadAvatarWithPermissionCheck()
                }

                confirm.setOnClickListener {
                    newUser.nickname = name.text.toString()
                    if (avatarChanged) {
                        uploadRequest(uris[0], context).prepare(context).subscribe {
                            newUser.avatar = it.file
                            updateMyUserInfo(newUser)
                        }
                    } else updateMyUserInfo(newUser)
                }
                back.setOnClickListener { requireActivity().onBackPressed() }
            }
        })
        return v
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    fun loadAvatar() = Matisse.from(this@EditInfoFragment).select(1, REQUEST_CODE)

    private fun updateMyUserInfo(user: UserInfo) {
        RecipieRetrofit.auth.updateMyUserInfo(user.nickname, user.gender, user.avatar).prepare(requireContext()).subscribe {
            (requireActivity() as MainActivity).updateUserInfo()
            requireContext().toast("修改完成！")
            requireActivity().onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE) {
            uris = Matisse.obtainResult(data)
            Glide.with(this)
                    .load(Matisse.obtainResult(data)[0].toString())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .apply(RequestOptions.circleCropTransform())
                    .into(v.avatar)
            avatarChanged = true
        }
    }
}
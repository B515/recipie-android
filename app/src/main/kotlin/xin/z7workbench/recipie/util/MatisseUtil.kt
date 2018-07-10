package xin.z7workbench.recipie.util

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.fragment.app.Fragment
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.internal.entity.CaptureStrategy
import xin.z7workbench.recipie.BuildConfig
import xin.z7workbench.recipie.R

object MatisseUtil {
    fun select(from: Fragment, num: Int, forResult: Int) = select(from.requireActivity(), num, forResult)

    fun select(from: Activity, num: Int, forResult: Int) {
        Matisse.from(from)
                .choose(MimeType.allOf())
                .countable(true)
                .capture(false)
                .captureStrategy(CaptureStrategy(true, BuildConfig.APPLICATION_ID + ".fileprovider"))
                .maxSelectable(num)
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .theme(R.style.AppTheme_Matisse)
                .imageEngine(GlideEngine())
                .forResult(forResult)
    }
}
package xin.z7workbench.recipie.util

import android.content.pm.ActivityInfo
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.internal.entity.CaptureStrategy
import xin.z7workbench.recipie.BuildConfig
import xin.z7workbench.recipie.R

object MatisseUtil {
    fun Matisse.select(num: Int, forResult: Int) {
        choose(MimeType.ofAll())
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
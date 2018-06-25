package xin.z7workbench.recipie.util

import android.content.Intent
import android.net.Uri
import android.os.Environment
import java.io.File

fun getAbsolutePath(id:Int,name:String): String {
    val folder = File("${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath}/Recipie/")
    if (!folder.exists()) folder.mkdirs()
    return "${folder.absolutePath}/${id}_$name"
}

fun openFile(url: String): Intent? {
    val uri = Uri.parse(url)
    val intent: Intent?

    when {
        url.contains(".doc") || url.contains(".docx") -> {
            // Word document
            intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(uri, "application/msword")
        }
        url.contains(".pdf") -> {
            // PDF file
            intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(uri, "application/pdf")
        }
        url.contains(".ppt") || url.contains(".pptx") -> {
            // Powerpoint file
            intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(uri, "application/vnd.ms-powerpoint")
        }
        url.contains(".xls") || url.contains(".xlsx") -> {
            // Excel file
            intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(uri, "application/vnd.ms-excel")
        }
        url.contains(".rtf") -> {
            // RTF file
            intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(uri, "application/rtf")
        }
        url.contains(".wav") -> {
            // WAV audio file
            intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(uri, "audio/x-wav")
        }
        url.contains(".gif") -> {
            // GIF file
            intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(uri, "image/gif")
        }
        url.contains(".jpg") || url.contains(".jpeg") -> {
            // JPG file
            intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(uri, "image/jpeg")
        }
        url.contains(".png") -> {
            // PNG file
            intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(uri, "image/png")
        }
        url.contains(".txt") -> {
            // Text file
            intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(uri, "text/plain")
        }
        url.contains(".mpg") || url.contains(".mpeg") || url.contains(".mpe") || url.contains(".mp4") || url.contains(".avi") -> {
            // Video files
            intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(uri, "video/*")
        }
        else -> {
            intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(uri, "*/*")
        }
    }
    return intent
}
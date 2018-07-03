package xin.z7workbench.recipie.util

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import java.io.File

fun getAbsolutePath(id: Int, name: String): String {
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

fun getPath(context: Context, uri: Uri): String? {
    when {
        DocumentsContract.isDocumentUri(context, uri) -> when {
            isExternalStorageDocument(uri) -> {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]

                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }
            }
            isDownloadsDocument(uri) -> {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id))

                return getDataColumn(context, contentUri, null, null)
            }
            isMediaDocument(uri) -> {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]

                var contentUri: Uri? = null
                when (type) {
                    "image" -> contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    "video" -> contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    "audio" -> contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }

                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])

                return getDataColumn(context, contentUri, selection, selectionArgs)
            }
        }
        "content".equals(uri.scheme!!, ignoreCase = true) -> return getDataColumn(context, uri, null, null)
        "file".equals(uri.scheme!!, ignoreCase = true) -> return uri.path
    }
    return null
}

fun getDataColumn(context: Context, uri: Uri?, selection: String?, selectionArgs: Array<String>?): String? {
    var cursor: Cursor? = null
    val column = "_data"
    val projection = arrayOf(column)

    try {
        cursor = context.contentResolver.query(uri, projection, selection, selectionArgs, null)
        if (cursor != null && cursor.moveToFirst()) {
            val column_index = cursor.getColumnIndexOrThrow(column)
            return cursor.getString(column_index)
        }
    } finally {
        cursor?.close()
    }
    return null
}

fun isExternalStorageDocument(uri: Uri) = "com.android.externalstorage.documents" == uri.authority
fun isDownloadsDocument(uri: Uri) = "com.android.providers.downloads.documents" == uri.authority
fun isMediaDocument(uri: Uri) = "com.android.providers.media.documents" == uri.authority
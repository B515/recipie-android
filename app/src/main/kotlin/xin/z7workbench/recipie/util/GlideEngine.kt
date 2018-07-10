package xin.z7workbench.recipie.util

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import com.zhihu.matisse.engine.ImageEngine

class GlideEngine : ImageEngine {
    override fun loadGifThumbnail(context: Context, resize: Int, placeholder: Drawable, imageView: ImageView, uri: Uri) {
        val options = RequestOptions().placeholder(placeholder).override(resize, resize).priority(Priority.HIGH)
        Glide.with(context)
                .asGif()
                .load(uri)
                .apply(options)
                .into(imageView)
    }

    override fun loadGifImage(context: Context, resizeX: Int, resizeY: Int, imageView: ImageView, uri: Uri) {
        val options = RequestOptions().override(resizeX, resizeY).priority(Priority.HIGH)
        Glide.with(context)
                .asGif()
                .load(uri)
                .apply(options)
                .into(imageView)
    }

    override fun loadThumbnail(context: Context, resize: Int, placeholder: Drawable, imageView: ImageView, uri: Uri) {
        val options = RequestOptions().placeholder(placeholder).override(resize, resize).centerCrop()
        Glide.with(context)
                .asBitmap()  // some .jpeg files are actually gif
                .load(uri)
                .apply(options)
                .into(imageView)
    }

    override fun loadImage(context: Context, resizeX: Int, resizeY: Int, imageView: ImageView, uri: Uri) {
        val options = RequestOptions().override(resizeX, resizeY).priority(Priority.HIGH)
        Glide.with(context)
                .load(uri)
                .apply(options)
                .into(imageView)
    }

    override fun supportAnimatedGif(): Boolean {
        return true
    }

}
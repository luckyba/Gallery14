package com.luckyba.myapplication.util

import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.luckyba.myapplication.R
import java.io.File

object GalleryBindingUtil {

    @BindingAdapter("app:image")
    @JvmStatic fun loadImage(view: ImageView, imageUrl: String) {
        Log.d("view.context.toString()", "loadImage $imageUrl")
        val options: RequestOptions = RequestOptions()
            .format(DecodeFormat.PREFER_RGB_565)
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)

        Glide.with(view.context)
            .load(imageUrl)
            .thumbnail(0.5f)
            .apply(options)
            .into(view)
    }

    @BindingAdapter("app:video")
    @JvmStatic fun loadVideo(view: ImageView, videoUrl: String) {
        val positionInMillis = 0
        val interval = (positionInMillis * 1000).toLong()
        val options = RequestOptions().frame(interval)
        Glide.with(view.context)
            .asBitmap()
            .load(File(videoUrl))
            .apply(options)
            .into(view)
    }

    @BindingAdapter("app:padding")
    @JvmStatic fun setPadding(view: View, array: IntArray) {
        view.setPadding(array[0], array[1], array[2], array[3])
    }

    @BindingAdapter("app:animate")
    @JvmStatic fun setAnimate(view: View, array: FloatArray) {
        view.animate().alpha(array[0]).duration = array[1].toLong()
    }

    @BindingAdapter("app:title", "app:listener")
    @JvmStatic fun updateToolbar (toolbar: Toolbar, title: String, onClickListener: View.OnClickListener) {
        toolbar.title = title
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        toolbar.setNavigationOnClickListener(onClickListener)
    }

    @BindingAdapter("app:setAnimate")
    @JvmStatic fun setAnimate (view: View, animation: Animation) {
        animation.reset()
        view.clearAnimation()
        view.startAnimation(animation)
    }

}
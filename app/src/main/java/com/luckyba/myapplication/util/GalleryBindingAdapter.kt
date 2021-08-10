package com.luckyba.myapplication.util

import android.util.Log
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions

object GalleryBindingAdapter {

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

}
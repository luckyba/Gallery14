package com.luckyba.myapplication.util

import android.util.Log
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

object GalleryBindingAdapter {

    @BindingAdapter("app:image")
    @JvmStatic fun loadImage(view: ImageView, imageUrl: String) {
        Log.d("view.context.toString()","loadImage $imageUrl")
        Glide.with(view.context)
            .load(imageUrl)
            .into(view)
    }

}
package com.luckyba.myapplication.ui.media

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.luckyba.myapplication.data.model.AlbumFile
import com.luckyba.myapplication.util.GalleryBindingAdapter.loadImage

class MediaPagerAdapter(val context: Context, private val listAlbumFile: ArrayList<AlbumFile>) : PagerAdapter() {

    override fun getCount(): Int {
        return listAlbumFile.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val image = ImageView(context)
        listAlbumFile[position].path?.let { loadImage(image, it) }
        container.addView(image)
        return image
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}
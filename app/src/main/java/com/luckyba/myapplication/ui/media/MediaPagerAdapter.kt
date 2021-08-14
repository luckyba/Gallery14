package com.luckyba.myapplication.ui.media

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.luckyba.myapplication.data.model.AlbumFile
import com.luckyba.myapplication.ui.media.fragment.ImageFragment
import com.luckyba.myapplication.ui.media.fragment.VideoFragment
import com.luckyba.myapplication.util.MediaType
import kotlin.collections.ArrayList

class MediaPagerAdapter(fm: FragmentManager,lifecycle: Lifecycle,  private var media: ArrayList<AlbumFile>) :
    FragmentStateAdapter(fm, lifecycle) {

    override fun getItemCount(): Int {
        return if(media.size >100) 50
        else media.size
//        return media.size
    }

    override fun createFragment(position: Int): Fragment {
        val media = media[position]
        return when(media.mediaType) {
            MediaType.TYPE_IMAGE -> ImageFragment.newInstance(media)
            MediaType.TYPE_VIDEO -> VideoFragment.newInstance(media)
            else -> ImageFragment.newInstance(media)
        }
    }

    fun setData(arrayList: ArrayList<AlbumFile>) {
        media = arrayList
        notifyDataSetChanged()
    }
}
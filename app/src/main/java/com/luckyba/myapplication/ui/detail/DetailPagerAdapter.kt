package com.luckyba.myapplication.ui.detail

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.luckyba.myapplication.data.model.AlbumFile
import com.luckyba.myapplication.ui.detail.fragment.ImageFragment
import com.luckyba.myapplication.ui.detail.fragment.VideoFragment
import com.luckyba.myapplication.util.MediaType

class DetailPagerAdapter(
    fm: FragmentManager,
    lifecycle: Lifecycle,
    private var media: ArrayList<AlbumFile>
) :
    FragmentStateAdapter(fm, lifecycle) {

    override fun getItemCount(): Int {
        return media.size
    }

    override fun createFragment(position: Int): Fragment {
        val media = media[position]
        return when (media.mediaType) {
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
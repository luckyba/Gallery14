package com.luckyba.myapplication.ui.media.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.luckyba.myapplication.R
import com.luckyba.myapplication.common.BaseMediaFragment
import com.luckyba.myapplication.data.model.AlbumFile
import com.luckyba.myapplication.databinding.FragmentVideoBinding

/**
 * A Media Fragment for showing a Video Preview.
 */
class VideoFragment : BaseMediaFragment() {
    lateinit var binding: FragmentVideoBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_video, container, false)
        setHasOptionsMenu(false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.path = media.path
        binding.fragment = this
    }

    fun onClickPlay() {
        val uri = Uri.parse(media.path)
        val intent = Intent(Intent.ACTION_VIEW).setDataAndType(uri, media.mimeType)
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        startActivity(intent)
    }

    companion object {
        @JvmStatic
        fun newInstance(media: AlbumFile): VideoFragment {
            return newInstance(VideoFragment(), media)
        }
    }
}
package com.luckyba.myapplication.ui.media

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.luckyba.myapplication.R
import com.luckyba.myapplication.data.model.AlbumFile
import com.luckyba.myapplication.data.model.AlbumFolder
import com.luckyba.myapplication.databinding.ActivityMediaBinding
import com.luckyba.myapplication.util.StringUtils.EXTRA_ARGS_ALBUM
import com.luckyba.myapplication.util.StringUtils.EXTRA_ARGS_POSITION

class MediaActivity: AppCompatActivity() {

    lateinit var binding: ActivityMediaBinding
    lateinit var mediaAdapter: MediaPagerAdapter
    var position = 0
    var albumFiles: ArrayList<AlbumFile> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_media)

        loadAlbumFile()
        mediaAdapter = MediaPagerAdapter(this, albumFiles)
        binding.viewpagerMedia.adapter = mediaAdapter
        binding.viewpagerMedia.currentItem = position
    }

    private fun loadAlbumFile() {
        albumFiles = (intent.getParcelableExtra<AlbumFolder>(EXTRA_ARGS_ALBUM) as AlbumFolder).albumFiles
        position = intent.getIntExtra(EXTRA_ARGS_POSITION, 0)
    }



}
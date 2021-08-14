package com.luckyba.myapplication.ui.media

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.luckyba.myapplication.R
import com.luckyba.myapplication.data.model.AlbumFile
import com.luckyba.myapplication.data.model.AlbumFolder
import com.luckyba.myapplication.databinding.ActivityMediaBinding
import com.luckyba.myapplication.util.StringUtils.ACTION_OPEN_ALBUM
import com.luckyba.myapplication.util.StringUtils.EXTRA_ARGS_ALBUM
import com.luckyba.myapplication.util.StringUtils.EXTRA_ARGS_MEDIA
import com.luckyba.myapplication.util.StringUtils.EXTRA_ARGS_POSITION
import com.luckyba.myapplication.viewmodel.GalleryViewModel

class MediaActivity: AppCompatActivity() {
    lateinit var viewModel: GalleryViewModel
    lateinit var binding: ActivityMediaBinding
    lateinit var adapter: MediaPagerAdapter
    var position = 0
    var albumFiles: ArrayList<AlbumFile> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_media)
        viewModel = ViewModelProvider(this).get(GalleryViewModel::class.java)
        position = intent.getIntExtra(EXTRA_ARGS_POSITION, 0)
        if (intent.action == ACTION_OPEN_ALBUM) loadAlbumFile()
        else loadLazyAlbumFile()
        adapter = MediaPagerAdapter(supportFragmentManager, lifecycle, albumFiles)
        binding.viewpagerMedia.adapter  = adapter
        binding.viewpagerMedia.setCurrentItem(position, false)
    }

    private fun loadAlbumFile() {
        albumFiles = (intent.getParcelableExtra<AlbumFolder>(EXTRA_ARGS_ALBUM) as AlbumFolder).albumFiles
    }

    private fun loadLazyAlbumFile () {
        viewModel.getAll()
        viewModel.listData.observe(this, {
            albumFiles = it[0].albumFiles
            Log.d("fdsafas", " album Size ${albumFiles.size} ")
            position = albumFiles.indexOf(intent.getParcelableExtra<AlbumFile>(EXTRA_ARGS_MEDIA) as AlbumFile)
            adapter.setData(albumFiles)
            binding.viewpagerMedia.setCurrentItem(position, false)
        })
    }
}
package com.luckyba.myapplication.ui.detail

import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.luckyba.myapplication.R
import com.luckyba.myapplication.common.BaseActivity
import com.luckyba.myapplication.data.model.AlbumFile
import com.luckyba.myapplication.data.model.AlbumFolder
import com.luckyba.myapplication.databinding.ActivityDetailBinding
import com.luckyba.myapplication.util.StringUtils.ACTION_OPEN_ALBUM
import com.luckyba.myapplication.util.StringUtils.EXTRA_ARGS_ALBUM
import com.luckyba.myapplication.util.StringUtils.EXTRA_ARGS_MEDIA
import com.luckyba.myapplication.util.StringUtils.EXTRA_ARGS_POSITION
import com.luckyba.myapplication.viewmodel.GalleryViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class DetailActivity: BaseActivity("DetailActivity") {
    lateinit var viewModel: GalleryViewModel
    lateinit var binding: ActivityDetailBinding
    lateinit var adapter: DetailPagerAdapter
    var position = 0
    var albumFiles: ArrayList<AlbumFile> = ArrayList()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail)
        viewModel = ViewModelProvider(this).get(GalleryViewModel::class.java)

        position = intent.getIntExtra(EXTRA_ARGS_POSITION, 0)
        if (intent.action == ACTION_OPEN_ALBUM) loadAlbumFile()
        else loadLazyAlbumFile()
        adapter = DetailPagerAdapter(supportFragmentManager, lifecycle, albumFiles)
        binding.viewpagerMedia.adapter  = adapter
        binding.viewpagerMedia.setCurrentItem(position, false)

    }

    private fun loadAlbumFile() {
        albumFiles = (intent.getParcelableExtra<AlbumFolder>(EXTRA_ARGS_ALBUM) as AlbumFolder).albumFiles
    }

    private fun loadLazyAlbumFile () {
        GlobalScope.launch(Dispatchers.Main) {
            viewModel.getAllData()// back on UI thread
        }
//        viewModel.getAll()
        viewModel.listData.observe(this, {
            albumFiles = it[0].albumFiles
            Log.d("fdsafas", " album Size ${albumFiles.size} ")
            position =
                albumFiles.indexOf(intent.getParcelableExtra<AlbumFile>(EXTRA_ARGS_MEDIA) as AlbumFile)
            adapter.setData(albumFiles)
            binding.viewpagerMedia.setCurrentItem(position, false)
        })
    }

    /**
     * release caches to avoid killing UI when low memory
     */
    override fun onLowMemory() {
        super.onLowMemory()
        Glide.get(applicationContext).clearMemory()
        Glide.get(applicationContext).trimMemory(TRIM_MEMORY_COMPLETE)
        System.gc()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        return super.onTouchEvent(event)
    }
}
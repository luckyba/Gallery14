package com.luckyba.myapplication.ui.media

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.luckyba.myapplication.R
import com.luckyba.myapplication.data.model.AlbumFile
import com.luckyba.myapplication.data.model.AlbumFolder
import com.luckyba.myapplication.data.sort.MediaComparators
import com.luckyba.myapplication.data.sort.SortingMode
import com.luckyba.myapplication.data.sort.SortingOrder
import com.luckyba.myapplication.databinding.ActivityMediaBinding
import com.luckyba.myapplication.util.StringUtils.ACTION_OPEN_ALBUM
import com.luckyba.myapplication.util.StringUtils.EXTRA_ARGS_ALBUM
import com.luckyba.myapplication.util.StringUtils.EXTRA_ARGS_MEDIA
import com.luckyba.myapplication.util.StringUtils.EXTRA_ARGS_POSITION
import com.luckyba.myapplication.viewmodel.GalleryViewModel
import java.util.*
import kotlin.collections.ArrayList

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
        binding.viewpagerMedia.currentItem = position
    }

    private fun loadAlbumFile() {
        albumFiles = (intent.getParcelableExtra<AlbumFolder>(EXTRA_ARGS_ALBUM) as AlbumFolder).albumFiles
    }

    private fun loadLazyAlbumFile () {
        viewModel.getAll()
        viewModel.listData.observe(this, {
            albumFiles = it[0].albumFiles
            Collections.sort(
                albumFiles, MediaComparators.getComparator(
                    SortingMode.DATE,
                    SortingOrder.DESCENDING
                )
            )
            position = albumFiles.indexOf(intent.getParcelableExtra<AlbumFile>(EXTRA_ARGS_MEDIA) as AlbumFile)
            binding.viewpagerMedia.currentItem = position
            adapter.setData(albumFiles)
        })
    }
}
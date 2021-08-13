package com.luckyba.myapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.luckyba.myapplication.app.GalleryApplication
import com.luckyba.myapplication.data.model.AlbumFile
import com.luckyba.myapplication.data.model.AlbumFolder
import com.luckyba.myapplication.data.model.TaskRunner
import com.luckyba.myapplication.util.FilterMode
import com.luckyba.myapplication.util.MediaType

class GalleryViewModel : ViewModel(){

    private val task: TaskRunner = TaskRunner(GalleryApplication.getRepository())

    private var _listData = MutableLiveData<ArrayList<AlbumFolder>>()

    var listData: LiveData<ArrayList<AlbumFolder>> = _listData

    fun getAll() {
        task.executeAsync(TaskRunner.LoadAllMedia()
            , ::onComplete)
    }

    private fun onComplete(result: Any?) {
        _listData.value = result as ArrayList<AlbumFolder>
    }

    fun getDataFilterBy(filterMode: FilterMode): ArrayList<AlbumFile> {
        return when (filterMode) {
            FilterMode.ALL -> getFilterAll()
            FilterMode.IMAGES -> getFilterImage()
            FilterMode.VIDEO -> getFilterVideo()
            FilterMode.OTHER -> getFilterOther()
            else -> getFilterAll()
        }
    }

    private fun getFilterAll(): ArrayList<AlbumFile> {
        val data: ArrayList<AlbumFile> = ArrayList()
        data.addAll(_listData.value!![0].albumFiles)
        return data
    }

    private fun getFilterImage(): ArrayList<AlbumFile> {
        val imageData: ArrayList<AlbumFile> = ArrayList()
        val listAlbumFile = _listData.value!![0].albumFiles
        for (file in listAlbumFile) {
            if (file.mediaType == MediaType.TYPE_IMAGE) imageData.add(file)
        }

        return imageData
    }

    private fun getFilterVideo(): ArrayList<AlbumFile> {
        val videoData: ArrayList<AlbumFile> = ArrayList()
        val listAlbumFile = _listData.value!![0].albumFiles
        for (file in listAlbumFile) {
            if (file.mediaType == MediaType.TYPE_VIDEO) videoData.add(file)
        }

        return videoData
    }

    private fun getFilterOther(): ArrayList<AlbumFile> {
        val otherData: ArrayList<AlbumFile> = ArrayList()
        val listAlbumFile = _listData.value!![0].albumFiles
        for (file in listAlbumFile) {
            if (file.mediaType == MediaType.OTHER) otherData.add(file)
        }

        return otherData
    }
}
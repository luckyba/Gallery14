package com.luckyba.myapplication.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.luckyba.myapplication.app.GalleryApplication
import com.luckyba.myapplication.data.model.AlbumFile
import com.luckyba.myapplication.data.model.AlbumFolder
import com.luckyba.myapplication.data.model.TaskRunner

class HomeViewModel : ViewModel(){

    private val task: TaskRunner = TaskRunner(GalleryApplication.getRepository())

    private var _listData = MutableLiveData<ArrayList<AlbumFile>>()

    var listData: LiveData<ArrayList<AlbumFile>> = _listData

    fun getAll() {
        task.executeAsync(TaskRunner.LoadAllMedia()
            , ::onComplete)
    }

    private fun onComplete(result: Any?) {
        _listData.value = (result as ArrayList<AlbumFolder>?)?.get(0)?.albumFiles
    }

}
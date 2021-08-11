package com.luckyba.myapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.luckyba.myapplication.app.GalleryApplication
import com.luckyba.myapplication.data.model.AlbumFile
import com.luckyba.myapplication.data.model.AlbumFolder
import com.luckyba.myapplication.data.model.TaskRunner

class HomeViewModel : ViewModel(){

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

}
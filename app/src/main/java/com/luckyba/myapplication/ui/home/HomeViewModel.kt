package com.luckyba.myapplication.ui.home

import androidx.databinding.BaseObservable
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.luckyba.myapplication.app.GalleryApplication
import com.luckyba.myapplication.data.model.Media
import com.luckyba.myapplication.data.model.MediaFileListModel
import com.luckyba.myapplication.data.model.TaskRunner
import com.luckyba.myapplication.data.sort.SortingMode
import com.luckyba.myapplication.data.sort.SortingOrder

class HomeViewModel : ViewModel(){

    private val task: TaskRunner = TaskRunner(GalleryApplication.getRepository())

    private var _listData = MutableLiveData<ArrayList<Media>>().apply {
        value = ArrayList()
    }

    var listData: LiveData<ArrayList<Media>> = _listData

    fun getAll() {
        task.executeAsync(TaskRunner.LoadAllMedia(SortingMode.fromValue(0), SortingOrder.fromValue(1))
            , ::onComplete)
    }

    private fun onComplete(result: Any?) {
        _listData.value = result as ArrayList<Media>?
    }

    class Observable: BaseObservable() {
        var isEmpty = ObservableBoolean()

        fun getIsEmpty(): ObservableBoolean {
            return isEmpty
        }

        fun setIsEmpty(empty: Boolean) {
            isEmpty.set(empty)
        }
    }
}
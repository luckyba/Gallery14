package com.luckyba.myapplication.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.luckyba.myapplication.app.GalleryApplication
import com.luckyba.myapplication.data.model.AlbumFile
import com.luckyba.myapplication.data.model.AlbumFolder
import com.luckyba.myapplication.data.model.TaskRunner
import com.luckyba.myapplication.util.FilterMode
import com.luckyba.myapplication.util.MediaType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GalleryViewModel : ViewModel() {

    private val task: TaskRunner = TaskRunner(GalleryApplication.getRepository())
    private val repository = GalleryApplication.getRepository()

    private var _listData = MutableLiveData<ArrayList<AlbumFolder>>()

    private var _dataChange = MutableLiveData<Boolean>()

    var isDataChanged: LiveData<Boolean> = _dataChange

    var listData: LiveData<ArrayList<AlbumFolder>> = _listData

    suspend fun getAllData() {
        _listData.value = withContext(Dispatchers.IO) {
            Log.d("fdsafas", "Thread "+ Thread.currentThread().name)
            repository.getAlbums()
        } as ArrayList<AlbumFolder>
    }

    suspend fun deleteListFile(listPath: MutableSet<String>) {
        _dataChange.value = withContext(Dispatchers.IO) {
            repository.deleteListFile(listPath)
        } as Boolean
    }

    suspend fun moveFile(listPath: MutableSet<String>, outPath: String) {
        _dataChange.value = withContext(Dispatchers.IO) {
            repository.moveFileToAlbum(listPath, outPath)
        } as Boolean
    }

    suspend fun copyFile(listPath: MutableSet<String>, outPath: String) {
        _dataChange.value = withContext(Dispatchers.IO) {
            repository.copyFile(listPath, outPath)
        } as Boolean
    }

    suspend fun renameFile(path: String, oldName: String, newName: String) {
        _dataChange.value = withContext(Dispatchers.IO) {
            repository.reName(path, oldName, newName)
        } as Boolean
    }

    //    fun getAll() {
//        task.executeAsync(
//            TaskRunner.LoadAllMedia(), ::onLoadComplete
//        )
//    }
//    private fun onLoadComplete(result: Any?) {
//        _listData.value = result as ArrayList<AlbumFolder>
//    }
//
//    private fun onComplete(result: Any?) {
//        _dataChange.value = result as Boolean
//    }
//
//    fun deleteListFile(listPath: MutableSet<String>) =
//        task.executeAsync(TaskRunner.DeleteListFile(listPath), ::onComplete)
//
//    fun moveFile(listPath: MutableSet<String>, outPath: String) =
//        task.executeAsync(TaskRunner.MoveFile(listPath, outPath), ::onComplete)
//
//    fun copyFile(listPath: MutableSet<String>, outPath: String) =
//        task.executeAsync(TaskRunner.CopyFile(listPath, outPath), ::onComplete)
//
//    fun renameFile(path: String, oldName: String, newName: String) =
//        task.executeAsync(TaskRunner.ReNameFile(path, oldName,  newName), ::onComplete)

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
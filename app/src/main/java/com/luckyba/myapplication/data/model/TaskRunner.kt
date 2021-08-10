package com.luckyba.myapplication.data.model


import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.luckyba.myapplication.data.responsitory.FileRepository
import java.util.concurrent.Callable
import java.util.concurrent.Executor
import java.util.concurrent.Executors


class TaskRunner(private val fileRepository: FileRepository) {
    private val executor: Executor = Executors.newScheduledThreadPool(2)
    private val handler = Handler(Looper.getMainLooper())

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var fileRepository: FileRepository
    }

    init {
        Companion.fileRepository = fileRepository
    }


    fun <Any> executeAsync(callable: Callable<Any>, onComplete: (results: Any?) ->Unit = {}) {
        executor.execute {
            try {
                val result = callable.call()
                handler.post { onComplete(result) }
            } catch (e: Exception) {
                Log.d("lucky", "can't excuse this function ")
                e.printStackTrace()
            }
        }
    }

    class LoadAllMediaTest() : Callable<Any> {
        override fun call(): Any {
            return fileRepository.getAllMedia()
        }
    }

    class LoadAllMedia() : Callable<Any> {
        override fun call(): Any {
            return fileRepository.getAlbums()
        }
    }

}

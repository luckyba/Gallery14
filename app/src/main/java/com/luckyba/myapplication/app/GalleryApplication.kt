package com.luckyba.myapplication.app

import android.app.Application
import android.os.Bundle
import com.luckyba.myapplication.data.responsitory.FileRepository
import com.luckyba.myapplication.util.ObservableViewModel

class GalleryApplication: Application() {
    companion object {
        lateinit var mInstance: GalleryApplication
        lateinit var args: Bundle
        lateinit var observableViewModel: ObservableViewModel
        fun getInstance(): GalleryApplication = mInstance

        fun getRepository (): FileRepository = FileRepository(mInstance.applicationContext)
        fun getObservableViewMode() = observableViewModel
    }

    init {
        mInstance = this
        observableViewModel = ObservableViewModel()
    }



}
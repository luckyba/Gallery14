package com.luckyba.myapplication.app

import android.app.Application
import com.luckyba.myapplication.data.responsitory.FileRepository

class GalleryApplication: Application() {
    companion object {
        lateinit var mInstance: GalleryApplication
        fun getInstance(): GalleryApplication = mInstance

        fun getRepository (): FileRepository = FileRepository(mInstance.applicationContext)
    }

    init {
        mInstance = this
    }



}
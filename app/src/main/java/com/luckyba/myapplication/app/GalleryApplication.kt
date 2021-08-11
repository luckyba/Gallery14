package com.luckyba.myapplication.app

import android.app.Application
import android.os.Bundle
import com.luckyba.myapplication.data.responsitory.FileRepository

class GalleryApplication: Application() {
    companion object {
        lateinit var mInstance: GalleryApplication
        lateinit var args: Bundle
        fun getInstance(): GalleryApplication = mInstance

        fun getRepository (): FileRepository = FileRepository(mInstance.applicationContext)
    }

    init {
        mInstance = this
    }



}
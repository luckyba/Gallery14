package com.luckyba.myapplication.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AlbumFolder(var name: String? = null
                       , var albumFiles: ArrayList<AlbumFile> = ArrayList(), var isChecked: Boolean = false) : Parcelable {

    fun addAlbumFile(albumFile: AlbumFile) {
        albumFiles.add(albumFile)
    }

}
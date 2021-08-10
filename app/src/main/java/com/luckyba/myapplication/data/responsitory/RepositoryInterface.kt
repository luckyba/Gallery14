package com.luckyba.myapplication.data.responsitory

import com.luckyba.myapplication.data.model.AlbumFile
import com.luckyba.myapplication.data.model.AlbumFolder
import com.luckyba.myapplication.data.sort.SortingMode
import com.luckyba.myapplication.data.sort.SortingOrder

interface RepositoryInterface {
    fun getAllMedia(): ArrayList<AlbumFile>

    fun getMedias(): ArrayList<AlbumFile>
    fun getAlbums(): ArrayList<AlbumFolder>

    fun getHiddenAlbums(sortingMode: SortingMode, sortingOrder: SortingOrder): ArrayList<AlbumFolder>
}
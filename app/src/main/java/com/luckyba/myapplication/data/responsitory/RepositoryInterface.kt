package com.luckyba.myapplication.data.responsitory

import com.luckyba.myapplication.data.model.Album
import com.luckyba.myapplication.data.model.Media
import com.luckyba.myapplication.data.model.MediaFileListModel
import com.luckyba.myapplication.data.sort.SortingMode
import com.luckyba.myapplication.data.sort.SortingOrder
import kotlin.collections.ArrayList

interface RepositoryInterface {
    fun getAllMedia(): ArrayList<MediaFileListModel>

    fun getMedias(sortingMode: SortingMode, sortingOrder: SortingOrder): ArrayList<Media>
    fun getAlbums(sortingMode: SortingMode, sortingOrder: SortingOrder): ArrayList<Album>

    fun getHiddenAlbums(sortingMode: SortingMode, sortingOrder: SortingOrder): ArrayList<Album>
}
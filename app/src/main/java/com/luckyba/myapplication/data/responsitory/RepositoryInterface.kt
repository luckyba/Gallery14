package com.luckyba.myapplication.data.responsitory

import com.luckyba.myapplication.data.model.AlbumFile
import com.luckyba.myapplication.data.model.AlbumFolder

interface RepositoryInterface {
    fun getAllMedia(): ArrayList<AlbumFile>

    fun getMedias(): ArrayList<AlbumFile>
    fun getAlbums(): ArrayList<AlbumFolder>

    fun getHiddenAlbums(): ArrayList<AlbumFolder>

    fun deleteListFile (listPath: List<String>): Boolean
    fun deleteAlbum (listPath: List<String>): Boolean
    fun moveFileToAlbum (listPath: List<String>, outPath: String): Boolean
    fun copyFile (listPath: List<String>, outPath: String): Boolean


}
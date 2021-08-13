package com.luckyba.myapplication.data.responsitory

import com.luckyba.myapplication.data.model.AlbumFile
import com.luckyba.myapplication.data.model.AlbumFolder

interface RepositoryInterface {
    fun getAllMedia(): ArrayList<AlbumFile>

    fun getMedias(): ArrayList<AlbumFile>
    fun getAlbums(): ArrayList<AlbumFolder>

    fun getHiddenAlbums(): ArrayList<AlbumFolder>

    fun deleteListFile (listPath: MutableSet<String>): Boolean
    fun deleteAlbum (listPath: MutableSet<String>): Boolean
    fun moveFileToAlbum (listPath: MutableSet<String>, outPath: String): Boolean
    fun copyFile (listPath: MutableSet<String>, outPath: String): Boolean
    fun createAlbum (albumName: String, listPath: MutableSet<String>): Boolean
    fun reName(path: String,oldName: String, newName: String): Boolean
}
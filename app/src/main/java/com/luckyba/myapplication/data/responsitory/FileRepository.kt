package com.luckyba.myapplication.data.responsitory

import android.content.Context
import com.luckyba.myapplication.data.model.AlbumFile
import com.luckyba.myapplication.data.model.AlbumFolder
import com.luckyba.myapplication.data.provider.MediaReader
import com.luckyba.myapplication.data.sort.SortingMode
import com.luckyba.myapplication.data.sort.SortingOrder

class FileRepository(val context: Context):RepositoryInterface {
    private var mMedia: MediaReader = MediaReader(context)

    //this is for test
    override fun getAllMedia(): ArrayList<AlbumFile> {
        TODO("Not yet implemented")
    }

    override fun getMedias(): ArrayList<AlbumFile> {
        TODO("Not yet implemented")
    }

    override fun getAlbums(): ArrayList<AlbumFolder> {
        return mMedia.allMedia
    }

    override fun getHiddenAlbums(sortingMode: SortingMode, sortingOrder: SortingOrder): ArrayList<AlbumFolder> {
        TODO("Not yet implemented")
    }


}
package com.luckyba.myapplication.data.responsitory

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import com.luckyba.myapplication.data.model.Album
import com.luckyba.myapplication.data.model.Media
import com.luckyba.myapplication.data.model.MediaFileListModel
import com.luckyba.myapplication.data.provider.Query
import com.luckyba.myapplication.data.provider.QueryUtils
import com.luckyba.myapplication.data.sort.SortingMode
import com.luckyba.myapplication.data.sort.SortingOrder
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class FileRepository(val context: Context):RepositoryInterface {

    //this is for test
    override fun getAllMedia(): ArrayList<MediaFileListModel> {
        val mediaFileListModels = ArrayList<MediaFileListModel>()
        val mCursor: Cursor? = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, arrayOf(
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME
            ), null, null,
            "LOWER(" + MediaStore.Images.Media.TITLE + ") ASC"
        )
        if (mCursor != null) {
            if (mCursor.count != 0) {
                if (mCursor.moveToFirst()) {
                    do {
                        val mediaFileListModel = MediaFileListModel()
                        mediaFileListModel.fileName = mCursor.getString(
                            mCursor.getColumnIndexOrThrow(
                                MediaStore.Images.Media.DISPLAY_NAME
                            )
                        )
                        mediaFileListModel.filePath = mCursor.getString(
                            mCursor.getColumnIndexOrThrow(
                                MediaStore.Images.Media.DATA
                            )
                        )
                        try {
                            val file = File(
                                mCursor.getString(
                                    mCursor.getColumnIndexOrThrow(
                                        MediaStore.Images.Media.DATA
                                    )
                                )
                            )
                            var length = file.length()
                            length = length / 1024
                            if (length >= 1024) {
                                length = length / 1024
                                mediaFileListModel.fileSize = "$length MB"
                            } else {
                                mediaFileListModel.fileSize = "$length KB"
                            }
                            val lastModDate = Date(file.lastModified())
                            mediaFileListModel.fileCreatedTime = lastModDate.toString()
                        } catch (e: Exception) {
                            e.printStackTrace()
                            mediaFileListModel.fileSize = "unknown"
                        }
                        mediaFileListModels.add(mediaFileListModel)
                    } while (mCursor.moveToNext())
                }
                mCursor.close()
            }
        }
        return mediaFileListModels
    }

    override fun getMedias(sortingMode: SortingMode, sortingOrder: SortingOrder): ArrayList<Media> {
        TODO("Not yet implemented")
    }

    override fun getAlbums(sortingMode: SortingMode, sortingOrder: SortingOrder): ArrayList<Album> {
        val query: Query.Builder = Query.Builder()
            .uri(MediaStore.Files.getContentUri("external"))
            .projection(Album().projection)
            .sort(sortingMode.albumsColumn)
            .ascending(sortingOrder.isAscending)

        val args = ArrayList<Any>()
        query.selection(
            String.format("%s=? or %s=?) group by (%s",
                MediaStore.Files.FileColumns.MEDIA_TYPE,
                MediaStore.Files.FileColumns.MEDIA_TYPE,
                MediaStore.Files.FileColumns.PARENT
            )
        )
        args.add(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE)
        args.add(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)
        query.args(args.toArray())
        return QueryUtils.query(query.build(), context.contentResolver, Album())
    }

    override fun getHiddenAlbums(sortingMode: SortingMode, sortingOrder: SortingOrder): ArrayList<Album> {
        TODO("Not yet implemented")
    }

    private fun getHavingFilter(excludedCount: Int): String {
        if (excludedCount == 0) return "("
        val res = StringBuilder()
        res.append("HAVING (")
        res.append(MediaStore.Images.Media.DATA).append(" NOT LIKE ?")
        for (i in 1 until excludedCount) res.append(" AND ")
            .append(MediaStore.Images.Media.DATA)
            .append(" NOT LIKE ?")

        // NOTE: dont close ths parenthesis it will be closed by ContentResolver
        //res.append(")");
        return res.toString()
    }
}
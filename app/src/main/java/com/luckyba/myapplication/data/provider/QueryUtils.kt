package com.luckyba.myapplication.data.provider

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.luckyba.myapplication.data.CursorHandler


object QueryUtils {
    fun <T> query(q: Query, cr: ContentResolver?, cursorHandler: CursorHandler<T>): ArrayList<T> {
        var listData: ArrayList<T> = ArrayList()
        var cursor: Cursor? = null
        try {
            cursor = cr?.let { q.getCursor(it) }
            if (cursor != null && cursor.count > 0)
                while (cursor.moveToNext()) {
                    listData.add(cursorHandler.handle(cursor))
                }

        } catch (err: Exception) {
            Log.d("lucky ", "query $err")
        } finally {
            cursor?.close()
        }
        return listData
    }

    fun <T> querySingle(q: Query, cr: ContentResolver?) {
        var cursor: Cursor? = null
        try {
            cursor = cr?.let { q.getCursor(it)}
            if (cursor != null && cursor.moveToFirst()) {

            }
        } catch (err: Exception) {
            Log.d("lucky ", "querySingle $err")
        } finally {
            cursor?.close()
        }
    }

    private fun getCount(context: Context, contentUri: Uri, bucketId: String): Int {
        context.getContentResolver().query(
            contentUri,
            null, MediaStore.Images.Media.BUCKET_ID + "=?", arrayOf(bucketId), null
        )
            .use { cursor -> return if (cursor == null || cursor.moveToFirst() == false) 0 else cursor.getCount() }
    }
}

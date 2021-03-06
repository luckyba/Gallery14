package com.luckyba.myapplication.util

import android.os.Bundle
import android.os.Parcel
import android.webkit.MimeTypeMap


class GalleryUtil {

    companion object {
        val UNKNOWN_MIME_TYPE = "unknown/unknown"
        val ACTION_MOVE = "action_move"
        val ACTION_COPY = "action_copy"
        val ACTION_DELETE = "action_delete"
        @JvmStatic
        fun isMaxBundle(bundle: Bundle): Boolean {

            return getBundleSizeInBytes(bundle) > 500000
        }

        private fun getBundleSizeInBytes(bundle: Bundle) : Int {
            val parcel = Parcel.obtain()
            parcel.writeValue(bundle)

            val bytes = parcel.marshall()
            parcel.recycle()

            return bytes.size
        }

    }


    fun getMimeType(path: String?): String? {
        var index: Int = 0
        if (path == null || path.lastIndexOf('.')
                .also { index = it } == -1
        ) return UNKNOWN_MIME_TYPE
        val mime = MimeTypeMap.getSingleton()
            .getMimeTypeFromExtension(path.substring(index + 1).toLowerCase())
        return mime ?: UNKNOWN_MIME_TYPE
    }

    fun getGenericMIME(mime: String): String {
        return mime.split("/".toRegex()).toTypedArray()[0] + "/*"
    }

    fun getTypeMime(mime: String): String? {
        return mime.split("/".toRegex()).toTypedArray()[0]
    }

}
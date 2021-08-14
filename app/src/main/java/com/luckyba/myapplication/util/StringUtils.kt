package com.luckyba.myapplication.util

import android.content.Context
import android.os.Build
import android.text.Html
import android.text.Spanned
import android.widget.Toast
import java.util.*
import java.util.regex.Pattern

/**
 * Created by dnld on 1/3/16.
 */
object StringUtils {
    val ACTION_OPEN_ALBUM = "com.luckyba.myapplication.intent.VIEW_ALBUM"
    val ACTION_OPEN_ALBUM_LAYZY = "com.luckyba.myapplication.intent.VIEW_ALBUM_LAZY"
    val ACTION_REVIEW = "com.android.camera.action.REVIEW"

    val EXTRA_ARGS_ALBUM = "args_album"
    val EXTRA_ARGS_MEDIA = "args_media"
    val EXTRA_ARGS_POSITION = "args_position"
    val EXTRA_ARGS_AlBUM_TITLE = "args_title"

    fun getPhotoNameByPath(path: String): String {
        val b = path.split("/".toRegex()).toTypedArray()
        val fi = b[b.size - 1]
        return fi.substring(0, fi.lastIndexOf('.'))
    }

    fun html(s: String?): Spanned {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) Html.fromHtml(
            s,
            Html.FROM_HTML_MODE_LEGACY
        ) else Html.fromHtml(s)
    }

    fun getName(path: String): String {
        val b = path.split("/".toRegex()).toTypedArray()
        return b[b.size - 1]
    }

    fun getPhotoPathRenamed(olderPath: String, newName: String?): String {
        val c = StringBuilder()
        val b = olderPath.split("/".toRegex()).toTypedArray()
        for (x in 0 until b.size - 1) c.append(b[x]).append("/")
        c.append(newName)
        val name = b[b.size - 1]
        c.append(name.substring(name.lastIndexOf('.')))
        return c.toString()
    }

    fun incrementFileNameSuffix(name: String): String {
        val builder = StringBuilder()
        val dot = name.lastIndexOf('.')
        val baseName = if (dot != -1) name.subSequence(0, dot).toString() else name
        var nameWoSuffix = baseName
        val matcher = Pattern.compile("_\\d").matcher(baseName)
        if (matcher.find()) {
            val i = baseName.lastIndexOf("_")
            if (i != -1) nameWoSuffix = baseName.subSequence(0, i).toString()
        }
        builder.append(nameWoSuffix).append("_").append(Date().time)
        builder.append(name.substring(dot))
        return builder.toString()
    }

    fun getPhotoPathRenamedAlbumChange(olderPath: String, albumNewName: String): String {
        var c = ""
        val b = olderPath.split("/".toRegex()).toTypedArray()
        for (x in 0 until b.size - 2) c += b[x] + "/"
        c += albumNewName + "/" + b[b.size - 1]
        return c
    }

    fun getAlbumPathRenamed(olderPath: String, newName: String): String {
        return olderPath.substring(0, olderPath.lastIndexOf('/')) + "/" + newName
    }

    fun getPhotoPathMoved(olderPath: String, folderPath: String): String {
        val b = olderPath.split("/".toRegex()).toTypedArray()
        val fi = b[b.size - 1]
        var path = "$folderPath/"
        path += fi
        return path
    }

    fun getBucketPathByImagePath(path: String): String {
        val b = path.split("/".toRegex()).toTypedArray()
        var c = ""
        for (x in 0 until b.size - 1) c += b[x] + "/"
        c = c.substring(0, c.length - 1)
        return c
    }

    fun showToast(x: Context?, s: String?) {
        val t = Toast.makeText(x, s, Toast.LENGTH_SHORT)
        t.show()
    }
}
package com.luckyba.myapplication.data.model

import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import com.luckyba.myapplication.data.CursorHandler
import com.luckyba.myapplication.ui.timeline.data.TimelineItem
import com.luckyba.myapplication.ui.timeline.data.TimelineItem.TYPE_MEDIA
import com.luckyba.myapplication.util.ArrayUtils
import com.luckyba.myapplication.util.MimeTypeUtils
import com.luckyba.myapplication.util.StringUtils
import org.jetbrains.annotations.NotNull
import java.io.File

data class Media(
    var path: String? = null,
    var dateModified: Long = -1,
    var mimeType: String = MimeTypeUtils.UNKNOWN_MIME_TYPE,
    var uriString: String? = null,
    var size: Long = -1,
    val selected: Boolean = false
): TimelineItem, CursorHandler<Media> {

    private val sProjection = arrayOf(
        MediaStore.Images.Media.DATA,
        MediaStore.Images.Media.DATE_TAKEN,
        MediaStore.Images.Media.MIME_TYPE,
        MediaStore.Images.Media.SIZE,
        MediaStore.Images.Media.ORIENTATION
    )

    private val CURSOR_POS_DATA: Int =
        ArrayUtils.getIndex(sProjection, MediaStore.Images.Media.DATA)
    private val CURSOR_POS_DATE_TAKEN: Int =
        ArrayUtils.getIndex(sProjection, MediaStore.Images.Media.DATE_TAKEN)
    private val CURSOR_POS_MIME_TYPE: Int =
        ArrayUtils.getIndex(sProjection, MediaStore.Images.Media.MIME_TYPE)
    private val CURSOR_POS_SIZE: Int =
        ArrayUtils.getIndex(sProjection, MediaStore.Images.Media.SIZE)

    private operator fun invoke(path: String?, dateModified: Long) {
        this.path = path
        this.dateModified = dateModified
        mimeType = MimeTypeUtils.getMimeType(path)
    }

    fun Media(file: File) {
        this(file.path, file.lastModified())
        size = file.length()
        mimeType = MimeTypeUtils.getMimeType(path)
    }

    fun Media(path: String?) {
        this(path, -1)
    }

    fun Media(mediaUri: Uri) {
        uriString = mediaUri.toString()
        path = null
        mimeType = MimeTypeUtils.getMimeType(uriString)
    }

     fun Media(@NotNull cur: Cursor) {
        path = cur.getString(CURSOR_POS_DATA)
        dateModified = cur.getLong(CURSOR_POS_DATE_TAKEN)
        mimeType = cur.getString(CURSOR_POS_MIME_TYPE)
        size = cur.getLong(CURSOR_POS_SIZE)
    }

    fun isGif(): Boolean {
        return mimeType.endsWith("gif")
    }

    fun isImage(): Boolean {
        return mimeType.startsWith("image")
    }

    fun isVideo(): Boolean {
        return mimeType.startsWith("video")
    }

    fun getDisplayPath(): String? {
        return if (path != null) path else getUri()?.encodedPath
    }

    fun getFile(): File? {
        if (path != null) {
            val file = File(path)
            if (file.exists()) return file
        }
        return null
    }

    fun getUri(): Uri? {
        return if (uriString != null) Uri.parse(uriString) else Uri.fromFile(File(path))
    }

    fun getName(): String? {
        return StringUtils.getPhotoNameByPath(path)
    }

    override fun getTimelineType(): Int {
        return TYPE_MEDIA
    }

    override fun handle(cur: Cursor): Media {
        val media = Media()
        media.Media(cur)
        return media
    }

}
/*
 * Copyright 2017 Yan Zhenjie.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.luckyba.myapplication.data.model

import android.os.Parcelable
import com.luckyba.myapplication.ui.timeline.data.TimelineItem
import com.luckyba.myapplication.util.MediaType
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AlbumFile(var path: String? = null
                     , var bucketName: String? = null,
                     var mimeType: String? = null,
                     var addDate: Long = 0,
                     var latitude: Float = 0f,
                     var longitude: Float = 0f,
                     var mediaType: MediaType = MediaType.OTHER,
                     var size: Long = 0,
                     var duration: Long = 0,
                     var isChecked: Boolean = false,
                     var isDisable: Boolean = false,
                     var modifiedDate: Long = 0) : Parcelable, Comparable<AlbumFile>, TimelineItem {


    override fun compareTo(albumFile: AlbumFile): Int {
        val time = albumFile.addDate - addDate
        if (time > Int.MAX_VALUE) return Int.MAX_VALUE else if (time < -Int.MAX_VALUE) return -Int.MAX_VALUE
        return time.toInt()
    }

    override fun equals(obj: Any?): Boolean {
        if (obj != null && obj is AlbumFile) {
            val inPath = obj.path
            if (path != null && inPath != null) {
                return path == inPath
            }
        }
        return super.equals(obj)
    }

    override fun hashCode(): Int {
        return if (path != null) path.hashCode() else super.hashCode()
    }

    fun dataToString(): String {
        return ("path " + path + " BucketName " + bucketName + " thumbPath "
                + " " + mimeType + " AddDate " + addDate + " " + mimeType + " " + duration
                + " " + latitude + " " + longitude + " " + mediaType)
    }

    override fun getTimelineType() = TimelineItem.TYPE_MEDIA
}
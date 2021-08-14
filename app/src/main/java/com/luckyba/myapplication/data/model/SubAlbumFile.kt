package com.luckyba.myapplication.data.model

import android.os.Parcelable
import com.luckyba.myapplication.util.MediaType
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SubAlbumFile(
    var path: String? = null,
    var mimeType: String? = null,
    var mediaType: MediaType = MediaType.OTHER,
    var size: Long = 0,
    var duration: Long = 0,
    var modifiedDate: Long = 0
) : Parcelable
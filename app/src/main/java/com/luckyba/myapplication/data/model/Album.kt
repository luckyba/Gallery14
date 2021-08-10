package com.luckyba.myapplication.data.model

import android.database.Cursor
import android.os.Parcel
import android.os.Parcelable
import com.luckyba.myapplication.data.CursorHandler


data class Album(
     var name: String? = null,
     var medias: ArrayList<Media>? = ArrayList(),
     var selected: Boolean = false
):CursorHandler<Album>, Parcelable {

    private operator fun invoke(cur: Cursor) {

    }

    override fun handle(cur: Cursor): Album {
        val album = Album()
        album.invoke(cur)
        return album
    }

    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest!!.writeString(name)
        dest.writeTypedList(medias)
        dest.writeBoolean(selected)
    }

    companion object CREATOR : Parcelable.Creator<Album> {
        override fun createFromParcel(parcel: Parcel): Album {
            return Album(
                parcel.readString(),
                parcel.createTypedArrayList(CREATOR),
                parcel.readBoolean()
            )
        }

        override fun newArray(size: Int): Array<Album?> {
            return arrayOfNulls(size)
        }
    }


}
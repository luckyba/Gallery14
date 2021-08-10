package com.luckyba.myapplication.data

import android.database.Cursor

interface CursorHandler<T> {
    fun handle(cur: Cursor): T

    companion object {
        val projection: Array<String?>?
            get() = arrayOfNulls(0)
    }
}
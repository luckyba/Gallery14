package com.luckyba.myapplication.common

import android.view.View


interface Listener {
    fun onClick(view: View, pos: Int)

    fun onLongClick(view: View, pos: Int)
}

package com.luckyba.myapplication.app

import android.view.View


interface Listener {
    fun onClick(view: View, pos: Int)

    fun onLongClick(view: View, pos: Int)
}

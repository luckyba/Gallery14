package com.luckyba.myapplication.common

interface ActionsListener {

    fun onItemSelected(position: Int)

    fun onSelectMode(selectMode: Boolean)

    fun onSelectionCountChanged(selectionCount: Int, totalCount: Int)
}
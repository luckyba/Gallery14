package com.luckyba.myapplication.common

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

class GridSpacingItemDecoration(
    private val spanCount: Int,
    private val spacing: Float,
    private val includeEdge: Boolean
) : ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view) // item position
        val column = position % spanCount // item column
        if (includeEdge) {
            outRect.left =
                (spacing - column * spacing / spanCount).toInt() // spacing - column * ((1f / spanCount) * spacing)
            outRect.right =
                ((column + 1) * spacing / spanCount).toInt() // (column + 1) * ((1f / spanCount) * spacing)
            if (position < spanCount) { // top edge
                outRect.top = spacing.toInt()
            }
            outRect.bottom = spacing.toInt() // item bottom
        } else {
            outRect.left =
                (column * spacing / spanCount).toInt() // column * ((1f / spanCount) * spacing)
            outRect.right =
                (spacing - (column + 1) * spacing / spanCount).toInt() // spacing - (column + 1) * ((1f /    spanCount) * spacing)
            if (position >= spanCount) {
                outRect.top = spacing.toInt() // item top
            }
        }
    }
}
package com.luckyba.myapplication.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

class GridSpacingItemDecoration(
    private val spanCount: Int,
    private val spacing: Float,
    private val includeEdge: Boolean
) :
    ItemDecoration() {
    companion object {
        fun getItemOffsets(
            gridSpacingItemDecoration: GridSpacingItemDecoration,
            outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
            val position = parent.getChildAdapterPosition(view)
            val column = position % gridSpacingItemDecoration.spanCount
            if (gridSpacingItemDecoration.includeEdge) {
                outRect.left =
                    (gridSpacingItemDecoration.spacing - column * gridSpacingItemDecoration.spacing / gridSpacingItemDecoration.spanCount).toInt()
                outRect.right =
                    ((column + 1) * gridSpacingItemDecoration.spacing / gridSpacingItemDecoration.spanCount).toInt()
                if (position < gridSpacingItemDecoration.spanCount) {
                    outRect.top = gridSpacingItemDecoration.spacing.toInt()
                }
                outRect.bottom = gridSpacingItemDecoration.spacing.toInt()
            } else {
                outRect.left =
                    (column * gridSpacingItemDecoration.spacing / gridSpacingItemDecoration.spanCount).toInt()
                outRect.right =
                    (gridSpacingItemDecoration.spacing - (column + 1) * gridSpacingItemDecoration.spacing / gridSpacingItemDecoration.spanCount).toInt()
                if (position >= gridSpacingItemDecoration.spanCount) {
                    outRect.top = gridSpacingItemDecoration.spacing.toInt()
                }
            }
        }
    }
}
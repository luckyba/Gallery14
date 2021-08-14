package com.luckyba.myapplication.common

import android.view.View

interface EditModeListener {
    /**
     * Propagate Edit Mode switches to listeners.
     *
     * @param editMode Whether we are in Edit Mode or not.
     * @param selected The number of items selected.
     * @param total    The total number of items.
     * @param listener The listener for Toolbar Back Button presses.
     * @param title    The Toolbar's title.
     */
    fun changedEditMode(
        editMode: Boolean,
        selected: Int,
        total: Int,
        listener: View.OnClickListener?,
        title: String?
    )

    /**
     * Propagate the selected item count to listeners.
     *
     * @param count The number of items selected.
     * @param total The total number of items.
     */
    fun onItemsSelected(count: Int, total: Int)
}
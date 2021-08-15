package com.luckyba.myapplication.ui.album

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.internal.ViewUtils
import com.luckyba.myapplication.R
import com.luckyba.myapplication.common.GridSpacingItemDecoration
import com.luckyba.myapplication.common.Listener
import com.luckyba.myapplication.data.model.AlbumFolder
import com.luckyba.myapplication.databinding.DialogFolderBinding

@SuppressLint("RestrictedApi")
class FolderDialog(
    context: Context, private var albumFolders: ArrayList<AlbumFolder>, listener: Listener
): BottomSheetDialog(context) {

    lateinit var binding: DialogFolderBinding
    var adapter: AlbumAdapter
    var recyclerView: RecyclerView? = null
    init {
        setContentView(R.layout.dialog_folder)
        recyclerView = delegate.findViewById(R.id.rv_content_list)
        recyclerView?.layoutManager = GridLayoutManager(context, 1)
        recyclerView?.addItemDecoration(
            GridSpacingItemDecoration(
                1,
                ViewUtils.dpToPx(context, 3),
                true
            )
        )
        recyclerView?.itemAnimator = DefaultItemAnimator()
        recyclerView?.isNestedScrollingEnabled = false
        adapter = AlbumAdapter(albumFolders, listener)
        recyclerView?.adapter = adapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (window != null) {
            val display = context.display
            val metrics = DisplayMetrics()
            display!!.getRealMetrics(metrics)
            val minSize = Math.min(metrics.widthPixels, metrics.heightPixels)
            window!!.setLayout(minSize, -1)
            window!!.statusBarColor = Color.TRANSPARENT
        }
    }
}
package com.luckyba.myapplication.ui.home

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.luckyba.myapplication.app.Listener
import com.luckyba.myapplication.data.model.MediaFileListModel
import com.luckyba.myapplication.databinding.GalleryListItemViewBinding

class GalleryListAdapter(listData: ArrayList<MediaFileListModel>, private val listener: Listener) :
    RecyclerView.Adapter<GalleryListAdapter.ViewHolder>() {
    var listData: ArrayList<MediaFileListModel>? = listData

    fun setData (list: ArrayList<MediaFileListModel>?) {
        listData = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.d("GalleryListAdapter","onCreateViewHolder")
        val inflate = LayoutInflater.from(parent.context)
        val binding = GalleryListItemViewBinding.inflate(inflate, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.mediafile = listData?.get(position)
        var fileName: String? = listData?.get(position)?.fileName
        Log.d("GalleryListAdapter","loadImage $fileName")

        holder.itemView.setOnClickListener { v -> listener.onClick(v, position) }
        holder.itemView.setOnLongClickListener {v->
            listener.onLongClick(v, position)
            false }
    }

    override fun getItemCount(): Int {
        var size: Int = listData?.size ?: 0
        Log.d("GalleryListAdapter","size $size")

        return listData?.size ?: 0
    }

    class ViewHolder(var binding: GalleryListItemViewBinding) :
        RecyclerView.ViewHolder(binding.root)
}
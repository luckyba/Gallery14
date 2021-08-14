package com.luckyba.myapplication.ui.album

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.luckyba.myapplication.common.Listener
import com.luckyba.myapplication.data.model.AlbumFolder
import com.luckyba.myapplication.databinding.AlbumItemBinding

class AlbumAdapter(private var listData: ArrayList<AlbumFolder>?, private var listener: Listener): RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val binding = AlbumItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AlbumViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        holder.itemView.setOnClickListener{v -> listener.onClick(v, position)}
        holder.itemView.setOnLongClickListener{ v -> listener.onLongClick(v, position); false}

        holder.binding.albumFolder = listData?.get(position)

    }

    override fun getItemCount(): Int {
        return listData?.size!!
    }

    class AlbumViewHolder(var binding: AlbumItemBinding): RecyclerView.ViewHolder(binding.root)

    fun setData (data: ArrayList<AlbumFolder>) {
        listData = data
        notifyDataSetChanged()
    }
}
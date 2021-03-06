package com.luckyba.myapplication.ui.album

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.internal.ViewUtils
import com.luckyba.myapplication.common.GridSpacingItemDecoration
import com.luckyba.myapplication.common.Listener
import com.luckyba.myapplication.data.model.AlbumFolder
import com.luckyba.myapplication.data.model.DataHolder
import com.luckyba.myapplication.databinding.FragmentAlbumBinding
import com.luckyba.myapplication.ui.media.MediaActivity
import com.luckyba.myapplication.util.ObservableViewModel
import com.luckyba.myapplication.util.StringUtils
import com.luckyba.myapplication.util.StringUtils.showToast
import com.luckyba.myapplication.viewmodel.GalleryViewModel

class AlbumFragment : Fragment(), Listener {
    lateinit var binding: FragmentAlbumBinding
    lateinit var adapter: AlbumAdapter
    var albumFolder: ArrayList<AlbumFolder> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAlbumBinding.inflate(inflater, container, false)
        val viewmodel: GalleryViewModel by activityViewModels()
        binding.homeModel = viewmodel
        binding.observable = ObservableViewModel()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
    }

    private fun initView () {
        initRecycleView()
        binding.homeModel!!.listData.observe(viewLifecycleOwner
            ,{
                if (it[0].albumFiles.size > 0) {
                    albumFolder = it
                    adapter.setData(it)
                    binding.observable!!.isEmpty.set(false)
                    showToast(context,"Data ${it.size}")
                } else {
                    adapter.setData(ArrayList())
                    binding.observable!!.isEmpty.set(true)
                }
            })

    }

    @SuppressLint("RestrictedApi")
    fun initRecycleView() {
        val recycleView: RecyclerView = binding.recyclerViewImagesList
        recycleView.layoutManager = GridLayoutManager(context, 1)
        recycleView.addItemDecoration(GridSpacingItemDecoration(1, ViewUtils.dpToPx(requireContext(), 3), true))
        recycleView.itemAnimator = DefaultItemAnimator()
        recycleView.isNestedScrollingEnabled = false
        adapter = AlbumAdapter(ArrayList(), this)
        recycleView.adapter = adapter
    }

    override fun onClick(view: View, position: Int) {
        showToast(context, "onClick pos $position")

        /**
         * using singletons object to save data
         */
        DataHolder.albums = albumFolder
        val intent = Intent(context, MediaActivity::class.java)
        intent.putExtra(StringUtils.EXTRA_ARGS_POSITION, position)
        requireActivity().startActivity(intent)
    }

    override fun onLongClick(view: View, pos: Int) {
        showToast(context, "onLongClick pos $pos")
    }
}
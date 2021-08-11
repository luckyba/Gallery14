package com.luckyba.myapplication.ui.album

import android.annotation.SuppressLint
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
import com.luckyba.myapplication.app.Listener
import com.luckyba.myapplication.data.model.AlbumFile
import com.luckyba.myapplication.data.model.AlbumFolder
import com.luckyba.myapplication.databinding.FragmentAlbumBinding
import com.luckyba.myapplication.viewmodel.HomeViewModel
import com.luckyba.myapplication.util.GridSpacingItemDecoration
import com.luckyba.myapplication.util.ObservableViewModel
import com.luckyba.myapplication.util.StringUtils.showToast

class AlbumFragment : Fragment(), Listener {
    lateinit var binding: FragmentAlbumBinding
    lateinit var adapter: AlbumAdapter
    var listData: ArrayList<AlbumFolder> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAlbumBinding.inflate(inflater, container, false)
        val viewmodel: HomeViewModel by activityViewModels()
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
                adapter.setData(it)
                binding.observable!!.isEmpty.set(it.size == 0)
                showToast(context,"Data ${it.size}")})
    }

    @SuppressLint("RestrictedApi")
    fun initRecycleView() {
        val recycleView: RecyclerView = binding.recyclerViewImagesList
        recycleView.layoutManager = GridLayoutManager(context, 1)
        recycleView.addItemDecoration(GridSpacingItemDecoration(1, ViewUtils.dpToPx(requireContext(), 3), true))
        recycleView.itemAnimator = DefaultItemAnimator()
        recycleView.isNestedScrollingEnabled = false
        adapter = AlbumAdapter(listData, this)
        recycleView.adapter = adapter
    }

    override fun onClick(view: View, pos: Int) {
        showToast(context, "onClick pos $pos")
    }

    override fun onLongClick(view: View, pos: Int) {
        showToast(context, "onLongClick pos $pos")
    }
}
package com.luckyba.myapplication.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.internal.ViewUtils
import com.luckyba.myapplication.R
import com.luckyba.myapplication.app.Listener
import com.luckyba.myapplication.data.model.AlbumFile
import com.luckyba.myapplication.databinding.FragmentHomeBinding
import com.luckyba.myapplication.util.GridSpacingItemDecoration
import com.luckyba.myapplication.util.ObservableViewModel
import com.luckyba.myapplication.util.StringUtils.showToast
import com.luckyba.myapplication.viewmodel.HomeViewModel


class HomeFragment : Fragment(), Listener{
    lateinit var binding: FragmentHomeBinding
    lateinit var adapter: GalleryListAdapter
    var listData: ArrayList<AlbumFile> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_home, container, false
        );
        val viewModel: HomeViewModel by activityViewModels()
        binding.homeModel= viewModel
        binding.observable = ObservableViewModel()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
//        binding.homeModel!!.getAll()
    }

    private fun initView () {
        initRecycleView()

        binding.homeModel!!.listData.observe(viewLifecycleOwner
            ,{
                adapter.setData(it[0].albumFiles)
                binding.observable!!.isEmpty.set(it[0].albumFiles.size == 0)
                showToast(context,"Data ${it[0].albumFiles.size}")})
    }

    @SuppressLint("RestrictedApi")
    fun initRecycleView() {
        val recycleView: RecyclerView = binding.recyclerViewImagesList
        recycleView.layoutManager = GridLayoutManager(context, 3)
        recycleView.addItemDecoration(GridSpacingItemDecoration(3, ViewUtils.dpToPx(requireContext(), 3), true))
        recycleView.itemAnimator = DefaultItemAnimator()
        recycleView.isNestedScrollingEnabled = false
        adapter = GalleryListAdapter(listData, this)
        adapter.setHasStableIds(true)
        recycleView.adapter = adapter
    }


    override fun onClick(view: View, pos: Int) {
        showToast(context,"onClick pos $pos")
    }

    override fun onLongClick(view: View, pos: Int) {
        showToast(context,"onLongClick pos $pos")
    }
}
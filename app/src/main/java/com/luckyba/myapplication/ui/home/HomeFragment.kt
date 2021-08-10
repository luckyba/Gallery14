package com.luckyba.myapplication.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.internal.ViewUtils
import com.luckyba.myapplication.R
import com.luckyba.myapplication.app.Listener
import com.luckyba.myapplication.data.model.MediaFileListModel
import com.luckyba.myapplication.databinding.FragmentHomeBinding
import com.luckyba.myapplication.util.GridSpacingItemDecoration


class HomeFragment : Fragment(), Listener{
    lateinit var binding: FragmentHomeBinding
    lateinit var adapter: GalleryListAdapter
    var listData: ArrayList<MediaFileListModel> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_home, container, false
        );
        binding.homeModel =
                ViewModelProvider(this).get(HomeViewModel::class.java)
        binding.observable = HomeViewModel.Observable()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
        binding.homeModel!!.getAll()
    }

    private fun initView () {
        initRecycleView()

        binding.homeModel!!.listData.observe(viewLifecycleOwner
            ,{
//                adapter.setData(it)
                binding.observable!!.isEmpty.set(it.size == 0)
            toast("Data ${it.size}")})
    }

    @SuppressLint("RestrictedApi")
    fun initRecycleView() {
        val recycleView: RecyclerView = binding.recyclerViewImagesList
        recycleView.layoutManager = GridLayoutManager(context, 3)
        recycleView.addItemDecoration(GridSpacingItemDecoration(3, ViewUtils.dpToPx(requireContext(), 4), true))
        recycleView.itemAnimator = DefaultItemAnimator()
        recycleView.isNestedScrollingEnabled = false
        adapter = GalleryListAdapter(listData, this)
        recycleView.adapter = adapter
    }

    fun toast(message: CharSequence) = Toast.makeText(context, message, Toast.LENGTH_LONG).show()

    override fun onClick(view: View, pos: Int) {
        toast("onClick pos $pos")
    }

    override fun onLongClick(view: View, pos: Int) {
        toast("onLongClick pos $pos")
    }
}
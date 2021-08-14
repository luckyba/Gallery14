package com.luckyba.myapplication.ui.media.fragment

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.luckyba.myapplication.R
import com.luckyba.myapplication.databinding.FragmentScreenSlidePageBinding


class ScreenSlidePageFragment(val path: String?) : Fragment() {

    lateinit var binding: FragmentScreenSlidePageBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.setContentView(activity as Activity, R.layout.fragment_screen_slide_page)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.path = path
    }
}
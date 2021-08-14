package com.luckyba.myapplication.ui.media.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.databinding.DataBindingUtil
import com.luckyba.myapplication.R
import com.luckyba.myapplication.common.BaseMediaFragment
import com.luckyba.myapplication.data.model.AlbumFile
import com.luckyba.myapplication.databinding.FragmentPhotoBinding

/**
 * A Media Fragment for showing an Image (static)
 */
class ImageFragment : BaseMediaFragment() {
    lateinit var binding: FragmentPhotoBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_photo, container, false)
        setHasOptionsMenu(false)
        return binding.root
    }

    override fun onViewCreated(@NonNull view: View, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.path = media.path
    }

    override fun onDestroyView() {

        super.onDestroyView()
    }

    companion object {
        @JvmStatic
        @NonNull
        fun newInstance(@NonNull media: AlbumFile): ImageFragment {
            return newInstance(ImageFragment(), media)
        }
    }
}
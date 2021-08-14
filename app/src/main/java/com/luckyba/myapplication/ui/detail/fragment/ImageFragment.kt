package com.luckyba.myapplication.ui.detail.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ScaleGestureDetector
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

    private var scaleGestureDetector: ScaleGestureDetector? = null
    private var mScaleFactor = 1.0f

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_photo, container, false)

        binding.root.setOnTouchListener { v, event ->
            scaleGestureDetector!!.onTouchEvent(event)
            true }

        return binding.root
    }

    override fun onViewCreated(@NonNull view: View, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.path = media.path
        scaleGestureDetector = ScaleGestureDetector(context, ScaleListener())
    }

    companion object {
        @JvmStatic
        @NonNull
        fun newInstance(@NonNull media: AlbumFile): ImageFragment {
            return newInstance(ImageFragment(), media)
        }
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(scaleGestureDetector: ScaleGestureDetector): Boolean {
            mScaleFactor *= scaleGestureDetector.scaleFactor
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 10.0f))
            return true
        }

        override fun onScaleEnd(detector: ScaleGestureDetector?) {
            binding.icon.scaleX = mScaleFactor
            binding.icon.scaleY = mScaleFactor
        }
    }
}
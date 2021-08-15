package com.luckyba.myapplication

import android.content.ContentResolver
import android.os.*
import android.provider.MediaStore
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.luckyba.myapplication.app.GalleryApplication
import com.luckyba.myapplication.common.BaseActivity
import com.luckyba.myapplication.common.EditModeListener
import com.luckyba.myapplication.data.model.DataObserver
import com.luckyba.myapplication.data.model.DataObserver.SCAN_DATA_CALLBACK
import com.luckyba.myapplication.databinding.ActivityMainBinding
import com.luckyba.myapplication.util.StringUtils
import com.luckyba.myapplication.viewmodel.GalleryViewModel

class MainActivity : BaseActivity("MainActivity"), EditModeListener {

    lateinit var viewModel: GalleryViewModel
    lateinit var binding: ActivityMainBinding
    lateinit var observer: DataObserver
    lateinit var contentResolve: ContentResolver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(binding.mainToolbar)

        viewModel = ViewModelProvider(this).get(GalleryViewModel::class.java)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_favorite, R.id.navigation_album
            )
        )

        contentResolve = GalleryApplication.getInstance()
            .contentResolver
        observer = DataObserver(mHandler)
        listenDataChange()

        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)

        binding.progressCircular.isVisible = true
        binding.animate = AnimationUtils.loadAnimation(this, R.anim.roate)
        viewModel.getAll()

        viewModel.listData.observe(this, {
            binding.progressCircular.isVisible = false
        })

    }

    private fun listenDataChange() {

        contentResolve.registerContentObserver(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true,
            DataObserver(mHandler)
        )
        contentResolve
            .registerContentObserver(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI, true,
                DataObserver(mHandler)
            )
    }

    private val mHandler = Handler(Looper.getMainLooper()) { msg: Message ->
        if (msg.what == SCAN_DATA_CALLBACK) {
            Toast.makeText(this, " " + msg.obj.toString(), Toast.LENGTH_SHORT).show()
            viewModel.getAll()
        }
        true
    }

    override fun onDestroy() {
        super.onDestroy()
        contentResolve.unregisterContentObserver(observer)
        StringUtils.showToast(this, " MainActivity onDestroy")
    }

    override fun changedEditMode(
        editMode: Boolean,
        selected: Int,
        total: Int,
        listener: View.OnClickListener?,
        title: String?
    ) {
//        StringUtils.showToast(
//            this, " changedEditMode editmode $editMode" +
//                    " selected $selected total $total + title $title"
//        )
        if (selected > 0)
            updateToolbar(" $selected of $total ", editMode, listener)
        else {
            updateToolbar(title!!, editMode, listener)
        }
    }

    override fun onItemsSelected(count: Int, total: Int) {
//        StringUtils.showToast(this, " onItemsSelected count $count total $total ")
        if (count > 0) {
            binding.mainToolbar.title = " $count of $total "
        } else {

        }
    }

    private fun updateToolbar(
        title: String,
        editMode: Boolean,
        onClickListener: View.OnClickListener?
    ) {
        binding.mainToolbar.title = title
        if (editMode) {
            binding.mainToolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
            binding.mainToolbar.setNavigationOnClickListener(onClickListener)
        } else {
            binding.mainToolbar.navigationIcon = null
            binding.mainToolbar.setNavigationOnClickListener(null)
        }

    }

}
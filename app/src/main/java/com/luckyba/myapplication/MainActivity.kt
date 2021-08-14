package com.luckyba.myapplication

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.luckyba.myapplication.common.EditModeListener
import com.luckyba.myapplication.databinding.ActivityMainBinding
import com.luckyba.myapplication.util.StringUtils
import com.luckyba.myapplication.viewmodel.GalleryViewModel

class MainActivity : AppCompatActivity(), EditModeListener {

    lateinit var viewModel: GalleryViewModel
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(binding.mainToolbar)

        viewModel =  ViewModelProvider(this).get(GalleryViewModel::class.java)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_favorite, R.id.navigation_album
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)

        viewModel.getAll()
    }

    override fun changedEditMode(
        editMode: Boolean,
        selected: Int,
        total: Int,
        listener: View.OnClickListener?,
        title: String?
    ) {
        StringUtils.showToast(
            this, " changedEditMode editmode $editMode" +
                    " selected $selected total $total + title $title"
        )
        if (selected > 0)
            updateToolbar(" $selected of $total ", editMode,  listener)
        else {
            updateToolbar(title!!, editMode, listener)
        }
    }

    override fun onItemsSelected(count: Int, total: Int) {
        StringUtils.showToast(this, " onItemsSelected count $count total $total ")
        if (count > 0) {
            binding.mainToolbar.title = " $count of $total "
        } else {

        }
    }

    private fun updateToolbar(title: String, editMode: Boolean, onClickListener: View.OnClickListener?) {
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
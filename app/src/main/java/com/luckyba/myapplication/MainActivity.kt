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
import com.luckyba.myapplication.app.EditModeListener
import com.luckyba.myapplication.app.GalleryApplication
import com.luckyba.myapplication.databinding.ActivityMainBinding
import com.luckyba.myapplication.util.StringUtils
import com.luckyba.myapplication.viewmodel.GalleryViewModel

class MainActivity : AppCompatActivity(), EditModeListener  {

    lateinit var viewModel: GalleryViewModel
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(binding.mainToolbar)

        binding.observable = GalleryApplication.getObservableViewMode()
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
        StringUtils.showToast(this, " changedEditMode ")
    }

    override fun onItemsSelected(count: Int, total: Int) {
        var arr = IntArray(2)
        arr[0] = count
        arr[1] = total
        binding.editMode = true
        binding.observable!!.changeTitleTb.set(arr)
    }
}
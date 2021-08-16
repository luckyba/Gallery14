package com.luckyba.myapplication.ui.media

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import androidx.annotation.IdRes
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions.fitCenterTransform
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.luckyba.myapplication.R
import com.luckyba.myapplication.common.ActionsListener
import com.luckyba.myapplication.common.BaseActivity
import com.luckyba.myapplication.common.EditModeListener
import com.luckyba.myapplication.common.Listener
import com.luckyba.myapplication.data.model.AlbumFile
import com.luckyba.myapplication.data.model.AlbumFolder
import com.luckyba.myapplication.data.model.DataHolder
import com.luckyba.myapplication.data.sort.MediaComparators
import com.luckyba.myapplication.data.sort.SortingMode
import com.luckyba.myapplication.data.sort.SortingOrder
import com.luckyba.myapplication.databinding.ActivityMediaBinding
import com.luckyba.myapplication.ui.album.FolderDialog
import com.luckyba.myapplication.ui.detail.DetailActivity
import com.luckyba.myapplication.ui.timeline.GroupingMode
import com.luckyba.myapplication.ui.timeline.TimelineAdapter
import com.luckyba.myapplication.util.*
import com.luckyba.myapplication.viewmodel.GalleryViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.ArrayList


class MediaActivity : BaseActivity("MediaActivity"), ActionsListener, EditModeListener, Listener {

    lateinit var binding: ActivityMediaBinding
    lateinit var adapter: TimelineAdapter
    lateinit var viewModel: GalleryViewModel
    private lateinit var recycleView: RecyclerView
    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var dialog: FolderDialog

    lateinit var albumFiles: ArrayList<AlbumFile>
    lateinit var albumFolder: ArrayList<AlbumFolder>
    var menuAction: String? = null
    var position: Int = 0
    var title: String? = null

    private lateinit var groupingMode: GroupingMode

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_media)
        viewModel = ViewModelProvider(this).get(GalleryViewModel::class.java)
//        loadData()

        binding.observable = ObservableViewModel()
        recycleView = binding.recyclerViewImagesList
        binding.animate = AnimationUtils.loadAnimation(this, R.anim.roate)

        position = intent.getIntExtra(StringUtils.EXTRA_ARGS_POSITION, 0)
        albumFolder = DataHolder.listData
        albumFiles = albumFolder[position].albumFiles

        initView()

    }

    private val timelineGridSize: Int
        get() = if (DeviceUtils.isPortrait(resources)) DeviceUtils.TIMELINE_ITEMS_PORTRAIT
        else DeviceUtils.TIMELINE_ITEMS_LANDSCAPE

    private fun initView() {
        binding.mainToolbar.title = title
        setSupportActionBar(binding.mainToolbar)
        groupingMode = GroupingMode.DAY
        initRecycleView()

        viewModel.listData.observe(this, {
            binding.progressCircular.isVisible = false
            albumFolder = it
            DataHolder.albums = it
            val albumFiles = it[position].albumFiles
            Collections.sort(
                albumFiles, MediaComparators.getComparator(
                    SortingMode.DATE,
                    SortingOrder.DESCENDING
                )
            )
            adapter.setData(albumFiles)
            binding.observable!!.isEmpty.set(albumFiles.size == 0)
            StringUtils.showToast(this, "Data ${albumFiles.size}")
        })

        viewModel.isDataChanged.observe(this, ::dataChanged)
    }

    private fun dataChanged(isChanged: Boolean) {
        binding.progressCircular.isVisible = false
        if (isChanged) {
            if (menuAction == GalleryUtil.ACTION_DELETE || menuAction == GalleryUtil.ACTION_MOVE) {
                if(getSelectedCount() == getTotalCount()) {
                    finish()
                    return
                }
            }
            GlobalScope.launch(Dispatchers.Main) {
                viewModel.getAllData()// back on UI thread
            }
//            viewModel.getAll()
            exitContextMenu()

        }
    }

    @SuppressLint("RestrictedApi")
    fun initRecycleView() {
        val decorator = TimelineAdapter.TimelineItemDecorator(
            this,
            R.dimen.timeline_decorator_spacing
        )
        gridLayoutManager = GridLayoutManager(this, timelineGridSize)

        recycleView.layoutManager = gridLayoutManager
        recycleView.addItemDecoration(decorator)

        recycleView.itemAnimator = DefaultItemAnimator()
        recycleView.isNestedScrollingEnabled = false
        adapter = TimelineAdapter(ArrayList(), this)
        adapter.setTimelineGridSize(timelineGridSize)
        adapter.setGroupingMode(groupingMode)
        adapter.setGridLayoutManager(gridLayoutManager)
        // multi item should keep false
//        adapter.setHasStableIds(true)
        recycleView.adapter = adapter
        adapter.setData(albumFiles)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_timeline, menu)
        menuMediaMode(menu)
        return true
    }

    private fun menuMediaMode(menu: Menu) {
        menu.findItem(R.id.timeline_menu_move).isVisible = true
        menu.findItem(R.id.timeline_menu_copy).isVisible = true
        menu.findItem(R.id.timeline_menu_filter).isVisible = false
        menu.findItem(R.id.timeline_menu_grouping).isVisible = false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        getGroupingMode(item.itemId)?.let {
            // Handling Grouping Mode selections
            groupingMode = it
            item.isChecked = true
            adapter.setGroupingMode(it)
            return true
        }

        getFilterMode(item.itemId)?.let {
            // Handling Filter Mode selections
            item.isChecked = true
            adapter.setData(binding.homeModel!!.getDataFilterBy(it))
            return true
        }

        return when (item.itemId) {
            R.id.timeline_share -> {
                StringUtils.showToast(this, getString(R.string.comming_soon))
                false
//                if (getSelectedCount() > 0) {
//                    shareImages(convertUrlToUri(adapter.getPathSelectedItem()), "Share")
//                    exitContextMenu()
//                }
//                true
            }

            R.id.timeline_menu_delete -> {
                binding.progressCircular.isVisible = true
                menuAction = GalleryUtil.ACTION_DELETE
                StringUtils.showToast(this, "Delete")
                if (adapter.getSelectedCount() == 0) {
                    StringUtils.showToast(
                        this,
                        getString(R.string.no_item_selected_cant_delete)
                    ); false
                } else {
                    GlobalScope.launch(Dispatchers.Main) {
                        viewModel.deleteListFile(adapter.getPathSelectedItem())
                    }
//                    viewModel.deleteListFile(adapter.getPathSelectedItem())
                    true
                }
            }

            R.id.timeline_menu_select_all -> {
                StringUtils.showToast(this, "Select All")
                if (adapter.getSelectedCount() == adapter.getMediaCount()) {
                    adapter.deSelectAll()
                    item.title = "Select All"
                } else {
                    adapter.selectAll()
                    item.title = "Deselect All"
                    changedEditMode(
                        editMode(),
                        getSelectedCount(),
                        getTotalCount(),
                        getToolbarButtonListener(editMode()),
                        getToolbarTitle()
                    )
                }
                true
            }

            R.id.timeline_menu_copy -> {
                StringUtils.showToast(this, "Copy ")
                menuAction = GalleryUtil.ACTION_COPY
                if (getSelectedCount() > 0) {
                    dialog = FolderDialog(this, albumFolder, this)
                    if (!dialog.isShowing) {
                        dialog.show()
                    }
                }
                true
            }

            R.id.timeline_menu_move -> {
                menuAction = GalleryUtil.ACTION_MOVE
                StringUtils.showToast(this, "Copy ")
                if (getSelectedCount() > 0) {
                    dialog = FolderDialog(this, albumFolder, this)
                    if (!dialog.isShowing) {
                        dialog.show()
                    }
                }

                true
            }

            else -> false

        }
    }

    override fun onClick(view: View, pos: Int) {
        StringUtils.showToast(this, "onClick album pos $pos")
        dialog.dismiss()
        binding.progressCircular.isVisible = true
        when(menuAction) {
            GalleryUtil.ACTION_COPY -> {
                GlobalScope.launch(Dispatchers.Main) {
                    albumFolder[pos].albumFiles[0].path?.let {
                        viewModel.copyFile(
                            adapter.getPathSelectedItem(),
                            StringUtils.getBucketPathByImagePath(it)
                        )
                    }
                }
//                albumFolder[pos].albumFiles[0].path?.let {
//                    viewModel.copyFile(
//                        adapter.getPathSelectedItem(),
//                        StringUtils.getBucketPathByImagePath(it)
//                    )
//                }
            }
            GalleryUtil.ACTION_MOVE -> {
                GlobalScope.launch(Dispatchers.Main) {
                    albumFolder[pos].albumFiles[0].path?.let {
                        viewModel.moveFile(
                            adapter.getPathSelectedItem(),
                            StringUtils.getBucketPathByImagePath(it)
                        )
                    }
                }
//                albumFolder[pos].albumFiles[0].path?.let {
//                    viewModel.moveFile(
//                        adapter.getPathSelectedItem(),
//                        StringUtils.getBucketPathByImagePath(it)
//                    )
//                }
            }
        }
    }

    override fun onLongClick(view: View, pos: Int) {
        StringUtils.showToast(this, "onLongClick album pos $pos")
        dialog.dismiss()
    }

    private fun getGroupingMode(@IdRes menuId: Int) = when (menuId) {
        R.id.timeline_grouping_day -> GroupingMode.DAY
        R.id.timeline_grouping_week -> GroupingMode.WEEK
        R.id.timeline_grouping_month -> GroupingMode.MONTH
        R.id.timeline_grouping_year -> GroupingMode.YEAR
        else -> null
    }

    @IdRes
    private fun getMenuForGroupingMode(groupingMode: GroupingMode) = when (groupingMode) {
        GroupingMode.DAY -> R.id.timeline_grouping_day
        GroupingMode.WEEK -> R.id.timeline_grouping_week
        GroupingMode.MONTH -> R.id.timeline_grouping_month
        GroupingMode.YEAR -> R.id.timeline_grouping_year
    }

    private fun getFilterMode(@IdRes menuId: Int) = when (menuId) {
        R.id.all_media_filter -> FilterMode.ALL
        R.id.video_media_filter -> FilterMode.VIDEO
        R.id.image_media_filter -> FilterMode.IMAGES
        R.id.other_media_filter -> FilterMode.OTHER
        else -> null
    }

    @IdRes
    private fun getMenuForFilterMode(filterMode: FilterMode) = when (filterMode) {
        FilterMode.ALL -> R.id.all_media_filter
        FilterMode.IMAGES -> R.id.image_media_filter
        FilterMode.OTHER -> R.id.other_media_filter
        FilterMode.VIDEO -> R.id.video_media_filter
        FilterMode.NO_VIDEO -> R.id.all_media_filter
    }

    override fun onItemSelected(position: Int) {
//        StringUtils.showToast(this, " click selected item $position")
        val intent = Intent(this, DetailActivity::class.java)
        val bundle = Bundle()
        albumFiles = albumFolder[this.position].albumFiles
        var albumFolder = AlbumFolder(name = albumFiles[0].bucketName, albumFiles = albumFiles)
        bundle.putParcelable(StringUtils.EXTRA_ARGS_ALBUM, albumFolder)

        if (!GalleryUtil.isMaxBundle(bundle)) {
            intent.action = StringUtils.ACTION_OPEN_ALBUM
            intent.putExtra(StringUtils.EXTRA_ARGS_ALBUM, albumFolder)
            intent.putExtra(StringUtils.EXTRA_ARGS_POSITION, position)
            startActivity(intent)
        } else {
            intent.action = StringUtils.ACTION_OPEN_ALBUM_LAZY
            intent.putExtra(StringUtils.EXTRA_ARGS_MEDIA, albumFolder.albumFiles[position])
            startActivity(intent)
        }


    }

    /**
     * handle when bundle data is large
     */
    fun convertToPath(list: ArrayList<AlbumFile>): ArrayList<String> {
        var listPath: ArrayList<String> = ArrayList()
        for (album in list) {
            album.path?.let { listPath.add(it) }
        }
        return listPath
    }

    override fun onSelectMode(selectMode: Boolean) {
        changedEditMode(
            editMode(),
            getSelectedCount(),
            getTotalCount(),
            getToolbarButtonListener(editMode()),
            getToolbarTitle()
        )
    }

    override fun onSelectionCountChanged(selectionCount: Int, totalCount: Int) {
        onItemsSelected(selectionCount, totalCount)
    }

    private fun getSelectedCount() = adapter.getSelectedCount()

    private fun getTotalCount() = adapter.getMediaCount()

    private fun editMode() = adapter.isSelecting()

    private fun clearSelected() = adapter.clearSelected()

    private fun getToolbarButtonListener(editMode: Boolean) = when (editMode) {
        true -> View.OnClickListener { exitContextMenu() }
        false -> null
    }

    private fun getToolbarTitle() = when (editMode()) {
        true -> null
        false -> title
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

        invalidateOptionsMenu()
    }

    private fun exitContextMenu() {
        clearSelected()
        changedEditMode(
            editMode(),
            getSelectedCount(),
            getTotalCount(),
            getToolbarButtonListener(editMode()),
            getToolbarTitle()
        )
    }

    private fun convertUrlToUri(filesPath: MutableSet<String>?): ArrayList<Uri> {
        var listOfUris: ArrayList<Uri> = ArrayList()
        if (filesPath != null) {
            for (path in filesPath) {
                Glide.with(this)
                    .asBitmap()
                    .load(path)
                    .into(object : SimpleTarget<Bitmap>(250, 250) {
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?
                        ) {
                            resource.compress(
                                Bitmap.CompressFormat.PNG,
                                100,
                                ByteArrayOutputStream()
                            )
                            listOfUris.add(
                                Uri.parse(
                                    MediaStore.Images.Media.insertImage(
                                        contentResolver,
                                        resource,
                                        "",
                                        null
                                    )
                                )
                            )
                        }
                    })
            }
        }
        return listOfUris
    }

    private fun shareImages(listOfUris: ArrayList<Uri>, comment: String?) {
        if (listOfUris.isNotEmpty()) {
            val shareIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND_MULTIPLE
                type = "*/*"
                putParcelableArrayListExtra(Intent.EXTRA_STREAM, listOfUris)
                comment?.let {putExtra(Intent.EXTRA_TEXT, it)  }
            }
            try {
                startActivity(Intent.createChooser(shareIntent, "Share via"))
                listOfUris.clear()
            } catch (e: ActivityNotFoundException) {
                StringUtils.showToast(this, "No App Available")
            }
        }

    }
}
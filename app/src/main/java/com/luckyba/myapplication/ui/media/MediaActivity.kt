package com.luckyba.myapplication.ui.media

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.luckyba.myapplication.R
import com.luckyba.myapplication.common.ActionsListener
import com.luckyba.myapplication.common.EditModeListener
import com.luckyba.myapplication.data.model.AlbumFile
import com.luckyba.myapplication.data.model.AlbumFolder
import com.luckyba.myapplication.data.sort.MediaComparators
import com.luckyba.myapplication.data.sort.SortingMode
import com.luckyba.myapplication.data.sort.SortingOrder
import com.luckyba.myapplication.databinding.ActivityMediaBinding
import com.luckyba.myapplication.ui.detail.DetailActivity
import com.luckyba.myapplication.ui.timeline.GroupingMode
import com.luckyba.myapplication.ui.timeline.TimelineAdapter
import com.luckyba.myapplication.util.*
import com.luckyba.myapplication.viewmodel.GalleryViewModel
import java.util.*
import kotlin.collections.ArrayList

class MediaActivity : AppCompatActivity(), ActionsListener, EditModeListener {

    lateinit var binding: ActivityMediaBinding
    lateinit var adapter: TimelineAdapter
    lateinit var viewModel: GalleryViewModel
    private lateinit var recycleView: RecyclerView
    private lateinit var gridLayoutManager: GridLayoutManager

    lateinit var albumFiles: ArrayList<AlbumFile>
    var position: Int = 0

    private lateinit var groupingMode: GroupingMode

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadData()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_media)
        viewModel = ViewModelProvider(this).get(GalleryViewModel::class.java)
        binding.observable = ObservableViewModel()
        recycleView = binding.recyclerViewImagesList
        initView()

    }

    private fun loadData() {
        position = intent.getIntExtra(StringUtils.EXTRA_ARGS_POSITION, 0)
        if (intent.action == StringUtils.ACTION_OPEN_ALBUM) loadAlbumFile()
        else loadLazyAlbumFile()
    }

    private fun loadAlbumFile() {
        albumFiles = (intent.getParcelableExtra<AlbumFolder>(StringUtils.EXTRA_ARGS_ALBUM) as AlbumFolder).albumFiles
    }

    private fun loadLazyAlbumFile () {
        viewModel.getAll()
    }

    private val timelineGridSize: Int
        get() = if (DeviceUtils.isPortrait(resources)) DeviceUtils.TIMELINE_ITEMS_PORTRAIT
        else DeviceUtils.TIMELINE_ITEMS_LANDSCAPE


    private fun initView() {
        groupingMode = GroupingMode.DAY
        initRecycleView()
        viewModel.listData.observe(this, {
            val albumFiles = it[position].albumFiles
            Collections.sort(
                albumFiles, MediaComparators.getComparator(
                    SortingMode.DATE,
                    SortingOrder.DESCENDING
                )
            )
            adapter.setData(albumFiles)
            binding.mainToolbar.title = albumFiles[0].bucketName
            binding.observable!!.isEmpty.set(albumFiles.size == 0)
            StringUtils.showToast(this, "Data ${albumFiles.size}")
        })

        viewModel.isDataChanged.observe(this, ::dataChanged)
    }

    private fun dataChanged(isChanged: Boolean) {
        if (isChanged) exitContextMenu()
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
        binding.mainToolbar.title = albumFiles[0].bucketName
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_timeline, menu)
        return true
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

            R.id.timeline_menu_delete -> {
                StringUtils.showToast(this, "Delete")
                if (adapter.getSelectedCount() == 0) {
                    StringUtils.showToast(
                        this,
                        getString(R.string.no_item_selected_cant_delete)
                    ); false
                } else {
                    binding.homeModel!!.deleteListFile(adapter.getPathSelectedItem());
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
                    changedEditMode(
                        editMode(),
                        getSelectedCount(),
                        getTotalCount(),
                        getToolbarButtonListener(editMode()),
                        getToolbarTitle()
                    )
                    item.title = "Deselect All"
                }
                true
            }

            else -> false

        }
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
        StringUtils.showToast(this, " click selected item $position")
        val intent = Intent(this, DetailActivity::class.java)
        val bundle = Bundle()
        var albumFolder = AlbumFolder(name = albumFiles[0].bucketName, albumFiles = albumFiles)
        bundle.putParcelable(StringUtils.EXTRA_ARGS_ALBUM, albumFolder)

        if (!GalleryUtil.isMaxBundle(bundle)) {
            intent.action = StringUtils.ACTION_OPEN_ALBUM
            intent.putExtra(StringUtils.EXTRA_ARGS_ALBUM, albumFolder)
            intent.putExtra(StringUtils.EXTRA_ARGS_POSITION, position)
            startActivity(intent)
        } else {
            intent.action = StringUtils.ACTION_OPEN_ALBUM_LAYZY
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
        false -> albumFiles[0].bucketName
    }

    override fun onItemsSelected(count: Int, total: Int) {
        StringUtils.showToast(this, " onItemsSelected count $count total $total ")
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
        StringUtils.showToast(
            this, " changedEditMode editmode $editMode" +
                    " selected $selected total $total + title $title"
        )
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

}
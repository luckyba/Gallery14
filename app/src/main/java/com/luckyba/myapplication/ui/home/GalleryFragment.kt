package com.luckyba.myapplication.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.annotation.IdRes
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.luckyba.myapplication.R
import com.luckyba.myapplication.app.ActionsListener
import com.luckyba.myapplication.app.BaseMediaGridFragment
import com.luckyba.myapplication.data.model.AlbumFile
import com.luckyba.myapplication.data.sort.MediaComparators
import com.luckyba.myapplication.data.sort.SortingMode
import com.luckyba.myapplication.data.sort.SortingOrder
import com.luckyba.myapplication.databinding.FragmentHomeBinding
import com.luckyba.myapplication.ui.media.MediaActivity
import com.luckyba.myapplication.ui.timeline.GroupingMode
import com.luckyba.myapplication.util.DeviceUtils
import com.luckyba.myapplication.util.DeviceUtils.TIMELINE_ITEMS_LANDSCAPE
import com.luckyba.myapplication.util.DeviceUtils.TIMELINE_ITEMS_PORTRAIT
import com.luckyba.myapplication.util.FilterMode
import com.luckyba.myapplication.util.ObservableViewModel
import com.luckyba.myapplication.util.StringUtils.ACTION_OPEN_ALBUM
import com.luckyba.myapplication.util.StringUtils.EXTRA_ARGS_ALBUM
import com.luckyba.myapplication.util.StringUtils.EXTRA_ARGS_POSITION
import com.luckyba.myapplication.util.StringUtils.showToast
import com.luckyba.myapplication.viewmodel.GalleryViewModel
import java.util.*
import kotlin.collections.ArrayList

class GalleryFragment : BaseMediaGridFragment(), ActionsListener{
    lateinit var binding: FragmentHomeBinding
    lateinit var adapter: GalleryListAdapter
    private lateinit var recycleView: RecyclerView
    private lateinit var gridLayoutManager: GridLayoutManager

    var listData: ArrayList<AlbumFile> = ArrayList()

    private lateinit var groupingMode: GroupingMode

    private val timelineGridSize: Int
        get() = if (DeviceUtils.isPortrait(resources)) TIMELINE_ITEMS_PORTRAIT
        else TIMELINE_ITEMS_LANDSCAPE

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_home, container, false
        );
        val viewModel: GalleryViewModel by activityViewModels()
        binding.homeModel= viewModel
        binding.observable = ObservableViewModel()
        recycleView = binding.recyclerViewImagesList
        initView()
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        groupingMode = GroupingMode.DAY

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }

    private fun initView () {
        initRecycleView()
        setHasOptionsMenu(true)
        binding.homeModel!!.listData.observe(viewLifecycleOwner, {
            val albumFiles = it[0].albumFiles
            Collections.sort(albumFiles, MediaComparators.getComparator(SortingMode.DATE, SortingOrder.DESCENDING))
            adapter.setData(albumFiles)
            binding.observable!!.isEmpty.set(albumFiles.size == 0)
            showToast(context, "Data ${albumFiles.size}")
        })
    }

    @SuppressLint("RestrictedApi")
    fun initRecycleView() {
        val decorator = GalleryListAdapter.TimelineItemDecorator(
            requireContext(),
            R.dimen.timeline_decorator_spacing
        )
        gridLayoutManager =  GridLayoutManager(context, timelineGridSize)

        recycleView.layoutManager = gridLayoutManager
        recycleView.addItemDecoration(decorator)

        recycleView.itemAnimator = DefaultItemAnimator()
        recycleView.isNestedScrollingEnabled = false
        adapter = GalleryListAdapter(listData, this)
        adapter.setTimelineGridSize(timelineGridSize)
        adapter.setGroupingMode(groupingMode)
        adapter.setGridLayoutManager(gridLayoutManager)
        // multi item should keep false
//        adapter.setHasStableIds(true)
        recycleView.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_timeline, menu)
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
                showToast(context, "Delete")
                if (adapter.getSelectedCount() == 0) {
                    showToast(context, getString(R.string.no_item_selected_cant_delete)); false
                } else { binding.homeModel!!.deleteListFile(adapter.getPathSelectedItem()); true }
            }

            R.id.timeline_menu_select_all -> {
                showToast(context, "Select All")
                if (adapter.getSelectedCount() == adapter.getMediaCount()) {
                    adapter.deSelectAll()
                    item.title = "Select All"
                }
                else {
                    adapter.selectAll()
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
        showToast(context, " click selected item ")
        val intent = Intent(context, MediaActivity::class.java)
        intent.putExtra(EXTRA_ARGS_ALBUM, binding.homeModel!!.listData.value?.get(0))
        intent.action = ACTION_OPEN_ALBUM
        intent.putExtra(EXTRA_ARGS_POSITION, position)
        activity?.startActivity(intent)
    }

    override fun onSelectMode(selectMode: Boolean) {
        updateToolbar()
    }

    override fun onSelectionCountChanged(selectionCount: Int, totalCount: Int) {
        editModeListener.onItemsSelected(selectionCount, totalCount)
    }

    override fun getSelectedCount() = adapter.getSelectedCount()

    override fun getTotalCount() = adapter.getMediaCount()

    override fun getToolbarButtonListener(editMode: Boolean) = when (editMode) {
        true -> View.OnClickListener { exitContextMenu() }
        false -> null
    }

    override fun getToolbarTitle() = when(editMode()) {
        true->null
        false-> getString(R.string.title_library)
    }

    override fun editMode() = adapter.isSelecting()

    override fun clearSelected() = adapter.clearSelected()
}
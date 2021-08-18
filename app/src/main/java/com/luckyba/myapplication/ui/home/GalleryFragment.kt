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
import com.luckyba.myapplication.common.ActionsListener
import com.luckyba.myapplication.common.BaseMediaGridFragment
import com.luckyba.myapplication.data.model.AlbumFile
import com.luckyba.myapplication.data.model.AlbumFolder
import com.luckyba.myapplication.data.sort.MediaComparators
import com.luckyba.myapplication.data.sort.SortingMode
import com.luckyba.myapplication.data.sort.SortingOrder
import com.luckyba.myapplication.databinding.FragmentHomeBinding
import com.luckyba.myapplication.ui.detail.DetailActivity
import com.luckyba.myapplication.ui.timeline.GroupingMode
import com.luckyba.myapplication.ui.timeline.TimelineAdapter
import com.luckyba.myapplication.util.DeviceUtils
import com.luckyba.myapplication.util.DeviceUtils.TIMELINE_ITEMS_LANDSCAPE
import com.luckyba.myapplication.util.DeviceUtils.TIMELINE_ITEMS_PORTRAIT
import com.luckyba.myapplication.util.FilterMode
import com.luckyba.myapplication.util.GalleryUtil.Companion.isMaxBundle
import com.luckyba.myapplication.util.ObservableViewModel
import com.luckyba.myapplication.util.StringUtils
import com.luckyba.myapplication.util.StringUtils.EXTRA_ARGS_ALBUM
import com.luckyba.myapplication.util.StringUtils.EXTRA_ARGS_MEDIA
import com.luckyba.myapplication.util.StringUtils.showToast
import com.luckyba.myapplication.viewmodel.GalleryViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList


class GalleryFragment : BaseMediaGridFragment(), ActionsListener {
    lateinit var binding: FragmentHomeBinding
    lateinit var adapter: TimelineAdapter
    private lateinit var recycleView: RecyclerView
    private lateinit var gridLayoutManager: GridLayoutManager

    var albumFolder: AlbumFolder = AlbumFolder()

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
            albumFolder = it[0]

            val albumFiles = it[0].albumFiles
            Collections.sort(
                albumFiles, MediaComparators.getComparator(
                    SortingMode.DATE,
                    SortingOrder.DESCENDING
                )
            )
            adapter.setData(albumFiles)
            binding.observable!!.isEmpty.set(albumFiles.size == 0)
            showToast(context, "Data ${albumFiles.size}")
        })

        binding.homeModel!!.isDataChanged.observe(viewLifecycleOwner, ::dataChanged)
    }

    private fun dataChanged (isChanged: Boolean) {
        if (isChanged) exitContextMenu()
    }

    @SuppressLint("RestrictedApi")
    fun initRecycleView() {
        val decorator = TimelineAdapter.TimelineItemDecorator(
            requireContext(),
            R.dimen.timeline_decorator_spacing
        )
        gridLayoutManager =  GridLayoutManager(context, timelineGridSize)

        recycleView.layoutManager = gridLayoutManager
        recycleView.addItemDecoration(decorator)

        recycleView.itemAnimator = DefaultItemAnimator()
        recycleView.isNestedScrollingEnabled = false
        adapter = TimelineAdapter(ArrayList(),this)
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
            R.id.timeline_share -> {
                showToast(context, getString(R.string.comming_soon))
                false
            }

            R.id.timeline_menu_delete -> {
                showToast(context, "Delete")
                if (adapter.getSelectedCount() == 0) {
                    showToast(context, getString(R.string.no_item_selected_cant_delete)); false
                } else {
                    GlobalScope.launch(Dispatchers.Main) {
                        binding.homeModel!!.deleteListFile(adapter.getPathSelectedItem());
                    }
//                    binding.homeModel!!.deleteListFile(adapter.getPathSelectedItem());
                    true
                }
            }

            R.id.timeline_menu_select_all -> {
                showToast(context, "Select All")
                if (adapter.getSelectedCount() == adapter.getMediaCount()) {
                    adapter.deSelectAll()
                    item.title = "Select All"
                } else {
                    adapter.selectAll()
                    updateToolbar()
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
//        showToast(context, " click selected item $position")
        val intent = Intent(context, DetailActivity::class.java)
        val bundle = Bundle()
        bundle.putParcelable(EXTRA_ARGS_ALBUM, albumFolder)

        if(!isMaxBundle(bundle)) {
            intent.action = StringUtils.ACTION_OPEN_ALBUM
            intent.putExtra(EXTRA_ARGS_ALBUM, albumFolder)
            intent.putExtra(StringUtils.EXTRA_ARGS_POSITION, position)
            requireActivity().startActivity(intent)
        } else {
            intent.action = StringUtils.ACTION_OPEN_ALBUM_LAZY
            intent.putExtra(EXTRA_ARGS_MEDIA, albumFolder.albumFiles[position])
            requireActivity().startActivity(intent)
        }


    }

    fun convertToPath(list: ArrayList<AlbumFile>): ArrayList<String> {
        var listPath: ArrayList<String> = ArrayList()
        for (album in list) {
            album.path?.let { listPath.add(it) }
        }
        return listPath
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
        true -> null
        false -> getString(R.string.title_library)
    }

    override fun editMode() = adapter.isSelecting()

    override fun clearSelected() = adapter.clearSelected()
}
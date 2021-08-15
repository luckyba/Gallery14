package com.luckyba.myapplication.ui.timeline

import android.content.Context
import android.graphics.Rect
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DimenRes
import androidx.annotation.NonNull
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.luckyba.myapplication.common.ActionsListener
import com.luckyba.myapplication.data.model.AlbumFile
import com.luckyba.myapplication.data.sort.SortingOrder
import com.luckyba.myapplication.databinding.GalleryHeaderItemBinding
import com.luckyba.myapplication.databinding.GalleryListItemViewBinding
import com.luckyba.myapplication.ui.timeline.data.TimelineHeaderModel
import com.luckyba.myapplication.ui.timeline.data.TimelineItem
import com.luckyba.myapplication.util.MediaType
import java.util.*
import kotlin.collections.ArrayList

class TimelineAdapter(
    private var listData: ArrayList<AlbumFile>?,
    private val actionsListener: ActionsListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var timelineItems: MutableList<TimelineItem>? = null

    private var sortingOrder: SortingOrder? = null
    private var groupingMode: GroupingMode? = null

    // span 3 items
    private var timelineGridSize = 3

    private var selectedPositions: MutableSet<Int> = hashSetOf()


    fun setGridLayoutManager(gridLayoutManager: GridLayoutManager) {
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val timelineItem: TimelineItem = getItem(position)

                // If we have a header item, occupy the entire width
                return if (timelineItem.timelineType == TimelineItem.TYPE_HEADER) timelineGridSize
                else 1

                // Else, a media item takes up a single space
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflate = LayoutInflater.from(parent.context)
        return if (viewType == TimelineItem.TYPE_HEADER) {
            val binding = GalleryHeaderItemBinding.inflate(inflate, parent, false)
            GalleryHeaderViewHolder(binding)
        } else {
            val binding = GalleryListItemViewBinding.inflate(inflate, parent, false)
            GalleryViewHolder(binding)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).timelineType
    }

     fun getItem(pos: Int) = timelineItems!![pos]

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val timelineItem = getItem(position)
        if (holder is GalleryViewHolder) {
            val galleryViewHolder = holder as GalleryViewHolder
            val albumFile = timelineItem as AlbumFile
            galleryViewHolder.binding.mediafile = albumFile

//            Log.d("GalleryListAdapter", "loadImage " + albumFile?.dataToString())
            if (albumFile.isChecked) {
                holder.binding.paddingValue = intArrayOf(15, 15, 15, 15)
            } else {
                holder.binding.paddingValue = intArrayOf(0, 0, 0, 0)
            }

            galleryViewHolder.itemView.setOnClickListener {
                if (isSelecting()) triggerSelection(holder.getAbsoluteAdapterPosition())
                else displayMedia(timelineItem)
            }
            galleryViewHolder.itemView.setOnLongClickListener {
                if (isSelecting()) triggerSelectionAllUpTo(holder.getAbsoluteAdapterPosition())
                else triggerSelection(holder.getAbsoluteAdapterPosition())
                true
            }

        } else if (holder is GalleryHeaderViewHolder) {
            val galleryHeaderViewHolder = holder as GalleryHeaderViewHolder
            galleryHeaderViewHolder.binding.dateTime =
                (timelineItem as TimelineHeaderModel).headerText
            // need handle more to set long click to select all in this group
        }

    }

    override fun getItemCount() = timelineItems!!.size

    class GalleryViewHolder(var binding: GalleryListItemViewBinding) :
        RecyclerView.ViewHolder(binding.root)

    class GalleryHeaderViewHolder(var binding: GalleryHeaderItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    private fun clearAll() = timelineItems?.clear()
    fun isSelecting() = selectedPositions.isNotEmpty()
    fun getSelectedCount() = selectedPositions.size
    fun getMediaCount() = listData!!.size

    fun setData(@NonNull mediaList: ArrayList<AlbumFile>) {
        listData = mediaList
        selectedPositions = hashSetOf()
        buildTimelineItems()
    }

    private fun buildTimelineItems() {
        clearAll()
        timelineItems = listData?.let { getTimelineItems(it) }
        notifyDataSetChanged()
    }


    private fun getTimelineItems(@NonNull mediaList: ArrayList<AlbumFile>): MutableList<TimelineItem>? {
        val timelineItemList: MutableList<TimelineItem> = ArrayList()
        var headersAdded = 0
        var currentDate: Calendar? = null
        for (position in mediaList.indices) {
            val mediaDate: Calendar = GregorianCalendar()
            mediaDate.timeInMillis = mediaList[position].modifiedDate * 1000L
            if (currentDate == null || !groupingMode!!.isInGroup(currentDate, mediaDate)) {
                currentDate = mediaDate
                val timelineHeaderModel = TimelineHeaderModel(mediaDate)
                timelineHeaderModel.setHeaderText(groupingMode!!.getGroupHeader(mediaDate))
//                Log.d("fdasfsa", " date " + groupingMode!!.getGroupHeader(mediaDate))
                timelineItemList.add(position + headersAdded, timelineHeaderModel)
                headersAdded++
            }
            timelineItemList.add(mediaList[position])
        }
        return timelineItemList
    }


    private fun triggerSelection(elementPos: Int) {
        val oldCount = selectedPositions.size
        val albumFile = getItem(elementPos) as AlbumFile
        if (selectedPositions.contains(elementPos)) {
            selectedPositions.remove(elementPos)
            albumFile.isChecked = false
        }
        else selectedPositions.add(elementPos)
        if (oldCount == 0 && isSelecting()) {
            actionsListener.onSelectMode(true)
            albumFile.isChecked = true
        }
        else if (oldCount == 1 && !isSelecting()) actionsListener.onSelectMode(false)
        else {
            actionsListener.onSelectionCountChanged(
                selectedPositions.size, listData!!.size
            )
            if (oldCount < selectedPositions.size) albumFile.isChecked = true
        }
        notifyItemChanged(elementPos)
    }

    private fun triggerSelectionAllUpTo(elemPos: Int) {
        var indexRightBeforeOrAfter = -1
        var minOffset = Int.MAX_VALUE
        for (selectedPosition in selectedPositions) {
            val offset = Math.abs(elemPos - selectedPosition)
            if (offset < minOffset) {
                minOffset = offset
                indexRightBeforeOrAfter = selectedPosition
            }
        }
        if (indexRightBeforeOrAfter != -1) {
            for (index in Math.min(elemPos, indexRightBeforeOrAfter)..Math.max(
                elemPos,
                indexRightBeforeOrAfter
            )) {
                if (timelineItems!![index] is AlbumFile) {
                    selectedPositions.add(index)
                    notifyItemChanged(index)
                }
            }
            actionsListener.onSelectionCountChanged(selectedPositions.size, listData!!.size)
        }
    }

    fun removeItem(item: AlbumFile?) {
        timelineItems
        for (pos in timelineItems!!.indices) {
            val timelineItem = timelineItems!![pos]
            if (timelineItem.timelineType == TimelineItem.TYPE_HEADER) continue
            val mediaItem: AlbumFile = timelineItem as AlbumFile
            if (mediaItem != item) continue
            timelineItems!!.removeAt(pos)
            notifyItemRemoved(pos)
            break
        }
    }

    class TimelineItemDecorator(@NonNull context: Context, @DimenRes dimenRes: Int) :
        RecyclerView.ItemDecoration() {
        private val pixelOffset: Int = context.resources.getDimensionPixelOffset(dimenRes)
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)
            outRect[pixelOffset, pixelOffset, pixelOffset] = pixelOffset
        }

    }

    fun setSortingOrder(@NonNull sortingOrder: SortingOrder?) {
        this.sortingOrder = sortingOrder
        notifyDataSetChanged()
    }

    fun setTimelineGridSize(timelineGridSize: Int) {
        this.timelineGridSize = timelineGridSize
    }

    fun setGroupingMode(@NonNull groupingMode: GroupingMode?) {
        this.groupingMode = groupingMode
        if (listData == null) return

        // Rebuild the Timeline Items
        buildTimelineItems()
    }

    private fun displayMedia(timelineItem: TimelineItem) {
        if (timelineItem.timelineType == TimelineItem.TYPE_MEDIA) {
            actionsListener.onItemSelected(listData!!.indexOf(timelineItem))
        }
    }

    fun clearSelected(): Boolean {
        val oldSelections: Set<Int> = HashSet(selectedPositions)
        selectedPositions.clear()
        for (selectedPos in oldSelections) {
            (timelineItems?.get(selectedPos) as AlbumFile).isChecked = false
            notifyItemChanged(selectedPos)
        }
        return true
    }

    fun selectAll() {
        val timelineItemSize = timelineItems!!.size
        for (pos in 0 until timelineItemSize) {
            val timelineItem = getItem(pos)
            if (timelineItem.timelineType == TimelineItem.TYPE_HEADER) continue

            // Select the element
            selectedPositions.add(pos)
            (timelineItem as AlbumFile).isChecked = true
        }
        notifyDataSetChanged()
        actionsListener.onSelectionCountChanged(selectedPositions.size, listData!!.size)
    }

    fun deSelectAll() {
        val timelineItemSize = timelineItems!!.size
        selectedPositions = hashSetOf()
        actionsListener.onSelectMode(false)

        for (pos in 0 until timelineItemSize) {
            val timelineItem = getItem(pos)
            if (timelineItem.timelineType == TimelineItem.TYPE_HEADER) continue

            (timelineItem as AlbumFile).isChecked = false
        }
        notifyDataSetChanged()
    }

    fun getPathSelectedItem (): MutableSet<String> {
        val listPathS: MutableSet<String> = hashSetOf()
        for (pos in selectedPositions) {
            (timelineItems!![pos] as AlbumFile).path?.let { listPathS.add(it) }
        }
        Log.d("fdsfdsa", "path $listPathS")
        return listPathS
    }

}
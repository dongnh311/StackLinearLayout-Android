package com.dongnh.smartlayoutmanager

import android.graphics.PointF
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sign

/**
 * Project : SmartlinearLayout
 * Created by DongNH on 26/12/2022.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
class SmartLayoutManager : RecyclerView.LayoutManager , RecyclerView.SmoothScroller.ScrollVectorProvider {
    companion object {
        const val HORIZONTAL = OrientationHelper.HORIZONTAL
        const val VERTICAL = OrientationHelper.VERTICAL
        const val MAX_VISIBLE_ITEMS = 3
        const val INVALID_POSITION = -1
        const val STACK_TOP = 1
        const val STACK_BOTTOM = 2
        const val STACK_LEFT = 3
        const val STACK_RIGHT = 4
    }

    constructor(orientation: Int, itemVisible: Int, isStackLayout: Boolean, offset: Int, modeStack: Int) {
        this.orientation = orientation
        this.isStackLayout = isStackLayout
        if (isStackLayout) {
            this.isCircleLayout = false
        }
        this.maxItemShow = itemVisible
        this.offsetBetweenItem = offset
        this.modeStack = modeStack

        if ((modeStack == STACK_TOP || modeStack == STACK_BOTTOM) && orientation == HORIZONTAL) {
            throw java.lang.Exception("Wrong type stack")
        }

        if ((modeStack == STACK_LEFT || modeStack == STACK_RIGHT) && orientation == VERTICAL) {
            throw java.lang.Exception("Wrong type stack")
        }
    }

    constructor(orientation: Int, itemVisible: Int, isCircleLayout: Boolean, offset: Int, isStackLayout: Boolean = false) {
        this.orientation = orientation
        this.isStackLayout = false
        this.isCircleLayout = isCircleLayout
        this.maxItemShow = itemVisible
        this.offsetBetweenItem = offset
    }

    var orientation = OrientationHelper.HORIZONTAL

    private var isCircleLayout = false
    private var isStackLayout = false

    private var maxItemShow = MAX_VISIBLE_ITEMS
    private var modeStack = STACK_TOP

    private var decoratedChildSizeInvalid = false
    private var decoratedChildWidth: Int = 0
    private var decoratedChildHeight: Int = 0
    private var offsetBetweenItem = 0

    private var centerItemPosition: Int = INVALID_POSITION
    private var itemsCount = 0
    private var viewPostLayout: PostLayoutListener? = null

    private var pendingScrollPosition = 0

    private val layoutHelper by lazy {
        LayoutHelper(maxItemShow)
    }

    private var onCenterItemSelectionListeners: OnCenterItemSelectionListener? = null

    private var pendingSmartSavedState: SmartSavedState? =
        null

    /**
     * Config post listener
     */
    fun configPostListener(postLayoutListener: PostLayoutListener) {
        viewPostLayout = postLayoutListener
        requestLayout()
    }

    /**
     * @return Scroll offset from nearest item from center
     */
    fun getOffsetCenterView(): Int {
        return getCurrentScrollPosition().roundToInt() * getScrollItemSize() - layoutHelper.scrollOffset
    }

    /**
     * @return current scroll position of center item. this value can be in any range if it is cycle layout.
     * if this is not, that then it is in [0, [- 1][.mItemsCount]]
     */
    private fun getCurrentScrollPosition(): Float {
        val fullScrollSize: Int = getMaxScrollOffset()
        return if (0 == fullScrollSize) {
            0F
        } else 1.0f * layoutHelper.scrollOffset / getScrollItemSize()
    }


    /**
     * @return full item size
     */
    private fun getScrollItemSize(): Int {
        return if (VERTICAL == orientation) {
            decoratedChildHeight
        } else {
            decoratedChildWidth
        }
    }

    /**
     * @return maximum scroll value to fill up all items in layout. Generally this is only needed for non cycle layouts.
     */
    private fun getMaxScrollOffset(): Int {
        return getScrollItemSize() * (itemsCount - 1)
    }

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun canScrollHorizontally(): Boolean {
        return 0 != childCount && HORIZONTAL == orientation
    }

    override fun canScrollVertically(): Boolean {
        return 0 != childCount && VERTICAL == orientation
    }

    /**
     * @return current layout center item
     */
    fun getCenterItemPosition(): Int {
        return centerItemPosition
    }

    /**
     * @param onCenterItemSelectionListener listener that will trigger when ItemSelectionChanges. can't be null
     */
    fun addOnItemSelectionListener(onCenterItemSelectionListener: OnCenterItemSelectionListener) {
        onCenterItemSelectionListeners = onCenterItemSelectionListener
    }

    /**
     * Remove listener
     */
    fun removeOnItemSelectionListener() {
        onCenterItemSelectionListeners = null
    }

    override fun scrollToPosition(position: Int) {
        require(0 <= position) { "position can't be less then 0. position is : $position" }
        pendingScrollPosition = position
        requestLayout()
    }

    override fun smoothScrollToPosition(
        recyclerView: RecyclerView,
        state: RecyclerView.State,
        position: Int
    ) {
        val linearSmoothScroller: LinearSmoothScroller =
            object : LinearSmoothScroller(recyclerView.context) {
                override fun calculateDyToMakeVisible(view: View, snapPreference: Int): Int {
                    return if (!canScrollVertically()) {
                        0
                    } else getOffsetForCurrentView(view)
                }

                override fun calculateDxToMakeVisible(view: View, snapPreference: Int): Int {
                    return if (!canScrollHorizontally()) {
                        0
                    } else getOffsetForCurrentView(view)
                }
            }
        linearSmoothScroller.targetPosition = position
        startSmoothScroll(linearSmoothScroller)
    }

    override fun computeScrollVectorForPosition(targetPosition: Int): PointF? {
        if (0 == childCount) {
            return null
        }
        val directionDistance: Float = getScrollDirection(targetPosition)
        val direction = -sign(directionDistance).toInt()
        return if (HORIZONTAL == orientation) {
            PointF(direction.toFloat(), 0F)
        } else {
            PointF(0F, direction.toFloat())
        }
    }

    private fun getScrollDirection(targetPosition: Int): Float {
        val currentScrollPosition: Float =
            makeScrollPositionInRange0ToCount(
                getCurrentScrollPosition(),
                itemsCount
            )
        return if (isCircleLayout) {
            val t1 = currentScrollPosition - targetPosition
            val t2: Float = abs(t1) - itemsCount
            if (abs(t1) > abs(t2)) {
                sign(t1) * t2
            } else {
                t1
            }
        } else {
            currentScrollPosition - targetPosition
        }
    }

    override fun scrollVerticallyBy(
        dy: Int,
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State
    ): Int {
        return if (HORIZONTAL == orientation) {
            0
        } else scrollBy(dy, recycler, state)
    }

    override fun scrollHorizontallyBy(
        dx: Int,
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State
    ): Int {
        return if (VERTICAL == orientation) {
            0
        } else scrollBy(dx, recycler, state)
    }

    /**
     * This method is called from [.scrollHorizontallyBy] and
     * [.scrollVerticallyBy] to calculate needed scroll that is allowed. <br></br>
     * <br></br>
     * This method may do relayout work.
     *
     * @param diff     distance that we want to scroll by
     * @param recycler Recycler to use for fetching potentially cached views for a position
     * @param state    Transient state of RecyclerView
     * @return distance that we actually scrolled by
     */
    @CallSuper
    private fun scrollBy(
        diff: Int,
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State
    ): Int {
        if (0 == childCount || 0 == diff) {
            return 0
        }

        val resultScroll: Int
        if (isCircleLayout) {
            resultScroll = diff
            layoutHelper.scrollOffset += resultScroll
            val maxOffset: Int = getScrollItemSize() * itemsCount
            while (0 > layoutHelper.scrollOffset) {
                layoutHelper.scrollOffset += maxOffset
            }
            while (layoutHelper.scrollOffset > maxOffset) {
                layoutHelper.scrollOffset -= maxOffset
            }
            layoutHelper.scrollOffset -= resultScroll
        } else {
            val maxOffset = getMaxScrollOffset()
            resultScroll = if (0 > layoutHelper.scrollOffset + diff) {
                -layoutHelper.scrollOffset //to make it 0
            } else if (layoutHelper.scrollOffset + diff > maxOffset) {
                maxOffset - layoutHelper.scrollOffset //to make it maxOffset
            } else {
                diff
            }
        }
        if (0 != resultScroll) {
            layoutHelper.scrollOffset += resultScroll
            fillData(recycler, state)
        }
        return resultScroll
    }

    @CallSuper
    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        if (0 == state.itemCount) {
            removeAndRecycleAllViews(recycler)
            selectItemCenterPosition(INVALID_POSITION)
            return
        }
        detachAndScrapAttachedViews(recycler)
        if (decoratedChildSizeInvalid) {
            val scrapList = recycler.scrapList
            val shouldRecycle: Boolean
            val view: View
            if (scrapList.isEmpty()) {
                shouldRecycle = true
                val itemsCount = state.itemCount
                view = recycler.getViewForPosition(
                    if (pendingScrollPosition == INVALID_POSITION) 0 else 0.coerceAtLeast(
                        (itemsCount - 1).coerceAtMost(pendingScrollPosition)
                    )
                )
                addView(view)
            } else {
                shouldRecycle = false
                view = scrapList[0].itemView
            }
            measureChildWithMargins(view, 0, 0)
            val decoratedChildWidth = getDecoratedMeasuredWidth(view)
            val decoratedChildHeight = getDecoratedMeasuredHeight(view)
            if (shouldRecycle) {
                detachAndScrapView(view, recycler)
            }
            if (decoratedChildWidth != this@SmartLayoutManager.decoratedChildWidth || decoratedChildHeight != this@SmartLayoutManager.decoratedChildHeight) {
                if (INVALID_POSITION == pendingScrollPosition && null == pendingSmartSavedState) {
                    pendingScrollPosition = centerItemPosition
                }
            }
            this@SmartLayoutManager.decoratedChildWidth = decoratedChildWidth
            this@SmartLayoutManager.decoratedChildHeight = decoratedChildHeight
            decoratedChildSizeInvalid = false
        }
        if (INVALID_POSITION != pendingScrollPosition) {
            val itemsCount = state.itemCount
            pendingScrollPosition =
                if (0 == itemsCount) INVALID_POSITION else 0.coerceAtLeast(
                    (itemsCount - 1).coerceAtMost(pendingScrollPosition)
                )
        }
        if (INVALID_POSITION != pendingScrollPosition) {
            layoutHelper.scrollOffset =
                calculateScrollForSelectingPosition(pendingScrollPosition, state)
            pendingScrollPosition =
                INVALID_POSITION
            pendingSmartSavedState = null
        } else if (null != pendingSmartSavedState) {
            layoutHelper.scrollOffset = calculateScrollForSelectingPosition(
                pendingSmartSavedState!!.centerItemPosition,
                state
            )
            pendingSmartSavedState = null
        } else if (state.didStructureChange() && INVALID_POSITION != centerItemPosition) {
            layoutHelper.scrollOffset =
                calculateScrollForSelectingPosition(centerItemPosition, state)
        }
        fillData(recycler, state)
    }

    private fun calculateScrollForSelectingPosition(
        itemPosition: Int,
        state: RecyclerView.State
    ): Int {
        if (itemPosition == INVALID_POSITION) {
            return 0
        }
        val fixedItemPosition =
            if (itemPosition < state.itemCount) itemPosition else state.itemCount - 1
        return fixedItemPosition * if (VERTICAL == orientation) decoratedChildHeight else decoratedChildWidth
    }

    private fun fillData(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        val currentScrollPosition = getCurrentScrollPosition()
        generateLayoutOrder(currentScrollPosition, state)
        detachAndScrapAttachedViews(recycler)
        recyclerOldViews(recycler)
        val width: Int = widthNoPadding()
        val height: Int = heightNoPadding()
        if (VERTICAL == orientation) {
            fillDataVertical(recycler, width, height)
        } else {
            fillDataHorizontal(recycler, width, height)
        }
        recycler.clear()
        detectOnItemSelectionChanged(currentScrollPosition, state)
    }

    private fun detectOnItemSelectionChanged(
        currentScrollPosition: Float,
        state: RecyclerView.State
    ) {
        val absCurrentScrollPosition: Float =
            makeScrollPositionInRange0ToCount(
                currentScrollPosition,
                state.itemCount
            )
        val centerItem = absCurrentScrollPosition.roundToInt()
        if (centerItemPosition != centerItem) {
            centerItemPosition = centerItem
            Handler(Looper.getMainLooper()).post { selectItemCenterPosition(centerItem) }
        }
    }

    private fun selectItemCenterPosition(centerItem: Int) {
        onCenterItemSelectionListeners?.onCenterItemChanged(centerItem)
    }

    private fun fillDataVertical(recycler: RecyclerView.Recycler, width: Int, height: Int) {
        val start: Int = (width - decoratedChildWidth) / 2
        val end: Int = start + decoratedChildWidth
        var centerViewTop: Int = (height - decoratedChildHeight) / 2
        if (isStackLayout) {
            when (modeStack) {
                STACK_TOP -> {
                    centerViewTop = 0
                }
                STACK_BOTTOM -> {
                    centerViewTop = height
                }
            }
        }
        var i = 0
        val count: Int = layoutHelper.layoutOrder?.size ?: 0
        while (i < count) {
            val layoutOrder: LayoutOrder = layoutHelper.layoutOrder?.get(i)!!
            val offset: Int = getCardOffsetByPositionDiff(layoutOrder.itemPositionDiff)
            var top = centerViewTop + offset

            if (isStackLayout) {
                if (modeStack == STACK_TOP) {
                    if (layoutOrder.itemPositionDiff > 0F) {
                        top += offsetBetweenItem
                    } else if (layoutOrder.itemPositionDiff < 0F) {
                        top = -decoratedChildHeight
                    }
                }
            }

            var bottom: Int = top + decoratedChildHeight

            if (isStackLayout) {
                if (modeStack == STACK_BOTTOM) {
                    bottom = centerViewTop
                    if (layoutOrder.itemPositionDiff > 0F) {
                        bottom = -decoratedChildHeight
                    } else if (layoutOrder.itemPositionDiff < 0F) {
                        bottom = centerViewTop + offset
                    }
                    top = bottom - decoratedChildHeight
                }
            }
            fillChildItem(start, top, end, bottom, layoutOrder, recycler, i)
            ++i
        }
    }

    private fun fillDataHorizontal(recycler: RecyclerView.Recycler, width: Int, height: Int) {
        val top: Int = (height - decoratedChildHeight) / 2
        val bottom: Int = top + decoratedChildHeight
        var centerViewStart: Int = (width - decoratedChildWidth) / 2

        if (isStackLayout) {
            when (modeStack) {
                STACK_LEFT -> {
                    centerViewStart = 0
                }
                STACK_RIGHT -> {
                    centerViewStart = width
                }
            }
        }
        var i = 0
        val count: Int = layoutHelper.layoutOrder?.size ?: 0
        while (i < count) {
            val layoutOrder: LayoutOrder = layoutHelper.layoutOrder?.get(i)!!
            val offset: Int = getCardOffsetByPositionDiff(layoutOrder.itemPositionDiff)
            var start = centerViewStart + offset

            if (isStackLayout) {
                if (modeStack == STACK_LEFT) {
                    if (layoutOrder.itemPositionDiff > 0F) {
                        start += offsetBetweenItem
                    } else if (layoutOrder.itemPositionDiff < 0F) {
                        start = -decoratedChildWidth
                    }
                }
            }

            var end: Int = start + decoratedChildWidth

            if (isStackLayout) {
                if (modeStack == STACK_RIGHT) {
                    end = centerViewStart
                    if (layoutOrder.itemPositionDiff > 0F) {
                        end = -decoratedChildWidth
                    } else if (layoutOrder.itemPositionDiff < 0F) {
                        end = centerViewStart + offset
                    }
                    start = end - decoratedChildWidth
                }
            }
            fillChildItem(start, top, end, bottom, layoutOrder, recycler, i)
            ++i
        }
    }

    private fun fillChildItem(
        start: Int,
        top: Int,
        end: Int,
        bottom: Int,
        layoutOrder: LayoutOrder,
        recycler: RecyclerView.Recycler,
        i: Int
    ) {
        val view: View = bindChild(layoutOrder.itemAdapterPosition, recycler)
        ViewCompat.setElevation(view, i.toFloat())
        var transformation: ItemTransformation? = null
        if (null != viewPostLayout) {
            transformation = viewPostLayout!!.transformChild(
                view,
                layoutOrder.itemPositionDiff,
                orientation,
                layoutOrder.itemAdapterPosition
            )
        }
        if (null == transformation) {
            view.layout(start, top, end, bottom)
        } else {
            view.layout(
                (start + transformation.translationX).roundToInt(),
                (top + transformation.translationY).roundToInt(),
                (end + transformation.translationX).roundToInt(),
                (bottom + transformation.translationY).roundToInt()
            )
            view.scaleX = transformation.scaleX
            view.scaleY = transformation.scaleY
        }
    }

    /**
     * Because we can support old Android versions, we should layout our children in specific order to make our center view in the top of layout
     * (this item should layout last). So this method will calculate layout order and fill up [.mLayoutHelper] object.
     * This object will be filled by only needed to layout items. Non visible items will not be there.
     *
     * @param currentScrollPosition current scroll position this is a value that indicates position of center item
     * (if this value is int, then center item is really in the center of the layout, else it is near state).
     * Be aware that this value can be in any range is it is cycle layout
     * @param state                 Transient state of RecyclerView
     * @see .getCurrentScrollPosition
     */
    private fun generateLayoutOrder(currentScrollPosition: Float, state: RecyclerView.State) {
        itemsCount = state.itemCount
        val absCurrentScrollPosition: Float =
            makeScrollPositionInRange0ToCount(
                currentScrollPosition,
                itemsCount
            )
        val centerItem = absCurrentScrollPosition.roundToInt()
        if (isCircleLayout && 1 < itemsCount) {
            val layoutCount: Int = (layoutHelper.maxVisibleItems * 2 + 1).coerceAtMost(itemsCount)
            layoutHelper.initLayoutOrder(layoutCount)
            val countLayoutHalf = layoutCount / 2
            // before center item
            for (i in 1..countLayoutHalf) {
                val position: Int =
                    (absCurrentScrollPosition - i + itemsCount).roundToInt() % itemsCount
                layoutHelper.setLayoutOrder(
                    countLayoutHalf - i,
                    position,
                    centerItem - absCurrentScrollPosition - i
                )
            }
            // after center item
            for (i in layoutCount - 1 downTo countLayoutHalf + 1) {
                val position: Int =
                    (absCurrentScrollPosition - i + layoutCount).roundToInt() % itemsCount
                layoutHelper.setLayoutOrder(
                    i - 1,
                    position,
                    centerItem - absCurrentScrollPosition + layoutCount - i
                )
            }
            layoutHelper.setLayoutOrder(
                layoutCount - 1,
                centerItem,
                centerItem - absCurrentScrollPosition
            )
        } else {
            val firstVisible = (centerItem - layoutHelper.maxVisibleItems).coerceAtLeast(0)
            val lastVisible: Int =
                (centerItem + layoutHelper.maxVisibleItems).coerceAtMost(itemsCount - 1)
            val layoutCount = lastVisible - firstVisible + 1
            layoutHelper.initLayoutOrder(layoutCount)
            for (i in firstVisible..lastVisible) {
                if (i == centerItem) {
                    layoutHelper.setLayoutOrder(layoutCount - 1, i, i - absCurrentScrollPosition)
                } else if (i < centerItem) {
                    layoutHelper.setLayoutOrder(i - firstVisible, i, i - absCurrentScrollPosition)
                } else {
                    layoutHelper.setLayoutOrder(
                        layoutCount - (i - centerItem) - 1,
                        i,
                        i - absCurrentScrollPosition
                    )
                }
            }
        }
    }

    private fun widthNoPadding(): Int {
        return width - paddingStart - paddingEnd
    }

    private fun heightNoPadding(): Int {
        return height - paddingEnd - paddingStart
    }

    private fun bindChild(position: Int, recycler: RecyclerView.Recycler): View {
        val view = recycler.getViewForPosition(position)
        addView(view)
        measureChildWithMargins(view, 0, 0)
        return view
    }

    private fun recyclerOldViews(recycler: RecyclerView.Recycler) {
        for (viewHolder in ArrayList(recycler.scrapList)) {
            val adapterPosition = viewHolder.adapterPosition
            var found = false
            for (layoutOrder in layoutHelper.layoutOrder!!) {
                if (layoutOrder?.itemAdapterPosition == adapterPosition) {
                    found = true
                    break
                }
            }
            if (!found) {
                recycler.recycleView(viewHolder.itemView)
            }
        }
    }

    /**
     * Called during [.fillData] to calculate item offset from layout center line. <br></br>
     * <br></br>
     * Returns [.convertItemPositionDiffToSmoothPositionDiff] * (size off area above center item when it is on the center). <br></br>
     * Sign is: plus if this item is bellow center line, minus if not<br></br>
     * <br></br>
     * ----- - area above it<br></br>
     * ||||| - center item<br></br>
     * ----- - area bellow it (it has the same size as are above center item)<br></br>
     *
     * @param itemPositionDiff current item difference with layout center line. if this is 0, then this item center is in layout center line.
     * if this is 1 then this item is bellow the layout center line in the full item size distance.
     * @return offset in scroll px coordinates.
     */
    private fun getCardOffsetByPositionDiff(itemPositionDiff: Float): Int {
        val smoothPosition = convertItemPositionDiffToSmoothPositionDiff(itemPositionDiff)
        val dimenDiff: Int =
            if (VERTICAL == orientation) {
                (heightNoPadding() - decoratedChildHeight) / 2
            } else {
                (widthNoPadding() - decoratedChildWidth) / 2
            }
        return (sign(itemPositionDiff) * dimenDiff * smoothPosition).roundToInt()
    }

    /**
     * Called during [.getCardOffsetByPositionDiff] for better item movement. <br></br>
     * Current implementation speed up items that are far from layout center line and slow down items that are close to this line.
     * This code is full of maths. If you want to make items move in a different way, probably you should override this method.<br></br>
     * Please see code comments for better explanations.
     *
     * @param itemPositionDiff current item difference with layout center line. if this is 0, then this item center is in layout center line.
     * if this is 1 then this item is bellow the layout center line in the full item size distance.
     * @return smooth position offset. needed for scroll calculation and better user experience.
     * @see .getCardOffsetByPositionDiff
     */
    private fun convertItemPositionDiffToSmoothPositionDiff(itemPositionDiff: Float): Double {
        // generally item moves the same way above center and bellow it. So we don't care about diff sign.
        val absIemPositionDiff = abs(itemPositionDiff)

        // Calculator position
        return (absIemPositionDiff / layoutHelper.maxVisibleItems).toDouble()
    }

    override fun onMeasure(
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State,
        widthSpec: Int,
        heightSpec: Int
    ) {
        decoratedChildSizeInvalid = true
        super.onMeasure(recycler, state, widthSpec, heightSpec)
    }

    override fun onAdapterChanged(
        oldAdapter: RecyclerView.Adapter<*>?,
        newAdapter: RecyclerView.Adapter<*>?
    ) {
        super.onAdapterChanged(oldAdapter, newAdapter)
        removeAllViews()
    }

    private fun getOffsetForCurrentView(view: View): Int {
        val targetPosition = getPosition(view)
        val directionDistance = getScrollDirection(targetPosition)
        return (directionDistance * getScrollItemSize()).roundToInt()
    }

    /**
     * Helper method that make scroll in range of [0, count). Generally this method is needed only for cycle layout.
     *
     * @param currentScrollPosition any scroll position range.
     * @param count                 adapter items count
     * @return good scroll position in range of [0, count)
     */
    private fun makeScrollPositionInRange0ToCount(currentScrollPosition: Float, count: Int): Float {
        var absCurrentScrollPosition = currentScrollPosition
        while (0 > absCurrentScrollPosition) {
            absCurrentScrollPosition += count.toFloat()
        }
        while (absCurrentScrollPosition.roundToInt() >= count) {
            absCurrentScrollPosition -= count.toFloat()
        }
        return absCurrentScrollPosition
    }
}
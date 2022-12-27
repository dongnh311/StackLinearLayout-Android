package com.dongnh.smartlayoutmanager

import java.lang.ref.WeakReference

/**
 * Project : SmartlinearLayout
 * Created by DongNH on 26/12/2022.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
class LayoutHelper(var maxVisibleItems: Int = 0) {

    var scrollOffset = 0

    var layoutOrder: Array<LayoutOrder?>? = null

    private val reusedItems =
        mutableListOf<WeakReference<LayoutOrder>>()

    /**
     * Called before any fill calls. Needed to recycle old items and init new array list. Generally this list is an array an it is reused.
     *
     * @param layoutCount items count that will be layout
     */
    fun initLayoutOrder(layoutCount: Int) {
        if (null == layoutOrder || layoutOrder!!.size != layoutCount) {
            if (null != layoutOrder) {
                recycleItems(layoutOrder!!)
            }
            layoutOrder = arrayOfNulls(layoutCount)

            fillLayoutOrder()
        }
    }

    /**
     * Called during layout generation process of filling this list. Should be called only after [.initLayoutOrder] method call.
     *
     * @param arrayPosition       position in layout order
     * @param itemAdapterPosition adapter position of item for future data filling logic
     * @param itemPositionDiff    difference of current item scroll position and center item position.
     * if this is a center item and it is in real center of layout, then this will be 0.
     * if current layout is not in the center, then this value will never be int.
     * if this item center is bellow layout center line then this value is greater then 0,
     * else less then 0.
     */
    fun setLayoutOrder(arrayPosition: Int, itemAdapterPosition: Int, itemPositionDiff: Float) {
        if (layoutOrder?.size!! > arrayPosition) {
            val item: LayoutOrder? =
                layoutOrder?.get(arrayPosition)
            item?.itemAdapterPosition = itemAdapterPosition
            item?.itemPositionDiff = itemPositionDiff
        }
    }

    /**
     * Checks is this screen Layout has this adapterPosition view in layout
     *
     * @param adapterPosition adapter position of item for future data filling logic
     * @return true is adapterItem is in layout
     */
    fun hasAdapterPosition(adapterPosition: Int): Boolean {
        if (null != layoutOrder) {
            for (layoutOrder in layoutOrder!!) {
                if (layoutOrder?.itemAdapterPosition == adapterPosition) {
                    return true
                }
            }
        }
        return false
    }

    private fun recycleItems(layoutOrders: Array<LayoutOrder?>) {
        for (layoutOrder in layoutOrders.toMutableList()) {
            reusedItems.add(
                WeakReference(layoutOrder)
            )
        }
    }


    private fun fillLayoutOrder() {
        var i = 0
        val length = layoutOrder!!.size
        while (i < length) {
            if (null == layoutOrder!![i]) {
                layoutOrder!![i] = createLayoutOrder()
            }
            ++i
        }
    }

    private fun createLayoutOrder(): LayoutOrder {
        val iterator: MutableIterator<WeakReference<LayoutOrder>> =
            reusedItems.iterator()
        while (iterator.hasNext()) {
            val layoutOrderWeakReference: WeakReference<LayoutOrder> =
                iterator.next()
            val layoutOrder: LayoutOrder? =
                layoutOrderWeakReference.get()
            iterator.remove()
            if (null != layoutOrder) {
                return layoutOrder
            }
        }
        return LayoutOrder()
    }
}
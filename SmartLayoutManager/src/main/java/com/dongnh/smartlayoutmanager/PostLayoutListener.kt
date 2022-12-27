package com.dongnh.smartlayoutmanager

import android.view.View

/**
 * Project : SmartlinearLayout
 * Created by DongNH on 26/12/2022.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */

abstract class PostLayoutListener {
    /**
     * Called after child layout finished. Generally you can do any translation and scaling work here.
     *
     * @param child                    view that was layout
     * @param itemPositionToCenterDiff view center line difference to layout center. if > 0 then this item is bellow layout center line, else if not
     * @param orientation              layoutManager orientation [.getLayoutDirection]
     * @param itemPositionInAdapter    item position inside adapter for this layout pass
     */
    open fun transformChild(
        child: View,
        itemPositionToCenterDiff: Float,
        orientation: Int,
        itemPositionInAdapter: Int
    ): ItemTransformation? {
        return transformChild(child, itemPositionToCenterDiff, orientation)
    }

    /**
     * Called after child layout finished. Generally you can do any translation and scaling work here.
     *
     * @param child                    view that was layout
     * @param itemPositionToCenterDiff view center line difference to layout center. if > 0 then this item is bellow layout center line, else if not
     * @param orientation              layoutManager orientation [.getLayoutDirection]
     */
    open fun transformChild(
        child: View,
        itemPositionToCenterDiff: Float,
        orientation: Int
    ): ItemTransformation? {
        throw IllegalStateException("at least one transformChild should be implemented")
    }
}
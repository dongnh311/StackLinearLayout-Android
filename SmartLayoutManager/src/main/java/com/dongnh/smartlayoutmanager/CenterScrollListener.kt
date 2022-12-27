package com.dongnh.smartlayoutmanager

import androidx.recyclerview.widget.RecyclerView

/**
 * Project : SmartlinearLayout
 * Created by DongNH on 26/12/2022.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
class CenterScrollListener : RecyclerView.OnScrollListener() {
    private var mAutoSet = true
    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        val layoutManager = recyclerView.layoutManager
        if (layoutManager !is SmartLayoutManager) {
            mAutoSet = true
            return
        }
        val lm: SmartLayoutManager? = layoutManager as SmartLayoutManager?
        if (!mAutoSet) {
            if (RecyclerView.SCROLL_STATE_IDLE == newState) {
                val scrollNeeded: Int? = lm?.getOffsetCenterView()
                if (scrollNeeded != null) {
                    if (SmartLayoutManager.HORIZONTAL == lm.orientation) {
                        recyclerView.smoothScrollBy(scrollNeeded, 0)
                    } else {
                        recyclerView.smoothScrollBy(0, scrollNeeded)
                    }
                }

                mAutoSet = true
            }
        }
        if (RecyclerView.SCROLL_STATE_DRAGGING == newState || RecyclerView.SCROLL_STATE_SETTLING == newState) {
            mAutoSet = false
        }
    }
}
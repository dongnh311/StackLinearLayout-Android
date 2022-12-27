package com.dongnh.smartlayoutmanager

/**
 * Project : SmartlinearLayout
 * Created by DongNH on 26/12/2022.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
class LayoutOrder {
    /**
     * Item adapter position
     */
    var itemAdapterPosition = 0

    /**
     * Item center difference to layout center. If center of item is bellow layout center, then this value is greater then 0, else it is less.
     */
    var itemPositionDiff = 0f
}
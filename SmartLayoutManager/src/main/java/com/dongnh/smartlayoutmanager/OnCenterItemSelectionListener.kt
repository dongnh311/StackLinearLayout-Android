package com.dongnh.smartlayoutmanager

/**
 * Project : SmartlinearLayout
 * Created by DongNH on 26/12/2022.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
interface OnCenterItemSelectionListener {
    /**
     * Listener that will be called on every change of center item.
     * This listener will be triggered on **every** layout operation if item was changed.
     * Do not do any expensive operations in this method since this will effect scroll experience.
     *
     * @param adapterPosition current layout center item
     */
    fun onCenterItemChanged(adapterPosition: Int)
}